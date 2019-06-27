package karbonator.net;

import karbonator.BitUtils;

/**
 * A web socket message that is sent when the web socket session has been closed.
 * 
 * @author Hydrawisk793
 * @since 2017-03-15
 */
public class WebSocketCloseMessage extends WebSocketMessage {
    public WebSocketCloseMessage(
        byte[] messageBytes
    ) {
        super(true, messageBytes);
    }
    
    public WebSocketCloseMessage(
        WebSocketCloseCode closeCode,
        String reason
    ) {
        super(
            true,
            BitUtils.concat(
                BitUtils.toBytes(closeCode.getCode(), false),
                reason.getBytes(CHARSET)
            )
        );
    }
    
    public WebSocketCloseCode getCloseCode() {
        if(getMessageBytes().length < WebSocketCloseCode.CLOSE_CODE_SIZE) {
            throw new RuntimeException("Not enough message size.");
        }
        
        return WebSocketCloseCode.fromCode((getMessageBytes()[0] & 0xFF) << 8 | (getMessageBytes()[1] & 0xFF));
    }
    
    public String getReason() {
        return (
            getMessageBytes().length > WebSocketCloseCode.CLOSE_CODE_SIZE
            ? new String(
                getMessageBytes(),
                WebSocketCloseCode.CLOSE_CODE_SIZE,
                getMessageBytes().length - WebSocketCloseCode.CLOSE_CODE_SIZE,
                CHARSET
            )
            : ""
        );
    }
}
