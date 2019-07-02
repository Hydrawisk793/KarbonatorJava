package karbonator.math;

public class Utils {

    private static final float EPSILON = 0.000001f;

    public static boolean isZero(float v) {
        return v < EPSILON && v > -EPSILON;
    }
    public static boolean isZero(float v, float epsilon) {
        return v < epsilon && v > -epsilon;
    }
    public static boolean isSignSame(float a, float b) {
        return (a*b >= 0.0f);
    }
    public static boolean isEqual(float lhs, float rhs) {
        return isZero(lhs-rhs);
    }
    public static boolean isEqual(float lhs, float rhs, float epsilon) {
        return isZero(lhs-rhs, epsilon);
    }
    public static boolean intersects(float minA, float maxA, float minB, float maxB) {
        return !(minA > maxB || maxA < minB);
    }
    public static boolean intersects(float minA, float maxA, float minB, float maxB, float epsilon) {
        minA -= epsilon;
        maxA += epsilon;
        minB -= epsilon;
        maxB += epsilon;

        return intersects(minA, maxA, minB, maxB);
    }
    
    public static float getSign(float v) {
        if(v > 0.0f) {
            return 1.0f;
        }
        else if(v < 0.0f) {
            return -1.0f;
        }
        else {
            return 0.0f;
        }
    }
    public static float getNegatedSign(float v) {
        if(v < 0.0f) {
            return 1.0f;
        }
        else if(v > 0.0f) {
            return -1.0f;
        }
        else {
            return 0.0f;
        }
    }
    public static float abs(float v) {
        return Math.abs(v);
    }
    public static float inverse(float v) {
        return 1.0f/v;
    }    
    public static float sqrt(float v) {
        return (float)Math.sqrt(v);
    }
    public static float inverseSqrt(float v) {
        return (float)Math.sqrt(1.0f/v);
    }

}
