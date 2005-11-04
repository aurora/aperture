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

    private ArrayList requiringTypes;

    public MimeTypeDescription(String mimeType, ArrayList extensions, ArrayList conditions) {
        this.mimeType = mimeType;
        this.extensions = extensions;
        this.conditions = conditions;
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
        int nrConditions = conditions.size();
        for (int i = 0; i < nrConditions; i++) {
            Condition condition = (Condition) conditions.get(i);
            if (condition.matches(bytes)) {
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
