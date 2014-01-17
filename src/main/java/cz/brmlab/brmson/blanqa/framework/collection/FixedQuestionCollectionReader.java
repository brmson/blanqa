package cz.brmlab.brmson.blanqa.framework.collection;

import org.apache.uima.resource.ResourceInitializationException;

import cz.brmlab.brmson.blanqa.framework.collection.FixedListCollectionReader;

public final class FixedQuestionCollectionReader extends FixedListCollectionReader {
	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();

		String qtext = (String) getConfigParameterValue("qtext");
		if (qtext == null)
			throw new IllegalArgumentException(String.format("Parameter 'qtext' must be specified"));
		addElement("0", qtext);
	}
}
