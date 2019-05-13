package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class CancelClosingEditor extends AbstractAction {
	EditorFrame editor;
	boolean closeAfterwards;
	
	public CancelClosingEditor(EditorFrame editor) {
		this.editor = editor;
		this.putValue(NAME, "Cancel");
	}
	
	public void actionPerformed(ActionEvent e) {
	}
	
}
