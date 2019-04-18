package algorithm;

import config.Themes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import algorithm.raters.Rater;
import algorithm.raters.RaterAverageSpeed;
import bols.BolBase;

public class IndividualTest {
	BolBase bb;
	
	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testIndividual() throws Exception {
		 
		Individual in1 = new Individual(Themes.getTheme01(bb));
		assertEquals(0, in1.getFeatures().size(), "0 features should be existent.");
		assertFalse(in1.isRated(), "sould not be rated yet");
		Rater rater1 = new RaterAverageSpeed(bb);
		rater1.rate(in1);
		assertEquals(1, in1.getFeatures().size(), "Only one feature should be assigned.");
		assertTrue(in1.isRated(), "should be rated now");
		
		
	}
	
	@Test
	public void testGetFeature() {
		Individual in1 = new Individual(Themes.getTheme01(bb));
		assertEquals(0, in1.getFeatures().size(), "0 features should be existent.");
		assertFalse(in1.isRated(), "sould not be rated yet");
		Rater rater1 = new RaterAverageSpeed(bb);
		Feature f1 = rater1.rate(in1);
		System.out.println("f1:"+ f1 + ", in1.getF1:" + in1.getFeature(rater1));
		assertEquals(f1, in1.getFeature(rater1), "should be same");
		assertEquals(f1, in1.getFeature(rater1.getClass()), "should be same");
		
	}
	
	@Test
	public void testIndividualGetCopy() throws Exception {
		Individual in1 = new Individual(Themes.getTheme01(bb));
		Rater rater1 = new RaterAverageSpeed(bb);
		rater1.rate(in1);
		Individual in2 = in1.getCopyKeepBolSequence();
		assertNotSame(in1.getFeatures(), in2.getFeatures(), "they should have a differing features ArrayList ");
		assertNotSame(in1.getFeatures().get(0), in2.getFeatures().get(0), "they should have a differing features ");
		assertEquals(in1.getFeatures().get(0).value, in2.getFeatures().get(0).value, "but the features should have same value  ");
		
	}	
	
}
