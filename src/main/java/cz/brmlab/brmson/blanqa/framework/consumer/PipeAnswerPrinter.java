package cz.brmlab.brmson.blanqa.framework.consumer;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.OutputElement;

/**
 * A consumer that will extract the final answer and write it to a file.
 *
 * In a typical setup, this would be a pipe file that connect Blanqa
 * to the outside world.
 *
 * Example:
 *   - inherit: phases.consumer.pipe
 *     ansfile: /tmp/brmson.ans
 */

public class PipeAnswerPrinter extends JCasConsumer_ImplBase {
	PrintWriter answ;

	public void initialize(UimaContext context) throws ResourceInitializationException {
		String ansfile = (String) context.getConfigParameterValue("ansfile");
		if (ansfile == null)
			throw new IllegalArgumentException(String.format("Parameter 'ansfile' must be specified"));

		try {
			answ = new PrintWriter(new FileWriter(ansfile));
		} catch (FileNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public void process(JCas jcas) throws AnalysisEngineProcessException {
		OutputElement output = ((OutputElement) BaseJCasHelper.getAnnotation(
					jcas, OutputElement.type));
		if (output != null) {
			String questionText = output.getAnswer();
			answ.println(questionText);
		} else {
			answ.println("No answer found.");
		}
		answ.flush();
	}
}
