package algorithm.raters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RaterTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for algorithm.raters");
		//$JUnit-BEGIN$
		suite.addTestSuite(RaterSpeedStdDeviationTest.class);
		suite.addTestSuite(RaterSimilarEndTest.class);
		suite.addTestSuite(RaterAverageSpeedTest.class);
		suite.addTestSuite(FitnessRaterEuklidTest.class);
		suite.addTestSuite(RaterInnerRepetitivenessTest.class);
		suite.addTestSuite(RaterDifferentFromBeforeTest.class);
		//$JUnit-END$
		return suite;
	}

}
