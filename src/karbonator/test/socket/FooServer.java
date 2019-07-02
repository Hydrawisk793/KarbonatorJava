package karbonator.test.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FooServer {

    public static void main(String [] args) {
        int portNumber = 8023;
        ServerSocket serverSocket = null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        Socket clientSocket = null;
        Object inputObject = null;
        Object outputObject = null;
        
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Listening at port : " + portNumber);
            
            for(;;) {
               clientSocket = serverSocket.accept();
               System.out.println("Connected client : " + clientSocket.getLocalAddress() + ", " + clientSocket.getPort());
               
               ois = new ObjectInputStream(clientSocket.getInputStream());
               inputObject = ois.readObject();
               System.out.println("Input : " + inputObject);
               
               oos = new ObjectOutputStream(clientSocket.getOutputStream());
               outputObject = ((String)inputObject) + " from Server...";
               oos.writeObject(outputObject);
               oos.flush();
               
               clientSocket.close();
               System.out.println("The connection is closed...");
            }
        }
        catch(Throwable t) {
            t.printStackTrace(System.err);
        }
        finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch(IOException ioe) {}
            }
            
            if(ois != null) {
                try {
                    ois.close();
                }
                catch(IOException ioe) {}
            }
            
            if(oos != null) {
                try {
                    oos.close();
                }
                catch(IOException ioe) {}
            }
        }
    }

}
