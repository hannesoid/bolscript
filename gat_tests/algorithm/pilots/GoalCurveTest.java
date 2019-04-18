package algorithm.pilots;

import static algorithm.pilots.WayPoint.CurvePointTypes.LEVEL;
import static algorithm.pilots.WayPoint.CurvePointTypes.LINEAR;
import algorithm.raters.RaterAverageSpeed;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class GoalCurveTest {

	/*
	 * Test method for 'algorithm.pilots.GoalCurve.addPoint(CurveTypes, long, double, CurvePointTypes)'
	 */
	@Test
	public void testAddPoint() {
		GoalCurve gc = new GoalCurve(RaterAverageSpeed.class);
		gc.addPoint(0,1f,1f, LEVEL);
		gc.addPoint(2,2f,1f, LINEAR);
		gc.addPoint(3,4f,1f, LINEAR);
		gc.addPoint(6,2f,1f, LINEAR);
		gc.addPoint(10,1f,1f, LEVEL);
		System.out.println("gc1: " + gc.toString());
		System.out.println("gc1.pointsToString: " + gc.pointsToString());
		//assertEquals("at 0 it should have val 1", 1f, gc.);
	}

	/*
	 * Test method for 'algorithm.pilots.GoalCurve.updateGoal(Goal, long)'
	 */
	@Test
	public void testUpdateGoal() {

	}

}
