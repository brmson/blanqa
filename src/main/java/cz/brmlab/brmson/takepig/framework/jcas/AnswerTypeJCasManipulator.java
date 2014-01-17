package cz.brmlab.brmson.takepig.framework.jcas;

import java.util.Iterator;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.oaqa.model.AnswerType;

public class AnswerTypeJCasManipulator {
	/**
	 * Convert UIMA data model
	 * 
	 * @param questionView
	 * @return answerType
	 */
	public static String loadAnswerType(JCas questionView) {
		String result = null;
		AnnotationIndex<?> index = questionView.getAnnotationIndex(AnswerType.type);
		Iterator<?> it = index.iterator();

		if (it.hasNext()) {
			AnswerType atype = (AnswerType) it.next();
			result = atype.getLabel();
		}
		return result;
	}

	/**
	 * Stores (overwrite) answer type in a view
	 * 
	 * @param questionView
	 * @param type
	 */
	public static void storeAnswerType(JCas questionView, String type) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = questionView.getJFSIndexRepository().getAllIndexedFS(
				AnswerType.type);
		while (it.hasNext()) {
			AnswerType oaqaType = (AnswerType) it.next();
			oaqaType.removeFromIndexes();
		}

		AnswerType oaqaType = new AnswerType(questionView);
		oaqaType.setLabel(type);
		oaqaType.addToIndexes();
	}
}
