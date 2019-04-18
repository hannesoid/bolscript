package algorithm.raters;

import config.Themes;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.BolSequence;
import bols.Variation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RaterOffBeatnessTest {
	BolBase bb = BolBase.getStandard();
	
	@Test
	public void testNew() throws Exception {
		RaterOffBeatness rater = new RaterOffBeatness(bb);		
	}
	
	@Test
	public void testRateOffBeatness() {
		
		Variation var1 = Themes.getTheme01(bb);
		Variation var2 = Themes.getTheme01(bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		
		RaterOffBeatness rater = new RaterOffBeatness(bb);
		Feature f1 = rater.rate(in1);
		Feature f2 = rater.rate(in2);
		System.out.println("f1: " + f1.toString());
		assertTrue(f1.equals(f2), "two similar individuals should get the same features: ");
		
		
		var1 = new Variation("- Na, - Dha, Ge -, Dha -", bb);
		BolSequence seq = var1.getBasicBolSequence();
		in1 = new Individual(var1);
		
		assertEquals(0f, rater.rate(in1).value, "var1 should have no offbeats ");
		
		var1 = new Variation(seq);
		var1.addSubSequence(0,2,2);
		var1.addSubSequence(0,2,2);
		var1.addSubSequence(0,2,2);
		var1.addSubSequence(0,2,2);	
		in1 = new Individual(var1);
		
		assertEquals(8f, rater.rate(in1).value, "var1 should have 8 offbeats ");
		
		var1 = new Variation(seq);
		var1.addSubSequence(0, 2, 8);
		var1.addSubSequence(0, 2, 8);
		var1.addSubSequence(0, 2, 8);
		var1.addSubSequence(0, 2, 8);	
		in1 = new Individual(var1);
		
		assertEquals(32f, rater.rate(in1).value, "var1 should have 32 offbeats ");
		
		var1 = new Variation(new BolSequence("Na Dha - Dha Dha -",bb));
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);	
		in1 = new Individual(var1);
				
		assertEquals(0f, rater.rate(in1).value, "var1 should have 0 offbeats ");
		
		var1 = new Variation(new BolSequence("Na Dha - Dha Dha -",bb));
		var1.addSubSequence(0, 1, 2);
		var1.addSubSequence(1, 1, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);	
		in1 = new Individual(var1);
				
		assertEquals(2f, rater.rate(in1).value, "var1 should have offbeatness 2 ");
		
		var1 = new Variation(new BolSequence("Na Dha - Dha Dha -",bb));
		var1.addSubSequence(0, 1, 2);
		var1.addSubSequence(1, 1, 2);
		var1.addSubSequence(2, 2, 2);
		var1.addSubSequence(0, 2, 2);
		var1.addSubSequence(0, 2, 2);	
		in1 = new Individual(var1);
				
		assertEquals(1f, rater.rate(in1).value, "var1 should have offbeatness 1 ");
		
	}
	
	@Test
	public void testSpecial1() {

	}
}
