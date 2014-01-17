package cz.brmlab.brmson.takepig.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.TOP;

public class JCasManipulator {

	/**
	 * Helper method to add an item to a stupid FS list. UIMA's FSList seems to
	 * be a classic linked-list implementation
	 */
	public static NonEmptyFSList addToFSList(JCas aJCas, FSList list, TOP item) {
		NonEmptyFSList result = new NonEmptyFSList(aJCas);
		result.setHead(item);
		result.setTail(list);
		return result;
	}
}
