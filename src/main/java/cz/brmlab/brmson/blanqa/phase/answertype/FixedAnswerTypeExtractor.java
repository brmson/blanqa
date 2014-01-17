package cz.brmlab.brmson.blanqa.phase.answertype;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import cz.brmlab.brmson.takepig.basephase.AbstractAnswerTypeExtractor;


/**
 * Answer type extractor that will simply return a fixed answer type.
 *
 * Just for testing.
 *
 * Example (for question "What prize did Watson receive?"):
 *   - inherit: jdbc.sqlite.cse.phase
 *     name: answer-type-extractor
 *     options: |
 *       - inherit: phases.answertype.fixed
 *         atype: "NEnumber->NEquantity"
 */

public class FixedAnswerTypeExtractor extends AbstractAnswerTypeExtractor {
	String atype;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		atype = (String) aContext.getConfigParameterValue("atype");
		if (atype == null)
			throw new IllegalArgumentException(String.format("Parameter 'atype' must be specified"));
	}

	@Override
	public String extractAnswerTypes(String question) {
		return atype;
	}
}
