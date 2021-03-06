package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;
import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.config.Config;


public class DecreaseFontSize extends AbstractAction {
	CompositionPanel compPanel;

	public DecreaseFontSize(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Decrease fontsize");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.decreaseFontSize();
	}
	
}
