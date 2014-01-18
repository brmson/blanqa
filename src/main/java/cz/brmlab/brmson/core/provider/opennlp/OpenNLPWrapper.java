package cz.brmlab.brmson.core.provider.opennlp;

import info.ephyra.nlp.OpenNLP;

/**
 * This class serves as a wrapper of the OpenNLP Ephyra library,
 * taking care of initialization etc.
 */

public final class OpenNLPWrapper extends OpenNLP {
	/* These data files can be download at http://pasky.or.cz/dev/brmson/res-opennlp.zip */

	private static final String TOKENIZER_PATH = "res/ephyra/nlp/tokenizer/opennlp/EnglishTok.bin.gz";

	private static final String SENT_DETECTOR_PATH = "res/ephyra/nlp/sentencedetector/opennlp/EnglishSD.bin.gz";

	private static final String TAGGER_PATH = "res/ephyra/nlp/postagger/opennlp/tag.bin.gz";
	private static final String TAGGER_DICT_PATH = "res/ephyra/nlp/postagger/opennlp/tagdict";

	private static final String CHUNKER_PATH = "res/ephyra/nlp/phrasechunker/opennlp/EnglishChunk.bin.gz";


	private static boolean initialized = false;

	public static void initialize() throws Exception {
		if (initialized)
			return;

		if (!createTokenizer(TOKENIZER_PATH))
			throw new Exception("Could not initialize tokenizer.");

		// sentence segmenter
		if (!createSentenceDetector(SENT_DETECTOR_PATH))
			throw new Exception("Could not initialize sentence segmenter.");

		// part of speech tagger
		if (!createPosTagger(TAGGER_PATH, TAGGER_DICT_PATH))
			throw new Exception("Could not initialize POS tagger.");

		// phrase chunker
		if (!createChunker(CHUNKER_PATH))
			throw new Exception("Could not initialize phrase chunker.");

		initialized = true;
	}
}
