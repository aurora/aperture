/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util.ontology;

import org.ontoware.rdf2go.model.Model;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NID3;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.semanticdesktop.aperture.vocabulary.SOURCEFORMAT;
import org.semanticdesktop.aperture.vocabulary.TAGGING;

/**
 * Provides convenience methods to get actual content of ontologies. This class
 * is deprecated please use get...Ontology methods in appropriate vocabulary 
 * classes.
 * @deprecated
 */
public class OntologyUtil {

    /**
     * Puts the source ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getSourceOntology(Model model) throws Exception {
        DATASOURCE.getDATASOURCEOntology(model);
    }

    /**
     * Puts the source ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getSourceFormatOntology(Model model) throws Exception {
        SOURCEFORMAT.getSOURCEFORMATOntology(model);
    }
    
    public static void getNIEOntology(Model model) throws Exception {
        NIE.getNIEOntology(model);
    }
    
    public static void getNFOOntology(Model model) throws Exception {
        NFO.getNFOOntology(model);
    }
    
    public static void getNCOOntology(Model model) throws Exception {
        NCO.getNCOOntology(model);
    }
    
    public static void getNMOOntology(Model model) throws Exception {
        NMO.getNMOOntology(model);
    }
    
    public static void getNCALOntology(Model model) throws Exception {
        NCAL.getNCALOntology(model);
    }
    
    public static void getNEXIFOntology(Model model) throws Exception {
        NEXIF.getNEXIFOntology(model);
    }
    
    public static void getNID3Ontology(Model model) throws Exception {
        NID3.getNID3Ontology(model);
    }
    
    /**
     * @deprecated This method is even more deprecated than the entire class
     * the TAGGING 'ontology' is not to be used at all, use NAO.
     */
    public static void getTAGGINGOntology(Model model) throws Exception {
        TAGGING.getTAGGINGOntology(model);
    }
}
