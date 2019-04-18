package bols;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BolSequenceAdvancedTest {

	/*
	 * Test method for 'bols.BolSequenceAdvanced.BolSequenceAdvanced(BolSequence)'
	 */
	@Test
	public void testBolSequenceAdvancedBolSequence() {
		ArrayList<BolSequence> bolSeqs = new ArrayList<BolSequence>();
		ArrayList<BolSequenceAdvanced> bolSeqsAdvanced = new ArrayList<BolSequenceAdvanced>();
		
		BolSequence bSeq = new BolSequence("Dha Ge Ti Ri Ke Te Dha -", BolBase.getStandard());
		BolSequenceAdvanced bSeqAdv  = new BolSequenceAdvanced(bSeq);
		bolSeqs.add(bSeq);
		bolSeqsAdvanced.add(bSeqAdv);
		
		Variation var1 = new Variation(bSeq);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(6,2,0.5f);
		bolSeqs.add(var1.getAsSequence());
		bolSeqsAdvanced.add(new BolSequenceAdvanced(var1.getAsSequence()));
		
		for (int s=0; s < bolSeqs.size(); s++) {
			BolSequence bolSeq = bolSeqs.get(s);
			BolSequenceAdvanced seq = bolSeqsAdvanced.get(s);
			
			assertEquals(bolSeq.getDuration(), seq.getDuration(), "duration should equal that of the bolSeq");
			
			for ( int i=0; i < bolSeq.getLength(); i++) {
				assertTrue(seq.getBol((int)i).equals(bolSeq.getBol(i)), "should have same bols ");
			}
		}
	}
	@Test
	public void testBolSequenceAdvancedRemoveBol() {
		ArrayList<BolSequence> bolSeqs = new ArrayList<BolSequence>();
		ArrayList<BolSequenceAdvanced> bolSeqsAdvanced = new ArrayList<BolSequenceAdvanced>();
		
		BolSequence bSeq = new BolSequence("Dha Ge Ti Ri Ke Te Dha -", BolBase.getStandard());
		BolSequenceAdvanced bSeqAdv  = new BolSequenceAdvanced(bSeq);
		bolSeqs.add(bSeq);
		bolSeqsAdvanced.add(bSeqAdv);
		
		Variation var1 = new Variation(bSeq);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(6,2,0.5f);
		bolSeqs.add(var1.getAsSequence());
		bolSeqsAdvanced.add(new BolSequenceAdvanced(var1.getAsSequence()));
		
		for (int s=0; s < bolSeqs.size(); s++) {
			BolSequence bolSeq = bolSeqs.get(s);
			BolSequenceAdvanced seq = bolSeqsAdvanced.get(s);
			
			assertEquals(bolSeq.getLength(), seq.getLength(), "length should be same ");
			
			int[] indexesOfBolsToRemove = {3,1};
			System.out.println("before removing: " + seq.toString());
			for ( int j=0; j < indexesOfBolsToRemove.length; j++) {
				seq.removeBol(indexesOfBolsToRemove[j]);
				System.out.println("removed " + indexesOfBolsToRemove[j] + ": " + seq.toString());
				assertEquals(bolSeq.getLength()-(j+1),seq.getLength(), "length should have decreased");
			}
			
			assertEquals(bolSeq.getDuration(), seq.getDuration(), "duration should equal that of the bolSeq");
			
			seq.removeBol(seq.getLength()-1);
			assertEquals((double) bolSeq.getDuration()-(1.0d / bolSeq.getBol(bolSeq.getLength()-1).getSpeed()),
					seq.getDuration(), "duration should get smaller by the last bols duration ");
		}
	}
	@Test
	public void testInsertBol () {
		ArrayList<BolSequence> bolSeqs = new ArrayList<BolSequence>();
		ArrayList<BolSequenceAdvanced> bolSeqsAdvanced = new ArrayList<BolSequenceAdvanced>();
		
		BolSequence bSeq = new BolSequence("Dha Ge Ti Ri Ke Te Dha -", BolBase.getStandard());
		BolSequenceAdvanced bSeqAdv  = new BolSequenceAdvanced(bSeq);
		bolSeqs.add(bSeq);
		bolSeqsAdvanced.add(bSeqAdv);
		
		Variation var1 = new Variation(bSeq);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(0,6,2);
		var1.addSubSequence(6,2,0.5f);
		bolSeqs.add(var1.getAsSequence());
		bolSeqsAdvanced.add(new BolSequenceAdvanced(var1.getAsSequence()));
		
		for (int s=0; s < bolSeqs.size(); s++) {
			BolSequence bolSeq = bolSeqs.get(s);
			BolSequenceAdvanced seq = bolSeqsAdvanced.get(s);
			
			
			
			assertEquals(bolSeq.getLength(), seq.getLength(), "length should be same ");
			
			
			ArrayList<BolPositionedAndWeighted> bolsToInsert = new ArrayList<BolPositionedAndWeighted>();
			bolsToInsert.add(new BolPositionedAndWeighted(BolBase.getStandard().getBolName("Na"), new PlayingStyle(1f,1f),0.5f,1));
			bolsToInsert.add(new BolPositionedAndWeighted(BolBase.getStandard().getBolName("Na"), new PlayingStyle(1f,1f), 0, 1));
			
			for (BolPositionedAndWeighted newBol : bolsToInsert) {
				System.out.println("before inserting: " + seq.toString());
				
				seq.insertBol(newBol);
				System.out.println("after inserting: " + seq.toString());
				
				assertEquals(bolSeq.getDuration(), seq.getDuration(), "duration should equal that of the bolSeq");
			}
						
			BolPositionedAndWeighted newBol = new BolPositionedAndWeighted(BolBase.getStandard().getBolName("Na"), new PlayingStyle(1f,1f),seq.getDuration(),1);
			
			System.out.println("before appending: " + seq.toString());
			seq.appendBol(newBol);
			System.out.println("after appending: " + seq.toString());
			
			assertEquals(bolSeq.getDuration()+(1.0f / newBol.getSpeed()), seq.getDuration(), "duration should get greater by the new bols duration ");
			
			newBol = new BolPositionedAndWeighted(BolBase.getStandard().getBolName("Na"), new PlayingStyle(1f,1f), 100, 1);
			
			System.out.println("before inserting: " + seq.toString());
			seq.insertBol(newBol);
			System.out.println("after inserting: " + seq.toString());
			
			
		}
	}

}
