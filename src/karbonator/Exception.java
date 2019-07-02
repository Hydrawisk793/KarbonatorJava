package karbonator;

public class Exception extends RuntimeException {
    public Exception() {
        this("An exception has occured.");
    }
    
    public Exception(Exception src) {
        super(src);
    }
    
    public Exception(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
