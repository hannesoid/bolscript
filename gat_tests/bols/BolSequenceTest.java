package bols;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import basics.Debug;
import bolscript.config.Config;
import bolscript.sequences.RepresentableSequence;

public class BolSequenceTest {
	BolBaseGeneral bb;

	
	@BeforeAll
	public void setUp() throws Exception {
		bb = BolBase.getStandard();
	}

	@Test
	public void testDurationStuff () throws Exception{

		BolSequence seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));
		
		assertEquals(4.0d, seq1.getDuration(), "duration of seq1 should be 4")
		
		
		seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		
		assertEquals(3.0d, seq1.getDuration(), "duration should be 3")
		
		seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(2,1), null, false));		
		
		assertEquals(2.0d, seq1.getDuration(), "duration should be 2.0f")
		
		seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(2,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(2,1), null, false));
		
		assertEquals(1.5d, seq1.getDuration(), "duration should be 1.5f")
		
	}
	@Test	
	public void testCopyPausesMerged() throws Exception {
		BolSequence seq1 = new BolSequence();
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Ge"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq1.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));
		
		BolSequence seq2 = new BolSequence();
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));		
		seq2.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(2,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(2,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("Dha"), new PlayingStyle(1,1), null, false));
		seq2.addBol(new Bol(bb.getBolName("-"), new PlayingStyle(1,1), null, false));		
		
		BolSequence seq3 = seq1.getCopyWithMergedPauses(bb);
		assertEquals(seq3.getDuration(), seq1.getDuration(), "duration of seq3 should be same as of seq1")
		assertEquals(3, seq3.getLength(), "seq3 should have three bols left")
		
		BolSequence seq4 = seq2.getCopyWithMergedPauses(bb);	
		assertEquals(seq2.getDuration(), seq4.getDuration(), "duration of seq4 should be same as of seq2")
		assertEquals(5.0d, seq4.getDuration(), "duration of seq4 should be 5")
		assertEquals(3, seq4.getLength(), "seq4 should have three bols left")
		
		
	}
	@Test	
	public void testEquals() {
		BolSequence seq1 = new BolSequence("Dha Dhin Dhin Dha", bb);
		BolSequence seq2 = new BolSequence("Dha Dhin Dhin Dha", bb);
		assertTrue("seq1 should be equals to seq1", seq1.equals(seq2));
		seq2 = new BolSequence("Dha Dhin Na Dha", bb);
		assertFalse("seq1 should differ from seq2", seq1.equals(seq2));
		seq2 = new BolSequence("Dha Dhin Dhin Dha Dha", bb);
		assertFalse("seq1 should differ from seq2", seq1.equals(seq2));
		seq1 = new BolSequence("Dha Dhin Dhin Dha Dha -", bb);
		seq2 = new BolSequence("Dha Dhin Dhin Dha Dha", bb);
		assertFalse("seq1 should differ from seq2", seq1.equals(seq2));
		
	}
	
	@Test
	public void testBolSequenceFromRepresentableSeq() {
		RepresentableSequence r = new RepresentableSequence();
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dhin"), new PlayingStyle(1d), null, false));
		r.add(new Bol(bb.getBolName("Dha"), new PlayingStyle(1d), null, false));
		
		BolSequence bs = new BolSequence(r);
		assertEquals(4, bs.getLength());
		assertEquals(4d, bs.getDuration());
	}

}
