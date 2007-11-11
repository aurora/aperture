/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.semanticdesktop.aperture.vocabulary.FRESNEL;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NAO;
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
 * Utility class to infer additional statements in RDF containers. 
 */
public class InferenceUtil {
    
    /**
     * mapping from a property to a super-property
     */
    final private ListMap<URI, URI> superProperties = new ListMap<URI, URI>(); 

    /**
     * mapping from a class to a super-class
     */
    final private ListMap<URI, URI> superClasses = new ListMap<URI, URI>(); 
    
    /**
     * Create an uninitialized version of the Inference Util
     *
     */
    public InferenceUtil() {
        //
    }
    
    /**
     * Read the subclass/supproperty rules from the passed ontology and remember them.
     * No reference to the model will be kept.
     * The passed model is assumed to be open.
     * @param model the ontology to parse
     */
    public void readOntology(Model model) {
        for (ClosableIterator<? extends Statement> i = model.findStatements(Variable.ANY, RDFS.subPropertyOf, Variable.ANY);
            i.hasNext(); )
        {
            Statement s = i.next();
            learnInferenceFor(s, superProperties);
        }
        for (ClosableIterator<? extends Statement> i = model.findStatements(Variable.ANY, RDFS.subClassOf, Variable.ANY);
        i.hasNext(); )
        {
            Statement s = i.next();
            learnInferenceFor(s, superClasses);
        }
    }
    
    /**
     * Look into the passed rdfcontainer and extend existing triples with inferred triples.
     * For example, if information is written as "x - nmo:messageSubject - y",
     * this method will add "x - nie:subject - y", because messageSubject is a sub-property
     * of subject. 
     * @param container the container to extend
     */
    public void extendContent(RDFContainer container) {
        extendContent(container.getModel());
    }
    
    /**
     * Look into the passed model and extend existing triples with inferred triples.
     * For example, if information is written as "x - nmo:messageSubject - y",
     * this method will add "x - nie:subject - y", because messageSubject is a sub-property
     * of subject. 
     * @param model the model to extend, data will be read and written to it
     */
    public void extendContent(Model model) {
        
        // the statements to add afterwards
        LinkedList<Statement> toadd = new LinkedList<Statement>();
        // iterate through the container
        for (Statement s : model) {
            // sub-property inference
            URI p = s.getPredicate();
            List<URI> sprops = superProperties.get(p);
            if (sprops != null)
            {
                for (URI sprop : sprops) {
                    // This line has been commented out by Antoni Mylka
                    // the statement impl class introduces a dependency between
                    // aperture core bundle and rdf2go model.impl.base bundle
                    // this is to be avoided
                    //toadd.add(new StatementImpl(s.getContext(), s.getSubject(), sprop, s.getObject()));
                    // the line below is equivalent to the line above because
                    // model.iterator() used implicitly in the foreach loop
                    // returns only statements from the context of that model
                    // whereas the model.createStatement automatically adds the
                    // appropriate context, so the context is preserved even though
                    // it doesn't appear here
                    toadd.add(model.createStatement(s.getSubject(), sprop, s.getObject()));
                }   
            }
            // type inference
            if ((RDF.type.equals(s.getPredicate()))&&(s.getObject() instanceof URI)) {
                List<URI> sclasses = superClasses.get((URI)s.getObject());
                if (sclasses != null)
                {
                    for (URI sclass : sclasses) {
                        // This line has been commented out by Antoni Mylka
                        // the statement impl class introduces a dependency between
                        // aperture core bundle and rdf2go model.impl.base bundle
                        // this is to be avoided
                        // toadd.add(new StatementImpl(s.getContext(), s.getSubject(), RDF.type, sclass));
                        // the line below is equivalent to the line above because
                        // model.iterator() used implicitly in the foreach loop
                        // returns only statements from the context of that model
                        // whereas the model.createStatement automatically adds the
                        // appropriate context, so the context is preserved even though
                        // it doesn't appear here
                        toadd.add(model.createStatement(s.getSubject(), RDF.type, sclass));
                    }   
                }
            }
        }
        // add inferred triples
        model.addAll(toadd.iterator());
    }

    /**
     * Add inference for the passed statement.
     * The predicate of the sta
     * @param s
     */
    private void learnInferenceFor(Statement s, ListMap<URI, URI> list) {
        if (!(s.getSubject() instanceof URI))
            return;
        if (!(s.getObject() instanceof URI))
            return;
        URI sub = (URI)s.getSubject();
        URI obj = (URI)s.getObject();
        list.put(sub, obj);
        
        //add all super properies
        {
            List<URI> sp = list.get(obj);
            if (sp!= null)
            {
                for (URI u : sp)
                {
                    list.put(sub, u);
                }
            }
        }
        // inverse: see if the subject is already object of others
        for (Entry<URI, URI> entry : list.entrySet()) {
            if (entry.getValue().equals(sub)) {
                //add all super properies
                List<URI> sp = list.get(sub);
                if (sp!= null)
                {
                    for (URI u : sp)
                    {
                        list.put(entry.getKey(), u);
                    }
                }
            }
        }
        
    }

    /**
     * Create a new inference utility, loading the core ontologies 
     * (the ones from the vocabulary package)
     * @return a newly created and loaded inference utility, that knows
     * how to inference based on the subclass/subproperty relations defined
     * in the vocabulary package
     */
    public static InferenceUtil createForCoreOntologies() {
        Model m = RDF2Go.getModelFactory().createModel();
        m.open();
        DATASOURCE.getDATASOURCEOntology(m);
        FRESNEL.getFRESNELOntology(m);
        GEO.getGEOOntology(m);
        NAO.getNAOOntology(m);
        NCAL.getNCALOntology(m);
        NCO.getNCOOntology(m);
        NEXIF.getNEXIFOntology(m);
        NFO.getNFOOntology(m);
        NID3.getNID3Ontology(m);
        NIE.getNIEOntology(m);
        NMO.getNMOOntology(m);
        SOURCEFORMAT.getSOURCEFORMATOntology(m);
        TAGGING.getTAGGINGOntology(m);
        
        InferenceUtil util = new InferenceUtil();
        util.readOntology(m);
        m.close();
        return util;
    }
}

