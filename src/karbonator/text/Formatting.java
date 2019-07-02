package karbonator.text;

/**
 * @author Hydrawisk793
 * @since 2017-08-27
 */
public class Formatting {
    public static String padLeft(String str, int len, char ch) {
        StringBuilder sb = new StringBuilder();
        
        final int strLen = str.length();
        if(len > strLen) {
            final int padLen = len - strLen;
            
            for(int i = padLen; i > 0; ) {
                --i;
                sb.append(ch);
            }
        }
        
        sb.append(str);
        
        return sb.toString();
    }
    
    public static String padRight(String str, int len, char ch) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        
        final int strLen = str.length();
        if(len > strLen) {
            
            final int padLen = len - strLen;
            
            for(int i = padLen; i > 0; ) {
                --i;
                sb.append(ch);
            }
        }
        
        return sb.toString();
    }
    
    public static String toHexText(
        byte[] bytes,
        int offset, int byteCount,
        int bytesPerLine
    ) {
        StringBuilder sb = new StringBuilder();
        int count = byteCount;
        int i = offset;
        for(; i < byteCount && count >= bytesPerLine; count -= bytesPerLine) {
            for(int j = 0; j < bytesPerLine; ++j, ++i) {
                sb.append(padLeft(Integer.toHexString(bytes[i] & 0xFF), 2, '0'));
                sb.append(' ');
            }
            sb.append("\r\n");
        }
        for(; i < byteCount && count > 0; --count, ++i) {
            sb.append(padLeft(Integer.toHexString(bytes[i] & 0xFF), 2, '0'));
            sb.append(' ');
        }
        sb.append("\r\n");
        
        return sb.toString();
    }
    
    private Formatting() {}
}
