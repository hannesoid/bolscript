package basics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for basics");
		//$JUnit-BEGIN$
		suite.addTestSuite(CalcTest.class);
		//$JUnit-END$
		return suite;
	}

}
