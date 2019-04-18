package testsuites;

import midi.MidiStationTest;
import org.junit.jupiter.api.Test;

@RunWith(value=Suite.class)
@SuiteClasses(value={TestCase.class})
public class MidiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bols and midi");
		//$JUnit-BEGIN$
		suite.addTestSuite(MidiStationTest.class);
		//$JUnit-END$
		return suite;
	}
}
