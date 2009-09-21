package gui.bolscript;

import gui.bols.SequencePanel;
import gui.bols.SequenceTitlePanel;
import gui.bolscript.actions.DecreaseBundling;
import gui.bolscript.actions.DecreaseFontSize;
import gui.bolscript.actions.IncreaseBundling;
import gui.bolscript.actions.IncreaseFontSize;
import gui.bolscript.actions.ResetFontSize;
import gui.bolscript.actions.SetLanguage;
import gui.bolscript.composition.CommentText;
import gui.bolscript.composition.FootnoteText;
import gui.bolscript.composition.PageBreakPanel;
import gui.menus.ViewerActions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import basics.Debug;
import basics.GUI;
import bols.BolName;
import bols.BundlingDepthToSpeedMap;
import bols.tals.Tal;
import bols.tals.TalBase;
import bols.tals.Teental;
import bolscript.compositions.Composition;
import bolscript.config.Config;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.sequences.RepresentableSequence;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;


public class CompositionPanel extends JLayeredPane {
	
	private static final long serialVersionUID = -9072151161705640812L;
	
	private static int contentLayer = 1;
	private static int metaLayer = 2;
	
	private Composition composition;
	private Packets packets = null;
	
	private int language = bols.BolName.SIMPLE;
	private int renderingWidth;
	
	private AbstractAction decreaseBundling, increaseBundling, decreaseFontsize, increaseFontsize, resetFontsize;
	private AbstractAction[] setLanguage;
	private ViewerActions viewerActions;
	
	/**
	 * The talBase is queried for retrieving a Tal Object to a Name String 
	 */
	private TalBase talBase;
	
	/**
	 * The depth to which single bols are bundled.
	 */
	private int bundlingDepth = 0;
	
	/**
	 * The depth to speed map for determining bundling speeds.
	 */
	private BundlingDepthToSpeedMap bundlingMap = null;
	
	/**
	 *	This is added to the standard fontsize
	 */
	private float fontSizeIncrease = 0;
	
	/**
	 * The List of Swing components which will eventually be displayed.
	 */
	private ArrayList<JComponent> components;
	
	/**
	 * A list of floats storing the possible pageBreaks.
	 */	
	private ArrayList<Float> pageBreaksFloat;
	
	/**
	 * The Gui Components which can visualise the possible pageBreaks.
	 */
	private ArrayList<PageBreakPanel> pageBreakPanels;
	
	/**
	 * A Panel on a higher Layer, currently for displaying pagebreaks.
	 */
	private JPanel metaPanel;
	
	/**
	 * The Panel which contains the composition.
	 */
	private JPanel contentPanel;
	
	public CompositionPanel (Dimension size, int language, TalBase talBase) {
		super();
		this.language = language;
		this.talBase = talBase;
		init(size);
		
	}
	
	public void init(Dimension size) {
		this.setLayout(null);
		GUI.setAllSizes(this, size);
		
		fontSizeIncrease = 0;
		bundlingDepth = 0;
		
		this.contentPanel = new JPanel(null);
		contentPanel.setOpaque(true);
		this.add(contentPanel);
		this.setLayer(contentPanel, contentLayer);
		Border b = BorderFactory.createLineBorder(Color.black, 1);
		contentPanel.setBorder(b);
		GUI.setAllSizes(contentPanel, size);
		contentPanel.setLocation(0,0);
		
		this.metaPanel = new JPanel(null);
		metaPanel.setOpaque(false);
		this.add(metaPanel);
		this.setLayer(metaPanel, metaLayer);	
		metaPanel.setVisible(false);
		GUI.setAllSizes(metaPanel, size);		
		metaPanel.setLocation(0,0);
		
		this.components = new ArrayList<JComponent>();
		this.pageBreaksFloat = new ArrayList<Float>();
		this.pageBreakPanels = new ArrayList<PageBreakPanel>();
		this.composition = null;
		setVisible(true);
		
		initActions();
		
	}
	
	/**
	 * <p>Initializes the actions that are attached to this composition panel.</p>
	 * <p>Font size increase/decrease, Bundling, language choosing etc.</p>
	 */
	private void initActions() {
		
		ArrayList<AbstractAction> absActions = new ArrayList<AbstractAction>();
		
		decreaseBundling = new DecreaseBundling(this);		
		increaseBundling = new IncreaseBundling(this);
		decreaseFontsize = new DecreaseFontSize(this);
		increaseFontsize = new IncreaseFontSize(this);
		resetFontsize = new ResetFontSize(this);
		
		absActions.add(decreaseBundling); absActions.add(increaseBundling); absActions.add(decreaseFontsize); absActions.add(increaseFontsize);
			
		setLanguage = new SetLanguage[BolName.languagesCount];
		
		for (int i=0; i < BolName.languagesCount; i++) {
			setLanguage[i] = new SetLanguage(this, i);
			absActions.add(setLanguage[i]);
		}
		
		for (AbstractAction action: absActions) {
			action.setEnabled(false);
		}
		
		viewerActions = new ViewerActions(decreaseBundling,increaseBundling,decreaseFontsize,increaseFontsize,resetFontsize,setLanguage);
		
	}
	
	public ViewerActions getViewerActions() {
		return viewerActions;
	}

	/**
	 * <p>Sets each action to enabled or disabled, considering the current state.</p>
	 * <p>For example the font size increase action is disabled when maximum size is already set.</p>
	 */
	private void updateActionEnabling() {
		if (composition != null) {
		decreaseBundling.setEnabled(bundlingDepth > 0);
		increaseBundling.setEnabled(bundlingDepth <  bundlingMap.getMaxDepth());
		
		boolean incrFsPossible = (fontSizeIncrease + Config.fontSizeStep) <= (Config.bolFontSizeMax[language] - Config.bolFontSizeStd[language]);
		boolean decrFsPossible = (fontSizeIncrease - Config.fontSizeStep) >= (Config.bolFontSizeMin[language] - Config.bolFontSizeStd[language]);

		decreaseFontsize.setEnabled(decrFsPossible);
		increaseFontsize.setEnabled(incrFsPossible);
		resetFontsize.setEnabled(fontSizeIncrease != 0);
		
		for (int i=0; i < BolName.languagesCount; i++) {
				setLanguage[i].setEnabled(language!=i);
		}
		}
	}
	
	/**
	 * Generates a JMenu containing MenuItems which use the SetLanguage[LanguageId] Actions of this CompositionPanel.
	 * @return a language menu
	 */
	public JMenu getLanguageMenu() {
		int [] numberKeys = new int [] {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7,KeyEvent.VK_8,KeyEvent.VK_9,KeyEvent.VK_0};
		
		JMenu languageMenu = new JMenu("Language");
		for (int i=0; i < BolName.languagesCount; i++) {
			JMenuItem l = new JMenuItem(setLanguage[i]);
			l.setAccelerator(KeyStroke.getKeyStroke(
			        numberKeys[i], Config.MENU_SHORTKEY_MASK));

			languageMenu.add(l);
			
		}
		return languageMenu;
	}
	

	
	/**
	 * Generates a JMenu containing view-specific MenuItems using Actions of this CompositionPanel,
	 * handling font size and bol bundling.
	 * @return a view menu
	 */
	public JMenu getViewMenu() {
		
		JMenu viewMenu = new JMenu("View");
		JMenuItem incrFonts = new JMenuItem(increaseFontsize);
		viewMenu.add(incrFonts);
		incrFonts.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_I, KeyEvent.ALT_DOWN_MASK | Config.MENU_SHORTKEY_MASK));

		JMenuItem decrFonts = new JMenuItem (decreaseFontsize);
		decrFonts.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_U, KeyEvent.ALT_DOWN_MASK | Config.MENU_SHORTKEY_MASK));
		viewMenu.add(decrFonts);
		
		viewMenu.add(resetFontsize);
		
		viewMenu.addSeparator();
		JMenuItem incrBundling = new JMenuItem(increaseBundling);
		incrBundling.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_I, Config.MENU_SHORTKEY_MASK));			
		viewMenu.add(incrBundling);
		
		JMenuItem decrBundling = new JMenuItem(decreaseBundling);
		decrBundling.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_U, Config.MENU_SHORTKEY_MASK));
		viewMenu.add(decrBundling);
		return viewMenu;
	}
	

	
	public Composition getComposition() {
		return composition;
	}
	
	public void renderComposition(Composition comp) {
		if (comp.getPackets() != null) {
			this.composition = comp;
			this.bundlingMap = Config.getBundlingDepthToSpeedMap(composition.getMaxSpeed());
			setBundlingDepth(Config.stdBundlingDepth);
			setFontSizeInc(Config.stdFontSizeIncrease);
			this.packets = comp.getPackets();
			updateActionEnabling();
			render();			
		} else {
			this.composition = null;
			renderError("The Compositions content packets could not be read");
		}
	}
	
	private void renderError(String errorMessage) {
		this.removeAll();
		SequenceTitlePanel title = new SequenceTitlePanel();
		GUI.setAllSizes(title, new Dimension(this.getSize().width,40));
		title.setTitle(errorMessage);
		this.add(title);
		GUI.setAllSizes(this, title.getPreferredSize());
	}
	
	public void showLineBreaks(boolean show) {
		metaPanel.setVisible(show);
	}
	
	/**
	 * Set the width of the variation panels (the panels showing the bol sequences).
	 * @param width
	 */
	public void setRenderingWidth(int width) {
		renderingWidth = Math.max(50, width);
	}
	
	private void render() {	
		
		int newHeight = 0;
		//Debug.critical(this, "determined renderingwidth: " + this.getParent().getParent().getSize().width);
		
		int width = this.getParent().getParent().getSize().width;
		if (width != 0) {
			setRenderingWidth(width-20);
		} else {
			setRenderingWidth(this.getParent().getParent().getPreferredSize().width-40);	
		}
		
		
		int newWidth = renderingWidth+10;//Math.max(this.getPreferredSize().width, renderingWidth);//this.getPreferredSize().width;
		
		components = new ArrayList<JComponent> ();
		pageBreaksFloat.clear();
		pageBreakPanels.clear();
		
		if (packets != null) {
		Tal tal = Teental.getDefaultTeental();
		for (Packet p : packets) {
			//System.out.println("checking p");
			if (p.getType() == PacketTypeFactory.TAL) {
				//tal = (Tal) p.getObject();
				tal = talBase.getTalFromName((String) p.getObject());
				
				if (tal==null) {
					Debug.temporary(this, "Tal " + p.getObject() + " not found, using teental");
					tal = Teental.getDefaultTeental();
				} else {
					//Debug.temporary(this, "Tal found: " + tal);
				}
				
			}
			if (p.isVisible()) {
				if (p.getType()==PacketTypeFactory.FOOTNOTE) {
					FootnoteText ft = new FootnoteText(p);
					addLineBreak(new Float(newHeight), PageBreakPanel.LOW);
					components.add(ft);
					newHeight += components.get(components.size()-1).getPreferredSize().height;
					
					
					
				} else {
					components.add((JComponent) Box.createRigidArea(new Dimension(10,40)));
					addLineBreak(new Float(newHeight), PageBreakPanel.LOW);
					newHeight += components.get(components.size()-1).getPreferredSize().height;
					
					
				} 
				if ((p.getType()==PacketTypeFactory.COMMENT)) {
					CommentText ct = new CommentText(p);
					addLineBreak(new Float(newHeight), PageBreakPanel.HIGH);
					components.add(ct);
					newHeight += components.get(components.size()-1).getPreferredSize().height;

				} else  if ((p.getType()==PacketTypeFactory.BOLS)) {

					SequenceTitlePanel title = new SequenceTitlePanel(p.getKey());

					addLineBreak(new Float(newHeight), PageBreakPanel.HIGH);
					components.add(title);
					newHeight += components.get(components.size()-1).getPreferredSize().height;
					
					Dimension variationDim = new Dimension(renderingWidth, this.getSize().height);			
					
					
					RepresentableSequence seq = ((RepresentableSequence) p.getObject()).getBundled(bundlingMap,bundlingDepth, true);
					//Debug.temporary(this, "showing seq:" + seq);
					SequencePanel sequencePanel = new SequencePanel(seq, tal, variationDim, 0,"",0, language, Config.bolFontSizeStd[language] + fontSizeIncrease);
					
					addLineBreak(new Float(newHeight), PageBreakPanel.LOW);
					components.add(sequencePanel);
					
					ArrayList<Float> vLineBreaks = sequencePanel.getLineBreaks();
					for ( int j=0; j < vLineBreaks.size(); j++) {
						addLineBreak(vLineBreaks.get(j)+newHeight, PageBreakPanel.LOW);
					}
					
					//}
					newHeight += components.get(components.size()-1).getPreferredSize().height;
					
				}
			}
			
		}
		components.add((JComponent) Box.createRigidArea(new Dimension(10,40)));
		addLineBreak(new Float(newHeight), PageBreakPanel.LOW);
		newHeight += components.get(components.size()-1).getPreferredSize().height;
		
		}
		
		Dimension newSize = new Dimension(newWidth, newHeight);//+components.size()*10);//newHeight+120);//+components.size()*20);
		
		addLineBreak(new Float(newSize.height), PageBreakPanel.LOW);
		
		EventQueue.invokeLater(new Worker(this, components, newSize));

		
	}
	
	
	private void addLineBreak(Float lineBreak, int quality) {
		this.pageBreaksFloat.add(lineBreak);
		PageBreakPanel newPageBreak = new PageBreakPanel(lineBreak,renderingWidth+10, quality);
		pageBreakPanels.add(newPageBreak);

	}
	
	/**
	 * Creates a PDF document
	 */
	public void createPdf(String filename, boolean shapes) {
		if (filename!=null) {
		Insets margins = new Insets(20,10,30,10);
		
		Color backupBg = this.getBackground();
		contentPanel.setBackground(Color.WHITE);
		Border backupBorder = contentPanel.getBorder();
		contentPanel.setBorder(null);
		contentPanel.setOpaque(false);
		
		Debug.temporary(this, "compPanel createPDF started");
		// step 1: creation of a document-object
		Document document = new Document();
		try {
			// step 2:
			// we create a writer
			PdfWriter writer;
			
			if (shapes)
				writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
			else
				writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
			// step 3: we open the document
			document.open();
			// step 4: we add a table to the document
			
			PdfContentByte cb = writer.getDirectContent();
			
			Rectangle pageSize = writer.getPageSize();
			
			
			Dimension cSize = this.getPreferredSize();//new Dimension(compositionPanel.getPreferredSize().width, compositionPanel.getPreferredSize().height);
			Debug.debug(this, "Page size " + pageSize);
			Debug.debug(this, "compPanel size " + cSize);
			
			float pageWidth =  pageSize.getWidth() -margins.left - margins.right;
			float pageHeight =  pageSize.getHeight() -margins.top - margins.bottom;

			Debug.temporary(this, "starting to print");
			
			float scalingFactor = pageWidth /(float)getPreferredSize().width;
			
			float spaceLeft = scalingFactor * (float) this.getPreferredSize().height;
			Debug.temporary(this, "space left: " +spaceLeft);
			
			float pageNr = 1;
			
			float lastPositionOnPage = 0;
			
			int counter = 0;
			
			float exactness = 3;
			
			while (spaceLeft>exactness && counter < 100) {
				counter ++;
				
				float previousLastPos = lastPositionOnPage;
			    lastPositionOnPage += pageHeight;
			    
			    //TODO clean up this mess need clear seperation of pagebreaksfloat and gui
				//find closest lineBreak (max deviation 50)
				int i;
				for (i=0; i < pageBreaksFloat.size()-1; i++) {
					if (lastPositionOnPage < pageBreaksFloat.get(i+1)*scalingFactor) {
						Debug.temporary(this, "found linebreak nr "+i+ " : " + pageBreaksFloat.get(i)*scalingFactor);
						if (pageBreakPanels.get(i).quality == PageBreakPanel.LOW) {
							Debug.temporary(this, "backtracing to see if better linebreak is close");
							float yOriginal = pageBreaksFloat.get(i);
							float searchRadius = 100; // in pixels
							
							for (int j=i-1; j > 0; j--) {
								
								if ((yOriginal - pageBreaksFloat.get(j).floatValue() ) < searchRadius) {
									if (pageBreakPanels.get(j).quality > pageBreakPanels.get(i).quality) {
										i = j;
										Debug.temporary(this, "backtracing found better");
										break;
									}
								} else break;
							}
						}
						
						break;
					}
				}
				
				
				if (i>0 && i<pageBreaksFloat.size()-1) {
					//set to lineBreak.
					lastPositionOnPage = (float) Math.floor(pageBreaksFloat.get(i)*scalingFactor);
				} else if (i==pageBreaksFloat.size()-1) {
					//end of contentPanel
					lastPositionOnPage = (float) Math.floor(pageBreaksFloat.get(pageBreaksFloat.size()-1)*scalingFactor);
				}
				float coveredByThisPage = lastPositionOnPage - previousLastPos;//pageHeight - (previousLasPos+pageHeight - lastPositionOnPage);
				
				
				PdfTemplate template = cb.createTemplate(pageWidth, lastPositionOnPage);//pageHeight*pageNr);
				
				Graphics2D g;
				if (language==BolName.DEVANAGERI) {
					g = template.createGraphicsShapes(pageWidth, lastPositionOnPage);//pageHeight*pageNr);
				} else {
					g = template.createGraphics(pageWidth, lastPositionOnPage);//pageHeight*pageNr);
				}
					
				g.scale(scalingFactor, scalingFactor);
				contentPanel.print(g);
				g.clearRect(0, 0, (int) Math.ceil(pageWidth/scalingFactor), (int)Math.floor(previousLastPos/scalingFactor));

				g.dispose();
				
				cb.addTemplate(template, margins.left, margins.bottom + pageHeight - coveredByThisPage);
				
				document.newPage();
				spaceLeft -= coveredByThisPage;
				Debug.temporary(this, "space left: " +spaceLeft);
				pageNr++;
			}
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
 
		// step 5: we close the document
		document.close();
		
		contentPanel.setBackground(backupBg);
		contentPanel.setOpaque(true);
		contentPanel.setBorder(backupBorder);
		}
	}
	private class Worker implements Runnable {
		
		private Dimension size;
		private CompositionPanel comp;
		private ArrayList<JComponent> components;
		
		public Worker(CompositionPanel comp, ArrayList<JComponent> components, Dimension size) {
			this.size = size;
			this.comp = comp;
			this.components = components;
		}
		
		public void run() {
			contentPanel.removeAll();
			metaPanel.removeAll();
			
			int y =0;
			for (int i=0; i < components.size(); i++) {
				//((JComponent) components.get(i)).setAlignmentX(Component.LEFT_ALIGNMENT);
				JComponent c = components.get(i);		
				contentPanel.add(c);
				
				c.setBounds((renderingWidth - c.getPreferredSize().width)/2,y, c.getPreferredSize().width, c.getPreferredSize().height);
				y+=c.getPreferredSize().height;
				c.revalidate();
				
			}
			
			for (int i= 0; i< pageBreakPanels.size();i++) {
				metaPanel.add(pageBreakPanels.get(i));
			}
			
			GUI.setAllSizes(metaPanel, size);
			GUI.setAllSizes(contentPanel, size);
			GUI.setAllSizes(comp, size);
			
			//causes the right refreshing for some reason
			comp.setBorder(new LineBorder(Color.red,1));
			
			
			
		}
	}
	
	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		if (language != this.language) {
			Debug.debug(this, "set Language to " + BolName.languageNames[language]);
			this.language = language;
			
			setFontSizeInc(fontSizeIncrease);
			updateActionEnabling();
			render();
		} else Debug.debug(this, "Language is already " + BolName.languageNames[language]);
	}

	public void increaseBundling() {
		int oldBundlingDepth = bundlingDepth;
		setBundlingDepth(bundlingDepth+1);
		Config.setStandardBundlingDepth(bundlingDepth);
		if (oldBundlingDepth != bundlingDepth) render();
		updateActionEnabling();
	}
	
	public void decreaseBundling() {
		int oldBundlingDepth = bundlingDepth;
		setBundlingDepth(bundlingDepth-1);
		Config.setStandardBundlingDepth(bundlingDepth);
		if (oldBundlingDepth != bundlingDepth) render();
		updateActionEnabling();
	}
	
	private void setBundlingDepth(int newDepth) {
		Debug.temporary(this, "attempted to set bundling depth to : " + newDepth);
		bundlingDepth = Math.max(0,Math.min(newDepth, bundlingMap.getMaxDepth()));
		
		Debug.temporary(this, "bundlingMap: " + bundlingMap);
		Debug.temporary(this, "resulting bundling depth: " + bundlingDepth);
		Debug.temporary(this, "resulting bundling speed: " + bundlingMap.getBundlingSpeed(bundlingDepth));
	}
	
	public void increaseFontSize() {
		setFontSizeInc(fontSizeIncrease+Config.fontSizeStep);
		Config.setStandardFontSizeIncrease(fontSizeIncrease);
		updateActionEnabling();
		render();
	}
	
	public void decreaseFontSize() {
		setFontSizeInc(fontSizeIncrease-Config.fontSizeStep);
		Config.setStandardFontSizeIncrease(fontSizeIncrease);
		updateActionEnabling();
		render();
	}
	private void setFontSizeInc(float newOffset) {
		
		float maxIncrease = Config.bolFontSizeMax[language] - Config.bolFontSizeStd[language];
		
		float minIncrease = Config.bolFontSizeMin[language] - Config.bolFontSizeStd[language];
//		Debug.temporary(this, "trying to set to: " + newOffset);
//		Debug.temporary(this, "min: " + minIncrease);
//		Debug.temporary(this, "max: " + maxIncrease);
		fontSizeIncrease = (float) Math.max(Math.min(newOffset,maxIncrease),minIncrease);
		
//		Debug.temporary(this, "eventually set to: " + fontSizeIncrease);

	}
	public void resetFontSize() {
		fontSizeIncrease = 0;
		updateActionEnabling();
		render();
	}
	
	

	

}
