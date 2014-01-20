package cz.brmlab.brmson.blanqa.framework.consumer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.OutputElement;

/**
 * A trivial consumer that will extract the final answer and print it
 * on the standard output for the user to "officially" see.
 *
 * Pair this with InteractiveCollectionReader.
 */

public class InteractiveAnswerPrinter extends JCasConsumer_ImplBase {

	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
	}

	public void process(JCas jcas) throws AnalysisEngineProcessException {
		OutputElement output = ((OutputElement) BaseJCasHelper.getAnnotation(
					jcas, OutputElement.type));
		if (output != null) {
			String questionText = output.getAnswer();
			System.out.println(questionText);
		} else {
			System.out.println("No answer found.");
		}
	}
}
