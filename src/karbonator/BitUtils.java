package karbonator;

import static karbonator.string.Formatting.*;

public class BitUtils {
    public static byte[] reverse(byte[] dest) {
        byte temp = 0;
        for(int i = 0, j = dest.length; i < dest.length >> 1; ++i) {
            --j;
            
            temp = dest[i];
            dest[i] = dest[j];
            dest[j] = temp;
        }
        
        return dest;
    }
    
    public static byte[] toBytes(
        short s,
        boolean byteOrderReversed
    ) {
        return (
            byteOrderReversed
            ? new byte[] {
                (byte)(s & 0xFF),
                (byte)((s & 0xFF00) >> 8)
            }
            : new byte[] {
                (byte)((s & 0xFF00) >> 8),
                (byte)(s & 0xFF)
            }
        );
    }
    
    public static byte[] toBytes(
        int i,
        boolean byteOrderReversed
    ) {
        return (
            byteOrderReversed
            ? new byte[] {
                (byte)(i & 0xFF),
                (byte)((i & 0xFF00) >> 8),
                (byte)((i & 0xFF0000) >> 16),
                (byte)((i & 0xFF000000) >> 24)
            }
            : new byte[] {
                (byte)((i & 0xFF000000) >> 24),
                (byte)((i & 0xFF0000) >> 16),
                (byte)((i & 0xFF00) >> 8),
                (byte)(i & 0xFF)
            }
        );
    }
    
    public static byte[] toBytes(
        long l,
        boolean byteOrderReversed
    ) {
        return (
            byteOrderReversed
            ? new byte[] {
                (byte)(l & 0xFFL),
                (byte)((l & 0xFF00L) >> 8),
                (byte)((l & 0xFF0000L) >> 16),
                (byte)((l & 0xFF000000L) >> 24),
                (byte)((l & 0xFF00000000L) >> 32),
                (byte)((l & 0xFF0000000000L) >> 40),
                (byte)((l & 0xFF000000000000L) >> 48),
                (byte)((l & 0xFF00000000000000L) >> 56)
            }
            : new byte[] {
                (byte)((l & 0xFF00000000000000l) >> 56),
                (byte)((l & 0x00FF000000000000l) >> 48),
                (byte)((l & 0x0000FF0000000000l) >> 40),
                (byte)((l & 0x000000FF00000000l) >> 32),
                (byte)((l & 0x00000000FF000000l) >> 24),
                (byte)((l & 0x0000000000FF0000l) >> 16),
                (byte)((l & 0x000000000000FF00l) >> 8),
                (byte)((l & 0x00000000000000FFl) >> 0)
            }
        );
    }
    
    public static void toBytes(
        byte[] dest,
        int destOffset,
        short s,
        boolean byteOrderReversed
    ) {
        if(dest.length < 2) {
            throw new IllegalArgumentException();
        }
        if(destOffset < 0 || destOffset + 2 >= dest.length) {
            throw new IndexOutOfBoundsException();
        }
        
        if(byteOrderReversed) {
            dest[destOffset] = (byte)(s & 0xFF);
            ++destOffset;
            dest[destOffset] = (byte)((s & 0xFF00) >> 8);
        }
        else {
            dest[destOffset] = (byte)((s & 0xFF00) >> 8);
            ++destOffset;
            dest[destOffset] = (byte)(s & 0xFF);
        }
    }
    
    public static short int16ValueFromBytes(
        byte[] bytes,
        int offset,
        boolean byteOrderReversed
    ) {
        if(offset + 2 > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        
        short value = 0;
        if(byteOrderReversed) {
            value = (short)(
                (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
            );
        }
        else {
            value = (short)(
                ((bytes[offset] & 0xFF) << 8)
                | (bytes[offset + 1] & 0xFF)
            );
        }
        
        return value;
    }
    
    public static int uint16ValueFromBytes(
        byte[] bytes,
        int offset,
        boolean byteOrderReversed
    ) {
        if(offset + 2 > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        
        int value = 0;
        if(byteOrderReversed) {
            value = (
                (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
            );
        }
        else {
            value = (
                ((bytes[offset] & 0xFF) << 8)
                | (bytes[offset + 1] & 0xFF)
            );
        }
        
        return value;
    }
    
    public static int int32ValueFromBytes(
        byte[] bytes,
        int offset,
        boolean byteOrderReversed
    ) {
        if(offset + 4 > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        
        int value = 0;
        if(byteOrderReversed) {
            value = (
                (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16)
                | ((bytes[offset + 3] & 0xFF) << 24)
            );
        }
        else {
            value = (
                ((bytes[offset] & 0xFF) << 24)
                | ((bytes[offset + 1] & 0xFF) << 16)
                | ((bytes[offset + 2] & 0xFF) << 8)
                | (bytes[offset + 3] & 0xFF)
            );
        }
        
        return value;
    }
    
    public static long longValueFromBytes(
        byte[] bytes,
        int offset,
        boolean byteOrderReversed
    ) {
        if(offset + 8 > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        
        long longValue = 0;
        int index = offset;
        
        if(byteOrderReversed) {
            index += 8;
            for(int i = 8; i > 0; ) {
                --i;
                --index;
                longValue <<= 8;
                longValue |= bytes[index];
            }
        }
        else {
            for(int i = 8; i > 0; ++index) {
                --i;
                longValue <<= 8;
                longValue |= bytes[index];
            }
        }
        
        return longValue;
    }
    
    public static int extendSign(int value, int bitLength) {
        if(bitLength < 1 || bitLength > 32) {
            throw new RuntimeException("");
        }
        
        int signBitmask = (1 << (bitLength - 1));
        if((value & signBitmask) != 0) {
            for(int i = 32 - bitLength; i > 0; ) {
                --i;
                signBitmask <<= 1;
                value |= signBitmask;
            }
        }
        
        return value;
    }
    
    public static byte[] concat(byte[]... byteArrays) {
        int prevLength = 0, length = 0;
        for(byte[] byteArray : byteArrays) {
            prevLength = length;
            length += byteArray.length;
            if(length < prevLength) {
                throw new RuntimeException("Underflow...");
            }
        }
        
        byte[] result = new byte[length];
        int offset = 0;
        for(int i = 0; i < byteArrays.length; ++i) {
            byte[] src = byteArrays[i];
            System.arraycopy(src, 0, result, offset, src.length);
            offset += src.length;
        }
        
        return result;
    }
    
    public static String toHexText(
        byte[] bytes,
        int offset, int byteCount,
        int bytesPerLine
    ) {
        StringBuilder sb = new StringBuilder();
        int count = byteCount;
        int i = offset;
        for(; i < byteCount && count >= bytesPerLine; count -= bytesPerLine) {
            for(int j = 0; j < bytesPerLine; ++j, ++i) {
                sb.append(padLeft(Integer.toHexString(bytes[i] & 0xFF), 2, '0'));
                sb.append(' ');
            }
            sb.append("\r\n");
        }
        for(; i < byteCount && count > 0; --count, ++i) {
            sb.append(padLeft(Integer.toHexString(bytes[i] & 0xFF), 2, '0'));
            sb.append(' ');
        }
        sb.append("\r\n");
        
        return sb.toString();
    }
    
    private BitUtils() {}
}
