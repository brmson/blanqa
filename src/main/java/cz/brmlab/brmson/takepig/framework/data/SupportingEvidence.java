package cz.brmlab.brmson.takepig.framework.data;

import java.util.List;

import org.oaqa.model.SearchResult;

public class SupportingEvidence {

	private String questionText;
	private String answerType;

	private List<String> keywords;
	private List<String> keyphrases;

	private List<SearchResult> results;

	public SupportingEvidence(String questionText, String answerType,
			List<String> keywords, List<String> keyphrases,
			List<SearchResult> results) {
		this.questionText = questionText;
		this.answerType = answerType;

		this.keywords = keywords;
		this.keyphrases = keyphrases;

		this.results = results;
	}

	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}

	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getKeyphrases() {
		return keyphrases;
	}
	public void setKeyphrases(List<String> keyphrases) {
		this.keyphrases = keyphrases;
	}

	public List<SearchResult> getResults() {
		return results;
	}
}
