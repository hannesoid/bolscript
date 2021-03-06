package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;
import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.config.Config;


public class DecreaseBundling extends AbstractAction {
	CompositionPanel compPanel;


	public DecreaseBundling(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Decrease bundling");		
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.decreaseBundling();
	}
	
}
