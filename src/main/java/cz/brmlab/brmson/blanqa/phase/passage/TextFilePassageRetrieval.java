package cz.brmlab.brmson.blanqa.phase.passage;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.oaqa.model.SearchResult;
import org.oaqa.model.Document;

import cz.brmlab.brmson.takepig.basephase.AbstractPassageRetrieval;


/**
 * Passage retrieval that will simply return a text file contents.
 *
 * Just for testing.
 *
 * Example:
 *   - inherit: jdbc.sqlite.cse.phase
 *     name: passage-retrieval
 *     options: |
 *       - inherit: phases.passage.textfile
 *         file: "data/sample.txt"
 */

public class TextFilePassageRetrieval extends AbstractPassageRetrieval {
	protected String fileName;
	protected String text;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		fileName = (String) aContext.getConfigParameterValue("file");
		if (fileName == null)
			throw new IllegalArgumentException(String.format("Parameter 'file' must be specified"));

		text = "";
		File file = new File(fileName);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			while (in.ready()) {
				String line = in.readLine();
				if (line != null && !line.equals("")) {
					text += line;
					text += " ";
				}
			}
			in.close();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	public List<SearchResult> retrieveResults(String questionText, String answerType,
			List<String> keyterms, List<String> keyphrases) {

		List<SearchResult> results = new ArrayList<SearchResult>();

		Document d = new Document(this.jcas);
		d.setDocId(fileName);
		d.setUri("file://" + fileName);
		d.setText(text);
		d.setScore(1);
		results.add(d);

		return results;
	}
}
