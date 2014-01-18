package cz.brmlab.brmson.blanqa.phase.ie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.oaqa.model.SearchResult;
import org.oaqa.model.Answer;

import cz.brmlab.brmson.blanqa.analysis.SentenceSplitter;
import cz.brmlab.brmson.blanqa.analysis.NEExtractor;
import cz.brmlab.brmson.blanqa.framework.data.Sentence;
import cz.brmlab.brmson.core.provider.opennlp.OpenNLPWrapper;
import cz.brmlab.brmson.core.provider.netagger.NETaggerWrapper;
import cz.brmlab.brmson.takepig.basephase.AbstractInformationExtractor;
import cz.brmlab.brmson.takepig.framework.data.AnswerSupport;


/**
 * Information extractor that will do some NLP processing and extract
 * tagged data based on the requested NE answer type.
 *
 * So far, our method is very primitive - we simply generate answers
 * from all NE found, ranked by their number of occurences in sentences
 * of the provided support.
 */

public class NEInformationExtractor extends AbstractInformationExtractor {
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		try {
			// We know we are going to need this...
			SentenceSplitter.initialize();
			NEExtractor.initialize();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	public List<Answer> extractAnswerCandidates(AnswerSupport as, List<String> featureLabels) {
		NEExtractor NEx = new NEExtractor();
		NEx.setNEType(as.getAnswerType());

		HashMap<String,Integer> NEcounts = new HashMap<String,Integer>();

		for (SearchResult r : as.getResults()) {
			List<Sentence> sentences = SentenceSplitter.split(r, r.getText());

			ArrayList<LinkedList<String>> NEs = NEx.extractFromSentences(sentences);

			for (List<String> NEsOfSentence : NEs) {
				for (String NE : NEsOfSentence) {
					if (NEcounts.containsKey(NE)) {
						NEcounts.put(NE, NEcounts.get(NE) + 1);
					} else {
						NEcounts.put(NE, 1);
					}
				}
			}
		}

		List<Answer> answers = new LinkedList<Answer>();
		for (Map.Entry<String,Integer> e : NEcounts.entrySet()) {
			Answer a = new Answer(this.jcas);
			a.setText(e.getKey());
			a.setProbability(e.getValue());
			answers.add(a);
		}
		return answers;
	}
}
