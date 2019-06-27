package karbonator.net;

import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;

/**
 * An enum of web socket connection close code.
 * 
 * @author Hydrawisk793
 * @since 2017-03-15
 */
public enum WebSocketCloseCode {
    NORMAL_CLOSURE(1000),
    GOING_AWAY(1001),
    PROTOCOL_ERROR(1002),
    UNSUPPORTED_DATA(1003),
    RESERVED_1004(1004),
    NO_STATUS_RCVD(1005),
    ABNORMAL_CLOSURE(1006),
    INVALID_FRAME_PAYLOAD_DATA(1007),
    POLICY_VIOLATION(1008),
    MESSAGE_TOO_BIG(1009),
    MANDATORY_EXT(1010),
    INTERNAL_SERVER_ERROR(1011),
    TLS_HANDSHAKE(1015);
    
    public static final int CLOSE_CODE_SIZE = 2;
    
    public static WebSocketCloseCode fromCode(int code) {
        return codeEnumMap_.get(code);
    }
    
    private WebSocketCloseCode(int code) {
        code_ = code;
    }
    
    public int getCode() {
        return code_;
    }
    
    private static final Map<Integer, WebSocketCloseCode> codeEnumMap_ = new Hashtable<>();
    
    static {
        for(WebSocketCloseCode e : EnumSet.allOf(WebSocketCloseCode.class)) {
            codeEnumMap_.put(e.getCode(), e);
        }
    }
    
    private int code_;
}
