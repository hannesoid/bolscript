package gui.bolscript.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.Master;

public class ExitProgram extends AbstractAction {
	
	public ExitProgram() {
		this.putValue(NAME, "Quit");
	}
	
	public void actionPerformed(ActionEvent e) {
		Master.master.attemptExit();
	}
	
	
}
