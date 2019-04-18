package bols;

import java.util.ArrayList;

import algorithm.composers.kaida.Individual;
import algorithm.mutators.Mutator;
import algorithm.mutators.MutatorDoublifyAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class SubSequenceAdvancedTest {
	BolBase bb = BolBase.getStandard();
	
	@Test
	public void testSubSequenceSimple() {
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced(seq1,0,6,new PlayingStyle(1f,1f));
		assertEquals(6, sub1.getLength(), "sub1 should have length 6");
		assertEquals(6.0, sub1.getDuration(), "sub1 should have duration 6");
		assertFalse(sub1.hasSubSequences(), "sub1 has no subsequences");
	}
	
	@Test
	public void testSubSequenceComplex() {
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced subA1 = new SubSequenceAdvanced(seq1,0,6,new PlayingStyle(1f,1f));
		SubSequenceAdvanced subA2 = new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1f,1f));
		
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subA1,subA2);
		
		assertTrue(sub1.hasSubSequences(), "sub1 has subsequences");
		assertEquals(8.0, sub1.getDuration(), "sub1 should have duration 6");
		assertEquals(8, sub1.getLength(), "sub1 should have length 6");
	}

	
	@Test
	public void testSubSequenceGetSubSequencesRecursive() {
		
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced subA1 = new SubSequenceAdvanced(seq1,0,6,new PlayingStyle(1f,1f));
		SubSequenceAdvanced subA2 = new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1f,1f));
		
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subA1,subA2);
		
		ArrayList<SubSequence> subs = sub1.getSubSequencesRecursive(1);
		ArrayList<SubSequence> subs2 = sub1.getSubSequencesRecursive(10);
		assertEquals(subs.size(), sub1.getSubSequences().size(), "subs should be of same length as sub1");
		assertEquals(subs2.size(), sub1.getSubSequences().size(), "subs2 should be of same length as sub1");
		for (int i=0; i < subs.size(); i++) {
			assertTrue(subs.get(i).equals(sub1.getSubSequence(i)), "subSubSequences should be same");
			assertTrue(subs2.get(i).equals(sub1.getSubSequence(i)), "subSubSequences should be same");
		}
		
		
		seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		
		SubSequence subB1 = new SubSequenceAtomic(seq1,0,2, new PlayingStyle(1f,1f));
		SubSequence subB2 = new SubSequenceAtomic(seq1,2,4, new PlayingStyle(1f,1f));
		
		subA1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f), subB1, subB2);
		
		subA2 = new SubSequenceAdvanced(seq1,0,2, new PlayingStyle(1f,1f));
		
		sub1 = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subA1,subA2);
		
		subs = sub1.getSubSequencesRecursive(2);
		
		assertTrue(subB1.equals(subs.get(0)), "1. should be subB1 ");
		assertTrue(subB2.equals(subs.get(1)), "2. should be subB2 ");
		assertTrue(subA2.equals(subs.get(2)), "3. should be subA2 ");
		
	
	}
	
	@Test
	public void testGetSubSequencesRecursiveAdvanced() {
		
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAdvanced subA1, subA2, subB1, subB2;
		SubSequenceAdvanced sub1;
		
		ArrayList<SubSequence> subs;
		ArrayList<SubSequence> subs2;
		
		
		subA1 = new SubSequenceAdvanced(seq1,0,2,new PlayingStyle(2f,1f));
		subA2 = new SubSequenceAdvanced(seq1,0,4,new PlayingStyle(1f,1f));
		subB1 = new SubSequenceAdvanced(new PlayingStyle(2f,1f), subA1, subA2);
		
		sub1  = new SubSequenceAdvanced(new PlayingStyle(2f,1f), subA1, subA2, subB1);
		
		subs = sub1.getSubSequencesRecursive(1);
		assertEquals(3,subs.size(), "should have 3 subs at depth 1 ");
		assertEquals(4f, subs.get(0).getPlayingStyle().getSpeedValue(), "1. should have speed 4 ");
		assertEquals(2f, subs.get(1).getPlayingStyle().getSpeedValue(), "2. should have speed 2 ");
		assertEquals(4f, subs.get(2).getPlayingStyle().getSpeedValue(), "3. should have speed 4 ");
		
		subs = sub1.getSubSequencesRecursive(2);
		assertEquals(4,subs.size(), "should have 4 subs at depth 2 ");
		assertEquals(4f, subs.get(0).getPlayingStyle().getSpeedValue(), "1. should have speed 4 ");
		assertEquals(2f, subs.get(1).getPlayingStyle().getSpeedValue(), "2. should have speed 2 ");
		assertEquals(8f, subs.get(2).getPlayingStyle().getSpeedValue(), "3. should have speed 8 ");
		assertEquals(4f, subs.get(3).getPlayingStyle().getSpeedValue(), "4. should have speed 4 ");
	}

	@Test
	public void testSubSequenceNewFromString() {
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced("Dha Ge Ti Ri Ke Te, Dha -, Dha -, Dha Ge Ti Ri Ke Te", bb);
		
		assertEquals(4, sub1.getSubSequences().size(), "The Variation should have 4 Subsequences!");
		
		assertEquals(6, sub1.getSubSequence(0).getLength(), "The 1. subseq should be of length 6!");
		assertEquals(2, sub1.getSubSequence(1).getLength(), "The 2. subseq should be of length 2");
		assertEquals(6, sub1.getSubSequence(3).getLength(), "The last subseq should be of length 6!");
		assertEquals(16.0, sub1.getDuration(), "Variation should be of duration 16");
		assertEquals(16, sub1.getLength(), "Variation should be of length 16");
		
		System.out.println("var1: " + sub1.toString());
		System.out.println("var1.getSeq " + sub1.getAsSequence());
	}
	
	@Test
	public void testSubSequenceNewFromStringComplex() {
		SubSequenceAdvanced sub1 = new SubSequenceAdvanced("Dha Ge; Ti Ri Ke Te, Dha -, Dha -, Dha Ge; Ti Ri Ke Te", bb);
		
		System.out.println("var1: " + sub1.toString());
		System.out.println("var1.getSeq " + sub1.getAsSequence());	
		
		assertEquals(4, sub1.getSubSequences().size(), "The Variation should have 4 Subsequences!");
		
		assertEquals(6, sub1.getSubSequence(0).getLength(), "The 1. subseq should be of length 6!");
		assertEquals(2, sub1.getSubSequence(1).getLength(), "The 2. subseq should be of length 2");
		assertEquals(6, sub1.getSubSequence(3).getLength(), "The last subseq should be of length 6!");
		assertEquals(16.0, sub1.getDuration(), "Variation should be of duration 16");
		assertEquals(16, sub1.getLength(), "Variation should be of length 16");
		
		
		assertEquals(2, sub1.getSubSequence(0).getSubSequencesRecursive(1).size(), "The 1. subSeq should have 2 subSeqs ");
		assertEquals(1, sub1.getSubSequence(1).getSubSequencesRecursive(1).size(), "The 2. subSeq should have 1 subSeqs ");
		assertEquals(1, sub1.getSubSequence(2).getSubSequencesRecursive(1).size(), "The 3. subSeq should have 1 subSeqs ");
		assertEquals(2, sub1.getSubSequence(3).getSubSequencesRecursive(1).size(), "The 4. subSeq should have 2 subSeqs ");
		
		ArrayList <SubSequence> subs = sub1.getSubSequencesRecursive(2);
		assertEquals(2, subs.get(0).getLength(), "The 1. subsubseq should be of length 2!");
		assertEquals(4, subs.get(1).getLength(), "The 2. subsubseq should be of length 4");
		assertEquals(2, subs.get(2).getLength(), "The 3. subsubseq should be of length 6!");
		
		SubSequenceAdvanced subsAtomified = new SubSequenceAdvanced(new PlayingStyle(1f,1f), subs.toArray());
		System.out.println("subsAtomified: " + subsAtomified.toString());
		System.out.println("subsAtomified.getSeq " + subsAtomified.getAsSequence());	
	}
	

}
