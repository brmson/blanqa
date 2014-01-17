package cz.brmlab.brmson.takepig.basephase;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.oaqa.model.SearchResult;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import cz.brmlab.brmson.takepig.framework.TPLogEntry;
import cz.brmlab.brmson.takepig.framework.jcas.AnswerTypeJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.KeytermJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.SearchJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.ViewManager;
import cz.brmlab.brmson.takepig.framework.jcas.ViewType;

public abstract class AbstractPassageRetrieval extends AbstractLoggedComponent {
	protected JCas jcas;

	public abstract List<SearchResult> retrieveResults(String questionText,
			String answerType, List<String> keyterms, List<String> keyphrases);

	public final void process(JCas jcas) throws AnalysisEngineProcessException {
		super.process(jcas);
		this.jcas = jcas;
		try {
			// prepare input
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();

			List<String> keyterms = KeytermJCasManipulator
					.loadKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM));
			List<String> keyphrases = KeytermJCasManipulator
					.loadKeyphrases(ViewManager.getView(jcas, ViewType.KEYTERM));
			String answerType = AnswerTypeJCasManipulator
					.loadAnswerType(ViewManager.getView(jcas, ViewType.ANS_TYPE));

			// do task
			List<SearchResult> results
				= retrieveResults(questionText, answerType, keyterms, keyphrases);
			for (SearchResult r: results) {
				log("Retrieved: " + r);
			}

			// save output
			SearchJCasManipulator.storeSearchResults(
					ViewManager.getView(jcas, ViewType.PASSAGE), results);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected final void log(String message) {
		super.log(TPLogEntry.PASSAGE, message);
	}

}
