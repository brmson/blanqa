package cz.brmlab.brmson.takepig.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.oaqa.model.Search;
import org.oaqa.model.SearchResult;

public class SearchJCasManipulator {
	public static List<SearchResult> loadSearchResults(JCas searchView) {
		List<SearchResult> result = new ArrayList<SearchResult>();
		Iterator<?> it = searchView.getJFSIndexRepository().getAllIndexedFS(
				Search.type);

		if (it.hasNext()) {
			Search retrievalResult = (Search) it.next();
			FSArray hitList = retrievalResult.getHitList();
			for (int i = 0; i < hitList.size(); i++) {
				SearchResult sr = (SearchResult) hitList.get(i);
				result.add(sr);
			}
		}
		return result;
	}

	/**
	 * Store (overwrite) results in a view
	 * 
	 * @param searchView
	 * @param results
	 */
	public static void storeSearchResults(JCas searchView,
			List<SearchResult> results) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = searchView.getJFSIndexRepository().getAllIndexedFS(
				Search.type);
		while (it.hasNext()) {
			Search search = (Search) it.next();
			search.removeFromIndexes();
		}

		FSArray hitList = new FSArray(searchView, results.size());
		hitList.addToIndexes();
		for (int i = 0; i < results.size(); i++) {
			SearchResult sr = results.get(i);
			sr.setRank((i + 1));
			sr.addToIndexes();
			hitList.set(i, sr);
		}

		Search search = new Search(searchView);
		search.setHitList(hitList);
		search.addToIndexes();
	}
}
