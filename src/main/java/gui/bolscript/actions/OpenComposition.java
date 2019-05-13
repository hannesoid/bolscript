package gui.bolscript.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.Master;
import bolscript.compositions.Composition;

public class OpenComposition extends AbstractAction {

	Composition composition;
	
	public OpenComposition(Composition composition) {
		super("Open");
		this.composition = composition;
		if (composition == null) this.setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (composition != null) {
			Master.master.openEditor(composition);
		}
		
	}

	
}
