package com.xjh.common.utils;

import java.util.Random;

public class RandomizeUtils {
    static final Random RANDOMIZE_LONG = new Random();

    public static Long randomizeLong(long val) {
        long result = 0;
        int i = 0;
        while (val > 0) {
            long mask = ((val & 0B1111) << 1) | (RANDOMIZE_LONG.nextBoolean() ? 1 : 0);
            mask <<= (i * 5L);
            result = result | mask;
            val >>>= 4;
            i++;
        }
        return result;
    }

    public static Long retrieveLong(long val) {
        long result = 0;
        int i = 0;
        while (val > 0){
            long mask = val & 0B11110;
            mask >>= 1;
            mask <<= (i * 4L);
            result = result | mask;
            val >>>= 5;
            i++;
        }
        return result;
    }

    public static void main(String[] args) {
        /// long s = Math.abs(RANDOMIZE_LONG.nextLong()) >>> 16;
        long s = RANDOMIZE_LONG.nextInt(Integer.MAX_VALUE);
        System.out.println(s);

        for(int i = 0; i<10;i++){
            long rr = randomizeLong(s);
            System.out.println(rr);
            System.out.println(retrieveLong(rr));
            System.out.println();
        }
    }
}
