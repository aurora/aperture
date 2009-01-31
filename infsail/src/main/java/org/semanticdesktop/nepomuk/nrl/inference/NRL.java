/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.semanticdesktop.nepomuk.nrl.inference;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;


/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Feb 20 10:54:14 CET 2007
 * input file: /home/grimnes/projects/nepomuk/nrlWorkspace/NRLInf/schema/nrl.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#
 */
public interface NRL {
	
	
	public static final URI NS_NRL_GEN = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#");

    /**
     * Label: AsymmetricProperty 
     */
    public static final URI AsymmetricProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#AsymmetricProperty");

    /**
     * Label: Configuration 
     */
    public static final URI Configuration = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Configuration");

    /**
     * Label: Data 
     */
    public static final URI Data = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Data");

    /**
     * Label: DocumentGraph 
     */
    public static final URI DocumentGraph = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#DocumentGraph");

    /**
     * Label: ExternalViewSpecification 
     */
    public static final URI ExternalViewSpecification = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ExternalViewSpecification");

    /**
     * Label: FunctionalProperty 
     */
    public static final URI FunctionalProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#FunctionalProperty");

    /**
     * Label: Graph 
     */
    public static final URI Graph = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph");

    /**
     * Label: GraphView 
     */
    public static final URI GraphView = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#GraphView");

    /**
     * Label: InstanceBase 
     */
    public static final URI InstanceBase = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#InstanceBase");

    public static final URI GraphMetadata = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#GraphMetadata");
    
    /**
     * Label: InverseFunctionalProperty 
     */
    public static final URI InverseFunctionalProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#InverseFunctionalProperty");

    /**
     * Label: KnowledgeBase 
     */
    public static final URI KnowledgeBase = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#KnowledgeBase");

    /**
     * Label: Ontology 
     */
    public static final URI Ontology = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Ontology");

    /**
     * Label: ReflexiveProperty 
     */
    public static final URI ReflexiveProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ReflexiveProperty");

    /**
     * Label: RuleViewSpecification 
     */
    public static final URI RuleViewSpecification = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#RuleViewSpecification");

    /**
     * Label: Schema 
     */
    public static final URI Schema = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Schema");

    /**
     * Label: Semantics 
     */
    public static final URI Semantics = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Semantics");

    /**
     * Label: SymmetricProperty 
     */
    public static final URI SymmetricProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#SymmetricProperty");

    /**
     * Label: TransitiveProperty 
     */
    public static final URI TransitiveProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#TransitiveProperty");

    /**
     * Label: ViewSpecification 
     */
    public static final URI ViewSpecification = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ViewSpecification");

    /**
     * Label: cardinality 
     * Comment: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI cardinality = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#cardinality");

    /**
     * Label: equivalentGraph 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     */
    public static final URI equivalentGraph = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#equivalentGraph");

    /**
     * Label: externalRealizer 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ExternalViewSpecification 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI externalRealizer = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#externalRealizer");

    /**
     * Label: hasOntology 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Ontology 
     */
    public static final URI hasOntology = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#hasOntology");

    /**
     * Label: hasSchema 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#InstanceBase 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Schema 
     */
    public static final URI hasSchema = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#hasSchema");

    /**
     * Label: hasSemantics 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Data 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Semantics 
     */
    public static final URI hasSemantics = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#hasSemantics");

    /**
     * Label: hasSpecification 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#GraphView 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ViewSpecification 
     */
    public static final URI hasSpecification = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#hasSpecification");

    /**
     * Label: imports 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Data 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Data 
     */
    public static final URI imports = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#imports");

    /**
     * Label: inverseProperty 
     * Comment: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property 
     * Range: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property 
     */
    public static final URI inverseProperty = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#inverseProperty");

    /**
     * Label: maxCardinality 
     * Comment: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI maxCardinality = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#maxCardinality");

    /**
     * Label: minCardinality 
     * Comment: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI minCardinality = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#minCardinality");

    /**
     * Label: realizes 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ViewSpecification 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Semantics 
     */
    public static final URI realizes = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#realizes");

    /**
     * Label: rule 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#RuleViewSpecification 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI rule = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#rule");

    /**
     * Label: ruleLanguage 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#RuleViewSpecification 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI ruleLanguage = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#ruleLanguage");

    /**
     * Label: semanticLabel 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Semantics 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI semanticLabel = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#semanticLabel");

    /**
     * Label: semanticsDefinedBy 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Semantics 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI semanticsDefinedBy = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#semanticsDefinedBy");

    /**
     * Label: subGraphOf 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     */
    public static final URI subGraphOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#subGraphOf");

    /**
     * Label: superGraphOf 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     */
    public static final URI superGraphOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#superGraphOf");

    /**
     * Label: viewOn 
     * Comment: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#GraphView 
     * Range: http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#Graph 
     */
    public static final URI viewOn = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#viewOn");

    public static final URI metadataOn = new URIImpl("http://www.semanticdesktop.org/ontologies/2006/11/24/nrl#metadataOn");
}
