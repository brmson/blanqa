package cz.brmlab.brmson.core.provider.netagger;

import info.ephyra.nlp.NETagger;
import info.ephyra.nlp.StanfordNeTagger;

/**
 * This class serves as a wrapper of the NETagger Ephyra library,
 * taking care of initialization etc.
 *
 * Somewhat viciously, NETagger also silently depends on
 * info.ephyra.nlp.StanfordNeTagger so initialize it too.
 *
 * NE == Named Entity
 */

public final class NETaggerWrapper extends NETagger {
	/* These data files can be download at http://pasky.or.cz/dev/brmson/res-netagger.zip */

	private static final String NER_LIST_PATH = "res/ephyra/nlp/netagger/lists/";
	private static final String NER_REGEX_PATH = "res/ephyra/nlp/netagger/patterns.lst";
	private static final String NER_STANFORD_PATH = "res/ephyra/nlp/netagger/stanford/ner-eng-ie.crf-3-all2006-distsim.ser.gz";


	private static boolean initialized = false;

	public static void initialize() throws Exception {
		if (initialized)
			return;

		loadListTaggers(NER_LIST_PATH);
		loadRegExTaggers(NER_REGEX_PATH);
		if (!StanfordNeTagger.init(NER_STANFORD_PATH))
			throw new Exception("Could not initialize NE tagger.");

		initialized = true;
	}
}
