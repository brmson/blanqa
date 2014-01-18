package cz.brmlab.brmson.core.provider.snowball;

import info.ephyra.nlp.SnowballStemmer;

/**
 * This class serves as a wrapper of the SnowballStemmer Ephyra library,
 * taking care of initialization etc.
 */

public final class SnowballStemmerWrapper extends SnowballStemmer {

	private static boolean initialized = false;

	public static void initialize() {
		if (initialized)
			return;

		SnowballStemmer.create();

		initialized = true;
	}
}
