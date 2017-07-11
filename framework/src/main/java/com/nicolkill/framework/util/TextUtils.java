package com.nicolkill.framework.util;

/**
 * Created by nicolkill on 3/27/17.
 */

public class TextUtils {

    public static final String SPECIAL_CHARACTERS = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
    public static final String ASCII_CHARACTERS = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";

    public static final String removeSpecialCharacters(String text) {
        String output = text;
        for (int i=0; i<SPECIAL_CHARACTERS.length(); i++) {
            output = output.replace(SPECIAL_CHARACTERS.charAt(i), ASCII_CHARACTERS.charAt(i));
        }
        return output;
    }

}
