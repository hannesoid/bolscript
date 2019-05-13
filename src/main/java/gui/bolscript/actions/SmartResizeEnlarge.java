package gui.bolscript.actions;

import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class SmartResizeEnlarge extends AbstractAction {
	CompositionPanel compPanel;

	public SmartResizeEnlarge(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Show more");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.smartResizeLarger();
	}
	
}
