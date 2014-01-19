package cz.brmlab.brmson.blanqa.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.oaqa.model.SearchResult;

import cz.brmlab.brmson.blanqa.framework.data.NamedEntity;
import cz.brmlab.brmson.blanqa.framework.data.Sentence;
import cz.brmlab.brmson.core.provider.opennlp.OpenNLPWrapper;
import cz.brmlab.brmson.core.provider.netagger.NETaggerWrapper;

/**
 * Extract Named Entities (NE) of a given type from a set of sentences.
 */

public class NEExtractor {
	private String[] neTypes;
	/* Ids of taggers for a particular NE type. */
	private int[] neIds;

	/**
	 * Set up the extractor to match a particular type.
	 *
	 * Multiple types separated by -> in the order from general to
	 * particular may be specified. */
	public void setNEType(String type) {
		neTypes = type.split("->");

		/* The most specialized recognizable type will take precedence
		 * when tagging. */
		for (String neType : neTypes) {
			int[] thisIds = NETaggerWrapper.getNeIds(neType);
			if (thisIds.length > 0)
				neIds = thisIds;
		}

		if (neIds == null) {
			neIds = new int[0];
			System.err.println("No tagger for NE type " + type + " available");
		}
	}

	/**
	 * Check whether the extractor matches a particular type.
	 *
	 * In case multiple types were specified, return true if any of them
	 * matches. */
	public boolean isOfNEType(String type) {
		for (String t : neTypes)
			if (type.equalsIgnoreCase(t))
				return true;
		return false;
	}


	/**
	 * Convert a list of sentences to an array of NE string lists.
	 *
	 * This is an end-user wrapper of the other public functions. */
	public ArrayList<LinkedList<NamedEntity>> extractFromSentences(List<Sentence> sentences) {
		String[][] tokens = tokenizeSentences(sentences);
		return extractFromTokenized(sentences, tokens);
	}


	/**
	 * Convert a list of sentences to a token matrix for the purposes
	 * of NE extraction.
	 */
	public String[][] tokenizeSentences(List<Sentence> sentences) {
		String[][] tokens = new String[sentences.size()][];
		int i = 0;
		for (Sentence s: sentences)
			tokens[i++] = NETaggerWrapper.tokenize(s.getText());
		return tokens;
	}

	/**
	 * Extract NEs (untokenized) from tokenized sentences.
	 */
	public ArrayList<LinkedList<NamedEntity>> extractFromTokenized(List<Sentence> sentences, String[][] tokens) {
		ArrayList<LinkedList<NamedEntity>> NEs = new ArrayList<LinkedList<NamedEntity>>(sentences.size());
		List<HashSet<String>> NEdedup = new ArrayList<HashSet<String>>(sentences.size());
		for (int i = 0; i < sentences.size(); i++) {
			NEs.add(new LinkedList<NamedEntity>());
			NEdedup.add(new HashSet<String>());
		}

		/* We apply each available NE tagger in turn. */
		for (int neId : neIds) {
			String[][] recognizedNEs = NETaggerWrapper.extractNes(tokens, neId);

			int i = 0;
			for (Sentence s : sentences) {
				for (int j = 0; j < recognizedNEs[i].length; j++) {
					String NEtok = recognizedNEs[i][j];
					String NEtext = OpenNLPWrapper.untokenize(NEtok, s.getText());
					NEtext = NEtext.trim();
					if (NEtext.length() == 0) // ignore empty NEs
						continue;

					NamedEntity NE = new NamedEntity(NEtext);
					NE.setScore(1);
					// TODO: Additional context-based NE scoring

					System.err.println("NEtok " + NEtok + " NE " + NE.getText()
							+ " score " + NE.getScore()
							+ " (sencence " + s.getText() + " score " + s.getScore() + ")");

					if (NEdedup.get(i).contains(NEtext))
						continue;
					NEdedup.get(i).add(NEtext);
					NEs.get(i).add(NE);
				}
				i++;
			}
		}

		return NEs;
	}


	/**
	 * Kick-off, initializing static classes we are dependent on.
	 */
	public static void initialize() throws Exception {
		NETaggerWrapper.initialize();
		OpenNLPWrapper.initialize();
	}
}
