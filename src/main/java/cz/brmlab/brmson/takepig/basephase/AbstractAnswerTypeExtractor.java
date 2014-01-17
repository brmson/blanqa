package cz.brmlab.brmson.takepig.basephase;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import cz.brmlab.brmson.takepig.framework.TPLogEntry;
import cz.brmlab.brmson.takepig.framework.jcas.AnswerTypeJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.ViewManager;
import cz.brmlab.brmson.takepig.framework.jcas.ViewType;

public abstract class AbstractAnswerTypeExtractor extends AbstractLoggedComponent {
	protected JCas jcas;

	public abstract String extractAnswerTypes(String question);

	@Override
	public final void process(JCas jcas) throws AnalysisEngineProcessException {
		super.process(jcas);
		this.jcas = jcas;
		try {
			// prepare input
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();
			log("QUESTION: " + questionText);
			
			// do task
			String answerType = extractAnswerTypes(questionText);
			log("TYPE_DETECTED: " + answerType);

			// save output
			AnswerTypeJCasManipulator.storeAnswerType(
					ViewManager.getView(jcas, ViewType.ANS_TYPE), answerType);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected final void log(String message) {
		super.log(TPLogEntry.ANS_TYPE, message);
	}

}
