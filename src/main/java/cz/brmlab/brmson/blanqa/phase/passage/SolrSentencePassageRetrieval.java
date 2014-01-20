package cz.brmlab.brmson.blanqa.phase.passage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.oaqa.model.SearchResult;
import org.oaqa.model.Passage;

import cz.brmlab.brmson.blanqa.analysis.SentenceSplitter;
import cz.brmlab.brmson.blanqa.framework.data.Sentence;
import cz.brmlab.brmson.takepig.basephase.AbstractPassageRetrieval;
import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;

/**
 * Passage retrieval that will perform a solr search and then return
 * keyword-relevant sentences (with some context).
 *
 * This is based on helloqa's SimplePassageExtractor.
 */

public class SolrSentencePassageRetrieval extends AbstractPassageRetrieval {
	protected Integer contextSentences;

	protected Integer hitListSize;
	protected SolrWrapper Solr;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		contextSentences = (Integer) aContext.getConfigParameterValue("context-sentences");
		if (contextSentences == null)
			throw new IllegalArgumentException(String.format("Parameter 'context-sentences' must be specified"));

		try {
			hitListSize = (Integer) aContext.getConfigParameterValue("hit-list-size");
		} catch (ClassCastException e) { // all cross-opts are strings?
			hitListSize = Integer.parseInt((String) aContext
					.getConfigParameterValue("hit-list-size"));
		}
		if (hitListSize == null)
			throw new IllegalArgumentException(String.format("Parameter 'hit-list-size' must be specified"));

		Boolean embedded = (Boolean) aContext.getConfigParameterValue("embedded");
		if (embedded == null)
			embedded = (Boolean) false;
		String serverUrl = (String) aContext.getConfigParameterValue("server");
		String core = (String) aContext.getConfigParameterValue("core");
		if (embedded != null && embedded == true) {
			if (core == null)
				throw new IllegalArgumentException(String.format("Parameter 'core' must be specified"));
		} else {
			if (serverUrl == null)
				throw new IllegalArgumentException(String.format("Parameter 'server' must be specified"));
		}

		try {
			this.Solr = new SolrWrapper(serverUrl, null, embedded, core);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}

		try {
			SentenceSplitter.initialize();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public List<SearchResult> retrieveResults(String questionText, String answerType,
			List<String> keyterms, List<String> keyphrases) {

		String query = formulateQuery(keyterms);
		SolrDocumentList documents;
		try {
			documents = Solr.runQuery(query, hitListSize);
		} catch (Exception e) {
			log("Error retrieving documents from Solr: " + e);
			return null;
		}

		List<SearchResult> results = new ArrayList<SearchResult>();

		for (SolrDocument document : documents) {
			String id = (String) document.getFieldValue("id");
			String title = (String) document.getFieldValue("titleText");
			log(" FOUND: " + id + " " + (title != null ? title : ""));
			String text;
			try {
				text = Solr.getDocText(id);
			} catch (SolrServerException e) {
				e.printStackTrace();
				continue;
			}
			// log("--8<-- " + text + " --8<--");

			List<Sentence> sentences = SentenceSplitter.split(null, text);

			// create passages while combining successive matching sentences
			// TODO: actually take heed of contextSentences
			List<Sentence> sentenceRun = null;
			for (Sentence s : sentences) {
				if (sentenceMatches(s, keyterms)) {
					if (sentenceRun == null) {
						sentenceRun = new LinkedList<Sentence>();
						if (s.getPrev() != null)
							sentenceRun.add(s.getPrev());
					}
					sentenceRun.add(s);

				} else if (sentenceRun != null) {
					if (s.getNext() != null)
						sentenceRun.add(s.getNext());
					Passage p = new Passage(this.jcas);
					p.setDocId(id);
					p.setText(SentenceSplitter.join(sentenceRun));
					p.setScore(((Float) document.getFieldValue("score")).floatValue());
					results.add(p);
					sentenceRun = null;
				}
			}
		}
		return results;
	}

	protected String formulateQuery(List<String> keyterms) {
		StringBuffer result = new StringBuffer();
		for (String keyterm : keyterms) {
			result.append(keyterm + " ");
		}
		String query = result.toString();
		log(" QUERY: " + query);
		return query;
	}

	protected boolean sentenceMatches(Sentence s, List<String> keyterms) {
		for (String keyterm : keyterms) {
			if (s.getText().contains(keyterm))
				return true;
		}
		return false;
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		Solr.close();
	}
}
