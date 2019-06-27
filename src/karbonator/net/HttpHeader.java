package karbonator.net;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that represents HTTP header.
 * 
 * @author Hydrawisk793
 * @since 2017-03-15
 */
public class HttpHeader {
    private static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
    
    private static final String FIELD_VALUE_DELIMITER = ": ";
    
    private static final String END_OF_FIELD = "\r\n";
    
    private static final Pattern HTTP_HEADER_FIELD_REGEX = Pattern.compile(
        "^(.+?)" + FIELD_VALUE_DELIMITER + "(.+?)$"
    );
    
    public static String[] getEnumNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }
    
    /**
     * A enum that represents HTTP header versions.
     */
    public enum Version {
        VERSION_1_0,
        VERSION_1_1,
        VERSION_2_0
    }
    
    /**
     * Creates an empty HTTP 1.1 header.
     */
    public HttpHeader() {
        version_ = Version.VERSION_1_1;
        requestMethod_ = HttpRequestMethod.IS_NOT_REQUEST;
        requestUri_ = "";
        statusCode_ = HttpStatusCode.NULL;
        fields_ = new Hashtable<>();
    }
    
    public HttpHeader(HttpHeader o) {
        version_ = o.version_;
        requestMethod_ = o.requestMethod_;
        requestUri_ = o.requestUri_;
        statusCode_ = o.statusCode_;
        fields_ = new Hashtable<>();
        for(String fieldName : o.fields_.keySet()) {
            fields_.put(fieldName, o.fields_.get(fieldName));
        }
    }
    
    public HttpHeader(
        HttpRequestMethod requestMethod,
        String requestUri,
        Version version
    ) {
        if(version == Version.VERSION_2_0) {
            throw new RuntimeException("Not supported yet...");
        }
        
        version_ = version;
        requestMethod_ = requestMethod;
        requestUri_ = requestUri;
        statusCode_ = HttpStatusCode.NULL;
        fields_ = new Hashtable<>();
    }
    
    public HttpHeader(Version version, HttpStatusCode statusCode) {
        if(version == Version.VERSION_2_0) {
            throw new RuntimeException("Not supported yet...");
        }
        
        version_ = version;
        requestMethod_ = HttpRequestMethod.IS_NOT_REQUEST;
        requestUri_ = "";
        statusCode_ = statusCode;
        fields_ = new Hashtable<>();
    }
    
    /**
     * Parses an array of bytes and creates a HTTP header from it.
     * 
     * @param bytes the bytes to be parsed.
     */
    public HttpHeader(byte[] bytes) {
        String[] lines = new String(bytes, CHARSET_UTF_8).split("\\r\\n");
        if(lines.length < 1) {
            throw new RuntimeException("");
        }
        
        String[] firstLineTokens = lines[0].split(" ");
        String[] requestMethodNames = getEnumNames(HttpRequestMethod.class);
        int index = Arrays.asList(requestMethodNames).indexOf(firstLineTokens[0]);
        if(index >= 0) {
            requestMethod_ = Enum.valueOf(HttpRequestMethod.class, requestMethodNames[index]);
            requestUri_ = firstLineTokens[1];
            version_ = parseVersion(firstLineTokens[2]);
        }
        else {
            requestMethod_ = HttpRequestMethod.IS_NOT_REQUEST;
            version_ = parseVersion(firstLineTokens[0]);
            int statusCodeIndex = Arrays.asList(HttpStatusCode.values()).indexOf(Integer.parseInt(firstLineTokens[1], 10));
            statusCode_ = (
                statusCodeIndex >= 0
                ? HttpStatusCode.values()[statusCodeIndex]
                : HttpStatusCode.NULL
            );
        }
        
        fields_ = new Hashtable<>();
        for(int i = 1; i < lines.length - 1; ++i) {
            String line = lines[i];
            Matcher matcher = HTTP_HEADER_FIELD_REGEX.matcher(line);
            if(matcher.find() && matcher.groupCount() >= 2) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                fields_.put(key, value);
            }
        }
    }
    
    public Version getVersion() {
        return version_;
    }
    
    public HttpRequestMethod getRequestMethod() {
        return requestMethod_;
    }
    
    /**
     * Tests if the header has specified field.
     * 
     * @param fieldName
     * @return true if the header has the field, false otherwise.
     */
    public boolean has(String fieldName) {
        return fields_.containsKey(fieldName);
    }
    
    /**
     * Retrieves the value of specified field.
     * 
     * @param fieldName The name of the field.
     * @return the value of the specified field.
     */
    public String get(String fieldName) {
        return (
            fields_.containsKey(fieldName)
            ? fields_.get(fieldName)
            : null
        );
    }
    
    /**
     * Replaces the field value to specified one.
     * 
     * @param fieldName
     * @param value
     */
    public void set(String fieldName, String value) {
        fields_.remove(fieldName);
        fields_.put(fieldName, value);
    }
    
    /**
     * Removes specified field from the header.
     * 
     * @param fieldName
     */
    public void remove(String fieldName) {
        fields_.remove(fieldName);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if(requestMethod_ != HttpRequestMethod.IS_NOT_REQUEST) {
            sb.append(requestMethod_.toString());
            sb.append(' ');
            sb.append(requestUri_);
            sb.append(' ');
        }
        
        switch(version_) {
        case VERSION_1_0:
            sb.append("HTTP/");
            sb.append("1.0");
        break;
        case VERSION_1_1:
            sb.append("HTTP/");
            sb.append("1.1");
        break;
        case VERSION_2_0:
            sb.append("HTTP/");
            sb.append("2.0");
        break;
        }
        
        if(requestMethod_ == HttpRequestMethod.IS_NOT_REQUEST) {
            sb.append(' ');
            sb.append(Integer.toString(statusCode_.getCode(), 10));
            sb.append(' ');
            sb.append(statusCode_.getReason());
        }
        
        sb.append(END_OF_FIELD);
        
        for(String fieldName : fields_.keySet()) {
            sb.append(fieldName);
            sb.append(FIELD_VALUE_DELIMITER);
            sb.append(fields_.get(fieldName));
            sb.append(END_OF_FIELD);
        }
        
        sb.append(END_OF_FIELD);
        
        return sb.toString();
    }
    
    /**
     * Converts the header to an array of bytes.
     * 
     * @return a byte array representation of the header.
     */
    public byte[] toByteArray() {
        return toString().getBytes(CHARSET_UTF_8);
    }
    
    private Version parseVersion(String str) {
        Version version = Version.VERSION_1_1;
        
        String[] tokens = str.split("\\/");
        if(tokens.length >= 2) {
            switch(tokens[1]) {
            case "1.0":
                version = Version.VERSION_1_0;
            break;
            case "1.1":
                version = Version.VERSION_1_1;
            break;
            case "2.0":
                version = Version.VERSION_2_0;
            break;
            }
        }
        
        return version;
    }
    
    private Version version_;
    
    private HttpRequestMethod requestMethod_;
    
    private String requestUri_;
    
    private HttpStatusCode statusCode_;
    
    private Map<String, String> fields_;
}
