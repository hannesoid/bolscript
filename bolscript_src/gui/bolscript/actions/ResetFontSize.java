package gui.bolscript.actions;

import gui.bolscript.CompositionFrame;
import gui.bolscript.composition.CompositionPanel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.config.Config;


public class ResetFontSize extends AbstractAction {

	CompositionPanel compPanel;

	public ResetFontSize(CompositionPanel compPanel) {
		this.compPanel = compPanel;
		this.putValue(NAME, "Reset fontsize");
	}
	
	public void actionPerformed(ActionEvent e) {
		compPanel.resetFontSize();
	}
	
}
