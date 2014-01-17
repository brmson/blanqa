package cz.brmlab.brmson.takepig.basephase;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.oaqa.model.Answer;
import org.oaqa.model.SearchResult;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import cz.brmlab.brmson.takepig.framework.TPLogEntry;
import cz.brmlab.brmson.takepig.framework.data.SupportingEvidence;
import cz.brmlab.brmson.takepig.framework.jcas.AnswerJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.AnswerTypeJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.KeytermJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.SearchJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.ViewManager;
import cz.brmlab.brmson.takepig.framework.jcas.ViewType;

public abstract class AbstractInformationExtractor extends AbstractLoggedComponent {
	protected JCas jcas;

	public abstract List<Answer> extractAnswerCandidates(SupportingEvidence ev, List<String> featureLabels);

	@Override
	public final void process(JCas jcas) throws AnalysisEngineProcessException {
		super.process(jcas);
		this.jcas = jcas;
		try {
			// prepare input
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();

			String answerType = AnswerTypeJCasManipulator
					.loadAnswerType(ViewManager
							.getView(jcas, ViewType.ANS_TYPE));

			List<String> keyterms = KeytermJCasManipulator
					.loadKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM));
			List<String> keyphrases = KeytermJCasManipulator
					.loadKeyphrases(ViewManager.getView(jcas, ViewType.KEYTERM));
			List<SearchResult> results = SearchJCasManipulator
					.loadSearchResults(ViewManager.getView(jcas, ViewType.PASSAGE));

			SupportingEvidence ev = new SupportingEvidence(questionText, answerType,
					keyterms, keyphrases, results);

			// do task
			List<String> featureLabels = new LinkedList<String>();
			List<Answer> ansCandidates = extractAnswerCandidates(ev, featureLabels);

			// save output
			AnswerJCasManipulator.storeAnswers(
					ViewManager.getView(jcas, ViewType.IE),
					ansCandidates, featureLabels);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected final void log(String message) {
		super.log(TPLogEntry.IE, message);
	}

}
