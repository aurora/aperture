/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

public class MagicString {

    private char[] magicChars;
    
    public MagicString(char[] magicChars) {
    	if (magicChars == null) {
    		throw new IllegalArgumentException("magicChars should not be null");
    	}
    	
        this.magicChars = magicChars;
    }
    
    public char[] getMagicChars() {
        return magicChars;
    }
    
    public int getMinimumLength() {
        return magicChars.length;
    }
    
    public boolean matches(char[] chars, int skippedLeadingChars) {
    	// check whether the specified array is long enough to check for the char sequence
        if (chars.length < magicChars.length + skippedLeadingChars) {
            return false;
        }
        
        // check the magic chars
        for (int i = 0; i < magicChars.length; i++) {
            if (magicChars[i] != chars[i + skippedLeadingChars]) {
                return false;
            }
        }

        // apparently all magic chars are present
        return true;
    }
}
