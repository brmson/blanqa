package cz.brmlab.brmson.blanqa.framework.data;

import org.oaqa.model.SearchResult;

public class Sentence {
	private String text;
	private Sentence prev, next;

	private SearchResult resultRef;

	public Sentence(SearchResult resultRef, String text) {
		this.resultRef = resultRef;
		this.text = text;
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
}
