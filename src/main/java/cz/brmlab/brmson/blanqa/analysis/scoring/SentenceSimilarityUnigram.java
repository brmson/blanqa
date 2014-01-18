package cz.brmlab.brmson.blanqa.analysis.scoring;

import java.util.List;

import cz.brmlab.brmson.core.provider.snowball.SnowballStemmerWrapper;
import cz.brmlab.brmson.blanqa.framework.data.Sentence;
import cz.brmlab.brmson.takepig.framework.data.AnswerSupport;

/**
 * Score a set of sentences based on keywords present in them or their neighbors.
 */

public class SentenceSimilarityUnigram implements ISentenceScorer {
	protected static final float CURRENT_SCORE = 3;
	protected static final float NEIGHBOR_SCORE = 1;

	protected AnswerSupport as;
	protected float CURRENT_SCORE_NORM, NEIGHBOR_SCORE_NORM;

	public SentenceSimilarityUnigram(AnswerSupport as) {
		this.as = as;

		List<String> keywords = as.getKeywords();
		CURRENT_SCORE_NORM = CURRENT_SCORE / keywords.size();
		NEIGHBOR_SCORE_NORM = NEIGHBOR_SCORE / keywords.size();
	}

	public void assignScore(Sentence s) {
                String currentSentence = SnowballStemmerWrapper.stemAllTokens(s.getText().toLowerCase());
		String previousSentence = null, nextSentence = null;
		if (s.getPrev() != null)
			previousSentence = SnowballStemmerWrapper.stemAllTokens(s.getPrev().getText().toLowerCase());
		if (s.getNext() != null)
			nextSentence = SnowballStemmerWrapper.stemAllTokens(s.getNext().getText().toLowerCase());

                for (String keyword : as.getKeywords()) {
                        keyword = SnowballStemmerWrapper.stem(keyword).toLowerCase();
                        if (currentSentence.contains(keyword))
				s.addScore(CURRENT_SCORE_NORM);
                        if (previousSentence != null && previousSentence.contains(keyword))
				s.addScore(NEIGHBOR_SCORE_NORM);
                        if (nextSentence != null && nextSentence.contains(keyword))
				s.addScore(NEIGHBOR_SCORE_NORM);
                }
	}

	public static void initialize() throws Exception {
		SnowballStemmerWrapper.initialize();
	}
}
