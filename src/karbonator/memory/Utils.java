package karbonator.memory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    public static void readSerializable(Serializable dest, ByteOrder byteOrder, String path) 
        throws FileNotFoundException, IOException {
        FileInputStream fin = null;
        ByteBuffer buffer = null;
        
        try {
            fin = new FileInputStream(path);
            buffer = new ByteBuffer(byteOrder);
            
            for(;fin.available()>0;) {
                buffer.writeInt8(fin.read());
            }
            
            dest.unserialize(buffer);
        }
        finally {
            if(fin != null) {
                fin.close();
            }
        }
    }
    
    public static void writeSerializable(Serializable src, ByteOrder byteOrder, String path) 
        throws FileNotFoundException, IOException {
        FileOutputStream fout = null;
        
        try {
            fout = new FileOutputStream(path);
            
            fout.write(src.serialize(byteOrder).toByteArray());
        }
        finally {
            if(fout != null) {
                fout.close();
            }
        }
    }
}
