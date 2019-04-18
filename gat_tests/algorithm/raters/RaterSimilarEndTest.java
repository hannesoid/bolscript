package algorithm.raters;

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

public class RaterSimilarEndTest {
	BolBase bb;

	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();		
	}
	
	@Test
	public void testRaterSimilarEndNew() throws Exception {
		RaterSimilarEnd rater = new RaterSimilarEnd(bb);		
	}
	
	@Test
	public void testRaterSimilarEndRate() throws Exception {
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RaterSimilarEnd rater = new RaterSimilarEnd(bb);
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1.toString());
		assertTrue(f1.equals(f2), "two similar individuals should get the same features: ");
		
		var1 = new Variation("Dha Ge - -", bb);
		var2 = new Variation("Dha Ge - Dha", bb);
		
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		Feature f3 = rater.rate(in1);
		Feature f4 = rater.rate(in2);
		System.out.println("f3: " + f3.toString() + ", f4: " + f4.toString());
		
		assertTrue(f3.value == 4.0f, "f3 should be rated as 4.0 ");
		assertTrue(f4.value == 4.0f, "f4 should be rated as 4.0 ");
		

		BolSequence seq1 = new BolSequence("Dha Ge Dhin Na Ge Na Na Na", bb);
		var1 = new Variation(seq1);
		var2 = new Variation(seq1);
		var1.addSubSequence(new SubSequenceAdvanced(seq1,0,8, new PlayingStyle(1,1)));
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,8, new PlayingStyle(2,1)));
		
		
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		Feature f5 = rater.rate(in1);
		Feature f6 = rater.rate(in2);
		
		System.out.println("f5: " + f5.toString() + ", f6: " + f6.toString());
		assertTrue(f5.value == 8.0f, "f5 should be rated as 8.0 ");
		assertTrue(f6.value == 8.0f, "f6 should be rated as 8.0 ");
	
		seq1 = new BolSequence("Dha Ge Dhin Na Ge Ge Na Dha", bb);
		var1 = new Variation(seq1);
		var2 = new Variation(seq1);		
		var1.addSubSequence(new SubSequenceAdvanced(seq1,0,3,new PlayingStyle(4,1)));
		var1.addSubSequence(new SubSequenceAdvanced(seq1,0,3,new PlayingStyle(4,1)));
		var1.addSubSequence(new SubSequenceAdvanced(seq1,3,5,new PlayingStyle(4,1)));		
		
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,3,new PlayingStyle(4,1)));
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,3,new PlayingStyle(4,1)));
		var2.addSubSequence(new SubSequenceAdvanced(seq1,3,5,new PlayingStyle(2,1)));		
		
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		Feature f7 = rater.rate(in1);
		Feature f8 = rater.rate(in2);
		System.out.println("f7: " + f7.toString() + ", f8: " + f8.toString());
		assertNotSame(f7.value, f8.value, "unconstant speed in the end should make a difference");
		assertEquals(8.0f, f7.value, "f7 should be rated as 8.0");
		assertEquals(5.0f, f8.value, "f8 should be rated as 5.0");
		
		var1 = new Variation(seq1);
		var1.addSubSequence(new SubSequenceAdvanced(seq1,0,1, new PlayingStyle(1,1)));
		var2 = new Variation(seq1);
		var2.addSubSequence(new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1,1)));
		in1 = new Individual(var1);
		in2 = new Individual(var2);
		
		Feature f9 = rater.rate(in1);
		Feature f10 = rater.rate(in2);
		System.out.println("f9: " + f9.toString() + ", f10: " + f10.toString());	
		assertEquals(1.0f, f9.value, "f9 should be rated as 1");
		assertEquals(0f, f10.value, "f10 should be rated as 0");
		
		
	}
	
	@Test
	public void testNormalisedDistance() {
		Rater rater = new RaterSimilarEnd(bb);
		
		Individual in1 = new Individual(Themes.getTheme01(bb)); //just fake, not really needed
		
		double d1 = rater.normalisedDistanceToGoal(in1,0.0f,0.0f);
		assertEquals(0.0f,d1, "dist(0,0) should be 0");
		
		d1 = rater.normalisedDistanceToGoal(in1,1.0f,1.0f);
		assertEquals(0.0f,d1, "dist(1,1) should be 0");
		
		d1 = rater.normalisedDistanceToGoal(in1,10f,1.0f);
		assertEquals(0.0f, d1, "dist(10,1) should be 0");
		
		d1 = rater.normalisedDistanceToGoal(in1,4f,8.0f);
		assertEquals(0.5f, d1, "dist(4,8) should be 0.5");
	}	
}
