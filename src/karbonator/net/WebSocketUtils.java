package karbonator.net;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class WebSocketUtils {
    private static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
    
    private static final String WEB_SOCKET_ACCEPT_MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    
    /**
     * Tests if the HTTP header is valid web socket connection request header.
     * 
     * @param requestHeader a HTTP header that represents a web socket connection request.
     * @return true if the header is valid, false otherwise.
     */
    public static boolean isWebSocketConnectionRequestHeader(
        HttpHeader requestHeader
    ) {
        return requestHeader.getRequestMethod() == HttpRequestMethod.GET
            && requestHeader.has("Connection") && requestHeader.get("Connection").equals("Upgrade")
            && requestHeader.has("Upgrade") && requestHeader.get("Upgrade").equals("websocket")
            && requestHeader.has("Origin")
            && requestHeader.has("Sec-WebSocket-Key")
        ;
    }
    
    /**
     * Tries to create a web socket connection accept HTTP header from the request header.
     * 
     * @param requestHeader
     * @return a HTTP header object if succeeded, null otherwise.
     */
    public static HttpHeader tryCreateConnectionAcceptHeader(
        HttpHeader requestHeader
    ) {
        HttpHeader responseHeader = null;
        if(WebSocketUtils.isWebSocketConnectionRequestHeader(requestHeader)) {
            String secWebSocketKey = requestHeader.get("Sec-WebSocket-Key");
            
            try {
                MessageDigest sha1Md = MessageDigest.getInstance("SHA-1");
                
                responseHeader = new HttpHeader(
                    HttpHeader.Version.VERSION_1_1,
                    HttpStatusCode.SWITCHING_PROTOCOLS
                );
                responseHeader.set("Connection", "Upgrade");
                responseHeader.set("Upgrade", "websocket");
                responseHeader.set(
                    "Sec-WebSocket-Accept",
                    DatatypeConverter.printBase64Binary(
                        sha1Md.digest(
                            (secWebSocketKey + WEB_SOCKET_ACCEPT_MAGIC_STRING).getBytes(CHARSET_UTF_8)
                        )
                    )
                );
            }
            catch(NoSuchAlgorithmException nsae) {
                responseHeader = null;
            }
        }
        
        return responseHeader;
    }
    
    private WebSocketUtils() {}
}
