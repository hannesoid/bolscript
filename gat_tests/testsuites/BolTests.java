package testsuites;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import bols.BolBaseOldTests;
import bols.BolSequenceAdvancedTest;
import bols.BolSequenceTest;
import bols.SubSequenceAdvancedTest;
import bols.SubSequenceAtomicTest;
import bols.VariationTest;

public class BolTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Bol related Tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(BolBaseOldTests.class);
		suite.addTestSuite(BolSequenceTest.class);
		suite.addTestSuite(SubSequenceAtomicTest.class);
		suite.addTestSuite(SubSequenceAdvancedTest.class);
		suite.addTestSuite(VariationTest.class);
		//suite.addTestSuite(TeentalTest.class);		
		suite.addTestSuite(BolSequenceAdvancedTest.class);
		//$JUnit-END$
		return suite;
	}

}
