package gui.bolscript.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;

import bolscript.Master;

public class CheckForUpdates extends AbstractAction {
		
	public CheckForUpdates() {
		this.putValue(NAME, "Check for updates");
	}
	
	public void actionPerformed(ActionEvent e) {
		Master.master.checkForUpdates();
	}
		
		
}
