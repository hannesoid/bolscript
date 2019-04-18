package basics;

import algorithm.tools.Calc;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CalcTest {

	@Test
	public void testRoundDoubleInt() {
		assertEquals(10.0,Calc.round(10.1,0), "10.1 rounded with 0 post-comma decimals should be 10");
		assertEquals(11.0,Calc.round(10.9,0), "10.9 rounded with 0 post-comma decimals should be 11");
		assertEquals(10.0,Calc.round(10.01,0), "10.01 rounded with 0 post-comma decimals should be 10");
		
		assertEquals(10.0,Calc.round(10.01,1), "10.01 rounded with 1 post-comma decimals should be 10");
		assertEquals(10.1,Calc.round(10.09,1), "10.09 rounded with 1 post-comma decimals should be 10.1");

		assertEquals(10.01,Calc.round(10.01,2), "10.01 rounded with 2 post-comma decimals should be 10.01");
		assertEquals(10.09,Calc.round(10.09,2), "10.09 rounded with 2 post-comma decimals should be 10.09");
		
		assertEquals(10.0,Calc.round(10.0000001,4), "10.0000001 rounded with 4 post-comma decimals should be 10.0");
		assertEquals(10.0009,Calc.round(10.0009,4), "10.0009 rounded with 4 post-comma decimals should be 10.0");
		
	
	}

	@Test
	public void testEquals() {
		assertTrue(Calc.equalsTolerantly(10.00000001, 10), "10.00000001 should equal 10");
		assertTrue(Calc.equalsTolerantly(10, 10.00000001), "10 should equal 10.00000001");
		assertFalse(Calc.equalsTolerantly(10, 10.1), "10 should not equal 10.1");
		assertFalse(Calc.equalsTolerantly(10.1, 10), "10.1 should not equal 10");
	}

}
