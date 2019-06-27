package karbonator.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import karbonator.net.WebSocketFrame.Opcode;
import karbonator.util.EventDispatcher;
import karbonator.util.EventListener;

/**
 * A web socket server that implements RFC 6455.<br/>
 * TODO Fully document this class..
 *
 * @author Hydrawisk793
 * @since 2017-03-15
 */
public class WebSocketServer {
    public static final int DEFAULT_PORT = 30000;
    
    private static final Charset CHARSET = Charset.forName("UTF-8");
    
    public enum EventType {
        SERVER_STARTED,
        SERVER_STOPPED,
        SESSION_OPENED,
        SESSION_CLOSED,
        MESSAGE_RECEIVED,
        ERROR_OCCURED
    }
    
    public static class Event {
        public Event(
            WebSocketServer server
        ) {
            server_ = server;
        }
        
        public Event(
            WebSocketServer server,
            Session session
        ) {
            this(server);
            session_ = session;
        }
        
        public Event(
            WebSocketServer server,
            Session session,
            WebSocketMessage message
        ) {
            this(server, session);
            message_ = message;
        }
        
        public Event(
            WebSocketServer server,
            Session session,
            String errorReason
        ) {
            this(server, session);
            errorReason_ = errorReason;
        }
        
        public WebSocketServer getServer() {
            return server_;
        }
        
        public Session getSession() {
            return session_;
        }
        
        public WebSocketMessage getMessage() {
            return message_;
        }
        
        public String getErrorReason() {
            return errorReason_;
        }
        
        private WebSocketServer server_;
        
        private Session session_;

        private WebSocketMessage message_;
        
        private String errorReason_;
    }
    
    /**
     * Represents a connection of a web socket server and a web socket client.
     */
    public static class Session {
        private Session(WebSocketServer server, int id, Socket socket) {
            server_ = server;
            id_ = id;
            socket_ = socket;
            alive_ = true;
            running_ = false;
            sender_ = null;
            receiver_ = null;
            attributes_ = new Hashtable<>();
        }
        
        public int getId() {
            return id_;
        }
        
        public boolean isAlive() {
            return alive_;
        }
        
        public boolean isRunning() {
            return alive_ && running_;
        }
        
        public boolean hasAttribute(
            String key
        ) {
            synchronized(attributes_) {
                return attributes_.containsKey(key);
            }
        }
        
        public Object getAttribute(
            String key
        ) {
            synchronized(attributes_) {
                return attributes_.get(key);
            }
        }
        
        public void setAttribute(
            String key,
            Object value
        ) {
            synchronized(attributes_) {
                attributes_.put(key, value);
            }
        }
        
        public void removeAttribute(
            String key
        ) {
            synchronized(attributes_) {
                attributes_.remove(key);
            }
        }
        
        public void removeAllAttributes() {
            synchronized(attributes_) {
                attributes_.clear();
            }
        }
        
        private void start() {
            if(alive_ && !running_) {
                running_ = true;
                
                sender_ = new Sender(this);
                sender_.start();
                
                receiver_ = new Receiver(this);
                receiver_.start();
                
                server_.eventDispatcher_.notifyListeners(
                    EventType.SESSION_OPENED,
                    new Event(server_, this)
                );
            }
        }
        
        /**
         * Disconnects the client connected to this session and kills the session.
         * 
         * @param closeCode
         * @param reason
         * @param waitForClosed
         */
        public synchronized void close(WebSocketCloseCode closeCode, String reason, boolean waitForClosed) {
            if(running_) {
                sender_.enqueueFrame(WebSocketFrame.createCloseConnectionFrame(closeCode.getCode(), false, 0, reason.getBytes(CHARSET)));
                
                if(waitForClosed) {
                    try {
                        sender_.thread_.join();
                    }
                    catch(InterruptedException e) {}
                    try {
                        receiver_.thread_.join();
                    }
                    catch(InterruptedException e) {}
                }
            }
        }
        
        /**
         * Sends a message to the client connected to this session.
         * 
         * @param message A message to send the client.
         */
        public void sendMessage(WebSocketMessage message) {
            for(WebSocketFrame frame : WebSocketFrame.encode(message.isBinary(), false, 0, message.getMessageBytes(), 0)) {
                sender_.enqueueFrame(frame);
            }
        }
        
        private static class Sender {
            public Sender(Session session) {
                session_ = session;
                frameQueue_ = new LinkedList<>();
                thread_ = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runTask();
                    }
                });
                thread_.setDaemon(true);
            }
            
            public void start() {
                thread_.start();
            }
            
            public void enqueueFrame(WebSocketFrame frame) {
                synchronized(frameQueue_) {
                    frameQueue_.add(frame);
                }
            }
            
            private void runTask() {
                try {
                    OutputStream os = session_.socket_.getOutputStream();
                    WebSocketFrame frame = null;
                    
                    for(boolean loop = true; loop && session_.isRunning(); ) {
                        if(!isQueueEmpty()) {
                            frame = dequeueFrame();
                            if(frame != null) {
                                if(frame.getOpcode() == Opcode.CLOSE_CONNECTION) {
                                    loop = false;
                                }
                                os.write(frame.toByteArray());
                            }
                        }
                        
                        Thread.sleep(1);
                    }
                }
                catch(IOException ioe) {
                    ioe.printStackTrace();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    session_.running_ = false;
                    session_.alive_ = false;
                    
                    try {
                        session_.socket_.close();
                    }
                    catch(IOException e) {}
                    
                    session_.server_.removeSession(session_);
                    
                    session_.server_.eventDispatcher_.notifyListeners(
                        EventType.SESSION_CLOSED,
                        new Event(session_.server_, session_, (WebSocketCloseMessage)null)
                    );
                }
            }
            
            private boolean isQueueEmpty() {
                synchronized(frameQueue_) {
                    return frameQueue_.isEmpty();
                }
            }
            
            private WebSocketFrame dequeueFrame() {
                synchronized(frameQueue_) {
                    return frameQueue_.remove(0);
                }
            }
            
            private Session session_;
            
            private List<WebSocketFrame> frameQueue_;
            
            private Thread thread_;
        }
        
        private static class Receiver {
            public enum State {
                READY,
                RECEIVING_FRAGMENTS
            }
            
            public Receiver(Session session) {
                session_ = session;
                state_ = State.READY;
                fragmentFrames_ = new ArrayList<>();
                running_ = false;
                asyncFrameReader_ = new WebSocketFrame.AsynchronousFrameReader(65536 * 100);
                thread_ = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runTask();
                    }
                });
                thread_.setDaemon(true);
            }
            
            public void start() {
                if(!running_) {
                    running_ = true;
                    thread_.start();
                }
            }
            
            private void runTask() {
                try {
                    InputStream is = session_.socket_.getInputStream();
                    
                    byte[] byteArray = new byte[65536];
                    int byteCount = 0;
                    while(running_ && session_.isRunning()) {
                        byteCount = is.read(byteArray);
                        if(byteCount > 0) {
                            asyncFrameReader_.consume(byteArray, 0, byteCount);
                        }
                        
                        for(WebSocketFrame frame : asyncFrameReader_.createWebSocketFrames()) {
                            switch(state_){
                            case READY:
                                if(frame.getOpcode().isControl()) {
                                    processControlFrame(frame);
                                }
                                else if(frame.isFinalFragment()) {
                                    switch(frame.getOpcode()) {
                                    case CONTINUATION:
                                        //Error
                                    break;
                                    case BINARY:
                                        session_.server_.eventDispatcher_.notifyListeners(
                                            EventType.MESSAGE_RECEIVED,
                                            new Event(
                                                session_.server_,
                                                session_,
                                                new WebSocketMessage(true, frame.getPayload())
                                            )
                                        );
                                    break;
                                    case TEXT:
                                        session_.server_.eventDispatcher_.notifyListeners(
                                            EventType.MESSAGE_RECEIVED,
                                            new Event(
                                                session_.server_,
                                                session_,
                                                new WebSocketMessage(false, frame.getPayload())
                                            )
                                        );
                                    break;
                                    default:
                                        //Error;
                                    }
                                }
                                else {
                                    fragmentFrames_.add(frame);
                                    
                                    state_ = State.RECEIVING_FRAGMENTS;
                                }
                            break;
                            case RECEIVING_FRAGMENTS:
                                if(frame.getOpcode().isControl()) {
                                    processControlFrame(frame);
                                }
                                else if(frame.getOpcode() == Opcode.CONTINUATION) {
                                    fragmentFrames_.add(frame);
                                    if(frame.isFinalFragment()) {
                                        session_.server_.eventDispatcher_.notifyListeners(
                                            EventType.MESSAGE_RECEIVED,
                                            new Event(
                                                session_.server_,
                                                session_,
                                                WebSocketFrame.mergeFragments(fragmentFrames_)
                                            )
                                        );
                                        fragmentFrames_.clear();
                                        
                                        state_ = State.READY;
                                    }
                                }
                                else {
                                    System.out.println("" + frame.getOpcode());
                                    throw new RuntimeException("What..?");
                                }
                            break;
                            }
                        }
                        
                        Thread.sleep(1);
                    }
                }
                catch(IOException ioe) {
                    ioe.printStackTrace();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    session_.running_ = false;
                    session_.alive_ = false;
                }
            }
            
            private void processControlFrame(WebSocketFrame frame) {
                if(!frame.getOpcode().isControl()){
                    throw new RuntimeException("What the hell?");
                }
                if(!frame.isFinalFragment()) {
                    throw new RuntimeException("Control frames cannot be fragmented.");
                }
                
                switch(frame.getOpcode()) {
                case PING:
                    session_.sender_.enqueueFrame(
                        WebSocketFrame.createPongFrame(false, 0, frame.getPayload())
                    );
                break;
                case PONG:
                    
                break;
                case CLOSE_CONNECTION:
                    running_ = false;
                break;
                default:
                    System.out.println("" + frame.getOpcode());
                }
            }
            
            private Session session_;
            
            private State state_;
            
            private List<WebSocketFrame> fragmentFrames_;
            
            private volatile boolean running_;
            
            private Thread thread_;
            
            private WebSocketFrame.AsynchronousFrameReader asyncFrameReader_;
        }
        
        private WebSocketServer server_;
        
        private int id_;
        
        private Socket socket_;
        
        private boolean alive_;
        
        private volatile boolean running_;
        
        private Sender sender_;
        
        private Receiver receiver_;
        
        private Map<String, Object> attributes_;
    }
    
    /**
     * Creates a new web socket server with default settings.
     */
    public WebSocketServer() {
        this(DEFAULT_PORT);
    }
    
    /**
     * Creates a new web socket server with a port number.
     * 
     * @param portNumber
     */
    public WebSocketServer(
        int portNumber
    ) {
        if(portNumber < 0) {
            throw new IllegalArgumentException();
        }
        port_ = portNumber;
        
        sessions_ = new Hashtable<>();
        sessionIdSeq_ = 0;
        
        eventDispatcher_ = new EventDispatcher<>();
    }
    
    /**
     * Replaces the port number of the web socket server.
     * 
     * @param port A new port number to replace existing one.
     */
    public void setPort(
        int port
    ) {
        if(running_) {
            throw new RuntimeException("Cannot change the port number while running. stop the server first.");
        }
        
        port_ = port;
    }
    
    /**
     * Adds an event listener that listens for web socket events.
     * 
     * @param eventType
     * @param listener A listener to add.
     */
    public void addEventListener(EventType eventType, EventListener<Event> listener) {        
        eventDispatcher_.add(eventType, listener);
    }
    
    /**
     * Removes an event listener that listens for web socket events.
     * 
     * @param eventType
     * @param listener A listener to remove.
     */
    public void removeEventListener(EventType eventType, EventListener<Event> listener) {
        eventDispatcher_.remove(eventType, listener);
    }
    
    /**
     * Starts the web socket server.<br/>
     * If the server has been already started, it does nothing.
     */
    public synchronized void start() {
        if(!running_) {
            running_ = true;
            
            eventDispatcher_.start();
            
            clientListenerThread_ = new Thread(new Runnable() {
                @Override
                public void run() {
                    listenClients();
                }
            });
            clientListenerThread_.setDaemon(true);
            clientListenerThread_.start();
        }
    }
    
    /**
     * Stops the web socket server.<br/>
     * If the server is not running, it does nothing.
     */
    public synchronized void stop() {
        if(running_) {
            running_ = false;
            
            try {
                serverSocket_.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            try {
                clientListenerThread_.join();
            }
            catch(InterruptedException ie) {}
            
            eventDispatcher_.stop(true);
        }
    }
    
    /**
     * Retrieves the number of alive sessions.
     * 
     * @return the number of alive sessions.
     */
    public int getSessionCount() {
        return sessions_.size();
    }
    
    /**
     * Sends a message to all alive sessions.
     * 
     * @param message A message to send.
     */
    public void broadcast(WebSocketMessage message) {
        synchronized(sessions_) {
            for(Session session : sessions_.values()) {
                session.sendMessage(message);
            }
        }
    }
    
    private void listenClients() {
        try {
            serverSocket_ = new ServerSocket(port_);
            serverSocket_.setSoTimeout(1000);
            
            buffer_ = new byte[1024 * 32];
            
            while(running_) {
                Socket clientSocket = acceptClient(serverSocket_);
                if(clientSocket != null) {
                    Session session = createSession(clientSocket);
                    session.start();
                }
            }
        }
        catch(SocketException se) {}
        catch(IOException ioe) {}
        finally {
            for(Session s : getAllSessions()) {
                s.close(WebSocketCloseCode.GOING_AWAY, "The web socket server has been terminated.", true);
            }
            
            running_ = false;
            
//            fireOnServerStopped(new Event(this));
//            listeners_.clear();
            
            eventDispatcher_.notifyListeners(EventType.SERVER_STOPPED, new Event(this));
        }
    }
    
    private Socket acceptClient(ServerSocket ss) throws IOException {
        Socket socket = null;
        
        try {
            socket = ss.accept();
            
            InputStream is = socket.getInputStream();
            int byteCount = is.read(buffer_);
            if(byteCount > 0) {
                HttpHeader requestHeader = new HttpHeader(buffer_);
                //System.out.println(requestHeader);
                HttpHeader responseHeader = WebSocketUtils.tryCreateConnectionAcceptHeader(requestHeader);
                if(responseHeader != null) {
                    socket.getOutputStream().write(responseHeader.toByteArray());
                }
            }
        }
        catch(SocketTimeoutException ste) {}
        
        return socket;
    }
    
    private Session createSession(Socket socket) {
        synchronized(sessions_) {
            int sessionId = ++sessionIdSeq_;
            Session session = new Session(this, sessionId, socket);
            sessions_.put(sessionId, session);
            
            return session;
        }
    }
    
    private void removeSession(Session session) {
        synchronized(sessions_) {
            sessions_.remove(session.getId());
        }
    }
    
    private Collection<Session> getAllSessions() {
        synchronized(sessions_) {
            return sessions_.values();
        }
    }
    
    private ServerSocket serverSocket_;
    
    private volatile boolean running_;
    
    private Thread clientListenerThread_;
    
    private int port_;
    
    private byte[] buffer_;
    
    private int sessionIdSeq_;
    
    private Map<Integer, Session> sessions_;
    
    private EventDispatcher<EventType, Event> eventDispatcher_;
}
