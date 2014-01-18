package cz.brmlab.brmson.blanqa.phase.answertype;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.javelin.util.Language;
import edu.cmu.lti.util.Pair;
import info.ephyra.questionanalysis.atype.AnswerType;
import info.ephyra.questionanalysis.atype.QuestionClassifier;
import info.ephyra.questionanalysis.atype.QuestionClassifierFactory;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import cz.brmlab.brmson.takepig.basephase.AbstractAnswerTypeExtractor;
import cz.brmlab.brmson.core.provider.wordnet.WordNetWrapper;


/**
 * Answer type extractor that "just" offloads this work to legacy Ephyra code.
 *
 * This actually means pretty heavy NLP lifting.
 */

public class EphyraAnswerTypeExtractor extends AbstractAnswerTypeExtractor {
	protected QuestionClassifier qc;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		try {
			// Ephyra is going to need this...
			WordNetWrapper.initialize();

			// Question Classification initialization
			Language en_US = Language.valueOf("en_US");
			Pair<Language, Language> languagePair = new Pair<Language, Language>(en_US, en_US);
			qc = QuestionClassifierFactory.getInstance(languagePair);
			if (qc == null)
				throw new Exception("QuestionClassifier initialization failed");
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public String extractAnswerTypes(String question) {
		// run the Ephyra machinery
                List<AnswerType> atypes = new ArrayList<AnswerType>();
		try {
			atypes = qc.getAnswerTypes(question);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

                // map answer type(s) to the NE naming convention
                String[] res = new String[atypes.size()];
                for (int i = 0; i < atypes.size(); i++) {
			/* Prefix with NE */
                        String atype = atypes.get(i).getFullType(-1).toLowerCase()
                                        .replaceAll("\\.", "->NE").replaceAll("^", "NE");

			/* Uppercase initials (after _) */
                        StringBuilder sb = new StringBuilder(atype);
                        Matcher m = Pattern.compile("_(\\w)").matcher(atype);
                        while (m.find()) {
                                sb.replace(m.start(), m.end(), m.group(1).toUpperCase());
                                m = Pattern.compile("_(\\w)").matcher(sb.toString());
                        }

                        res[i] = sb.toString();
                }

		StringBuilder atypeCandidates = new StringBuilder();
		for (int i = 0; i < res.length; i++) {
			atypeCandidates.append((i > 0 ? ", " : "") + " " + res[i]);
		}
		log("  Answer type candidates: " + atypeCandidates);

		// XXX: We just take the first one now
		return res.length > 0 ? res[0] : null;
	}
}
