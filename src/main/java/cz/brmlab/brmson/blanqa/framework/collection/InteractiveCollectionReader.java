package cz.brmlab.brmson.blanqa.framework.collection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.collection.AbstractCollectionReader;

/**
 * A collection that talks to the user via stdin/stdout, allowing
 * them to ask questions. */

public class InteractiveCollectionReader extends AbstractCollectionReader {
	BufferedReader br;

	private int index;
	private String input;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		index = -1;

		br = new BufferedReader(new InputStreamReader(System.in));;
	}

	protected void acquireInput() {
		index++;
		if (index == 0) {
			System.out.println("Brmson.BlanQA interactive question answerer");
			System.out.println("(c) 2014  Petr Baudis, standing on the shoulders of giants");
		}
		try {
			System.out.print("brmson.blanqa> ");
			System.out.flush();
			input = br.readLine();
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
