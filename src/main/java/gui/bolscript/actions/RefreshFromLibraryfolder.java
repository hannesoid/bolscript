package gui.bolscript.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.Master;

public class RefreshFromLibraryfolder extends AbstractAction {
	
	public RefreshFromLibraryfolder() {
		this.putValue(NAME, "Refresh Library");
	}
	
	public void actionPerformed(ActionEvent e) {
		Master.master.refreshFromLibraryFolder();
	}
	
	
}
