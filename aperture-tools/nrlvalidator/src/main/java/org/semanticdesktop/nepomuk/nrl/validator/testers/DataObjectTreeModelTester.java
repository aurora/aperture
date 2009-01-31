package org.semanticdesktop.nepomuk.nrl.validator.testers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage.MessageType;
import org.semanticdesktop.nepomuk.nrl.validator.exception.ModelTesterException;

/**
 * This tester tests if the containment relations between DataObjects form a 
 * valid tree. That is: the root of that tree is to be marked with the
 * rootElementOf triple.  All elements below are to be accessible with the
 * hasPart triples. We make the assumption that the unionModel will have
 * appropriate inference settings so that the both the hasPart and isPartOf
 * triples will be present
 * 
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class DataObjectTreeModelTester implements ModelTester {

    
    private static final String NIE_NS = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#"; 
    private static final URI NIE_DATA_OBJECT = new URIImpl(NIE_NS + "DataObject");    
    private static final URI NIE_INFORMATION_ELEMENT = new URIImpl(NIE_NS + "InformationElement");
    private static final URI NIE_ROOT_ELEMENT_OF = new URIImpl(NIE_NS + "rootElementOf");
    private static final URI NIE_IS_PART_OF = new URIImpl(NIE_NS + "isPartOf");
    private static final URI NIE_HAS_PART = new URIImpl(NIE_NS + "hasPart");
    private static final URI NIE_IS_STORED_IN = new URIImpl(NIE_NS + "isStoredIn");
    
    
    /**
     * @see ModelTester#performTests(Model, Model)
     */
    public ValidationReport performTests(Model unionModel, Model dataModel) throws ModelTesterException {
        ValidationReport report = new ValidationReport();
        performTests(unionModel, dataModel, report);
        return report;
    }
    
    
    /**
     * @see ModelTester#performTests(Model, Model, ValidationReport)
     */
    public void performTests(Model unionModel, Model dataModel, ValidationReport report)
        throws ModelTesterException {
        
        /*
         * The first basic error occurs if there are no root elements in the 
         * given model. We don't want to to look for them, so report an error 
         * and return.
         */
        List<Resource> rootElementList = getRootElements(unionModel);
        if (rootElementList.size() == 0) {
            reportNoRootElements(report);
            return;
        }
        
        /*
         * Now the real validation happens, we get sets of all data objects and
         * information elements. We'll remove the elements from those sets to
         * prevent the same element from occuring twice within the same source
         * tree. 
         */
        Set<Resource> dataObjectSet = 
                getIndividuals(unionModel,NIE_DATA_OBJECT);
        Set<Resource> informationElementSet = 
                getIndividuals(unionModel, NIE_INFORMATION_ELEMENT);
        
        /*
         * We allow for multiple root elements.
         */
        for (Resource rootElement : rootElementList) {
            processSingleRootElement(
                rootElement,
                unionModel,
                dataObjectSet,
                informationElementSet,
                report);
        }
        
        /*
         * All resources that haven't been removed from those sets are considered
         * inaccessible and reported as an error.
         */
        reportInaccessibleDataObjects(unionModel, report, dataObjectSet);
    }
        
    private void processSingleRootElement(
        Resource rootElement,
        Model unionModel,
        Set<Resource> dataObjectSet,
        Set<Resource> informationElementSet,
        ValidationReport report) {
        
        if (!resourceHasType(unionModel,rootElement,NIE_INFORMATION_ELEMENT)) {
            // this is actually supposed to be checked by the normal NRLClosedWorld
            // model tester, but a bit of sanity wouldn't hurt anyone I guess...
            reportRootElementIsNotAnElement(rootElement, unionModel, report);
            return;
        }
        
        /*
         * The root element is allowed to have parents, but the parents of a 
         * root element cannot have any. This is to cover a situation where the
         * root element contains references to elements that have not been
         * crawled. This information may be useful and should not be 
         * discarded.
         */
        List<Resource> parentsOfRootElement = 
        	getParentsOfRootElement(rootElement,unionModel);
        boolean rootElementOk = true;
        for (Resource parentOfRootElement : parentsOfRootElement) {
	        if (parentOfRootElementIsAPartOfAnotherObject(
	        		parentOfRootElement,
	        		unionModel, 
	        		report,
	        		informationElementSet,
	        		dataObjectSet)) {
	        	rootElementOk = false;
	        } else {
	        	// this is a normal situation, nothing to be done
	        }
        }
        if (rootElementOk == false) {
            // we skip those 'rootElements' that are NOT root elements'
            return;
        }

        
        // now we can run the recursive method that crawls the containment tree
        crawlInformationElement(
            rootElement,
            unionModel,
            dataObjectSet,
            informationElementSet,
            report);
    }

    private List<Resource> getParentsOfRootElement(Resource rootElement,
			Model model) {
    	 ClosableIterator<? extends Statement> iterator = null;
    	 Resource dataObjectResource = getDataObjectForAnInformationElement(rootElement, model);
    	 List<Resource> result = new LinkedList<Resource>();
    	 if (dataObjectResource != null) {
	         try {
	             iterator = model.findStatements(dataObjectResource, NIE_IS_PART_OF, Variable.ANY);
	             while (iterator.hasNext()) {
	                 Statement statement = iterator.next();
	                 Node node = statement.getObject();
	                 if (node instanceof Resource) {
	                	 result.add(statement.getObject().asResource());
	                 }
	             }
	         } finally {
	             closeIterator(iterator);
	         }
    	 }
    	 return result;
	}


	private void crawlInformationElement(
        Resource element,
        Model model,
        Set<Resource> dataObjectSet,
        Set<Resource> informationElementSet,
        ValidationReport report) {
        
        if (!informationElementSet.remove(element)) {
            reportContainmentTreeNodeOccuringTwice(element,model,NIE_INFORMATION_ELEMENT,report);
            return;
        }
        
        Resource dataObject = getDataObjectForAnInformationElement(element, model);
        if (dataObject != null) {
            if (!dataObjectSet.remove(dataObject)) {
                reportContainmentTreeNodeOccuringTwice(dataObject,model,NIE_DATA_OBJECT,report);
                return;
            }
        }
        
        /*
         * This is supposed to work because the inference engine embedded in the
         * model will recognize that hasPart and isPartOf are inverse and we
         * can use either one since both will be present. 
         */
        List<Resource> childDataObjects = 
            getAllPropertyValues(model,element,NIE_HAS_PART);
        List<Resource> childInformationElements = new LinkedList<Resource>();
        
        for (Resource childDataObject : childDataObjects) {
            Resource childInformationElement = 
                getInformationElementForADataObject(childDataObject, model);
            if (childInformationElement != null) {
                /*
                 * If a child data object has a corresponding information element
                 * chances are that there are some other resources embedded within
                 * so the subtree must be crawled 
                 */
                childInformationElements.add(childInformationElement);
            } else {
                /*
                 * Else, there is no need to crawl that subtree and we can remove
                 * the data object from the data object set.
                 */
                dataObjectSet.remove(childDataObject);
            }
        }
        
        /*
         * Behold the power of recursion... 
         */
        for (Resource childInformationElement : childInformationElements) {
            crawlInformationElement(childInformationElement,model,dataObjectSet,informationElementSet,report);
        }
    }



    private boolean resourceHasType(
        Model model,
        Resource subject,
        URI type) {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, RDF.type, type);
            if (iterator.hasNext()) {
                return true;
            } else {
                return false;
            }
        } finally {
            closeIterator(iterator);
        }
    }

    private Set<Resource> getIndividuals(Model model, URI type) {
        Set<Resource> result = new HashSet<Resource>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, RDF.type, type);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                result.add(statement.getSubject());
            }
        } finally {
            closeIterator(iterator);
        }
        return result;
    }
    
    private List<Resource> getRootElements(Model model) {
        List<Resource> result = new LinkedList<Resource>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, NIE_ROOT_ELEMENT_OF, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                result.add(statement.getSubject());
            }
        } finally {
            closeIterator(iterator);
        }
        return result;
    }
    
    private boolean parentOfRootElementIsAPartOfAnotherObject(
        Resource parentOfRootElement,
        Model model,
        ValidationReport report,
        Set<Resource> dataObjectSet,
        Set<Resource> informationElementSet) {
        
        boolean result = false;
       
        Resource dataObjectResource 
            = getDataObjectForAnInformationElement(parentOfRootElement,model);
        if (dataObjectResource == null) {
            // This means that the parent of a root element is not "grounded" in any
            // data object, this may occur, we don't force anyone to do it
        	informationElementSet.remove(parentOfRootElement);
            return false;
        } else {
        	informationElementSet.remove(parentOfRootElement);
        	dataObjectSet.remove(dataObjectResource);
        }
        
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(dataObjectResource, NIE_IS_PART_OF, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                report.addMessage(MessageType.ERROR,
                    "Root element error",
                    "An element that contains the root element is itself contained somewhere",
                    statement);
                result = true;
            }
        } finally {
            closeIterator(iterator);
        }
        
        return result;
    }
    
    private Resource getDataObjectForAnInformationElement(
        Resource element,
        Model model) {

        if (resourceHasType(model, element, NIE_DATA_OBJECT)) {
            // the trivial case, if the data object and the information element 
            // share the same URI we simply return it
            return element;
        } else {
            // the non-so-trivial case involving the usage of he isStoredIn
            // property
            Node dataObjectNode = 
                RDFTool.getSingleValue(model, element, NIE_IS_STORED_IN);
            if (dataObjectNode != null && dataObjectNode instanceof Resource) {
                return dataObjectNode.asResource();
            } else  {
                return null;
            }
        }
    }
    
    private Resource getInformationElementForADataObject(
        Resource dataObject,
        Model model) {
        if (resourceHasType(model, dataObject, NIE_INFORMATION_ELEMENT)) {
            // the trivial case, if the data object and the information element 
            // share the same URI we simply return it
            return dataObject;
        } else {
            // the non-so-trivial case involving the usage of the isStoredIn
            // property 
            return getAnySubjectOfProperty(model,NIE_IS_STORED_IN,dataObject);
        }
    }

    private Resource getAnySubjectOfProperty(
        Model model,
        URI predicate,
        Resource object) {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, predicate, object);
            Resource result = null;
            if (iterator.hasNext()) {
                result = iterator.next().getSubject();
            }
            return result;
        } finally {
            closeIterator(iterator);
        }
    }
    
    private List<Resource> getAllPropertyValues(
        Model model,
        Resource subject,
        URI predicate) {
        List<Resource> result = new LinkedList<Resource>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                result.add(statement.getObject().asResource());
            }
        } finally {
            closeIterator(iterator);
        }
        return result;
    }
    
    private void closeIterator(ClosableIterator<? extends Object> iterator) {
        if (iterator != null) {
            try {
                iterator.close();
            } catch (Exception e) {
                // we can hardly do anything at the moment
            }
        }
    }

    private void reportRootElementIsNotAnElement(
        Resource rootElement,
        Model unionModel,
        ValidationReport report) {
        report.addMessage(MessageType.ERROR,
            "Root element error",
            "The root element is not an InformationElement",
            unionModel.createStatement(
                rootElement, 
                NIE_ROOT_ELEMENT_OF, 
                RDFTool.getSingleValue(unionModel,rootElement,NIE_ROOT_ELEMENT_OF)));
    }
    
    private void reportInaccessibleDataObjects(
        Model unionModel,
        ValidationReport report,
        Set<Resource> dataObjectSet) {
        for (Resource dataObject : dataObjectSet) {
            report.addMessage(
                MessageType.ERROR,
                "Inaccessible data object", 
                "Detected a DataObject instance that is not part of a containment tree",
                unionModel.createStatement(dataObject, RDF.type, NIE_DATA_OBJECT));
        }
    }
    
    private void reportNoRootElements(ValidationReport report) {
        report.addMessage(
            MessageType.ERROR, 
            "No root elements", 
            "The model doesn't contain any root elements", 
            (Statement [])null);
    }
    
    private void reportContainmentTreeNodeOccuringTwice(
        Resource element,
        Model model,
        URI type,
        ValidationReport report) {
        report.addMessage(
            MessageType.ERROR,
            "Containment tree error",
            "A node in the containment tree occurs more than once",
            model.createStatement(element, RDF.type, type));
    }
}
