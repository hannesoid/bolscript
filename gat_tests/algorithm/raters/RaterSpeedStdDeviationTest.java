package algorithm.raters;

import config.Themes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.Variation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaterSpeedStdDeviationTest {

	BolBase bb;
	
	@BeforeEach
	protected void setUp() throws Exception {
		bb = new BolBase();
	}
	
	/*
	 * Test method for 'algorithm.raters.RaterSpeedVariance.rate(Individual)'
	 */

	@Test
	public void testRateIndividual() throws Exception {
	
		
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);

		Rater rater = new RaterSpeedStdDeviation(bb);
		
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1 + ", f2: " + f2);
		assertEquals(f1.value, f2.value, "two similar individuals should be rated same ");
		
		
		var1 = new Variation("Dha Dha Ge Na", bb);
		var2 = new Variation("Dha - Dha - Ge - Na -", bb);
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		f1 = rater.rate(in1);
		f2 = rater.rate(in2);
		System.out.println("in1: " + in1.toString());
		System.out.println("in2: " + in2.toString());
		assertEquals(0.0f, f1.value, "in1 should have variance 0 ");
		assertEquals(0.0f, f2.value, "in2 should have variance 0 ");
		
		
		var1 = new Variation("Dha - - -, Dha - Ge -", bb);
		var2 = new Variation("Dha - - -, Dha Ge - -", bb);
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		f1 = rater.rate(in1);
		f2 = rater.rate(in2);
		System.out.println("in1: " + in1.toString());
		System.out.println("in2: " + in2.toString());
		assertTrue(f2.value > f1.value, "in2 should have greater variance ");
		
		var1 = new Variation("Dha - Dha - Dha - Ge Na Dha - Ge Na Dha - - - - - Ge Na Dha - Ge Na Ge Na Dha - Dha - Ge Na Dha - Ge Na Dha Ti Te Dha Ke Ta Ge Na Na Na Na Na", bb);
		in1 = new Individual(var1);
		f1 = rater.rate(in1);
		System.out.println("in1: " + in1.toString());
		
	}
	
	@Test
	public void testNormalisedDistance() {
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);

		Rater rater = new RaterSpeedStdDeviation(bb);
		
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		
		System.out.println("f1: " + f1 + ", f2: " + f2);
		double d = rater.normalisedDistanceToGoal(in1,f1.value, f2.value);
		assertEquals(0.0f, d, "two similar individuals should be have distance 0 ");
		
		d = rater.normalisedDistanceToGoal(in1, 0.0f, 1.0f);
		assertEquals(0.5f, d, "distance(0,1) should be .5 ");
		
		d = rater.normalisedDistanceToGoal(in1, 1.0f, 2.0f);
		assertEquals(0.5f, d, "distance(1,2) should be .5 ");
		
		d = rater.normalisedDistanceToGoal(in1, 1.0f, 2.0f);
		double d2 = rater.normalisedDistanceToGoal(in1, 2.0f, 1.0f);
		assertEquals(d, d2, "distance should be commutative ");
		
		d = rater.normalisedDistanceToGoal(in1, 1.0f, 3.0f);
		assertEquals(0.75f, d, "distance(1,3) should be .75 ");

		d = rater.normalisedDistanceToGoal(in1, 1.0f, 5.0f);
		assertEquals(1.0f, d, "distance(1,4) should be 1 ");
	}
}
