package cz.brmlab.brmson.blanqa.phase.passage;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.oaqa.model.SearchResult;
import org.oaqa.model.Document;

import cz.brmlab.brmson.takepig.basephase.AbstractPassageRetrieval;


/**
 * Passage retrieval that will simply return a fixed result.
 *
 * Just for testing.
 *
 * Example (for question "What prize did Watson receive?"):
 *   - inherit: jdbc.sqlite.cse.phase
 *     name: passage-retrieval
 *     options: |
 *       - inherit: phases.passage.fixed
 *         text: ""Watson received the first prize of $1 million."
 */

public class FixedPassageRetrieval extends AbstractPassageRetrieval {
	String text;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		text = (String) aContext.getConfigParameterValue("text");
		if (text == null)
			throw new IllegalArgumentException(String.format("Parameter 'text' must be specified"));
	}

	public List<SearchResult> retrieveResults(String questionText, String answerType,
			List<String> keyterms, List<String> keyphrases) {

		List<SearchResult> results = new ArrayList<SearchResult>();

		Document d = new Document(this.jcas);
		d.setDocId("fixed");
		d.setUri("fixed");
		d.setText(text);
		results.add(d);

		return results;
	}
}
