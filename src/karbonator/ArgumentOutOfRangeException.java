package karbonator;

public class ArgumentOutOfRangeException extends ArgumentException {
    public ArgumentOutOfRangeException() {
        this("One or more arguments are out of range.");
    }
    
    public ArgumentOutOfRangeException(ArgumentOutOfRangeException src) {
        super(src);
    }
    
    public ArgumentOutOfRangeException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
