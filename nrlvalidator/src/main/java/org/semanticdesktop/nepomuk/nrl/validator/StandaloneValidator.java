package org.semanticdesktop.nepomuk.nrl.validator;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.ontoware.rdf2go.model.Model;
import org.semanticdesktop.nepomuk.nrl.validator.exception.StandaloneValidatorException;

/**
 * A standalone validator. It is itself reponsible for the maintenance of the
 * ontology repository.
 * 
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public interface StandaloneValidator {

    /**
     * Validates the given input data against the ontologies stored in the
     * repository.
     * <p>
     * Validation entails performing a set of tests. They are determined by the
     * chosen ModelTester implementation.
     * </p>
     * 
     * @param dataGraphContent the content of the data graph.
     * @param syntax The syntax used for the data graph content string.
     * @return a report of the Validation
     * @throws StandaloneValidatorException if the content of the data graph
     *         couldn't be read
     */
    public ValidationReport validate(String dataGraphContent, String syntax)
        throws StandaloneValidatorException;

    /**
     * Validates the given input data against the ontologies stored in the
     * repository.
     * <p>
     * Validation entails performing a set of tests. They are determined by the
     * chosen ModelTester implementation.
     * </p>
     * 
     * @param stream the content of the data graph.
     * @param syntax The syntax used for the data graph content string.
     * @return a report of the Validation
     * @throws StandaloneValidatorException if the content of the data graph
     *         couldn't be read
     */
    public ValidationReport validate(InputStream stream, String syntax)
        throws StandaloneValidatorException;

    /**
     * Validates the given input data against the ontologies stored in the
     * repository.
     * <p>
     * Validation entails performing a set of tests. They are determined by the
     * chosen ModelTester implementation.
     * </p>
     * 
     * @param reader the content of the data graph.
     * @param syntax The syntax used for the data graph content string.
     * @return a report of the Validation
     * @throws StandaloneValidatorException if the content of the data graph
     *         couldn't be read
     */
    public ValidationReport validate(Reader reader, String syntax)
        throws StandaloneValidatorException;

    /**
     * Validates the given input data against the ontologies stored in the
     * repository.
     * <p>
     * Validation entails performing a set of tests. They are determined by the
     * chosen ModelTester implementation.
     * </p>
     * 
     * @param file the content of the data graph.
     * @param syntax The syntax used for the data graph content string.
     * @return a report of the Validation
     * @throws StandaloneValidatorException if the content of the data graph
     *         couldn't be read
     */
    public ValidationReport validate(File file, String syntax)
        throws StandaloneValidatorException;

    /**
     * Validates the given input data against ontologies stored in the
     * repository.
     * 
     * @param model the model containing the data to be validated. It should be
     *        opened before calling this method and it will NOT be closed within
     *        this method so the user should take care of this by him/herself.
     * @return a report of the validation
     * @throws StandaloneValidatorException if the data in the given model
     *         couldn't be validated for some reason.
     */
    public ValidationReport validate(Model model) throws StandaloneValidatorException;
    
    /**
     * Returns the repository tester.
     * 
     * @return the repository tester.
     */
    public ModelTester[] getModelTesters();

    /**
     * Sets the repository testers.
     * 
     * @param modelTesters the model testers
     */
    public void setModelTesters(ModelTester... modelTesters );

    /**
     * Adds an ontology to the repository.
     * 
     * @param reader The content of the ontology.
     * @param ontologySyntax The syntax in which the content of the Ontology is
     *        expressed.
     * @param ontologyURI The URI of the named graph, where the ontology should
     *        reside. This will usually be the namespace of the ontology. It
     *        will also serve as an identifier to remove this ontology later on.
     * @throws StandaloneValidatorException if the the ontology couldn't be
     *         added for some reason.
     */
    public void addOntology(
        Reader reader,
        String ontologySyntax,
        String ontologyURI) throws StandaloneValidatorException;

    /**
     * Adds an ontology to the repository.
     * 
     * @param stream The content of the ontology.
     * @param ontologySyntax The syntax in which the content of the Ontology is
     *        expressed.
     * @param ontologyURI The URI of the named graph, where the ontology should
     *        reside. This will usually be the namespace of the ontology. It
     *        will also serve as an identifier to remove this ontology later on.
     * @throws StandaloneValidatorException if the the ontology couldn't be
     *         added for some reason.
     */
    public void addOntology(
        InputStream stream,
        String ontologySyntax,
        String ontologyURI) throws StandaloneValidatorException;

    /**
     * Adds an ontology to the repository.
     * 
     * @param model the model containing the ontology. It should be opened before
     *        calling this method and it will NOT be closed within this method
     *        so the user should take care of this by him/herself.
     * @param ontologyURI the uri of the ontology, note that the
     *        model.getContextURI method is NOT used.
     * @throws StandaloneValidatorException if something goes wrong.       
     */
    public void addOntology(Model model, String ontologyURI) throws StandaloneValidatorException;
    
    /**
     * Lists the URIs of the ontologies in the ontology repository.
     * 
     * @return a list of the URIs of ontologies in the ontology repository.
     */
    public List<String> listOntologyUris();

    /**
     * Removes an ontology from the repository.
     * 
     * @param uri the uri of the ontology to be removed.
     * @throws StandaloneValidatorException if something goes wrong.
     */
    public void removeOntology(String uri) throws StandaloneValidatorException;
}
