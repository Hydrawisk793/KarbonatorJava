package karbonator.net;

public class StreamUnderflowException extends RuntimeException {
    private static final long serialVersionUID = 8406789590323037839L;
    
    public StreamUnderflowException() {
        super("Not enough bytes remained in the stream.");
    }
    
    public StreamUnderflowException(
        StreamUnderflowException o
    ) {
        super(o);
    }
    
    public StreamUnderflowException(
        String message
    ) {
        super(message);
    }
}
