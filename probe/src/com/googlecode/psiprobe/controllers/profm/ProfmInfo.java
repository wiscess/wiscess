package com.googlecode.psiprobe.controllers.profm;

import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class ProfmInfo implements Serializable {
	public static final String PROFM_SESS_ATTR = "profmData";
	private LinkedList<String> commandHistory = new LinkedList<String>();

	private int historySize = 0;
	private String rootPath;
	private String webPath;

	public void addCommandToHistory(String command) {
		this.commandHistory.remove(command);
		this.commandHistory.addFirst(command);

		while ((this.historySize >= 0) && (this.commandHistory.size() > this.historySize))
			this.commandHistory.removeLast();
	}

	public int getHistorySize() {
		return this.historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}
	public LinkedList<String> getCommandHistory() {
		return commandHistory;
	}

	public void setCommandHistory(LinkedList<String> commandHistory) {
		this.commandHistory = commandHistory;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}
}
