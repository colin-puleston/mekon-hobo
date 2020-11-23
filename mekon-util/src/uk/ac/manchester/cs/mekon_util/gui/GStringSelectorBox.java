/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon_util.gui;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class GStringSelectorBox extends GSelectorBox<String> {

	static private final long serialVersionUID = -1;

	public void addOptions(List<String> values) {

		for (String value : values) {

			addOption(value);
		}
	}

	public void addOption(String value) {

		addOption(value, value);
	}
}
