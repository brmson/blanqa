package cz.brmlab.brmson.blanqa.phase.keyterm;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import cz.brmlab.brmson.takepig.framework.jcas.KeytermJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.ViewManager;
import cz.brmlab.brmson.takepig.framework.jcas.ViewType;


/**
 * Keyterm/keyphrase extractor that will simply return fixed lists.
 *
 * Just for testing.
 *
 * Example (for question "What prize did Watson receive?"):
 *   - inherit: jdbc.sqlite.cse.phase
 *     name: keyterm-extractor
 *     options: |
 *       - inherit: phases.keyterm.fixed
 *         keywords: "prize|Watson|received"
 *         keyphrases: "Watson receive"
 */

/* TODO: Introduce AbstractKeytermExtractor */

public class FixedKeytermExtractor extends AbstractLoggedComponent {
	List<String> keyterms, keyphrases;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		String keytermsStr = (String) aContext.getConfigParameterValue("keyterms");
		if (keytermsStr != null) {
			keyterms = new ArrayList(Arrays.asList(keytermsStr.split("\\|")));
		} else {
			throw new IllegalArgumentException(String.format("Parameter 'keyterms' must be specified"));
		}

		String keyphrasesStr = (String) aContext.getConfigParameterValue("keyphrases");
		if (keyphrasesStr != null) {
			keyphrases = new ArrayList(Arrays.asList(keyphrasesStr.split("\\|")));
		} /* else that's all right. */
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();
			// we do nothing with the question here, the above is just pro forma

			// Save result into a view
			KeytermJCasManipulator.storeKeyTermsAndPhrases(ViewManager.getView(jcas, ViewType.KEYTERM),
					keyterms, keyphrases);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
