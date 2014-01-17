package cz.brmlab.brmson.takepig.framework.jcas;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

/**
 * View accessor with a create-on-first-access pattern.
 * 
 * @author ruil */

public class ViewManager {

	public static JCas getView(JCas jcas, ViewType type) throws CASException {
		return getOrCreateView(jcas, type);
	}

	public static JCas getOrCreateView(JCas jcas, ViewType type)
			throws CASException {
		String viewName = type.toString();
		try {
			return jcas.getView(viewName);
		} catch (Exception e) {
			jcas.createView(viewName);
			return jcas.getView(viewName);
		}
	}

}
