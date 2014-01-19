package cz.brmlab.brmson.blanqa.analysis;

import java.util.LinkedList;
import java.util.List;

import org.oaqa.model.SearchResult;

import cz.brmlab.brmson.blanqa.framework.data.Sentence;
import cz.brmlab.brmson.core.provider.opennlp.OpenNLPWrapper;

/**
 * Split text to a series of sentences.
 */

public class SentenceSplitter {
	private static final int MIN_SENTENCE_LENGTH = 3;
	private static final int MAX_SENTENCE_LENGTH = 512;

	/**
	 * Split a given result with a given text (may be preprocessed)
	 * to a list of contextualized sentences.
	 */
	public static List<Sentence> split(SearchResult srcResult, String srcText) {
		String[] sentenceTexts = OpenNLPWrapper.sentDetect(srcText);

		LinkedList<Sentence> sentences = new LinkedList<Sentence>();
		for (String text : sentenceTexts) {
			if (text.length() <= MIN_SENTENCE_LENGTH)
				continue;
			if (text.length() > MAX_SENTENCE_LENGTH) {
				System.err.println("Omitting extremely long sentence: " + text.substring(0, 64) + " ...");
				continue;
			}
			Sentence s = new Sentence(srcResult, text);
			if (sentences.size() > 0) {
				s.setPrev(sentences.getLast());
				sentences.getLast().setNext(s);
			}
			sentences.add(s);
		}

		return sentences;
	}

	public static String join(List<Sentence> sentences) {
		StringBuilder sb = new StringBuilder();
		for (Sentence s : sentences) {
			sb.append(s.getText());
			sb.append(". ");
		}
		return sb.toString();
	}

	public static void initialize() throws Exception {
		OpenNLPWrapper.initialize();
	}
}
