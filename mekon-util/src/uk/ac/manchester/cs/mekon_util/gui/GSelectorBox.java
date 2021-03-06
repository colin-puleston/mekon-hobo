/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon_util.gui;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public abstract class GSelectorBox<V> extends JComboBox<Object> {

	static private final long serialVersionUID = -1;

	private Map<String, V> valuesByLabel = new HashMap<String, V>();

	private class SelectionListener implements ItemListener {

		public void itemStateChanged(ItemEvent event) {

			onSelection(valuesByLabel.get((String)event.getItem()));
		}
	}

	public void addOption(String label, V value) {

		addItem(label);
		valuesByLabel.put(label, value);
	}

	public void activate() {

		if (valuesByLabel.size() > 1) {

			addItemListener(new SelectionListener());
		}
		else {

			setEnabled(false);
		}
	}

	protected abstract void onSelection(V value);
}
