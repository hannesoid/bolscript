package bolscript.config;

import java.io.File;
import java.util.prefs.Preferences;

import basics.Debug;
import basics.Tools;
import bols.BolName;

public class UserConfig {

	public static String tablaFolder = null;
	
	public static String pdfExportPath = null;
	
	private static UserConfig standard = null;
	
	/**
	 * This is the standard setting for fontsizeIncrease
	 * 0 means that there is no increase, so the standard value bolFontSizeStd is taken
	 */
	public static float stdFontSizeIncrease = 0f;
	
	/**
	 * The standard setting for bundling. Initially it is 0, meaning, there is no bundling active.
	 * This can be changed during the running of the program.
	 */
	public static int stdBundlingDepth = 0;

	public static int standardLanguage = BolName.SIMPLE;

	public static Preferences preferences;

	public static boolean firstRun = true;
	
	public UserConfig() {
	}
	
	public static UserConfig getCurrentUser() {
		if (standard == null) {
			standard = new UserConfig();
		}
		return standard;
	}

	/**
	 * Stores the part of config, that is kept in preferences.
	 * @throws Exception
	 */
	public static void storePreferences() throws Exception{
		if (tablaFolder != null) {
			UserConfig.preferences.put("tablaFolder", tablaFolder);
		}
		if (pdfExportPath != null) {
			UserConfig.preferences.put("pdfExportPath",pdfExportPath);
		}
		
		preferences.putInt("stdBundlingDepth", stdBundlingDepth);
		preferences.putFloat("stdFontSizeIncrease", stdFontSizeIncrease);
		preferences.putInt("standardLanguage", standardLanguage);
		
		//Debug.debug(Config.class, "storing properties under : " + new File(propertiesFilename).getAbsoluteFile());
		try {
			UserConfig.preferences.flush();
			Debug.debug(Config.class, "Preferences stored");
		} catch (Exception e) {
			Debug.critical(Config.class, "Preferences could not be stored: " + e + ", " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Most importantly attempts to load the String tablaFolder from the Properties file,
	 * then calling setTablaFolder.
	 */
	static boolean initFromStoredPreferences() {
		UserConfig.preferences = Preferences.userNodeForPackage(Config.class);
	
		boolean failed = false;
		try {
	
			tablaFolder = UserConfig.preferences.get("tablaFolder", null);
	
			if (tablaFolder == null) {
				UserConfig.firstRun = true;
				tablaFolder = Config.homeDir;
				pdfExportPath = null;
			} else {
				UserConfig.setTablaFolder(tablaFolder);
				UserConfig.firstRun = false;
			}
			pdfExportPath = preferences.get("pdfExportPath", tablaFolder);
	
			stdBundlingDepth 	= Tools.assure(0, preferences.getInt("stdBundlingDepth", 0), 4);
			stdFontSizeIncrease = preferences.getFloat("stdFontSizeIncrease", 0);
			standardLanguage 	= Tools.assure(0, 
					preferences.getInt("standardLanguage", BolName.SIMPLE), 
					BolName.languagesCount);
			
		} catch (Exception e) {
			Debug.critical(Config.class, "Preferences could not be loaded " + e);
			failed = false;
		}
	
		return failed;
	}

	public static void setPdfExportPath(String folder) {
		pdfExportPath = folder;
	}

	/**
	 * Sets and initialises the tablaFolder and the resulting subfolders settings compositions fonts etc,
	 * checking each for existence and creating the nonexistent.
	 * Especially pathToDevanageriFont is established.
	 * 
	 * @param chosenFolder
	 */
	public static void setTablaFolder(String chosenFolder) {
		Debug.temporary(Config.class, "setTablaFolder : " + chosenFolder + " (old: " + tablaFolder + ")");
		//if (!tablaFolder.equals(chosenFolder)) {
		tablaFolder = chosenFolder;
		preferences.put("tablaFolder", tablaFolder);
	
	
		File s = new File(chosenFolder + Config.fileSeperator + "settings");
		if (!s.exists()) {
			s.mkdir();
		} 
	
		File c = new File(chosenFolder + Config.fileSeperator + "compositions");
		if (!c.exists()) { 
			c.mkdir();
		}
	
		/*pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
			File fontFolder = new File(pathToFonts);
			if (!fontFolder.exists()) {
				fontFolder.mkdir();
			}*/
	
		Config.pathToBolBase = s.getAbsolutePath() + Config.fileSeperator + Config.bolBaseFilename;
		//Debug.temporary(Config.class, pathToBolBase);
	
		Config.pathToCompositionsNoSlash = c.getAbsolutePath();
		//Debug.temporary(Config.class, pathToCompositionsNoSlash);
		Config.pathToCompositions = Config.pathToCompositionsNoSlash + Config.fileSeperator;
		Debug.temporary(Config.class, Config.pathToCompositions);
		Config.pathToTalsNoSlash = c.getAbsolutePath() + Config.fileSeperator + "tals";
		//Debug.temporary(Config.class, pathToTalsNoSlash);
		Config.pathToTals = Config.pathToTalsNoSlash + Config.fileSeperator;
		//pathToFonts = s.getAbsolutePath() + fileSeperator + "fonts";
		//Debug.temporary(Config.class, pathToTals);
		Config.pathToDevanageriFont = s.getAbsolutePath() + Config.fileSeperator + "devanageri.ttf";
	
		String[] requiredFiles = new String[]{Config.pathToBolBase, Config.pathToDevanageriFont};
		boolean allEssentialsExist = true;
		for (int i=0; i < requiredFiles.length; i++ ) {
			allEssentialsExist = allEssentialsExist & (new File(requiredFiles[i])).exists();
			if (!allEssentialsExist) break;
		}
		if (!allEssentialsExist) {
			//defaults are copied non-destructively from the jar archives resources.zip
			try {
				Config.extractDefaultTablafolder(chosenFolder);
			} catch (Exception e) {
				Debug.critical(Config.class, "Default tabla folder could not be extracted.");
				Debug.critical(Config.class, e);
			}
		}
	
		//	}
	
	}

	public static void setStandardFontSizeIncrease(float fontsizeIncrease) {
		UserConfig.stdFontSizeIncrease = fontsizeIncrease;
	}

	public static void setStandardBundlingDepth(int bundlingDepth) {
		UserConfig.stdBundlingDepth = bundlingDepth;
	}

}