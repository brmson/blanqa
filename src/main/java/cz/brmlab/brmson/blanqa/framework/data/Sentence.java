package cz.brmlab.brmson.blanqa.framework.data;

import org.oaqa.model.SearchResult;

public class Sentence {
	private String text;
	private Sentence prev, next;

	private SearchResult resultRef;

	private float score;

	public Sentence(SearchResult resultRef, String text) {
		this.resultRef = resultRef;
		this.text = text;
		this.score = 0;
	}

	public String getText() {
		return text;
	}
	public SearchResult getResultRef() {
		return resultRef;
	}

	public Sentence getPrev() {
		return prev;
	}
	public void setPrev(Sentence s) {
		this.prev = s;
	}

	public Sentence getNext() {
		return next;
	}
	public void setNext(Sentence s) {
		this.next = s;
	}

	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public void addScore(float score) {
		this.score += score;
	}
}
