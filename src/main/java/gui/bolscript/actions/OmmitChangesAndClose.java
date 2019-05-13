package gui.bolscript.actions;

import gui.bolscript.EditorFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import bolscript.Master;

public class OmmitChangesAndClose extends AbstractAction {
	EditorFrame editor;
	
	public OmmitChangesAndClose(EditorFrame editor) {
		this.editor = editor;
		this.putValue(NAME, "No");
	}
	
	public void actionPerformed(ActionEvent e) {
		editor.getComposition().revertFromBackup();
		Master.master.closeEditor(editor);

	}
	
}
