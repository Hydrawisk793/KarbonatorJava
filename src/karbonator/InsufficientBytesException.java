package karbonator;

/**
 * 
 * @author Hydarwisk793
 * @since 2017-10-09
 */
public class InsufficientBytesException extends Exception {
    public InsufficientBytesException() {
        this("");
    }
    
    public InsufficientBytesException(InsufficientBytesException src) {
        this(src.getMessage());
    }
    
    public InsufficientBytesException(Exception xcept) {
        super(xcept);
    }
    
    public InsufficientBytesException(int requriedByteCount) {
        super("At least " + requriedByteCount + " bytes are required.");
    }
    
    public InsufficientBytesException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
