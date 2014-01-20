package cz.brmlab.brmson.takepig.basephase;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.oaqa.model.Answer;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.framework.types.OutputElement;
import cz.brmlab.brmson.takepig.framework.TPLogEntry;
import cz.brmlab.brmson.takepig.framework.jcas.AnswerJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.AnswerTypeJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.KeytermJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.ViewManager;
import cz.brmlab.brmson.takepig.framework.jcas.ViewType;

public abstract class AbstractAnswerGenerator extends AbstractLoggedComponent {
	protected JCas jcas;

	public abstract List<Answer> generateFinalAnswers(
			String answerType, List<String> keyterms,
			List<Answer> answerCandidates);

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		super.process(jcas);
		this.jcas = jcas;
		try {
			// prepare input
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));

			String answerType = AnswerTypeJCasManipulator
					.loadAnswerType(ViewManager
							.getView(jcas, ViewType.ANS_TYPE));

			List<String> keyterms = KeytermJCasManipulator
					.loadKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM));
			List<String> featureLabels = new LinkedList<String>();
			List<Answer> answerCandidates = AnswerJCasManipulator
					.loadAnswers(ViewManager.getView(jcas, ViewType.IE), featureLabels);

			// do task
			List<Answer> finalAnswers = generateFinalAnswers(answerType,
					keyterms, answerCandidates);
			
			StringBuilder builder = new StringBuilder();
			for(int i=0;i<Math.min(finalAnswers.size(), 10);i++){
				builder.append(" "+(i+1)+". "+finalAnswers.get(i));
			}
			log("ANS_DETECTED: " + builder.toString());
			
			AnswerJCasManipulator.storeAnswers(
					ViewManager.getView(jcas, ViewType.ANS),
					finalAnswers, featureLabels);

			if (finalAnswers.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<Math.min(finalAnswers.size(), 5);i++){
					sb.append("|"+finalAnswers.get(i).getText());
				}
				OutputElement outputAnswer = new OutputElement(jcas);
				//outputAnswer.setAnswer(finalAnswers.get(0).getText());
				outputAnswer.setAnswer(sb.toString());
				outputAnswer.setSequenceId(input.getSequenceId());
				outputAnswer.addToIndexes();
				log("FINAL ANSWER >>>>> " + outputAnswer.getAnswer());
			}

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected final void log(String message) {
		super.log(TPLogEntry.ANS, message);
	}
}
