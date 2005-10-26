/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture;

import org.semanticdesktop.aperture.rdf.TestRDFContainerSesame;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestPackage extends TestSuite {

    static public Test suite()
    {
        return new TestPackage();
    }

    /** Creates new TestPackage */
    private TestPackage() {
        super("aperture");
        addTest(new TestSuite(TestRDFContainerSesame.class));

    }

}

/*
 * $Log$
 * Revision 1.1  2005/10/26 14:57:02  leo_sauermann
 * added testcase for the RDFContainerSesame
 *
 */