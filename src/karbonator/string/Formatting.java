package karbonator.string;

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
    
    private Formatting() {}
}
