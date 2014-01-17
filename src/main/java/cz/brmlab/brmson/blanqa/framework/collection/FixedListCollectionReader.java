package cz.brmlab.brmson.blanqa.framework.collection;

import java.util.ArrayList;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.collection.AbstractCollectionReader;

/**
 * A collection that holds a fixed list of DataElements.
 *
 * Extend it to supply the list in a variety of ways (fixed string,
 * reading from file).  */

public abstract class FixedListCollectionReader extends AbstractCollectionReader {
	ArrayList<DataElement> dataList;
	int index;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		dataList = new ArrayList<DataElement>();
		index = 0;
	}

	public void addElement(String sequenceId, String question) {
		dataList.add(new DataElement(null, sequenceId, question, null));
	}

	@Override
	public boolean hasNext() throws CollectionException {
		return dataList.size() > 0 && dataList.size() > index ;
	}

	public int size() {
		return dataList.size();
	}

	@Override
	protected DataElement getNextElement() throws Exception {
		return dataList.get(index++);
	}
}
