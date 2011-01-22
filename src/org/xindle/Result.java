package org.xindle;

public class Result {
	String title;
	String summary;

	public Result(String title, String summary) {
		this.title = title;
		this.summary = summary;
	}

	public String toString() {
		return title + " " + summary;
	}
}