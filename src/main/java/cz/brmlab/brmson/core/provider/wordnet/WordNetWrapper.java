package cz.brmlab.brmson.core.provider.wordnet;

import info.ephyra.nlp.semantics.ontologies.WordNet;

/**
 * This class serves as a wrapper of the WordNet Ephyra library,
 * taking care of initialization etc.
 */

public final class WordNetWrapper extends WordNet {
	/* These data files can be download at http://pasky.or.cz/dev/brmson/res-wordnet.zip */

	private static final String WORDNET_PATH = "res/ephyra/ontologies/wordnet/file_properties.xml";


	private static boolean initialized = false;

	public static void initialize() throws Exception {
		if (initialized)
			return;

		if (!initialize(WORDNET_PATH))
			throw new Exception("Could not initialize WordNet.");

		initialized = true;
	}
}
