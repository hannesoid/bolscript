package bols;

import static bolscript.sequences.Representable.BOL_CANDIDATE;
import static bolscript.sequences.Representable.SPEED;
import static bolscript.sequences.Representable.WHITESPACES;
import gui.bolscript.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.tools.internal.xjc.Language;

import basics.Debug;
import basics.FileManager;
import basics.FileReadException;
import basics.Tools;
import bolscript.config.Config;
import bolscript.scanner.Parser;
import bolscript.scanner.SequenceScannerPublic;
import bolscript.scanner.SequenceToken;
import bolscript.sequences.Representable;


public class BolBase extends BolBaseGeneral {

	/**
	 * An auxilliary private class for storig prototype properties of a
	 * potential bundle during parsing a bolbase file.
	 * @author hannes
	 */
	private class PotentialBundle {
		String[] labels;
		String description;
		ArrayList<SequenceToken> bolNameStrings;
	}

	private class PotentialCombinedBolName {

		public PotentialCombinedBolName(BolName bolName,
				String[] bolNamesToBeCombined) {
			super();
			this.bolName = bolName;
			this.bolNamesToBeCombined = bolNamesToBeCombined;
		}
		BolName bolName;
		String[] bolNamesToBeCombined;
	}

	private static BolBase standard = null;
	public static boolean standardInitialised = false;

	public static void init(Class caller) {
		try {
			Debug.debug(BolBase.class, "init called by: " + caller.getSimpleName());
			setStandard(new BolBase());
			standardInitialised = true;
		} catch (Exception e) {
			Debug.critical(BolBase.class, "Error initialising BolBase!");
			System.out.println(e);
			e.printStackTrace();
			System.exit(1);
		}	
	}

	public static void initOnce(Class caller) {
		if (!standardInitialised)
			init(caller);
	}

	public static BolBaseGeneral standard() {
		return getStandard();
	}

	public static void setStandard(BolBase standard) {
		BolBase.standard = standard;
	}
	public static BolBase getStandard() {
		if (standard == null) init(BolBase.class);
		return standard;
	}
	public BolBase() {
		super();
		generalNoteOffset = 36;

		Task addFromFilesTask = new Task("Load bolbase from file", Task.ExecutionThread.AnyThread){

			@Override
			public void doTask() throws TaskException {
				try {
					addBolNamesFromFile(Config.pathToBolBase);
				} catch (FileReadException e) {
					throw new TaskException("BolBase could not be loaded", e);
				}
			}				
		};

		addFromFilesTask.run();

		if (addFromFilesTask.getState() == Task.State.CompletedWithError) {
			Debug.critical(this, "BolBase loading did not work: " + addFromFilesTask.getException() + ": ");
			addFromFilesTask.getException().printStackTrace();

			Debug.critical(this, "BolBase could not read bolbase file. Using defaults. ");
			addBolNames("Dha Ge Ti Ri Ke Te Tin Na Ne Ta Ke Dhin Ta3 Tun Dhun Ge2");
		} else {
			Debug.temporary(this, "bolbase loading took: " + addFromFilesTask.getDuration() + "ms");
		}
		Debug.temporary(this, "bols inited: " + Tools.toString(bolNames));
		Debug.temporary(this, "bundles : " + bundleMap);


		try {
			initMidiMaps();
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Midi maps could not be initialised. " + ex);
		}
		try{
			initBolMaps();
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Bol maps could not be initialised. " + ex);
		}

		try {
			setEmptyBol("-");
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Empty Bol '-' could not be set. " + ex);
		}

		try {
			initKaliMaps();
		} catch (NoBolNameException ex) {
			Debug.critical(this, "Kali maps could not be initialised. " + ex);
		}


	}
	/**
	 * Seperates the parts of an entry in the bolbase
	 */
	private static String bolbaseDefinitionSeperator = "\\s*;\\s*";
	
	/**
	 * Matches something like 'BolName1 + BolName2', where two groups are matches 1: BolName1 and 2: BolName2 
	 */
	private static Pattern combinedPattern = Pattern.compile(Parser.BOLNAME_GROUP+"\\s*\\+\\s*"+Parser.BOLNAME_GROUP);
	
	/**
	 * Matches a bundle. Basically the same matching a bolname.
	 */
	private static Pattern bundlePattern = Pattern.compile(Parser.BOLNAME_GROUP);
	
	private void addBolNamesFromFile(String filename) throws FileReadException {
		HashMap<String, Integer> handMap = new HashMap<String, Integer> ();
		handMap.put("LEFT", BolName.LEFT);
		handMap.put("RIGHT", BolName.RIGHT);
		handMap.put("OTHER", BolName.OTHER);
		handMap.put("COMBINED", BolName.COMBINED);

		ArrayList<PotentialCombinedBolName> potentialCombined = new ArrayList<PotentialCombinedBolName>();
		ArrayList<PotentialBundle> potentialBundles = new ArrayList<PotentialBundle>();

		Scanner scanner = new Scanner(FileManager.getContents(new File(filename), Config.bolBaseEncoding));
	
		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			//Debug.temporary(this.getClass(),line);
			if (!line.startsWith("#")) {
				//String line = scanner.nextLine();
				String [] entries = line.split(bolbaseDefinitionSeperator);
				Debug.temporary(getClass(), Tools.toString(entries));
				String description = "";

				if (entries.length >= BolName.languagesCount) {
					String[] labels = new String[BolName.languagesCount];
					for (int i = 0; i < labels.length; i++) {

						//labels[i] = BolName.formatString(entries[i].replaceAll(Parser.SNatBeginningOrEnd, ""), i);
						labels[i] = entries[i].replaceAll(Parser.SNatBeginningOrEnd, "");
						if (labels[i].equals("Sa")) {
							String blub = labels[i];
							blub = blub + "";
						}

					}
					//Debug.temporary(this, "exact name scanned: '" + labels[BolName.EXACT]+"'");

					if (entries.length >= BolName.languagesCount+1) {
						description = entries[BolName.languagesCount].replaceAll(Parser.SNatBeginningOrEnd,"");

						if (entries.length >= BolName.languagesCount+2) {
							String typeField = entries[BolName.languagesCount+1].replaceAll(Parser.SNatBeginningOrEnd,"");
							Integer handType = handMap.get(typeField.toUpperCase());

							if (handType != null) {
								//this is a bol with a known hand type
								BolName newBolName = new BolName(labels);
								newBolName.setDescription(description);
								newBolName.setHandType(handType);
								addBolName(newBolName);
							} else {
								//check if it is combined bol or a bundle

								//	Debug.temporary(this, newBolName + " looking for combined bols");
								
								Matcher combinedMatcher = combinedPattern.matcher(typeField);

								if (combinedMatcher.find()) {
									//found a combined Bol
									BolName newBolName = new BolName(labels);
									newBolName.setDescription(description);
									newBolName.setHandType(BolName.COMBINED);
									addBolName(newBolName);
									//	Debug.temporary(this,newBolName + " found hand: " + m.group(1));
									//	Debug.temporary(this,newBolName + " found hand: " + m.group(2));
									String hand1 = combinedMatcher.group(1);
									String hand2 = combinedMatcher.group(2);
									if (hand1 != null && hand2 != null) {
										potentialCombined.add(
												new PotentialCombinedBolName(
														newBolName,
														new String[]{hand1, hand2}));
									}
								} else {
									//check if it is a bundle
									PotentialBundle potentialBundle = new PotentialBundle();
									potentialBundle.labels = entries;
									potentialBundle.description = description;
									
									potentialBundle.bolNameStrings = parseBundleUnits(typeField);

									potentialBundles.add(potentialBundle);
								}

							}
						}
					}

				}
			}


		}

		//process combined bols
		for (int i = 0; i < potentialCombined.size(); i++) {
			PotentialCombinedBolName comb = potentialCombined.get(i);
			//			Debug.temporary(this, "searching for " + bolCombinations.get(i)[0]);
			BolName hand1 = getBolName(comb.bolNamesToBeCombined[0]);
			//			Debug.temporary(this, "found: " + hand1);
			//			Debug.temporary(this, "searching for " + bolCombinations.get(i)[1]);
			BolName hand2 = getBolName(comb.bolNamesToBeCombined[1]);
			//			Debug.temporary(this, "found: " + hand2);
			if ((hand1 != null) && (hand2 != null)) {
				//Debug.temporary(this,combinedBols.get(i) + " adding hand: " + hand1);
				//Debug.temporary(this,combinedBols.get(i) + " adding hand: " + hand2);
				comb.bolName.setHands(hand1, hand2);
			} else {
				comb.bolName.setHandType(BolName.UNKNOWN);
			}
		}

		//process bundles
		for (int i = 0; i < potentialBundles.size(); i++) {
			PotentialBundle potBundle = potentialBundles.get(i);
			//potentialBundleLabels.add(names);
			//potentialBundleDescriptions.add(description);
			//potentialBundles.add(currentBundle);
			
			if (potBundle.bolNameStrings.size() > 0) {
			boolean allUsedBolnamesExist = true;
			ArrayList<BolName> currentBundlesBolNames = new ArrayList<BolName>();
			
			ArrayList<String> bundleUnits = new ArrayList<String>();
			StringBuilder builder = new StringBuilder();
			int b = 0;
			for (SequenceToken token : potBundle.bolNameStrings) {
				boolean failed = false;
				builder.append(" ");
				if (token.type == BOL_CANDIDATE) {
					BolName bn = getBolName(token.text);
					if (bn == null) {
						allUsedBolnamesExist = false;
						break;
					} else {
						currentBundlesBolNames.add(bn);						
					}
					builder.append(bn.getName(BolName.EXACT));
				} else {
					builder.append(token.text);
				}
			} 
			if (allUsedBolnamesExist) {
				BolName[] currentArray = new BolName[currentBundlesBolNames.size()]; 
				currentArray = currentBundlesBolNames.toArray(currentArray);
				BolNameBundle bundle = new BolNameBundle(currentArray, potBundle.labels);
				bundle.setDescription(potBundle.description);

				standardBolNameBundles.add(bundle);
				//addBolNameBundle(bundle);
				//Debug.temporary(this, "setting replacement bundle name to '" + bundle.getName(BolName.EXACT).replaceAll(Reader.SNatBeginningOrEnd,"") +"'");
				addReplacementPacket(bundle.getName(BolName.EXACT), 
						" (" + builder.toString() + " ) ", bundle);
			}
		}
	
			/*if (potBundle.bolNameStrings.size() > 1) {

				Rational bundleSpeed = new Rational(1);
				if (potBundle.bolNameStrings.get(0).matches("[0-9]+")) {
					bundleSpeed = Rational.parseNonNegRational(potBundle.bolNameStrings.get(0));
				}
				boolean failed = false;
				ArrayList<BolName> currentBundlesBolNames = new ArrayList<BolName>();
				for (int j = 1; j < potBundle.bolNameStrings.size(); j++) {
					BolName bn = getBolName(potBundle.bolNameStrings.get(j));
					if (bn == null) {
						failed = true;
						break;
					} else {
						currentBundlesBolNames.add(bn);
					}
				}
				if (!failed) {
					BolName[] currentArray = new BolName[currentBundlesBolNames.size()]; 
					currentArray = currentBundlesBolNames.toArray(currentArray);
					BolNameBundle bundle = new BolNameBundle(currentArray, potBundle.labels);
					bundle.setDescription(potBundle.description);

					standardBolNameBundles.add(bundle);
					//addBolNameBundle(bundle);
					//Debug.temporary(this, "setting replacement bundle name to '" + bundle.getName(BolName.EXACT).replaceAll(Reader.SNatBeginningOrEnd,"") +"'");
					addReplacementPacket(bundle.getName(BolName.EXACT), 
							" ( " + bundleSpeed + "! " + bundle.getExactBolNames() + " ) ", bundle);
				}
			}*/
		}
		
		// Assign case sensitivities...
		
		
	}

	public ArrayList<SequenceToken> parseBundleUnits(String input) {
		ArrayList<SequenceToken> units = new ArrayList<SequenceToken>();
	
		SequenceScannerPublic scanner = new SequenceScannerPublic(input);
		
		int assumedTokenStartPosition = 0;		
		SequenceToken token = null;
		try {
			token = scanner.nextToken();
		} catch (IOException e) {
			Debug.critical(this, "scanner threw an exception!");
			e.printStackTrace();
			return null;
		}
		
		while (token != null) {
			if (token.textReference.start() > assumedTokenStartPosition) {
				SequenceToken failedToken = new SequenceToken(Representable.FAILED, 
						input.substring(assumedTokenStartPosition, 
										token.textReference.start()), 
										assumedTokenStartPosition, 
										token.textReference.line());				
			} 
			assumedTokenStartPosition = token.textReference.end();
			
			switch (token.type) {
			case BOL_CANDIDATE:
				units.add(token);
				break;

			case SPEED:
				units.add(token);
				break;
				
			default:
			}

			try {
				token = scanner.nextToken();
			} catch (IOException e) {
				Debug.critical(this, "scanner threw an exception!");
				e.printStackTrace();
				return null;
			}
		}
		return units;
	}

	/**
	 * BolMaps are maps from one Bol to a Midinote, coordinates and a hand
	 * @throws Exception
	 */
	private void initMidiMaps() throws NoBolNameException {

		// Parameters: name, coordinate, midinote, hand

		//pause
		addMidiMap("-",  0, 0, MidiMap.NONE);

		//righthand only
		addMidiMap("Ti", 1, 3, MidiMap.RIGHT);
		addMidiMap("Ta", 1, 3, MidiMap.RIGHT);
		addMidiMap("Ri", 1, 4, MidiMap.RIGHT);
		addMidiMap("Te", 1, 5, MidiMap.RIGHT);
		addMidiMap("Ne", 1, 6, MidiMap.RIGHT);

		addMidiMap("Ta3", 2, 1, MidiMap.RIGHT);
		addMidiMap("Tun", 2, 2, MidiMap.RIGHT);
		addMidiMap("Na", 3, 0, MidiMap.RIGHT);

		//addMidiMap("Ta", 3, 0, MidiMap.RIGHT);


		//lefthand only
		addMidiMap("Ke", 1, 31, MidiMap.LEFT);
		addMidiMap("Ge", 2, 28, MidiMap.LEFT);
		addMidiMap("Ge2",2,29, MidiMap.LEFT);

		Debug.debug(this, "midimaps set: " + Tools.toString(midiMaps));

	}

	/**
	 * BolMaps are maps from one Bol to multiple Midimaps
	 * @throws Exception
	 */
	private void initBolMaps() throws NoBolNameException {

		// Parameters: Bol name, left hand, right hand
		addBolMap("-", getMidiMap("-"), getMidiMap("-"));

		//one hand bols
		addBolMap("Ti", getMidiMap("-"), getMidiMap("Ti"));
		addBolMap("Ri", getMidiMap("-"), getMidiMap("Ri"));
		addBolMap("Ke", getMidiMap("Ke"), getMidiMap("-"));
		addBolMap("Te", getMidiMap("-"), getMidiMap("Te"));
		addBolMap("Ne", getMidiMap("-"), getMidiMap("Ne"));
		addBolMap("Ta", getMidiMap("-"), getMidiMap("Ta"));
		addBolMap("Na", getMidiMap("-"), getMidiMap("Na"));
		addBolMap("Ge", getMidiMap("Ge"), getMidiMap("-"));

		//two hand bols
		addBolMap("Dha", getMidiMap("Ge"), getMidiMap("Na"));
		addBolMap("Tin", getMidiMap("Ke"), getMidiMap("Ta3"));
		addBolMap("Dhin", getMidiMap("Ge"), getMidiMap("Ta3"));
		addBolMap("Tun", getMidiMap("Ke"), getMidiMap("Tun"));
		addBolMap("Dhun", getMidiMap("Ge"), getMidiMap("Tun"));		
		addBolMap("Ta3", getMidiMap("-"), getMidiMap("Ta3"));

		Debug.debug(this, "bolsMaps set: " + Tools.toString(bolMaps));
	}

	public void initKaliMaps() throws NoBolNameException {
		//auto-add KaliMaps where left hand is empty or nonresonant
		for (BolName bolName : getBolNames()) {
			try {
				BolMap bm = getBolMap(bolName);
				if ((bm.getLeftHand().getBolName() == emptyBol)||(bm.getLeftHand().getCoordinate()==1)) {
					//left hand is empty or nonresonant, keep in kali
					addKaliMap(bolName, bolName);
				} 	
			} catch (Exception e) {
				Debug.debug(this, "not adding kaliMap, no BolMap for " + bolName);
			}

		}	
		//add remaining
		addKaliMap("Ge", "Ke");
		addKaliMap("Dha", "Na");
		addKaliMap("Dhin", "Tin");
	}

	public BolName getResemblingBol(String input) {

		Debug.temporary(this, "checking out " + input);

		char[] inputChars = input.toCharArray();

		//int[] resemblence = new int[bolNames.size()];
		int maxResemblence = -1;
		int bestBol = 0;

		for (int i=0; i<bolNames.size();i++) {
			BolName bolName = bolNames.get(i);
			if (!bolName.isWellDefinedInBolBase()) continue;
			char[] bolNameChars = bolNames.get(i).getName(BolName.EXACT).toCharArray();
		
			//Debug.temporary(this, "comparing " + input.toLowerCase() + " to " + bolNames.get(i).getName(BolName.EXACT).toLowerCase());

			for (int j = 0 ;j < Math.min(inputChars.length,bolNameChars.length);j++) {
				//Debug.temporary(this,"comparing " + inputChars[j] + " to " + exact[j]); 
				char a = inputChars[j];
				char b = bolNameChars[j];
				
				boolean currentCharacterMatches = false;
				switch (bolName.getCaseSensitivityMode()) {
					case None:
						currentCharacterMatches = Character.toLowerCase(a) == Character.toLowerCase(b);
						break;
					case FirstLetter:						
						if (j > 0) {
							currentCharacterMatches = Character.toLowerCase(a) == Character.toLowerCase(b);
						} else currentCharacterMatches = a==b;
						break;
					case ExactMatch:
						currentCharacterMatches = a==b;
						break;
				}
				if (currentCharacterMatches) {
					//Debug.temporary(this,"match");	
					if (j > maxResemblence) {
						bestBol = i;
						//Debug.temporary(this, " setting best bol to " + bolNames.get(i).getName(BolName.EXACT));
						maxResemblence = j;
						//Debug.temporary(this, " setting maximum resemblence to " + maxResemblence);
					}
				} else break;
			}

			if (bestBol == i && maxResemblence == (inputChars.length-1)) {
				break;
			}
		}	
		if (maxResemblence == -1) {
			return null;
		} else {
			Debug.temporary(this, "chose best bol: " + bolNames.get(bestBol) + ", with resemblence " +maxResemblence );
			return bolNames.get(bestBol); 
		}
	}
}
