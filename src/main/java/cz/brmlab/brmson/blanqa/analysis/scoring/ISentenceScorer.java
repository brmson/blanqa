package cz.brmlab.brmson.blanqa.analysis.scoring;

import cz.brmlab.brmson.blanqa.framework.data.Sentence;

/**
 * Score a set of sentences based on their relevance.
 *
 * This helps us weight candidate answers based on the relevance
 * of sentences to question subject matter.
 */


public interface ISentenceScorer {
	void assignScore(Sentence s);
}
