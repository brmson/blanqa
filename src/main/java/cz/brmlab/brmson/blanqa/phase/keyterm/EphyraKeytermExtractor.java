package cz.brmlab.brmson.blanqa.phase.keyterm;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import info.ephyra.questionanalysis.KeywordExtractor;
import info.ephyra.questionanalysis.QuestionNormalizer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import cz.brmlab.brmson.core.provider.opennlp.OpenNLPWrapper;
import cz.brmlab.brmson.core.provider.netagger.NETaggerWrapper;
import cz.brmlab.brmson.core.provider.ephyraindices.EphyraIndices;
import cz.brmlab.brmson.takepig.framework.TPLogEntry;
import cz.brmlab.brmson.takepig.framework.jcas.KeytermJCasManipulator;
import cz.brmlab.brmson.takepig.framework.jcas.ViewManager;
import cz.brmlab.brmson.takepig.framework.jcas.ViewType;


/**
 * Keyterm extractor that "just" offloads this work to legacy Ephyra code.
 *
 * This actually means pretty heavy NLP lifting.
 *
 * TODO: Also generate bigram-based keyphrases.
 */

/* TODO: Introduce AbstractKeytermExtractor */

public class EphyraKeytermExtractor extends AbstractLoggedComponent {
	List<String> keyterms, keyphrases;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		try {
			// We know Ephyra is going to need this...
			OpenNLPWrapper.initialize();
			NETaggerWrapper.initialize();
			EphyraIndices.initialize();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();

			keyterms = new LinkedList();
			keyphrases = new LinkedList();

			extractKeytermsKeyphrases(questionText, keyterms, keyphrases);

			// Save result into a view
			KeytermJCasManipulator.storeKeyTermsAndPhrases(ViewManager.getView(jcas, ViewType.KEYTERM),
					keyterms, keyphrases);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	public void extractKeytermsKeyphrases(String questionText,
			List<String> keyterms, List<String> keyphrases) {

		// normalize question
		String qn = QuestionNormalizer.normalize(questionText);

		// resolve verb constructions with auxiliaries
		String verbMod = (QuestionNormalizer.handleAuxiliaries(qn))[0];

		// extract keywords
		String[] kws = KeywordExtractor.getKeywords(verbMod);
		log(TPLogEntry.KEYTERM, "  Keywords: " + Arrays.toString(kws));

		for (String kw : kws) {
			keyterms.add(kw);
		}
	}
}
