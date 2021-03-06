package bolscript;

import java.awt.EventQueue;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basics.Debug;
import basics.FileManager;
import basics.FileReadException;
import basics.Tools;
import bolscript.Download.DownloadTypes;
import bolscript.UpdateInfo.VersionState;
import bolscript.config.Config;
import bolscript.config.RunParameters;
import bolscript.config.VersionInfo;
import bolscript.scanner.Parser;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


public class UpdateManager implements Runnable{

	private static final int connectionTimeOut = 10000;
	private static final int readTimeOut = 10000;
	private static final String downloadsUrl = "http://code.google.com/p/bolscript/downloads/list";
	private static final String downloadsFeed = "http://code.google.com/feeds/p/bolscript/downloads/basic";
	
	
	public interface UpdateManagerListener {
		public void displayInfo(UpdateInfo updateInfo);
		
	}
	
	UpdateManagerListener listener = null;
	
	public UpdateManagerListener getListener() {
		return listener;
	}

	public void setListener(UpdateManagerListener listener) {
		this.listener = listener;
	}

	UpdateInfo updateInfo;

	public UpdateManager () {
		updateInfo = new UpdateInfo();
	}
	
	public void run() {
		Debug.debug(this, "starting run()");
		CheckForUpdates();
		if (listener != null) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					Debug.debug(this, "sending result to listeners");
					listener.displayInfo(updateInfo);
				}
			});	
		}
		Debug.debug(this, "UpdateManager ending run()");
	}
	
	/**
	 * Queries the download feed and fills the update info.
	 */
	public void CheckForUpdates() {
		VersionInfo currentVersionInfo;
		updateInfo.setDownloadUrl(downloadsUrl);
		try {
			Debug.temporary(this, "Determining current version...");
			currentVersionInfo = Config.getVersionInfoFromJar(Config.getJarPath());		
			updateInfo.setCurrentVersion(currentVersionInfo);
			if (currentVersionInfo.getOperatingSystem() == Config.OperatingSystems.Unknown) {
				currentVersionInfo.setOperatingSystem(VersionInfo.getRunningOperatingSystem());
			}
			if (RunParameters.fakeBuildNumber != -1) {
				currentVersionInfo.setBuildNumber(RunParameters.fakeBuildNumber);
			}

		} catch (Exception e) {				
			updateInfo.setError(new Exception("Could not determine current Version..."));
			return;
		}

		ArrayList<Download> downloads = new ArrayList<Download>();
		URL url = null;
		URLConnection connection = null;
		try {

			url = new URL(downloadsFeed);
			connection = url.openConnection();			
			connection.setConnectTimeout(connectionTimeOut);
			connection.setReadTimeout(readTimeOut);
		} catch (Exception e) {
			updateInfo.setError(new MalformedURLException("Could not find download url..."));
			return;
		}

		try {			
			Debug.temporary(this, "Connecting to download feed...");		
			SyndFeedInput input = new SyndFeedInput();		
			XmlReader reader = new XmlReader(connection);
			SyndFeed feed = input.build(reader);
			Debug.temporary(this, "Reading from download feed...");

			List entries = feed.getEntries();			    
			for (Object entryObj : entries) {
				SyndEntry entry = (SyndEntry) entryObj;
				try {
					downloads.add(parseDownload(entry));
				} catch (ParseException e) {						
					e.printStackTrace();
				}	
			}			    

		} catch (Exception e) {
			updateInfo.setError(new Exception("Could not contact downloads page...",e));
			return;
		}

		//determine fitting download
		Debug.temporary(this, "Determining download candidates...");
		Download bestCandidate = null;
		Download changelog = null;
		for (Download download : downloads) {
			if (download.getVersionInfo() != null) {
				if (download.getType() == Download.DownloadTypes.ChangelogHtml) {
					if (changelog == null) {
						changelog = download; 
						} else if (download.getVersionInfo().getBuildNumber()
								> changelog.getVersionInfo().getBuildNumber()) {
							changelog = download;
						}
											
				} else if (download.getVersionInfo().getOperatingSystem() 
						== currentVersionInfo.getBuiltForOperatingSystem()) {
					if (bestCandidate == null) {
						bestCandidate = download;
					} else if (download.getVersionInfo().getBuildNumber() 
							> bestCandidate.getVersionInfo().getBuildNumber()) {
						bestCandidate = download;
					}
				}
			}
		}
		
		//check for changelog
		String changelogContent = null;
		//download changelog
		if (changelog != null) {
			Debug.temporary(this, "Reading changelog...");
			try {
				if (!RunParameters.useLocalChangeLog) { 
					URL changelogUrl = Tools.URIFromDangerousPlainTextUrl(changelog.getDownloadLink()).toURL();	
					Debug.temporary(this, "changelog url: " + changelogUrl);
					URLConnection connect = changelogUrl.openConnection();
					Debug.temporary(this, "connection...");
					connect.setConnectTimeout(connectionTimeOut);
					connect.setReadTimeout(readTimeOut);
					Debug.temporary(this, "set Timeouts...");
					InputStream contentStream = connect.getInputStream();
					Debug.temporary(this, "connected");
					changelogContent = Tools.inputStreamToString(contentStream);
					Debug.temporary(this, "read content");
				} else {
					File file = new File("/Users/hannes/Projekte/Workspace/bolscript googlecode/Changelog.html");
					changelogContent = "";
					try {
						changelogContent = FileManager.getContents(file, Config.compositionEncoding);				
					} catch (FileReadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Debug.temporary(this, "stripping changelog content to relevant parts");
				updateInfo.setChangelog(stripToRelevantContent(changelogContent));
				Debug.temporary(this, "ok");
			} catch (Exception e) {
				//could not download changelog.
			}
		}
		
		if (bestCandidate == null) {
			Debug.temporary(this, "No Download candidate");
			//fehler
			updateInfo.setError(new Exception("Could not find an apropriate download."));
			return;
			
		} else if (bestCandidate.getVersionInfo().getBuildNumber()
					<=	currentVersionInfo.getBuildNumber()) {
			//everything is up to date
			Debug.temporary(this, "Is up to date");
			updateInfo.setResult(UpdateInfo.VersionState.OK);
			return;
			
		} else {								
			Debug.temporary(this, "Found update");
			updateInfo.setResult(UpdateInfo.VersionState.HasUpdates);
			updateInfo.setDownloadUrl(bestCandidate.getDetailsLink());
			updateInfo.setDownload(bestCandidate);
			return;
		}


	}

	private Download parseDownload(SyndEntry entry) throws ParseException{
		try {
			String link = entry.getLink();
			String title = entry.getTitle();

			Download download = new Download();			        
			download.setDetailsLink(link);
			download.setTitle(title);
			download.setDate(entry.getUpdatedDate());

			//read main contents of the entry
			String contents = ((SyndContent) entry.getContents().get(0)).getValue();

			//extract the download link
			String downloadLinkRegexString = "(?<=" + Pattern.quote("href=\"") +")"+ "([^\"]+)";
			Pattern downloadLinkPattern = Pattern.compile(downloadLinkRegexString);
			Matcher matcher = downloadLinkPattern.matcher(contents);
			while (matcher.find()){        
				download.setDownloadLink(matcher.group(1));
			}

			//extract the version info
			try {
				VersionInfo versionInfo = parseVersionInfo(contents);
				download.setVersionInfo(versionInfo);
			} catch (ParseException e) {

			}
			//set the download type
			download.setType(parseDownloadType(download));

			return download;

		} catch (Exception ex) {
			throw new ParseException(entry.toString(), -1);
		}		                    
	}

	private DownloadTypes parseDownloadType(Download download) {
		Pattern changelogRegex = Pattern.compile("(?i)change");
		Pattern dmgPattern = Pattern.compile(".*(?i)dmg$");
		Pattern zipPattern = Pattern.compile(".*(?i)zip$");

		if (changelogRegex.matcher(download.getTitle()).find()) {
			return Download.DownloadTypes.ChangelogHtml;
		}
		if (dmgPattern.matcher(download.getDownloadLink()).find()) 
			return Download.DownloadTypes.Dmg;
		if (zipPattern.matcher(download.getDownloadLink()).find()) 
			return Download.DownloadTypes.Zip;

		return Download.DownloadTypes.Unknown;
	}

	private VersionInfo parseVersionInfo(String contents) throws ParseException {
		String versionNrAndBuildRegexString = "(\\d\\.\\d+)(?:\\s|build|Build|b|\\.)+(\\d+)";
		int versionNrGroupIndex=1;
		int buildNrGroupIndex=2;
		Pattern versionNrAndBuildRegex = Pattern.compile(versionNrAndBuildRegexString);
		Matcher versionMatcher = versionNrAndBuildRegex.matcher(contents);
		boolean versionFound = versionMatcher.find();

		if (versionFound) {
			VersionInfo versionInfo = new VersionInfo();
			versionInfo.setVersionNumber(versionMatcher.group(versionNrGroupIndex));
			versionInfo.setBuildNumber(Integer.parseInt(versionMatcher.group(buildNrGroupIndex)));

			Pattern opSysPattern = Pattern.compile("OpSys-(Windows|OSX)");
			Matcher opSysMatcher = opSysPattern.matcher(contents);
			if (opSysMatcher.find()){
				versionInfo.setOperatingSystem(VersionInfo.parseOperatingSystem(opSysMatcher.group(1)));
			}

			return versionInfo;
		} else {
			throw new ParseException(contents, 0);
		}
	}

	public String stripToRelevantContent(String changeLog) {
		Debug.debug(this, "creating styleremoverPattern...");
		String styleRemover = "(?s)" + Pattern.quote("<style")+".*"+Pattern.quote("/style>");
		Debug.debug(this, styleRemover);
		Debug.debug(this, "stripping changelog");
		Debug.debug(this, "before:" + changeLog);
		String stripped = changeLog.replaceAll(styleRemover, "");		
		Debug.debug(this, "after:" + changeLog);
		Debug.debug(this, "replacing bodypart");
		stripped = stripped.replaceAll(Pattern.quote("body>"), Matcher.quoteReplacement("body style=\"font-size: 9px;\">"));
		Debug.debug(this, "r");
		return stripped;
	}
	public UpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(UpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
	}
}