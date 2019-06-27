package karbonator;

/**
 * 
 * @author Hydarwisk793
 * @since 2017-10-09
 */
public class InsufficientBytesException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public InsufficientBytesException(String message) {
        super(message);
    }
    
    public InsufficientBytesException(int requriedByteCount) {
        super("At least " + requriedByteCount + " bytes are required.");
    }
}
