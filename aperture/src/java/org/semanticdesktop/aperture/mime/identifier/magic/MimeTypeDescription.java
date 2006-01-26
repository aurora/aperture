/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

import java.util.ArrayList;

public class MimeTypeDescription {

    private final String mimeType;

    private ArrayList extensions;

    private ArrayList conditions;

    private boolean allowsLeadingWhiteSpace;

    private ArrayList requiringTypes;

    public MimeTypeDescription(String mimeType, ArrayList extensions, ArrayList conditions,
            boolean allowsLeadingWhiteSpace) {
        this.mimeType = mimeType;
        this.extensions = extensions;
        this.conditions = conditions;
        this.allowsLeadingWhiteSpace = allowsLeadingWhiteSpace;
        requiringTypes = new ArrayList(0);
    }

    public String getMimeType() {
        return mimeType;
    }

    public ArrayList getExtensions() {
        return extensions;
    }

    public ArrayList getConditions() {
        return conditions;
    }

    public boolean getAllowsLeadingWhiteSpace() {
        return allowsLeadingWhiteSpace;
    }

    /**
     * Register a requiring MimeTypeDescription on this MimeTypeDescription. The specified
     * MimeTypeDescription should have at least one Condition that has this MimeTypeDescription's
     * mimeType as parent type.
     */
    public void addRequiringType(MimeTypeDescription description) {
        requiringTypes.add(description);
    }

    public ArrayList getRequiringTypes() {
        return requiringTypes;
    }

    /**
     * Returns whether one of the Conditions in this MimeTypeDescription has a magic number byte sequence
     * that matches the specified bytes.
     */
    public boolean hasMatchingCondition(byte[] bytes) {
        // skip white space if necessary
        int skippedBytes = 0;

        if (allowsLeadingWhiteSpace) {
            // The range of chars in ASCII overlaps with Unicode in the range 0 - 127 so we can happily
            // cast bytes to chars here and add some additional tests for bytes from the UTF-8 and UTF-16
            // Byte Order Marks (see
            // http://msdn.microsoft.com/library/default.asp?url=/library/en-us/intl/unicode_42jv.asp).
            //
            // The only problem is with the null characters used by UTF-16: not only does this make
            // recognition of white space characters more difficult (null chars appearing before or after
            // the "real" character bytes), also the whole concept of testing for magic number *bytes*
            // after that won't work at all, regardless of whether it starts with whitespace or not.
            // However, this probably only affects text/html and text/xml documents in UTF-16 format that
            // have no typical format-specific file extension and that are not obtained from a web or
            // email server that tells you the MIME type as a fallback mechanism :)
            for (int i = 0; i < bytes.length; i++) {
                char c = (char) bytes[i];
                if (Character.isWhitespace(c) || c == '\u0000' || c == '\u00ff' || c == '\u00fe'
                        || c == '\u00ef' || c == '\u00bb' || c == '\u00bf') {
                    skippedBytes++;
                }
                else {
                    break;
                }
            }
        }

        // now see if one of the conditions match
        int nrConditions = conditions.size();
        for (int i = 0; i < nrConditions; i++) {
            Condition condition = (Condition) conditions.get(i);
            if (condition.matches(bytes, skippedBytes)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the set of file extensions of this MimeTypeDescription contains the specified file
     * extension.
     */
    public boolean containsExtension(String extension) {
        return extensions.contains(extension);
    }
}
