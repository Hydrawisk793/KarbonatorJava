package karbonator.test.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FooClient {

    public static void main(String [] args) {
        int portNumber = 8023;
        Socket socket = null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        
        try {
            socket = new Socket("localhost", portNumber);
            
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("Hello, World...");
            oos.flush();
            Thread.sleep(5000);
            
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println(ois.readObject());
            
            socket.close();
            System.out.println("Connection closed...");            
            socket = null;
        }
        catch(Throwable t) {
            t.printStackTrace(System.err);
        }
        finally {
            if(oos != null) {
                try {
                    oos.close();
                }
                catch(IOException ioe) {}
            }
            
            if(ois != null) {
                try {
                    ois.close();
                }
                catch(IOException ioe) {}
            }
            
            if(socket != null) {
                try {
                    socket.close();
                }
                catch(IOException ioe) {}
            }
        }
    }

}
