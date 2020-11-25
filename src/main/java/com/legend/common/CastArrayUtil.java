package com.legend.common;

/**
 * 基本类型数组与包装类型数组相互转换的工具类
 */
public class CastArrayUtil {

    private CastArrayUtil(){}

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Byte[] toWrap(byte[] original) {
        int length = original.length;
        Byte[] dest = new Byte[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Float[] toWrap(float[] original) {
        int length = original.length;
        Float[] dest = new Float[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Double[] toWrap(double[] original) {
        int length = original.length;
        Double[] dest = new Double[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Boolean[] toWrap(boolean[] original) {
        int length = original.length;
        Boolean[] dest = new Boolean[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Long[] toWrap(long[] original) {
        int length = original.length;
        Long[] dest = new Long[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Character[] toWrap(char[] original) {
        int length = original.length;
        Character[] dest = new Character[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    public static Character[] toWrap(String original) {
        return toWrap(original.toCharArray());
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Integer[] toWrap(int[] original) {
        int length = original.length;
        Integer[] dest = new Integer[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将基本类型数组转换为对应的包装类型数组
     *
     * @param original
     * @return
     */
    public static Short[] toWrap(short[] original) {
        int len = original.length;
        Short[] dest = new Short[len];
        for (int i = 0; i < len; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static byte[] toPrimitive(Byte[] original) {
        int length = original.length;
        byte[] dest = new byte[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static float[] toPrimitive(Float[] original) {
        int length = original.length;
        float[] dest = new float[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static double[] toPrimitive(Double[] original) {
        int length = original.length;
        double[] dest = new double[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static boolean[] toPrimitive(Boolean[] original) {
        int length = original.length;
        boolean[] dest = new boolean[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static long[] toPrimitive(Long[] original) {
        int length = original.length;
        long[] dest = new long[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static char[] toPrimitive(Character[] original) {
        int length = original.length;
        char[] dest = new char[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static int[] toPrimitive(Integer[] original) {
        int length = original.length;
        int[] dest = new int[length];
        for (int i = 0; i < length; i++) {
            dest[i] = original[i];
        }
        return dest;
    }
    /**
     * 将包装类型数组转换为对应的基本类型数组
     *
     * @param original
     * @return
     */
    public static short[] toPrimitive(Short[] original) {
        int len = original.length;
        short[] dest = new short[len];
        for (int i = 0; i < len; i++) {
            dest[i] = original[i];
        }
        return dest;
    }

}