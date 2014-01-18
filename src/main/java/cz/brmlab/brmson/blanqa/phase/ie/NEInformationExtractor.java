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
import cz.brmlab.brmson.blanqa.framework.data.NamedEntity;
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

		HashMap<String,Float> NEscores = new HashMap<String,Float>();

		for (SearchResult r : as.getResults()) {
			// split result to sentences
			List<Sentence> sentences = SentenceSplitter.split(r, r.getText());

			// score sentences based on relevance
			for (Sentence s : sentences)
				s.setScore(1); // TODO

			// extract scored named entities from sentences
			ArrayList<LinkedList<NamedEntity>> NEs = NEx.extractFromSentences(sentences);

			// record named entities in a global scoreboard
			for (List<NamedEntity> NEsOfSentence : NEs) {
				for (NamedEntity NE : NEsOfSentence) {
					if (NEscores.containsKey(NE.getText())) {
						double score = NEscores.get(NE.getText()).doubleValue();
						NEscores.put(NE.getText(), new Float(score + NE.getScore()));
					} else {
						NEscores.put(NE.getText(), new Float(NE.getScore()));
					}
				}
			}
		}

		// convert the scoreboard to answer candidates
		List<Answer> answers = new LinkedList<Answer>();
		for (Map.Entry<String,Float> e : NEscores.entrySet()) {
			Answer a = new Answer(this.jcas);
			a.setText(e.getKey());
			a.setScore(e.getValue().floatValue());
			answers.add(a);
		}
		return answers;
	}
}
