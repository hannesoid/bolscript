package bolscript;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitResponse;
import java.io.File;

public class MasterMac extends Master {

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        initRunparametersCommandLineArguments(args);
        master = new MasterMac();
        master.init();
    }

    public MasterMac() { runningAsMacApplication = true; }

    @Override
    public void init() {
        super.init();
        Desktop desktop = Desktop.getDesktop();
        desktop.setAboutHandler(e ->
                JOptionPane.showMessageDialog(null, "About dialog")
        );
        desktop.setPreferencesHandler(e ->
                this.showPreferences()
        );
        desktop.setQuitHandler((QuitEvent e, QuitResponse r) -> {
                boolean safelyQuit = this.prepareExit();
                if (safelyQuit) {
                    r.performQuit();
                } else {
                    r.cancelQuit();
                }
            }
        );
    }

    @Override
    public void revealFileInOSFileManager(String filename) {
        Desktop.getDesktop().browseFileDirectory(new File(filename));
    }
}