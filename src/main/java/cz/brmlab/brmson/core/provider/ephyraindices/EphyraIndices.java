package cz.brmlab.brmson.core.provider.ephyraindices;

import info.ephyra.nlp.indices.FunctionWords;
import info.ephyra.nlp.indices.IrregularVerbs;

import cz.brmlab.brmson.core.provider.snowball.SnowballStemmerWrapper;

/**
 * This class serves as a wrapper of the indices infrastructure
 * of the Ephyra library, taking care of its initialization.
 *
 * Just call EphyraIndices.initialize() when needed. You can then
 * import and use info.ephyra.nlp.indices.* directly.
 */

public final class EphyraIndices {
	/* These data files can be download at http://pasky.or.cz/dev/brmson/res-indices.zip */

	private static final String FUNCTIONW_PATH = "res/ephyra/indices/functionwords_nonumbers";
	private static final String IRREGULARV_PATH = "res/ephyra/indices/irregularverbs";


	private static boolean initialized = false;

	public static void initialize() throws Exception {
		if (initialized)
			return;

		SnowballStemmerWrapper.initialize();

		if (!FunctionWords.loadIndex(FUNCTIONW_PATH))
			throw new Exception("Could not load function words.");

		if (!IrregularVerbs.loadVerbs(IRREGULARV_PATH))
			throw new Exception("Could not load irregular verbs.");

		initialized = true;
	}
}
