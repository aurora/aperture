/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Nov 19 17:27:58 CET 2008
 * input file: swrc_v0.3.owl
 * namespace: http://swrc.ontoware.org/ontology#
 */
public class SWRC {

    /** Path to the ontology resource */
    public static final String SWRC_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/swrc_v0.3.owl";

    /**
     * Puts the SWRC ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getSWRCOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(SWRC_RESOURCE_PATH, SWRC.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + SWRC_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for SWRC */
    public static final URI NS_SWRC = new URIImpl("http://swrc.ontoware.org/ontology#");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI University = new URIImpl("http://swrc.ontoware.org/ontology#University");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Organization = new URIImpl("http://swrc.ontoware.org/ontology#Organization");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Department = new URIImpl("http://swrc.ontoware.org/ontology#Department");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Student = new URIImpl("http://swrc.ontoware.org/ontology#Student");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI AssociateProfessor = new URIImpl("http://swrc.ontoware.org/ontology#AssociateProfessor");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI FacultyMember = new URIImpl("http://swrc.ontoware.org/ontology#FacultyMember");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI SoftwareComponent = new URIImpl("http://swrc.ontoware.org/ontology#SoftwareComponent");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Product = new URIImpl("http://swrc.ontoware.org/ontology#Product");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI InBook = new URIImpl("http://swrc.ontoware.org/ontology#InBook");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Publication = new URIImpl("http://swrc.ontoware.org/ontology#Publication");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Person = new URIImpl("http://swrc.ontoware.org/ontology#Person");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Conference = new URIImpl("http://swrc.ontoware.org/ontology#Conference");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Event = new URIImpl("http://swrc.ontoware.org/ontology#Event");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI AssistantProfessor = new URIImpl("http://swrc.ontoware.org/ontology#AssistantProfessor");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI FullProfessor = new URIImpl("http://swrc.ontoware.org/ontology#FullProfessor");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Manual = new URIImpl("http://swrc.ontoware.org/ontology#Manual");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Institute = new URIImpl("http://swrc.ontoware.org/ontology#Institute");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI ProjectReport = new URIImpl("http://swrc.ontoware.org/ontology#ProjectReport");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Report = new URIImpl("http://swrc.ontoware.org/ontology#Report");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Project = new URIImpl("http://swrc.ontoware.org/ontology#Project");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Thesis = new URIImpl("http://swrc.ontoware.org/ontology#Thesis");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Employee = new URIImpl("http://swrc.ontoware.org/ontology#Employee");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI SoftwareProject = new URIImpl("http://swrc.ontoware.org/ontology#SoftwareProject");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI DevelopmentProject = new URIImpl("http://swrc.ontoware.org/ontology#DevelopmentProject");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Unpublished = new URIImpl("http://swrc.ontoware.org/ontology#Unpublished");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI InCollection = new URIImpl("http://swrc.ontoware.org/ontology#InCollection");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Lecturer = new URIImpl("http://swrc.ontoware.org/ontology#Lecturer");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI AcademicStaff = new URIImpl("http://swrc.ontoware.org/ontology#AcademicStaff");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI TechnicalReport = new URIImpl("http://swrc.ontoware.org/ontology#TechnicalReport");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI PhDThesis = new URIImpl("http://swrc.ontoware.org/ontology#PhDThesis");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Undergraduate = new URIImpl("http://swrc.ontoware.org/ontology#Undergraduate");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Article = new URIImpl("http://swrc.ontoware.org/ontology#Article");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Exhibition = new URIImpl("http://swrc.ontoware.org/ontology#Exhibition");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI ResearchTopic = new URIImpl("http://swrc.ontoware.org/ontology#ResearchTopic");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI AdministrativeStaff = new URIImpl("http://swrc.ontoware.org/ontology#AdministrativeStaff");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI ProjectMeeting = new URIImpl("http://swrc.ontoware.org/ontology#ProjectMeeting");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Meeting = new URIImpl("http://swrc.ontoware.org/ontology#Meeting");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Topic = new URIImpl("http://swrc.ontoware.org/ontology#Topic");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Lecture = new URIImpl("http://swrc.ontoware.org/ontology#Lecture");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Booklet = new URIImpl("http://swrc.ontoware.org/ontology#Booklet");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI ResearchGroup = new URIImpl("http://swrc.ontoware.org/ontology#ResearchGroup");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI PhDStudent = new URIImpl("http://swrc.ontoware.org/ontology#PhDStudent");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Graduate = new URIImpl("http://swrc.ontoware.org/ontology#Graduate");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Thing = new URIImpl("http://www.w3.org/2002/07/owl#Thing");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI ResearchProject = new URIImpl("http://swrc.ontoware.org/ontology#ResearchProject");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Manager = new URIImpl("http://swrc.ontoware.org/ontology#Manager");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Enterprise = new URIImpl("http://swrc.ontoware.org/ontology#Enterprise");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI TechnicalStaff = new URIImpl("http://swrc.ontoware.org/ontology#TechnicalStaff");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Book = new URIImpl("http://swrc.ontoware.org/ontology#Book");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Misc = new URIImpl("http://swrc.ontoware.org/ontology#Misc");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI MasterThesis = new URIImpl("http://swrc.ontoware.org/ontology#MasterThesis");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Proceedings = new URIImpl("http://swrc.ontoware.org/ontology#Proceedings");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Association = new URIImpl("http://swrc.ontoware.org/ontology#Association");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI InProceedings = new URIImpl("http://swrc.ontoware.org/ontology#InProceedings");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     */
    public static final URI Workshop = new URIImpl("http://swrc.ontoware.org/ontology#Workshop");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI keywords = new URIImpl("http://swrc.ontoware.org/ontology#keywords");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI fax = new URIImpl("http://swrc.ontoware.org/ontology#fax");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI abstract_ = new URIImpl("http://swrc.ontoware.org/ontology#abstract");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI chapter = new URIImpl("http://swrc.ontoware.org/ontology#chapter");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI number = new URIImpl("http://swrc.ontoware.org/ontology#number");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI location = new URIImpl("http://swrc.ontoware.org/ontology#location");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI series = new URIImpl("http://swrc.ontoware.org/ontology#series");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI source = new URIImpl("http://swrc.ontoware.org/ontology#source");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI volume = new URIImpl("http://swrc.ontoware.org/ontology#volume");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI email = new URIImpl("http://swrc.ontoware.org/ontology#email");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI month = new URIImpl("http://swrc.ontoware.org/ontology#month");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI pages = new URIImpl("http://swrc.ontoware.org/ontology#pages");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI phone = new URIImpl("http://swrc.ontoware.org/ontology#phone");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI photo = new URIImpl("http://swrc.ontoware.org/ontology#photo");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI price = new URIImpl("http://swrc.ontoware.org/ontology#price");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI title = new URIImpl("http://swrc.ontoware.org/ontology#title");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI howpublished = new URIImpl("http://swrc.ontoware.org/ontology#howpublished");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI booktitle = new URIImpl("http://swrc.ontoware.org/ontology#booktitle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI eventTitle = new URIImpl("http://swrc.ontoware.org/ontology#eventTitle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI edition = new URIImpl("http://swrc.ontoware.org/ontology#edition");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI date = new URIImpl("http://swrc.ontoware.org/ontology#date");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI isbn = new URIImpl("http://swrc.ontoware.org/ontology#isbn");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI name = new URIImpl("http://swrc.ontoware.org/ontology#name");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI note = new URIImpl("http://swrc.ontoware.org/ontology#note");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI type = new URIImpl("http://swrc.ontoware.org/ontology#type");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI year = new URIImpl("http://swrc.ontoware.org/ontology#year");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI homepage = new URIImpl("http://swrc.ontoware.org/ontology#homepage");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI journal = new URIImpl("http://swrc.ontoware.org/ontology#journal");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI address = new URIImpl("http://swrc.ontoware.org/ontology#address");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     */
    public static final URI hasPrice = new URIImpl("http://swrc.ontoware.org/ontology#hasPrice");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI atEvent = new URIImpl("http://swrc.ontoware.org/ontology#atEvent");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI affiliation = new URIImpl("http://swrc.ontoware.org/ontology#affiliation");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI organizerOrChairOf = new URIImpl("http://swrc.ontoware.org/ontology#organizerOrChairOf");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI financedBy = new URIImpl("http://swrc.ontoware.org/ontology#financedBy");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI supervises = new URIImpl("http://swrc.ontoware.org/ontology#supervises");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI supervisor = new URIImpl("http://swrc.ontoware.org/ontology#supervisor");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI product = new URIImpl("http://swrc.ontoware.org/ontology#product");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI RootRelation = new URIImpl("http://swrc.ontoware.org/ontology#RootRelation");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI memberOfPC = new URIImpl("http://swrc.ontoware.org/ontology#memberOfPC");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI develops = new URIImpl("http://swrc.ontoware.org/ontology#develops");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI givenBy = new URIImpl("http://swrc.ontoware.org/ontology#givenBy");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI headOfGroup = new URIImpl("http://swrc.ontoware.org/ontology#headOfGroup");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI worksAtProject = new URIImpl("http://swrc.ontoware.org/ontology#worksAtProject");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI author = new URIImpl("http://swrc.ontoware.org/ontology#author");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI editor = new URIImpl("http://swrc.ontoware.org/ontology#editor");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI publication = new URIImpl("http://swrc.ontoware.org/ontology#publication");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI headOf = new URIImpl("http://swrc.ontoware.org/ontology#headOf");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI projectInfo = new URIImpl("http://swrc.ontoware.org/ontology#projectInfo");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI member = new URIImpl("http://swrc.ontoware.org/ontology#member");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Range: http://www.w3.org/2002/07/owl#Thing  <br/>
     */
    public static final URI citedBy = new URIImpl("http://swrc.ontoware.org/ontology#citedBy");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI organization = new URIImpl("http://swrc.ontoware.org/ontology#organization");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI school = new URIImpl("http://swrc.ontoware.org/ontology#school");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI describesProject = new URIImpl("http://swrc.ontoware.org/ontology#describesProject");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI hasPartEvent = new URIImpl("http://swrc.ontoware.org/ontology#hasPartEvent");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI carriedOutBy = new URIImpl("http://swrc.ontoware.org/ontology#carriedOutBy");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI isWorkedOnBy = new URIImpl("http://swrc.ontoware.org/ontology#isWorkedOnBy");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI developedBy = new URIImpl("http://swrc.ontoware.org/ontology#developedBy");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI publisher = new URIImpl("http://swrc.ontoware.org/ontology#publisher");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI publishes = new URIImpl("http://swrc.ontoware.org/ontology#publishes");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI technicalReport = new URIImpl("http://swrc.ontoware.org/ontology#technicalReport");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI isAbout = new URIImpl("http://swrc.ontoware.org/ontology#isAbout");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI carriesOut = new URIImpl("http://swrc.ontoware.org/ontology#carriesOut");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI dealtWithIn = new URIImpl("http://swrc.ontoware.org/ontology#dealtWithIn");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI studiesAt = new URIImpl("http://swrc.ontoware.org/ontology#studiesAt");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI student = new URIImpl("http://swrc.ontoware.org/ontology#student");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI participant = new URIImpl("http://swrc.ontoware.org/ontology#participant");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI finances = new URIImpl("http://swrc.ontoware.org/ontology#finances");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI institution = new URIImpl("http://swrc.ontoware.org/ontology#institution");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI employs = new URIImpl("http://swrc.ontoware.org/ontology#employs");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI Root = new URIImpl("http://swrc.ontoware.org/ontology#Root");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI cite = new URIImpl("http://swrc.ontoware.org/ontology#cite");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI head = new URIImpl("http://swrc.ontoware.org/ontology#head");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI cooperateWith = new URIImpl("http://swrc.ontoware.org/ontology#cooperateWith");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     */
    public static final URI hasParts = new URIImpl("http://swrc.ontoware.org/ontology#hasParts");
}
