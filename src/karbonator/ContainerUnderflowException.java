package karbonator;

public class ContainerUnderflowException extends Exception {
    public ContainerUnderflowException() {
        this("The container is empty.");
    }
    
    public ContainerUnderflowException(Exception src) {
        super(src);
    }
    
    public ContainerUnderflowException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = -1L;
}
