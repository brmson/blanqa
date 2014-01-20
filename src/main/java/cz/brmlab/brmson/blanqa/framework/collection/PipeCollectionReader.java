package cz.brmlab.brmson.blanqa.framework.collection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.collection.AbstractCollectionReader;

/**
 * A collection that reads questions from file A and writes answers to file B.
 *
 * In a typical setup, both A and B are pipe files that connect Blanqa
 * to the outside world.
 *
 * Example:
 *   collection-reader:
 *     inherit: phases.collection.pipe
 *     askfile: /tmp/brmson.ask
 * */

public class PipeCollectionReader extends AbstractCollectionReader {
	BufferedReader askr;

	private int index;
	private String input;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		index = -1;

		String askfile = (String) getConfigParameterValue("askfile");
		if (askfile == null)
			throw new IllegalArgumentException(String.format("Parameter 'askfile' must be specified"));

		try {
			askr = new BufferedReader(new FileReader(askfile));
		} catch (FileNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	protected void acquireInput() {
		index++;
		try {
			input = askr.readLine();
		} catch (IOException io) {
			io.printStackTrace();
			input = null;
		}
	}

	@Override
	public boolean hasNext() throws CollectionException {
		if (input == null)
			acquireInput();
		return input != null;
	}

	@Override
	protected DataElement getNextElement() throws Exception {
		if (input == null)
			acquireInput();
		DataElement data = new DataElement(null, Integer.toString(index), input, null);
		input = null;
		return data;
	}
}
