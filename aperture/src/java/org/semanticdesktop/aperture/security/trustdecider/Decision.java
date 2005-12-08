/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustdecider;

/**
 * Instances of this class are used to model a decision taken by a TrustDecider.
 * 
 * @see org.semanticdesktop.aperture.security.trustdecider.TrustDecider
 */
public class Decision {

    public static final Decision TRUST_THIS_SESSION = new Decision("trust for this session");

    public static final Decision TRUST_ALWAYS = new Decision("trust always");

    public static final Decision DISTRUST = new Decision("never trust");

    private String name;

    private Decision(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof Decision) {
            Decision other = (Decision) object;
            return name.equals(other.getName());
        }
        else {
            return false;
        }
    }
}
