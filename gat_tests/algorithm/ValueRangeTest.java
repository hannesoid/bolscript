package algorithm;

import junit.framework.TestCase;
import algorithm.composers.kaida.ImportanceRange;
import algorithm.composers.kaida.ValueRange;
import algorithm.tools.Calc;
import org.junit.jupiter.api.Test;

public class ValueRangeTest {

	private double round(double num) {
		return Calc.round(num,ValueRange.precision);
	}

	/*
	 * Test method for 'algorithm.ValueRange.ValueRange(double, double, double)'
	 */
	@Test
	public void testValueRangeDoubleDoubleDouble() {
		ValueRange vr = new ValueRange(0.0,1.0,0.1);
		assertEquals(0.0,vr.getValue(), "first value should be 0")
		
		for (double f=0.0; f < 1.0; f = round(f+0.1)) {
			vr.setValue(f);
			
			if (f>0.0){
				assertEquals("value should be" + round(f-0.1),round(f-0.1),vr.getPreviousValue());
			}
			assertEquals("value should be " + f,f,vr.getValue());
			
			if (f< 0.9) {
				assertEquals("nextvalue should be " + round(f+0.1),round(f+0.1),vr.getNextValue());
			}
		}
		
		vr.setValue(-1.0);
		assertEquals(0.0,vr.getValue(), "setting value of -1 should lead to 0.0")
		vr.setValue(2.0);
		assertEquals(1.0,vr.getValue(), "setting value of 2 should lead to 1.0")
		vr.setValue(0.5);
		assertEquals(0.5,vr.getValue(), "setting value of 0.5 should lead to 0.5")
		
		for (int i=0;i<10;i++) {
			double val = round((double)i * 0.1);
			vr.setValue(val);
			
			assertEquals("value should be " + val,val,vr.getValue());
		}
		
		
			/*
		assertEquals(0.1f,vr.getNextValue(), "next value should be .1")
		vr.setValue(0.1f);
		assertEquals(0.2f,vr.getNextValue(), "next value should be .2")
		vr.setValue(0.2f);
		assertEquals(0.3f,vr.getNextValue(), "next value should be .3")
		vr.setValue(0.8f);
		assertEquals(0.9f,vr.getNextValue(), "next value should be .9")
		vr.setValue(0.9f);
		assertEquals(1f,vr.getNextValue(), "next value should be 1")
		vr.setValue(1.0f);
		assertEquals(1f,vr.getValue(), "value should be 1")*/
		
	}

	@Test
	public void testImportanceRange() {
		ValueRange vr;
		vr = new ImportanceRange();
		vr.setValue(0.3);
		assertEquals(0.4, vr.getNextValue(), "next importance after 0.3 should be 0.4")

	}
	/*
	 * Test method for 'algorithm.ValueRange.ValueRange(double, double, int)'
	 */
	@Test
	public void testValueRangeDoubleDoubleInt() {
		ValueRange vr = new ValueRange(0.0f,0.9f,10);
	}

}
