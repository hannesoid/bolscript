package bols;

import java.util.ArrayList;

import basics.Debug;
import bolscript.sequences.RepresentableSequence;

import config.Themes;

import algorithm.tools.RouletteWheel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class VariationTest {

	BolBase bb;
	
	@BeforeEach
	protected void setUp() throws Exception {
		Debug.init();
		Debug.setMute(true);
		BolBase.init(this.getClass());
		bb = BolBase.getStandard();
		Debug.setMute(false);
	}

	/*
	 * Test method for 'bols.Variation.Variation(String, BolBaseGeneral)'
	 */
	@Test
	public void testVariationStringBolBaseGeneral() {
		//setBolNames("Dha Ge Ti Ri Ke Te Tin - Na Ta Ke Dhin Ta3 Tun Dhun Ge2");
		 
		Variation var1 = new Variation("Dha Ge Ti Ri Ke Te, Dha -, Dha -, Dha Ge Ti Ri Ke Te", bb);
		
		assertEquals(4, var1.getSubSequences().size(), "The Variation should have 4 Subsequences!");
		
		assertEquals(6, var1.getSubSequence(0).getLength(), "The 1. subseq should be of length 6!");
		assertEquals(2, var1.getSubSequence(1).getLength(), "The 2. subseq should be of length 2");
		assertEquals(6, var1.getSubSequence(3).getLength(), "The last subseq should be of length 6!");
		assertEquals(16.0, var1.getDuration(), "Variation should be of duration 16");
		assertEquals(16, var1.getLength(), "Variation should be of length 16");
		
		System.out.println("var1: " + var1.toString());
		System.out.println("var1.getSeq " + var1.getAsSequence());
	}
	
	/*
	 * Test method for 'bols.Variation.getTestVariation(BolBaseGeneral)'
	 */
	@Test
	public void testVariationGetTestVariation() {
		//setBolNames("Dha Ge Ti Ri Ke Te Tin - Na Ta Ke Dhin Ta3 Tun Dhun Ge2");
		 
		Variation var1 = Themes.getTheme01(bb);
		
		assertEquals(4, var1.getSubSequences().size(), "The Variation should have 4 Subsequences!");
		assertEquals(6, var1.getSubSequence(0).getLength(), "The 1. subseq should be of length 6!");
		assertEquals(2, var1.getSubSequence(1).getLength(), "The 2. subseq should be of length 2");
		assertEquals(6, var1.getSubSequence(3).getLength(), "The last subseq should be of length 6!");
		
		System.out.println(var1.toString());
	}

	@Test
	public void testVariationGetDuration() {
		ArrayList<Variation> vars = new ArrayList<Variation>();
		ArrayList<BolName> bolNames = bb.getBolNames();
		RouletteWheel wheel = new RouletteWheel();
		RouletteWheel wheel2 = new RouletteWheel();
		try {
		for (BolName name : bolNames) {
			wheel.put(1,name.getNameShort());
		}
		wheel2.put(6," ");
		wheel2.put(1,", ");
		
		for (int i=0; i<50; i++) {
			int l = (int) (Math.random() * 50) + 1;
			//generate a randomized sequence
			String strSeq = "";
			for(int j=0; j<l; j++) {
				strSeq += wheel.getRandom();
				if (j<(l-1)) {
					strSeq += (String) wheel2.getRandom();
				}
			}
			Variation var1 = new Variation(strSeq, bb);
			assertEquals(var1.getAsSequence().getDuration(),(double)var1.getDuration(), ".getDuration() and .getAsSequence().getDuration() should be equal");

		}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			fail("some exception: " + e.getMessage());
		}
		
	}
	
	@Test
	public void testAddSubSequence() {
		BolSequence seq1 = new BolSequence("Dha Dhin Dhin Dha Dha Dhin Dhin Dha Dha Tin Tin Na Na Dhin Dhin Dha", bb);
		Variation var1 = new Variation(seq1);
		var1.addSubSequence(0,4);
		var1.addSubSequence(4,4);
		var1.addSubSequence(8,4);
		var1.addSubSequence(12,4);
		
		Variation var2 = new Variation(seq1);
		var2.addSubSequences(new int[]{0,4},
				new int[]{4,4},
				new int[]{8,4},
				new int[]{12,4});
		
		Variation var3 = new Variation(seq1);
		var3.addSubSequence(new SubSequenceAdvanced(seq1,0,4,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,4,4,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,8,4,new PlayingStyle(1,1)));
		var3.addSubSequence(new SubSequenceAdvanced(seq1,12,4,new PlayingStyle(1,1)));
		
		assertEquals(var1.getAsSequence(), var2.getAsSequence(), "var1 should equal var2 in sequence ");
		assertEquals(var2.getAsSequence(), var3.getAsSequence(), "var2 should equal var3 in sequence ");
	}
	
	@Test
	public void testGetCopyWithPausesMerged() {
		BolSequence seq1 = new BolSequence ("- Dha Ge Ti Ri Ke Te Dha -", bb);
		BolSequence seq2 = seq1.getCopyWithMergedPauses(bb);
		
		System.out.println("seq1:"+seq1.toStringFull()+",\nseq2(merged):"+seq2.toStringFull());
		
		assertEquals(seq1.getDuration(), seq2.getDuration(), "duration should be same ");
		assertEquals(seq1.getLength()-1, seq2.getLength(), "length should decrease by 1");
		assertTrue(new SubSequenceAdvanced(seq1,0,7,new PlayingStyle(1,1)).getAsSequence().equals(new SubSequenceAdvanced(seq2,0,7,new PlayingStyle(1,1)).getAsSequence()), "first 7 bols should be same");
		
	}
	
	@Test
	public void testGetAsSequence() {
		
	}
	
	@Test
	public void testNewVariationFromRepresentableSeq () {
		RepresentableSequence r = new RepresentableSequence();
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		
		Variation var = new Variation(r);
		var.addSubSequence(0, 4);
		assertEquals(4, var.getLength());
		assertEquals(4d, var.getDuration());
	}
	
}
