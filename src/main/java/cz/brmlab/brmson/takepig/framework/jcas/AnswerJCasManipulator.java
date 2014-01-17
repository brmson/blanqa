package cz.brmlab.brmson.takepig.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.oaqa.model.Answer;
import org.oaqa.model.AnswerList;
import org.oaqa.model.SearchResult;

public class AnswerJCasManipulator {
	public static List<Answer> loadAnswers(JCas candidateView, List<String> featureLabels) {
		List<Answer> result = new ArrayList<Answer>();
		Iterator<?> it = candidateView.getJFSIndexRepository().getAllIndexedFS(
				AnswerList.type);

		if (it.hasNext()) {
			AnswerList answers = (AnswerList) it.next();
			if (featureLabels != null && answers.getFeatureLabels() != null) {
				for (int i = 0; i < answers.getFeatureLabels().size(); i++) {
					featureLabels.add(answers.getFeatureLabels(i));
				}
			}
			FSArray answerList = answers.getAnswerList();
			for (int i = 0; i < answerList.size(); i++) {
				Answer a = (Answer) answerList.get(i);
				result.add(a);
			}
		}
		return result;
	}
	
	/**
	 * Store (overwrite) answers into a view
	 * 
	 * @param view
	 *            either candidate view or final answer view
	 * @param answers
	 * @param featureLabels
	 */
	public static void storeAnswers(JCas view, List<Answer> answers, List<String> featureLabels) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = view.getJFSIndexRepository().getAllIndexedFS(
				AnswerList.type);
		while (it.hasNext()) {
			AnswerList answerList = (AnswerList) it.next();
			answerList.removeFromIndexes();
		}

		FSArray answerArray = new FSArray(view, answers.size());
		answerArray.addToIndexes();

		for (int i = 0; i < answers.size(); i++) {
			Answer a = answers.get(i);
			a.addToIndexes();
			answerArray.set(i, a);
		}

		AnswerList answerList = new AnswerList(view);
		answerList.setAnswerList(answerArray);

		StringArray featureLabelList = new StringArray(view, featureLabels.size());
		int j = 0;
		for (String l: featureLabels) {
			featureLabelList.set(j, l);
			j++;
		}
		featureLabelList.addToIndexes();
		answerList.setFeatureLabels(featureLabelList);

		answerList.addToIndexes();
	}
}
