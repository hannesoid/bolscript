package bols;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Test
public class BolBaseOldTests {


	public BolBaseOldTests(String name) {
		super(name);
	}

	/*
	 * Test method for 'bols.BolBase.BolBase()'
	 */
	public void testBolBase() {
		BolBase bolBase;
		try {
			 bolBase = new BolBase();
		} catch (Exception e) {
			fail("BolBase threw an Exception when running Constructor");
		}
	}
	
	public void testEmptyBol() throws Exception {
		BolBase bolBase = new BolBase();
		
		BolName emptyBol = bolBase.getEmptyBol();
		assertNotNull("emptyBol should be no null", emptyBol);
		assertEquals("-",  emptyBol.getNameShort(), "empty bolname.name should be - ")
		
	
	}
	
	public void testBolDiffs() throws Exception {
		BolBase bb = new BolBase();
		BolName emptyBol = bb.getEmptyBol();
		assertEquals(0d, bb.getDifference("Dha", "Dha"), "Difference between the same Bol should be 0")
		assertEquals(bb.getDifference("Ge", "Dha"), bb.getDifference("Dha", "Ge"), "Difference should be commutative")
		assertTrue("Difference between Dha,Ge should be smaller than Dha,Ke ",bb.getDifference("Ge", "Dha") < bb.getDifference("Dha", "Ke"));
		assertTrue("Difference between Na,- should be smaller than Dha,- ",bb.getDifference("Na", "-") < bb.getDifference("Dha", "-"));
		System.out.println("diff(Dha,Ge)=" + bb.getDifference("Dha", "Ge") + ", diff(Dha, Ke)="+ bb.getDifference("Dha", "Ke"));
		//System.out.println("diff(Dha,Na)=" + bb.getDifference("Dha", "Na") + ", diff(Dhin, Tin)="+ bb.getDifference("Dhin", "Tin"));		
		//System.out.println("diff(Dha,Ti)=" + bb.getDifference("Dha", "Ti") + ", diff(Ti, Ke)="+ bb.getDifference("Ti", "Ke"));		
	}

	public void testKaliMaps() throws Exception {
		BolBase bb = new BolBase();
		
		BolName bn1 = bb.getBolName("Dha");
		BolName bn2 = bb.getBolName("Na");
		BolName bn3 = bb.getBolName("Ge");
		BolName bn4 = bb.getBolName("Ke");
		
		assertEquals(bn4, bb.getKaliBolName(bn3), "KaliMap of Ge should be Ke")
		assertEquals(bn2, bb.getKaliBolName(bn1), "KaliMap of Dha should be Na")
		//assertEquals(bn2, bb.getKaliBolName(bn2), "KaliMap of Ta should be Ta")
		
		
		
		
	}
}
