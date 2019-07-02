package karbonator.collection;

public class ArrayConverter {
    public static boolean[] toPrimitiveArray(Boolean[] src) {
        return toPrimitiveArray(src, false);
    }
    
    public static boolean[] toPrimitiveArray(Boolean[] src, boolean nullValue) {
        boolean[] result = new boolean [src.length];
        
        int index = 0;
        for(Boolean o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static byte[] toPrimitiveArray(Byte[] src) {
        return toPrimitiveArray(src, (byte)0);
    }
    
    public static byte[] toPrimitiveArray(Byte[] src, byte nullValue) {
        byte[] result = new byte [src.length];
        
        int index = 0;
        for(Byte o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static short[] toPrimitiveArray(Short[] src) {
        return toPrimitiveArray(src, (short)0);
    }
    
    public static short[] toPrimitiveArray(Short[] src, short nullValue) {
        short[] result = new short [src.length];
        
        int index = 0;
        for(Short o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static int[] toPrimitiveArray(Integer[] src) {
        return toPrimitiveArray(src, 0);
    }
    
    public static int[] toPrimitiveArray(Integer[] src, int nullValue) {
        int[] result = new int [src.length];
        
        int index = 0;
        for(Integer o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static long[] toPrimitiveArray(Long[] src) {
        return toPrimitiveArray(src, 0);
    }
    
    public static long[] toPrimitiveArray(Long[] src, long nullValue) {
        long[] result = new long [src.length];
        
        int index = 0;
        for(Long o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static float[] toPrimitiveArray(Float[] src) {
        return toPrimitiveArray(src, 0);
    }
    
    public static float[] toPrimitiveArray(Float[] src, float nullValue) {
        float[] result = new float [src.length];
        
        int index = 0;
        for(Float o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static double[] toPrimitiveArray(Double[] src) {
        return toPrimitiveArray(src, 0);
    }
    
    public static double[] toPrimitiveArray(Double[] src, double nullValue) {
        double[] result = new double [src.length];
        
        int index = 0;
        for(Double o : src) {
            result[index++] = (o==null?nullValue:o);
        }
        
        return result;
    }
    
    public static Boolean[] toWrapperArray(boolean[] src) {
        Boolean[] result = new Boolean [src.length];
        
        int index = 0;
        for(boolean v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    public static Byte[] toWrapperArray(byte[] src) {
        Byte[] result = new Byte [src.length];
        
        int index = 0;
        for(byte v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    public static Short[] toWrapperArray(short[] src) {
        Short[] result = new Short [src.length];
        
        int index = 0;
        for(short v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    public static Integer[] toWrapperArray(int[] src) {
        Integer[] result = new Integer [src.length];
        
        int index = 0;
        for(int v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    public static Long[] toWrapperArray(long[] src) {
        Long[] result = new Long [src.length];
        
        int index = 0;
        for(long v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    public static Float[] toWrapperArray(float[] src) {
        Float[] result = new Float [src.length];
        
        int index = 0;
        for(float v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    public static Double[] toWrapperArray(double[] src) {
        Double[] result = new Double [src.length];
        
        int index = 0;
        for(double v : src) {
            result[index++] = v;
        }
        
        return result;
    }
    
    private ArrayConverter() {
        
    }
}
