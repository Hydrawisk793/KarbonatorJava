package karbonator;

public class ContainerOverflowException extends Exception {
    public ContainerOverflowException() {
        this("The container cannot hold more elements.");
    }
    
    public ContainerOverflowException(Exception src) {
        super(src);
    }
    
    public ContainerOverflowException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
