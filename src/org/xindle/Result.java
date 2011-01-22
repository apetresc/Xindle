package org.xindle;

public class Result {
	String title;
	String summary;
	String id;

	public Result(String title, String summary, String id) {
		this.title = title;
		this.summary = summary.replace('\n', ' ');
		this.id = id;
	}

	public String toString() {
		return title + " " + summary + " " + id;
	}
}