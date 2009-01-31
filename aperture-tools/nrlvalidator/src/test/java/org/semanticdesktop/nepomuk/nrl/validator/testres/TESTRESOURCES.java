package org.semanticdesktop.nepomuk.nrl.validator.testres;

import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.semanticdesktop.nepomuk.nrl.validator.StandaloneValidatorTests;

public class TESTRESOURCES {
    public static final String RES_PACKAGE_NAME = TESTRESOURCES.class
        .getPackage()
        .getName()
        .replace('.', '/');

    public static final String OWL_RDF_FILE = RES_PACKAGE_NAME + "/owl.rdf";

    public static final String OWL_GRAPH_URI = OWL.NAMESPACE
        .toString()
        .substring(0, OWL.NAMESPACE.toString().length() - 1);

    public static final String RDF_RDF_FILE = RES_PACKAGE_NAME + "/rdf.rdf";

    public static final String RDF_GRAPH_URI = RDF.NAMESPACE
        .toString()
        .substring(0, RDF.NAMESPACE.toString().length() - 1);

    public static final String RDFS_RDF_FILE = RES_PACKAGE_NAME + "/rdfs.rdf";

}
