package algorithm.raters;

import java.util.ArrayList;

import config.Themes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.BolSequence;
import bols.PlayingStyle;
import bols.SubSequenceAdvanced;
import bols.Variation;

public class RaterInnerRepetitivenessTest {
	BolBase bb;

	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();		
	}
	
	@Test
	public void testRaterInnerRepetitivenessNew() throws Exception {
		RaterInnerRepetitiveness rater = new RaterInnerRepetitiveness(bb);		
	}
	
	@Test
	public void testRaterInnerRepetitivenessRate() {
		Variation var1 = Themes.getTheme02(bb);
		Variation var2 = Themes.getTheme02(bb);
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RaterInnerRepetitiveness rater = new RaterInnerRepetitiveness(bb);	
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		//System.out.println("f1: " + f1 + "f2: " + f2);
		assertTrue(f1.equals(f2), "two similar individuals should get the same features: ");
		
		var1 = new Variation("Dha Dhin Dhin Dha, Na Na Na Na, Dha Ge Dhin Dha, Tun Na Ge Na", bb);
		in1 = new Individual(var1);
		assertEquals(0f, rater.rate(in1).value, "should be rated 0");
	
		var1 = new Variation("Dha Dhin Dhin Dha, Dha Dhin Dhin Dha, Dha Ge Dhin Dha, Tun Na Ge Na", bb);
		in1 = new Individual(var1);
		assertEquals(1f, rater.rate(in1).value, "should be rated 1");
		
		var1 = new Variation("Dha Dhin Dhin Dha, Dha Dhin Dhin Dha, Dha Ge Dhin Dha, Dha Ge Dhin Dha", bb);
		in1 = new Individual(var1);
		assertEquals(2f, rater.rate(in1).value, "should be rated 2");
		
		var1 = new Variation("Dha Dhin Dhin Dha, Dha Dhin Dhin Dha, Dha Dhin Dhin Dha, Dha Dhin Dhin Dha", bb);
		in1 = new Individual(var1);
		assertEquals(3f, rater.rate(in1).value, "should be rated 3");
		
		BolSequence seq1 = new BolSequence("Dha Dhin Dhin Dha Ge Ge Ge Ge", bb);
		var1 = new Variation(seq1);
		var2 = new Variation(seq1);
		
		var1.addSubSequence(0,4, 2f);
		var1.addSubSequence(0,4, 2f);
		var1.addSubSequence(4,4, 2f);
		var1.addSubSequence(4,4, 2f);
		var1.addSubSequence(0,4, 2f);
		var1.addSubSequence(0,4, 2f);
		var1.addSubSequence(4,4, 2f);
		var1.addSubSequence(4,4, 2f);		
		in1 = new Individual(var1);
		assertEquals(3f, rater.rate(in1).value, "should be rated 3");

	}
	

	@Test
	public void testNormalisedDistance() {
		RaterInnerRepetitiveness rater = new RaterInnerRepetitiveness(bb);
		
		Individual in1 = new Individual(Themes.getTheme01(bb)); //just fake, not really needed
		
		double d1 = rater.normalisedDistanceToGoal(in1, 0.0f, 0.0f);
		assertEquals(0f,d1, "dist(0,0) should be 0");
		
		d1 = rater.normalisedDistanceToGoal(in1, 1.0f, 1.0f);
		assertEquals(0f,d1, "dist(1,1) should be 0");
		
		
		double is = 3f;
		double goal = 4.0f;
		d1 = rater.normalisedDistanceToGoal(in1, is, goal);
		double d2 = rater.normalisedDistanceToGoal(in1, is, goal);
		
		System.out.println("distance ("+is+", " +goal+") = " + d1);
		assertEquals(0.2f, d1, "dist(3, 4) should be 0.2");
		assertEquals(d1, d2, "dist should be commutative ");
		

		is = 0f;
		goal = 4.0f;
		d1 = rater.normalisedDistanceToGoal(in1, is, goal);
		assertEquals(0.8f, d1, "dist(0,4) should be 0.8");
		
		is = 0f;
		goal = 5.0f;
		d1 = rater.normalisedDistanceToGoal(in1, is, goal);
		assertEquals(0.84f, d1, "dist(0,5) should be 0.84");
			
	}	
}
