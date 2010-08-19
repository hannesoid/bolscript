package bolscript;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolscript.config.Config;
import bolscript.config.VersionInfo;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


public class UpdateManager {

	public void CheckForUpdates() {
		VersionInfo currentVersionInfo;
		try {
			currentVersionInfo = Config.getVersionInfoFromJar(Config.getJarPath());		
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		ArrayList<Download> downloads = new ArrayList<Download>();
		URL url = null;
		try {
			url = new URL("http://code.google.com/feeds/p/bolscript/downloads/basic");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			
			SyndFeedInput input = new SyndFeedInput();
            try {
				SyndFeed feed = input.build(new XmlReader(url));
				List entries = feed.getEntries();
				// Iterate through feed items, adding a footer each item
			    Iterator entryIter = entries.iterator();
			    while (entryIter.hasNext())
			    {
			        SyndEntry entry = (SyndEntry) entryIter.next();
			        downloads.add(parseDownload(entry));
			        
			    }

			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (FeedException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
			//sort downloads by buildnr
			//remove downloads with wrong opsys
			
		}



	}

	private Download parseDownload(SyndEntry entry) {
		String link = entry.getLink();
        String title = entry.getTitle();
        
        Download download = new Download();			        
        download.setDetailsLink(link);
        download.setTitle(title);
        download.setDate(entry.getUpdatedDate());
        
        String contents = ((SyndContent) entry.getContents().get(0)).getValue();
        
        String versionNrAndBuildRegexString = "(\\d\\.\\d+)(?:\\s|build|Build|b|\\.)+(\\d+)";
        int versionNrGroupIndex=1;
        int buildNrGroupIndex=2;
        Pattern versionNrAndBuildRegex = Pattern.compile(versionNrAndBuildRegexString);
        Matcher versionMatcher = versionNrAndBuildRegex.matcher(contents);
        boolean versionFound = versionMatcher.find();
        if (versionFound) {
        	download.getVersionInfo().setVersionNumber(versionMatcher.group(versionNrGroupIndex));
        	download.getVersionInfo().setBuildNumber(Integer.parseInt(versionMatcher.group(buildNrGroupIndex)));
        }
        
        String downloadLinkRegexString = "(?<=" + Pattern.quote("href=\"") +")"+ "([^\"]+)";
        Pattern downloadLinkPattern = Pattern.compile(downloadLinkRegexString);
        Matcher matcher = downloadLinkPattern.matcher(contents);
        while (matcher.find()){
        	download.setDownloadLink(matcher.group(1));
        }
        
        Pattern opSysPattern = Pattern.compile("OpSys-(Windows|OSX)");
        Matcher opSysMatcher = opSysPattern.matcher(contents);
        if (opSysMatcher.find()){
        	download.getVersionInfo().setBuiltForOperatingSystem(VersionInfo.parseOperatingSystem(opSysMatcher.group(1)));
        }
        
        
        return download;
	}
}