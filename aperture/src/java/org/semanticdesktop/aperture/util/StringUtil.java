/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

/**
 * Provides utility methods for String handling.
 */
public class StringUtil {

    /**
     * Substitute String "old" by String "new" in String "text" everywhere.
     * 
     * @param olds The String to be substituted.
     * @param news The String containing the new content.
     * @param text The String in which the substitution is done.
     * @return The result String containing the substitutions; if no substitutions were made, the
     *         specified 'text' instance is returned.
     */
    public static String replace(String olds, String news, String text) {
        if (olds == null || olds.length() == 0) {
            // nothing to substitute.
            return text;
        }
        if (text == null) {
            return null;
        }

        // search for any occurences of 'olds'.
        int oldsIndex = text.indexOf(olds);
        if (oldsIndex == -1) {
            // Nothing to substitute.
            return text;
        }

        // we're going to do some substitutions.
        StringBuilder buffer = new StringBuilder(text.length());
        int prevIndex = 0;

        while (oldsIndex >= 0) {
            // first, add the text between the previous and the current occurence
            buffer.append(text.substring(prevIndex, oldsIndex));

            // then add the substition pattern
            buffer.append(news);

            // remember the index for the next loop
            prevIndex = oldsIndex + olds.length();

            // search for the next occurence
            oldsIndex = text.indexOf(olds, prevIndex);
        }

        // add the part after the last occurence
        buffer.append(text.substring(prevIndex));

        return buffer.toString();
    }
}
