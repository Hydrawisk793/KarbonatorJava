package karbonator.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class WebSocketServerMk2 {
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
    
    private static void receiveFromChannel(
        ByteBuffer buffer,
        SocketChannel chan
    ) throws IOException {
        while(chan.read(buffer) > 0);
    }
    
    private static void sendToChannel(
        SocketChannel chan,
        ByteBuffer buffer
    ) throws IOException {
        while(buffer.hasRemaining()) {
            chan.write(buffer);
        }
    }
    
    public static class Event {
        public Event(
            WebSocketServerMk2 server
        ) {
            server_ = server;
        }
        
        public Event(
            WebSocketServerMk2 server,
            Session session
        ) {
            this(server);
            session_ = session;
        }
        
        public Event(
            WebSocketServerMk2 server,
            Session session,
            WebSocketMessage message
        ) {
            this(server, session);
            message_ = message;
        }
        
        public Event(
            WebSocketServerMk2 server,
            Session session,
            RuntimeException exception,
            String errorReason
        ) {
            this(server, session);
            exception_ = exception;
            errorReason_ = errorReason;
        }
        
        public WebSocketServerMk2 getServer() {
            return server_;
        }
        
        public Session getSession() {
            return session_;
        }
        
        public WebSocketMessage getMessage() {
            return message_;
        }
        
        public RuntimeException getException() {
            return exception_;
        }
        
        public String getErrorReason() {
            return errorReason_;
        }
        
        private WebSocketServerMk2 server_;
        
        private Session session_;
        
        private WebSocketMessage message_;
        
        private RuntimeException exception_;
        
        private String errorReason_;
    }
    
    private static class Agent implements Runnable {
        private enum State {
            READY,
            RUNNING,
            STOPPING,
            STOPPED
        }
        
        public Agent(WebSocketServerMk2 server) {
            server_ = server;
            selector_ = null;
            serverSockChan_ = null;
            state_ = State.READY;
            thread_ = null;
        }
        
        public synchronized void start() {
            if(state_ == State.READY) {
                state_ = State.RUNNING;
                
                thread_ = new Thread(this);
                thread_.setDaemon(true);
                thread_.start();
            }
        }
        
        public synchronized void stop() {
            if(state_ == State.RUNNING) {                
                for(Session s : server_.getAllSessions()) {
                    s.close(WebSocketCloseCode.GOING_AWAY, "The web socket server has been terminated.", false);
                }
                
                state_ = State.STOPPING;
                
                try {
                    thread_.join();
                }
                catch(InterruptedException ie) {}
            }
        }
        
        @Override
        public void run() {
            try {
                selector_ = Selector.open();
                serverSockChan_ = ServerSocketChannel.open();
                serverSockChan_.socket().bind(new InetSocketAddress(server_.port_));
                serverSockChan_.configureBlocking(false);
                serverSockChan_.register(
                    selector_,
                    SelectionKey.OP_ACCEPT
                );
                
                server_.eventDispatcher_.notifyListeners(
                    EventType.SERVER_STARTED,
                    new Event(server_)
                );
                
                while(state_ == State.RUNNING || state_== State.STOPPING) {
                    selector_.selectNow();
                    Set<SelectionKey> keys = selector_.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while(iter.hasNext()) {
                        SelectionKey key = iter.next();
                        if(key.isValid() && key.isAcceptable()) {
                            acceptClient(key);
                        }
                        if(key.isValid() && key.isReadable()) {
                            ((Session)key.attachment()).runRecevingTask();
                        }
                        if(key.isValid() && key.isWritable()) {
                            ((Session)key.attachment()).runSendingTask();
                        }
                        
                        iter.remove();
                    }
                    
                    if(state_ == State.STOPPING) {
                        if(server_.getAllSessions().size() < 1) {
                            break;
                        }
                    }
                    
                    Thread.sleep(1);
                }
            }
            catch(InterruptedException ie) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, null,
                        new RuntimeException(ie), "An internal server error occured."
                    )
                );
                
                System.out.println(ie.getClass().getName() + ':' + ie.getMessage());
            }
            catch(IOException ioe) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, null,
                        new RuntimeException(ioe), "An internal server error occured."
                    )
                );
                
                System.out.println(ioe.getClass().getName() + ':' + ioe.getMessage());
            }
            catch(Exception e) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, null,
                        new RuntimeException(e), "An internal server error occured."
                    )
                );
                
                System.out.println(e.getClass().getName() + ':' + e.getMessage());
            }
            finally {
                state_ = State.STOPPED;
                
                if(serverSockChan_ != null) {
                    try {
                        serverSockChan_.close();
                    }
                    catch(IOException ioe) {}
                }
                
                if(selector_ != null) {
                    try {
                        selector_.close();
                    }
                    catch(IOException ioe) {}
                }
                
                server_.eventDispatcher_.notifyListeners(
                    EventType.SERVER_STOPPED,
                    new Event(server_)
                );
            }
        }
        
        private Session acceptClient(SelectionKey key) {
            Session session = null;
            
            try {
                ServerSocketChannel serverSockChan = (ServerSocketChannel)key.channel();
                SocketChannel socketChannel = serverSockChan.accept();
                if(socketChannel != null) {
                    session = new Session(server_, socketChannel, selector_);
                }
            }
            catch(IOException e) {
                e.printStackTrace();
                
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, null,
                        new RuntimeException(e), "Failed to establish a new web socket connection."
                    )
                );
            }
            
            return session;
        }
        
        private WebSocketServerMk2 server_;
        
        private Selector selector_;
        
        private ServerSocketChannel serverSockChan_;
        
        private volatile State state_;
        
        private Thread thread_;
    }
    
    /**
     * Represents a connection of a web socket server and a web socket client.
     */
    public static class Session {
        private enum State {
            INITIALIZED,
            CONNECTING,
            RUNNING,
            CLOSING,
            CLOSED
        }
        
        private enum ReceivingState {
            READY,
            RECEIVING_FRAGMENTS
        }
        
        private Session(
            WebSocketServerMk2 server,
            SocketChannel socketChannel,
            Selector selector
        ) throws IOException {
            socketChannel.configureBlocking(false);
            selectionKey_ = socketChannel.register(
                selector,
                SelectionKey.OP_READ | SelectionKey.OP_WRITE
            );
            selectionKey_.attach(this);
            
            server_ = server;
            id_ = ++server_.sessionIdSeq_;
            server_.addSession(this);
            
            sockChan_ = socketChannel;
            state_ = State.INITIALIZED;
            requestHeader_ = null;
            sendingFrameQueue_ = new LinkedList<>();
            sBuffer_ = ByteBuffer.allocate(1024 * 48);
            receivingState_ = ReceivingState.READY;
            recevingFrameQueue_ = new LinkedList<>();
            asyncFrameReader_ = new WebSocketFrame.AsynchronousFrameReader(1024 * 16);
            attributes_ = new Hashtable<>();
        }
        
        public int getId() {
            return id_;
        }
        
        public boolean isAlive() {
            return state_.ordinal() <= State.RUNNING.ordinal();
        }
        
        /**
         * Disconnects the client connected to this session and kills the session.
         * 
         * @param closeCode
         * @param reason
         * @param waitForClosed
         */
        public synchronized void close(
            WebSocketCloseCode closeCode,
            String reason,
            boolean waitForClosed
        ) {
            if(isAlive()) {
                state_ = State.CLOSING;
                
                enqueueSendingFrame(
                    WebSocketFrame.createCloseConnectionFrame(
                        closeCode.getCode(),
                        false, 0,
                        reason.getBytes(CHARSET)
                    )
                );
                
                if(waitForClosed) {
                    try {
                        while(state_ != State.CLOSED) {
                            Thread.sleep(1);
                        }
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
        public void sendMessage(
            WebSocketMessage message
        ) {
            for(WebSocketFrame frame : WebSocketFrame.encode(message.isBinary(), false, 0, message.getMessageBytes(), 0)) {
                enqueueSendingFrame(frame);
            }
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
        
        private void suicide() {
            if(state_ != State.CLOSED) {
                state_ = State.CLOSED;
                
                server_.removeSession(this);
                
                try {
                    sockChan_.close();
                }
                catch(IOException e) {}
                
                server_.eventDispatcher_.notifyListeners(
                    EventType.SESSION_CLOSED,
                    new Event(server_, this)
                );
            }
        }
        
        private void runSendingTask() {
            if(state_ == State.CLOSED) {
                return;
            }
            
            try {
                switch(state_) {
                case CONNECTING:
                    HttpHeader responseHeader = WebSocketUtils.tryCreateConnectionAcceptHeader(requestHeader_);
                    if(responseHeader != null) {
                        byte[] resBytes = responseHeader.toByteArray();
                        sBuffer_.clear();
                        sBuffer_.put(resBytes);
                        sBuffer_.flip();
                        sendToChannel(sockChan_, sBuffer_);
                        
                        state_ = State.RUNNING;
                        
                        server_.eventDispatcher_.notifyListeners(
                            EventType.SESSION_OPENED,
                            new Event(server_, this)
                        );
                    }
                    else {
                        server_.eventDispatcher_.notifyListeners(
                            EventType.ERROR_OCCURED,
                            new Event(
                                server_, this,
                                null, "Cannot create a response header. The request header is invalid."
                            )
                        );
                        
                        suicide();
                    }
                break;
                case RUNNING:
                case CLOSING:
                    for(WebSocketFrame frame = null; (frame = dequeueSendingFrame()) != null; ) {
                        sBuffer_.clear();
                        sBuffer_.put(frame.toByteArray());
                        
                        sBuffer_.flip();
                        while(sBuffer_.hasRemaining()) {
                            sockChan_.write(sBuffer_);
                        }
                        
                        if(frame.getOpcode() == Opcode.CLOSE_CONNECTION) {
                            state_ = State.CLOSING;
                        }
                    }
                    
                    if(state_ == State.CLOSING) {
                        suicide();
                    }
                break;
                default:
                }
            }
            catch(IOException e) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, this,
                        new RuntimeException(e), "Cannot send messages to the client."
                    )
                );
                
                suicide();
            }
        }
        
        private void enqueueSendingFrame(WebSocketFrame frame) {
            synchronized(sendingFrameQueue_) {
                sendingFrameQueue_.add(frame);
            }
        }
        
        private WebSocketFrame dequeueSendingFrame() {
            synchronized(sendingFrameQueue_) {
                WebSocketFrame frame = null;
                if(!sendingFrameQueue_.isEmpty()) { 
                    frame = sendingFrameQueue_.get(0);
                    sendingFrameQueue_.remove(0);
                }
                
                return frame;
            }
        }
        
        private void runRecevingTask() {
            if(!isAlive()) {
                return;
            }
            
            try {
                switch(state_) {
                case INITIALIZED:
                    sBuffer_.clear();
                    receiveFromChannel(sBuffer_, sockChan_);
                    sBuffer_.flip();
                    if(sBuffer_.remaining() > 0) {
                        byte[] bytes = new byte[sBuffer_.remaining()];
                        sBuffer_.get(bytes);
                        requestHeader_ = new HttpHeader(bytes);
                        
                        state_ = State.CONNECTING;
                    }
                    else {
                        throw new RuntimeException("");
                    }
                break;
                case RUNNING:
                    asyncFrameReader_.consume(sockChan_);
                    
                    for(WebSocketFrame frame : asyncFrameReader_.createWebSocketFrames()) {
                        switch(receivingState_){
                        case READY:
                            onReceivingReady(frame);
                        break;
                        case RECEIVING_FRAGMENTS:
                            onReceivingFragments(frame);
                        break;
                        }
                    }
                    
                    if(state_ == State.CLOSING) {
                        suicide();
                    }
                break;
                default:
                    
                }
            }
            catch(IOException e) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, this,
                        new RuntimeException(e), "Cannot receive messages from the client."
                    )
                );
                
                suicide();
            }
            catch(RuntimeException re) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, this,
                        re, "An error has been occured from the client session."
                    )
                );
                
                suicide();
            }
            catch(Exception e) {
                server_.eventDispatcher_.notifyListeners(
                    EventType.ERROR_OCCURED,
                    new Event(
                        server_, this,
                        new RuntimeException(e), "An unexpected error has been occured from the client session."
                    )
                );
                
                suicide();
            }
        }
        
        private void onReceivingReady(WebSocketFrame frame) {
            if(frame.getOpcode().isControl()) {
                processControlFrame(frame);
            }
            else if(frame.isFinalFragment()) {
                switch(frame.getOpcode()) {
                case CONTINUATION:
                    //error
                break;
                case BINARY:
                    server_.eventDispatcher_.notifyListeners(
                        EventType.MESSAGE_RECEIVED,
                        new Event(
                            server_,
                            this,
                            new WebSocketMessage(true, frame.getPayload())
                        )
                    );
                break;
                case TEXT:
                    server_.eventDispatcher_.notifyListeners(
                        EventType.MESSAGE_RECEIVED,
                        new Event(
                            server_,
                            this,
                            new WebSocketMessage(false, frame.getPayload())
                        )
                    );
                break;
                default:
                    //Error;
                }
            }
            else {
                recevingFrameQueue_.add(frame);
                
                receivingState_ = ReceivingState.RECEIVING_FRAGMENTS;
            }
        }
        
        private void onReceivingFragments(WebSocketFrame frame) {
            if(frame.getOpcode().isControl()) {
                processControlFrame(frame);
            }
            else if(frame.getOpcode() == Opcode.CONTINUATION) {
                recevingFrameQueue_.add(frame);
                if(frame.isFinalFragment()) {
                    server_.eventDispatcher_.notifyListeners(
                        EventType.MESSAGE_RECEIVED,
                        new Event(
                            server_,
                            this,
                            WebSocketFrame.mergeFragments(recevingFrameQueue_)
                        )
                    );
                    recevingFrameQueue_.clear();
                    
                    receivingState_ = ReceivingState.READY;
                }
            }
            else {
                System.out.println("" + frame.getOpcode());
                throw new RuntimeException("What..?");
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
                enqueueSendingFrame(
                    WebSocketFrame.createPongFrame(false, 0, frame.getPayload())
                );
            break;
            case PONG:
                
            break;
            case CLOSE_CONNECTION:
                state_ = State.CLOSING;
            break;
            default:
                System.out.println("" + frame.getOpcode());
            }
        }
        
        private WebSocketServerMk2 server_;
        
        private int id_;
        
        private SocketChannel sockChan_;
        
        private SelectionKey selectionKey_;
        
        private volatile State state_;
        
        private HttpHeader requestHeader_;
        
        private List<WebSocketFrame> sendingFrameQueue_;
        
        private ByteBuffer sBuffer_;
        
        private ReceivingState receivingState_;
        
        private List<WebSocketFrame> recevingFrameQueue_;
        
        private WebSocketFrame.AsynchronousFrameReader asyncFrameReader_;
        
        private Map<String, Object> attributes_;
    }
    
    /**
     * Creates a new web socket server with default settings.
     */
    public WebSocketServerMk2() {
        this(DEFAULT_PORT);
    }
    
    /**
     * Creates a new web socket server with a port number.
     * 
     * @param portNumber
     */
    public WebSocketServerMk2(
        int portNumber
    ) {
        running_ = false;
        agent_ = null;
        setPort(portNumber);
        
        sessionIdSeq_ = 0;
        sessions_ = new Hashtable<>();
        
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
        
        if(port < 0) {
            throw new IllegalArgumentException();
        }
        port_ = port;
    }
    
    /**
     * Starts the web socket server.
     * <br/>If the server has been already started, it does nothing.
     */
    public synchronized void start() {
        if(!running_) {
            running_ = true;
            
            eventDispatcher_.start();
            
            agent_ = new Agent(this);
            agent_.start();
        }
    }
    
    /**
     * Stops the web socket server.
     * <br/>If the server is not running, it does nothing.
     */
    public synchronized void stop() {
        if(running_) {
            running_ = false;
            
            agent_.stop();
            
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
    
    private void addSession(Session session) {
        synchronized(sessions_) {
            sessions_.put(session.getId(), session);
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
    
    private volatile boolean running_;
    
    private Agent agent_;
    
    private int port_;
    
    private int sessionIdSeq_;
    
    private Map<Integer, Session> sessions_;
    
    private EventDispatcher<EventType, Event> eventDispatcher_;
}
