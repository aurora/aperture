package org.semanticdesktop.nepomuk.nrl.validator;

import org.ontoware.rdf2go.model.Model;
import org.semanticdesktop.nepomuk.nrl.validator.exception.ModelTesterException;

/**
 * Performs actual tests on a pre-prepared repository.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public interface ModelTester {

    /**
     * Performs tests on a repository.
     * 
     * @param unionModel a model containing the pre-prepared union of
     *        ontologies and data with appropriate inferencing settings,
     *        it needs to be opened
     * @param dataModel the model that contains the actual data, it is used
     *        to limit the scope of the test to data only. It needs to be 
     *        opened.
     * @return a validation report.
     * @throws ModelTesterException if something goes wrong...
     */
    public ValidationReport performTests(Model unionModel, Model dataModel) throws ModelTesterException ;
    
    /**
     * Performs tests on a repository, add the detected errors to the given
     * validation report. .
     * 
     * @param unionModel a model containing the pre-prepared union of
     *        ontologies and data with appropriate inferencing settings,
     *        it needs to be opened
     * @param dataModel the model that contains the actual data, it is used
     *        to limit the scope of the test to data only. It needs to be 
     *        opened.
     * @param report the ValidationReport where the results of the validation
     *        should be stored
     * @throws ModelTesterException if something goes wrong...
     */
    public void performTests(Model unionModel, Model dataModel, ValidationReport report) throws ModelTesterException ;
}
