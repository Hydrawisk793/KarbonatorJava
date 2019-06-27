package karbonator.net;

import java.nio.charset.Charset;

import karbonator.BitUtils;

/**
 * Represents messages in web socket communications.
 * 
 * @author Hydron Carter
 * @since 2017-03-15
 */
public class WebSocketMessage {
    protected static final Charset CHARSET = Charset.forName("UTF-8");
    
    public WebSocketMessage(
        boolean isBinary,
        byte[] messageBytes
    ) {
        binary_ = isBinary;
        messageBytes_ = messageBytes;
    }
    
    /**
     * Retrieves whether the payload is binary.
     * 
     * @return true if the payload is binary, false otherwise.
     */
    public boolean isBinary() {
        return binary_;
    }
    
    public byte[] getMessageBytes() {
        return messageBytes_;
    }
    
    @Override
    public String toString() {
        return (
            binary_
            ? BitUtils.toHexText(messageBytes_, 0, messageBytes_.length, 8)
            : new String(messageBytes_, CHARSET)
        );
    }
    
    private boolean binary_;
    
    private byte[] messageBytes_;
}
