package cz.brmlab.brmson.blanqa.phase.answer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.oaqa.model.Answer;

import cz.brmlab.brmson.takepig.basephase.AbstractAnswerGenerator;


/**
 * Answer type generator that will simply sort the candidates by their score
 * in descending order.
 *
 * Example (for question "What prize did Watson receive?"):
 *   - inherit: jdbc.sqlite.cse.phase
 *     name: answer-generator
 *     options: |
 *       - inherit: phases.answer.basic
 */

public class AnswerGenerator extends AbstractAnswerGenerator {
	@Override
	public List<Answer> generateFinalAnswers(
			String answerType, List<String> keyterms,
			List<Answer> answerCandidates) {

		// deduplicate candidates
		Map<String, Answer> results = new LinkedHashMap<String, Answer>();
		for (Answer candidate : answerCandidates) {
			String text = candidate.getText();
			Answer result = results.get(text);
			if (result == null) {
				// first time seen
				results.put(text, candidate);
			} else {
				// merge questions
				// TODO merge searchresults
				// Increase score
				result.setScore(result.getScore() + candidate.getScore());
			}
		}

		// sort answer candidates by sum of scores
		// (use original order for tie-breaking)
		Answer[] sorted = results.values().toArray(new Answer[results.size()]);
		Arrays.sort(sorted, Collections.reverseOrder());
		List<Answer> finalAnswers = new ArrayList<Answer>(sorted.length);
		for (Answer result : sorted) {
			finalAnswers.add(result);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Math.min(5, finalAnswers.size()); i++) {
			sb.append((i > 0 ? ", " : ""));
			sb.append("\"" + finalAnswers.get(i) + "\"");
			sb.append("\"" + finalAnswers.get(i).getScore() + "\"");
		}
		log("Final top answers: " + (sb.length() > 0 ? sb : "No answers found."));
		
		return finalAnswers;
	}
}
