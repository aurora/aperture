/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

public class Condition {

    private byte[] magicBytes;
    
    private int offset;
    
    private String parentType;
    
    public Condition(byte[] magicBytes, int offset, String parentType) {
        this.magicBytes = magicBytes;
        this.offset = offset;
        this.parentType = parentType;
    }
    
    public byte[] getMagicBytes() {
        return magicBytes;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getMinimumLength() {
        return magicBytes == null ? 0 : magicBytes.length + offset;
    }
    
    public String getParentType() {
        return parentType;
    }
    
    public boolean matches(byte[] bytes, int skippedLeadingBytes) {
        // check whether this Condition actually checks for a magic byte sequence 
        if (magicBytes == null || offset < 0) {
            return false;
        }
        
        // check whether the specified array is long enough to check for the byte sequence
        if (bytes.length < offset + magicBytes.length + skippedLeadingBytes) {
            return false;
        }
        
        // chech the magic bytes
        int realOffset = offset + skippedLeadingBytes;
        for (int i = 0; i < magicBytes.length; i++) {
            if (magicBytes[i] != bytes[i + realOffset]) {
                return false;
            }
        }

        // apparently all magic bytes are present
        return true;
    }
}
