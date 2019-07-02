package karbonator;

public class ArgumentNullException extends Exception {
    public ArgumentNullException() {
        this("One or more arguments are null.");
    }
    
    public ArgumentNullException(ArgumentNullException src) {
        super(src);
    }
    
    public ArgumentNullException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
