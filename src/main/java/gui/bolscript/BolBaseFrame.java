package gui.bolscript;

import gui.bolscript.tables.BolBasePanel;
import gui.bolscript.tables.BolBaseTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bols.BolBase;

public class BolBaseFrame extends JFrame {
	private BolBasePanel bolBasePanel;
	
	public BolBaseFrame(Dimension size) {
		
		super("BolBase");
		this.setSize(size);
		
		BolBaseTableModel model = new BolBaseTableModel(BolBase.getStandard());
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		bolBasePanel = new BolBasePanel();
		contentPane.add(bolBasePanel,BorderLayout.CENTER);
		this.setContentPane(contentPane);
	}
	
	
}
