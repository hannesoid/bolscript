package bolscript;

import org.junit.jupiter.api.Test;

public class UpdateManagerTest {

	@Test
	public void testCheckForUpdates() {
		UpdateManager updateManager = new UpdateManager();
		try {
			updateManager.CheckForUpdates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
