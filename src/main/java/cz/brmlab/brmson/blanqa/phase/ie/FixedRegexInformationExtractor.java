package cz.brmlab.brmson.blanqa.phase.ie;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.oaqa.model.SearchResult;
import org.oaqa.model.Answer;

import cz.brmlab.brmson.takepig.basephase.AbstractInformationExtractor;
import cz.brmlab.brmson.takepig.framework.data.AnswerSupport;


/**
 * Information extractor that will do a simple fixed regex match.
 *
 * Just for testing.
 *
 * Example (for question "What prize did Watson receive?" and
 * answer "Watson received the first prize of $1 million."):
 *   - inherit: jdbc.sqlite.cse.phase
 *     name: information-extractor
 *     options: |
 *       - inherit: phases.ie.fixedregex
 *         regex: "\$[^.]*"
 */

public class FixedRegexInformationExtractor extends AbstractInformationExtractor {
	Pattern regex;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		String regexStr = (String) aContext.getConfigParameterValue("regex");
		if (regexStr != null) {
			regex = Pattern.compile(regexStr);
		} else {
			throw new IllegalArgumentException(String.format("Parameter 'regex' must be specified"));
		}
	}

	public List<Answer> extractAnswerCandidates(AnswerSupport as, List<String> featureLabels) {
		List<Answer> answers = new LinkedList<Answer>();
		for (SearchResult r : as.getResults()) {
			Matcher m = regex.matcher(r.getText());
			while (m.find()) {
				Answer a = new Answer(this.jcas);
				a.setText(m.group());
				answers.add(a);
			}
		}
		return answers;
	}
}
