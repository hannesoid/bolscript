package gui.bolscript.tables;


import gui.bolscript.actions.OpenComposition;
import gui.bolscript.actions.RemoveSelected;
import gui.bolscript.actions.RevealCompositionInOSFileManager;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import basics.Debug;
import basics.GUI;
import bolscript.Master;
import bolscript.compositions.Composition;
import bolscript.config.Config;
import bolscript.config.GuiConfig;

public class CompositionListPanel extends JScrollPane  {

	JTable compositionTable = null;
	CompositionTableModel tableModel = null;

	public CompositionListPanel(CompositionTableModel model) {
		super();
		this.tableModel = model;
		compositionTable = new JTable(model);
		compositionTable.setDefaultRenderer(Integer.class, new StateRenderer(false));
		compositionTable.setDefaultRenderer(Object.class, new CellRenderer(true));
		compositionTable.setBackground(Color.white);
		compositionTable.getColumnModel().getColumn(0).setMaxWidth(20);
		compositionTable.getColumnModel().getColumn(1).setMinWidth(140);
		compositionTable.getColumnModel().getColumn(2).setMinWidth(90);
		compositionTable.getColumnModel().getColumn(3).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(4).setMinWidth(60);
		compositionTable.getColumnModel().getColumn(5).setWidth(90);
		compositionTable.addMouseListener(GUI.proxyClickListener(Master.master, "clickOnCompositionList"));
		//compositionTable.setShowGrid(false);
		compositionTable.setGridColor(GuiConfig.tableBG);
		compositionTable.setShowGrid(false);
		compositionTable.setShowHorizontalLines(false);
		compositionTable.setShowVerticalLines(true);
		//compositionTable.set
		
		
		compositionTable.addMouseListener(new MouseAdapter()
		{

			private Composition determineComposition (MouseEvent e, boolean ensureItIsSelected) {
				JTable source = (JTable)e.getSource();
				int row = source.rowAtPoint( e.getPoint() );
				int column = source.columnAtPoint( e.getPoint() );
				
				if (ensureItIsSelected) {
					if (! source.isRowSelected(row))
						source.changeSelection(row, column, false, false);
				}
				
				int index = source.getRowSorter().convertRowIndexToModel(row);
				Composition compAtRow = ((CompositionTableModel) source.getModel()).getComposition(index);
				return compAtRow;
			}

			private void showPopup(MouseEvent e) {

				Composition comp = determineComposition(e, true);
				getPopupMenu(comp).show(e.getComponent(), e.getX(), e.getY());

			}
			
			private void processClick(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopup(e);
				} else if (e.getClickCount() == 2){
					Composition comp = determineComposition(e, true);
					Master.master.openEditor(comp);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if (Config.OS == Config.MAC) {
					processClick(e);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if (Config.OS != Config.MAC) {
					processClick(e);
				}
			}

		});



		//set Rowsorters
		RowSorter<CompositionTableModel> sorter = new TableRowSorter<CompositionTableModel>(model);
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		for (int i = 1; i < model.getColumnCount(); i++) {
			sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
		}
		sorter.setSortKeys(sortKeys); 

		//= new Def
		compositionTable.setRowSorter(sorter);


		this.setViewportView(compositionTable);
		this.getViewport().setBackground(Color.white);


		this.setOpaque(false);

		//this.addMouseListener(this);

	}

	public JPopupMenu getPopupMenu(Composition comp) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem open = new JMenuItem(new OpenComposition(comp));
		JMenuItem reveal = new JMenuItem(new RevealCompositionInOSFileManager(null, null, comp));
		JMenuItem remove = new JMenuItem(new RemoveSelected(Master.master.getBrowser()));
		
		popupMenu.add(open);
		popupMenu.add(reveal);
		popupMenu.addSeparator();
		popupMenu.add(remove);
		
		return popupMenu;
	}

	public ArrayList<Composition> getSelectedCompositions() {
		ArrayList<Composition> selectedComps = new ArrayList<Composition> ();
		int[] selectedRows = compositionTable.getSelectedRows();
		for (int i=0; i < selectedRows.length; i++) {
			
			int index = compositionTable.getRowSorter().convertRowIndexToModel(selectedRows[i]);
			selectedComps.add(tableModel.getComposition(index));
		}
		return selectedComps;
	}
	
	public JTable getCompositionTable() {
		return compositionTable;
	}

	public CompositionTableModel getTableModel() {
		return tableModel;
	}


}
