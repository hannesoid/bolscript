package bolscript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import basics.Debug;
import bolscript.config.GuiConfig;
import java.awt.desktop.*;
/*
import com.apple.eawt.Application;
import com.apple.eawt.AppEventListener;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.QuitStrategy;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;*/

// https://developer.apple.com/library/archive/technotes/tn2007/tn2196.html

/*
public class MasterMac extends Master {
	
	@Override
	public void revealFileInOSFileManager(String filename) {
		    String script = 
		    	"tell application \"Finder\" \n" +
		    		"reveal (POSIX file \"/" + filename + "\") \n" +
		    		"activate \n"+
		    	"end tell";
		    
		    ScriptEngineManager mgr = new ScriptEngineManager();
		    ScriptEngine engine = mgr.getEngineByName("AppleScript");

		    try {
				engine.eval(script);
			} catch (ScriptException e) {
				Debug.critical(this, "AppleScript for revealing file could not be run");
				e.printStackTrace();
			}
	}

	public static Application application;
	
	public MasterMac() {
		super();
		runningAsMacApplication = true;
	}
	
	@Override
	public void init() {
		super.init();
		application = Application.getApplication();
		application = Application.getApplication();
		application.setPreferencesHandler(this);
		application.setQuitHandler(this);
		application.setQuitStrategy(QuitStrategy.SYSTEM_EXIT_0);
		application.addPreferencesMenuItem();
		application.setEnabledPreferencesMenu(true);
		application.addApplicationListener(this);
	}
	
	public static void main(String [] args) {	
		System.setProperty("apple.laf.useScreenMenuBar", "true");		
		initRunparametersCommandLineArguments(args);
		master = new MasterMac();
		master.init();
	}
	
	@Override
	public void showPreferences() {
		super.showPreferences();
		GuiConfig.setVisibleAndAdaptFrameLocation(browserFrame);
	}

	@Override
	public void revealFileInOSFileManager(String filename) {
		String script =
				"tell application \"Finder\" \n" +
						"reveal (POSIX file \"/" + filename + "\") \n" +
						"activate \n"+
						"end tell";

		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("AppleScript");

		try {
			engine.eval(script);
		} catch (ScriptException e) {
			Debug.critical(this, "AppleScript for revealing file could not be run");
			e.printStackTrace();
		}
	}


	@Override
	public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
		boolean safelyQuit = this.prepareExit();
		if (safelyQuit) {
			arg1.performQuit();
		} else {
			arg1.cancelQuit();
		}
	}
}


*/