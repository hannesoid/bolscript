package gui.bolscript.actions;

import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class IncreaseFontSize extends AbstractAction {
	CompositionPanel compPanel;

	public IncreaseFontSize(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Increase fontsize");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.increaseFontSize();
	}
	
}
