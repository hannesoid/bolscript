package bols;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BolBaseOldTests {

	/*
	 * Test method for 'bols.BolBase.BolBase()'
	 */
	@Test
	public void testBolBase() {
		BolBase bolBase;
		try {
			 bolBase = new BolBase();
		} catch (Exception e) {
			fail("BolBase threw an Exception when running Constructor");
		}
	}
	
	@Test
	public void testEmptyBol() throws Exception {
		BolBase bolBase = new BolBase();
		
		BolName emptyBol = bolBase.getEmptyBol();
		assertNotNull(emptyBol, "emptyBol should be no null");
		assertEquals("-",  emptyBol.getNameShort(), "empty bolname.name should be - ");
		
	
	}
	
	@Test
	public void testBolDiffs() throws Exception {
		BolBase bb = new BolBase();
		BolName emptyBol = bb.getEmptyBol();
		assertEquals(0d, bb.getDifference("Dha", "Dha"), "Difference between the same Bol should be 0");
		assertEquals(bb.getDifference("Ge", "Dha"), bb.getDifference("Dha", "Ge"), "Difference should be commutative");
		assertTrue(bb.getDifference("Ge", "Dha") < bb.getDifference("Dha", "Ke"), "Difference between Dha,Ge should be smaller than Dha,Ke ");
		assertTrue(bb.getDifference("Na", "-") < bb.getDifference("Dha", "-"), "Difference between Na,- should be smaller than Dha,- ");
		System.out.println("diff(Dha,Ge)=" + bb.getDifference("Dha", "Ge") + ", diff(Dha, Ke)="+ bb.getDifference("Dha", "Ke"));
		//System.out.println("diff(Dha,Na)=" + bb.getDifference("Dha", "Na") + ", diff(Dhin, Tin)="+ bb.getDifference("Dhin", "Tin"));		
		//System.out.println("diff(Dha,Ti)=" + bb.getDifference("Dha", "Ti") + ", diff(Ti, Ke)="+ bb.getDifference("Ti", "Ke"));		
	}

	@Test
	public void testKaliMaps() throws Exception {
		BolBase bb = new BolBase();
		
		BolName bn1 = bb.getBolName("Dha");
		BolName bn2 = bb.getBolName("Na");
		BolName bn3 = bb.getBolName("Ge");
		BolName bn4 = bb.getBolName("Ke");
		
		assertEquals(bn4, bb.getKaliBolName(bn3), "KaliMap of Ge should be Ke");
		assertEquals(bn2, bb.getKaliBolName(bn1), "KaliMap of Dha should be Na");
		//assertEquals(bn2, bb.getKaliBolName(bn2), "KaliMap of Ta should be Ta");
		
		
		
		
	}
}
