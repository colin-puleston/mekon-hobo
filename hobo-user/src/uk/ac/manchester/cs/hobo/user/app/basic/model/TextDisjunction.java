package uk.ac.manchester.cs.hobo.user.app.basic.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class TextDisjunction {

	private List<String> disjuncts = new ArrayList<String>();

	public TextDisjunction() {
	}

	public TextDisjunction(Collection<String> disjuncts) {

		this.disjuncts.addAll(disjuncts);
	}

	public void addDisjunct(String disjunct) {

		disjuncts.add(disjunct);
	}

	public List<String> getDisjuncts() {

		return new ArrayList<String>(disjuncts);
	}

	public boolean anyDisjuncts() {

		return !disjuncts.isEmpty();
	}
}