package karbonator.image;

public class RgbConverter {

    public static int getRGB888(byte r, byte g, byte b) {
        int rgb = r;
        rgb <<= 8;
        rgb |= g;
        rgb <<= 8;
        rgb |= b;
        
        return rgb;
    }
    public static int convertRGB888ToBGR555(byte r, byte g, byte b) {
        final double factor = (1/255.0)*31;
        
        int rgb = (int)(b*factor);
        rgb <<= 5;
        rgb |= (int)(g*factor);
        rgb <<= 5;
        rgb |= (int)(r*factor);
        
        return rgb;
    }

}
