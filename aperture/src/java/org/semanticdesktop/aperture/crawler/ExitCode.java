/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

/**
 * Class that defines ExitCodes to be used in the CrawlerHandler interface.
 */
public class ExitCode {

    /**
     * Indicates that the process completed naturally.
     */
    public static final ExitCode COMPLETED = new ExitCode("completed");
    
    /**
     * Indicates that the process was interrupted by a request to stop.
     */
    public static final ExitCode STOP_REQUESTED = new ExitCode("stop requested");
    
    /**
     * Indicates that the process was aborted by a fatal error.
     */
    public static final ExitCode FATAL_ERROR = new ExitCode("fatal error");
    
    private String name;
    
    private ExitCode(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean equals(Object object) {
        return object instanceof ExitCode && name.equals(((ExitCode) object).name);
    }
    
    public int hashCode() {
        return name.hashCode();
    }
}
