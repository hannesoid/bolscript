package testsuites;

//import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import basics.BasicsTests;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("All Tests");
		//$JUnit-BEGIN$
		suite.addTest(BolTests.suite());
		suite.addTest(BasicsTests.suite());
		suite.addTest(AlgorithmTests.suite());
		//$JUnit-END$
		return suite;
	}

}
