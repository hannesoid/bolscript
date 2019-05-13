package gui.bolscript.actions;

import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class SmartResizeShrink extends AbstractAction {
	CompositionPanel compPanel;

	public SmartResizeShrink(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Show less");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.smartResizeSmaller();
	}
	
}
