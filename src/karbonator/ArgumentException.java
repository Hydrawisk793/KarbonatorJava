package karbonator;

public class ArgumentException extends Exception {
    public ArgumentException() {
        this("One or more arguments are not valid.");
    }
    
    public ArgumentException(ArgumentException src) {
        super(src);
    }
    
    public ArgumentException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
