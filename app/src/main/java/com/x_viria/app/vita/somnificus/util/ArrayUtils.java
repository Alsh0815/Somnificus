package com.x_viria.app.vita.somnificus.util;

public class ArrayUtils {

    public static <T> T[] shiftForward(T[] array) {
        int length = array.length;
        if (length > 1) {
            T firstElement = array[0];
            for (int i = 1; i < length; i++) {
                array[i - 1] = array[i];
            }
            array[length - 1] = firstElement;
        }
        return array;
    }

}
