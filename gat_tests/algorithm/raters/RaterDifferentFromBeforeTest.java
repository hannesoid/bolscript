package algorithm.raters;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import algorithm.composers.kaida.Feature;
import algorithm.composers.kaida.Individual;
import bols.BolBase;
import bols.Variation;

public class RaterDifferentFromBeforeTest {

	BolBase bb;
	
	@BeforeEach
	protected void setUp() {
		bb = new BolBase();		
	}

@Test
	public void testRaterDifferentFromBefore() throws Exception {
		RaterDifferentFromBefore rater = new RaterDifferentFromBefore(bb);	
		
		Variation var1 = new Variation("Dha Dhin Dhin Dha", bb);
		Variation var2 = new Variation("Dha Dhin Dhin Ge", bb);
		
		Individual in1 = new Individual(var1);
		Individual in2 = new Individual(var2);
		ArrayList<Individual> previous = new ArrayList<Individual>();
		previous.add(in1);
		Feature f1 = rater.rate(in1, previous);
		
		assertEquals(0f,f1.value, "two similar vars should be rated 0 diff");
		
		Feature f2 = rater.rate(in2, previous);
		assertEquals(1f, f2.value, "two different vars should be rated 1 diff");
		
		
	}
}
