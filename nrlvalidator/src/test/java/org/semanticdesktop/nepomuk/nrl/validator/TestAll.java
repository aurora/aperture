package org.semanticdesktop.nepomuk.nrl.validator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value={
  StandaloneValidatorTests.class, 
	BasicTests.class, 
	PropertyDomainsAndRangesTests.class,
  PropertyCardinalityTests.class
})
public class TestAll {
    //empty
}
