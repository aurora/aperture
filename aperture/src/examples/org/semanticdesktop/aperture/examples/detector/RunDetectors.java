/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.detector;

import java.util.List;
import java.util.Set;

import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.detector.DataSourceDescription;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.detector.impl.DefaultDataSourceDetectorRegistry;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * Run the default detectors and print results on command line
 * @author sauermann
 */
public class RunDetectors {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DefaultDataSourceDetectorRegistry reg = new DefaultDataSourceDetectorRegistry();
        Set<DataSourceDetector> all = reg.getAll();
        for (DataSourceDetector detector : all) {
            detectDataSources(detector);
        }

    }

    private static void detectDataSources(DataSourceDetector detector) {
        List<DataSourceDescription> detected;
        try {
            detected = detector.detect();
            if (detected.size() == 0)
            {
                System.out.println(detector.getSupportedType()+": \n   No datasources detected");
            } else {
                System.out.println(detector.getSupportedType()+":");
                for (DataSourceDescription d : detected) {
                    DataSource ds = d.getDataSource();
                    System.out.println("   "+ ds.getID()+":");
                    System.out.println("    name: "+ ds.getName());
                    System.out.println("    comment: "+ ds.getConfiguration().getString(DATASOURCE.dataSourceComment));
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error detecing for datasource "+detector.getSupportedType());
            e.printStackTrace();
            return;
        }
    }

}

