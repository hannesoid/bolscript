package bols;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SubSequenceAtomicTest {

	BolBase bb = BolBase.getStandard();

	@Test
	public void testSubSequenceAtomic() {
		BolSequence seq1 = new BolSequence("Dha Ge Ti Ri Ke Te", bb);
		SubSequenceAtomic sub1 = new SubSequenceAtomic(seq1,0,6,new PlayingStyle(1f,1f));
		assertEquals(6, sub1.getLength(), "sub1 should have length 6");
		assertEquals(6.0, sub1.getDuration(), "sub1 should have duration 6");
		assertFalse(sub1.hasSubSequences(), "sub1 has no subsequences");
		
		sub1.setPlayingStyle(new PlayingStyle(2f,1f));
		assertEquals(6, sub1.getLength(), "sub1 should have length 6");
		assertEquals(3.0, sub1.getDuration(), "sub1 should have duration 3");
		assertFalse(sub1.hasSubSequences(), "sub1 has no subsequences");
		
	}

}
