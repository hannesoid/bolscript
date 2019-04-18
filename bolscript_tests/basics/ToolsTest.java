package basics;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class ToolsTest {

	@Test
	public void testFormatName() {
		String s = "--TA tA, taTa-Dha";
		assertEquals(Tools.formatName(s), "--Ta Ta, Tata-Dha");
	}
	
}
