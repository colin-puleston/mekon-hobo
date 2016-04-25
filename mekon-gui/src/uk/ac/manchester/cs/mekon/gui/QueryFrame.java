/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.util.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class QueryFrame extends InstantiationFrame {

	static private final long serialVersionUID = -1;

	static private final String QUERY_LABEL = "Query";
	static private final String MATCHES_TITLE = "Matches";
	static private final String EXECUTE_ACTION_LABEL = "Execute";

	private class ExecuteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			execute();
		}

		ExecuteButton() {

			super(EXECUTE_ACTION_LABEL);
		}
	}

	private class MatchesClearer implements KUpdateListener {

		private class Propagator extends ISlotUpdateListenerPropagator {

			protected boolean targetSlot(ISlot slot) {

				return true;
			}

			Propagator(IFrame frame) {

				super(MatchesClearer.this);

				propagateFrom(frame);
			}
		}

		public void onUpdated() {

			checkClearMatches();
		}

		MatchesClearer(IFrame frame) {

			new Propagator(frame);
		}
	}

	private class MatchDisplayer extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity entity) {

			getActions().retrieveAndDisplayInstance(entity);
		}
	}

	QueryFrame(CFramesTree modelTree, IFrame frame) {

		super(modelTree, frame);

		new MatchesClearer(frame);
	}

	String getCategoryLabel() {

		return QUERY_LABEL;
	}

	JComponent createControlsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new ExecuteButton(), BorderLayout.EAST);

		return panel;
	}

	private void execute() {

		IMatches matches = getActions().executeQuery(getFrame());

		if (matches.anyMatches()) {

			showMatches(matches);
		}
	}

	private void showMatches(IMatches matches) {

		GSplitPane panel = new GSplitPane();

		panel.setHorizontalSplit(false);
		panel.setTopComponent(getInstanceComponent());
		panel.setBottomComponent(createMatchesComponent(matches));

		resetMainComponent(panel);
	}

	private void checkClearMatches() {

		checkResetInstanceComponent();
	}

	private JComponent createMatchesComponent(IMatches matches) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createMatchesLabelComponent(), BorderLayout.NORTH);
		panel.add(createMatchesListComponent(matches), BorderLayout.CENTER);

		return panel;
	}

	private JComponent createMatchesLabelComponent() {

		JLabel label = new JLabel(MATCHES_TITLE);

		GFonts.setMedium(label);
		label.setBackground(Color.BLUE);

		return label;
	}

	private JComponent createMatchesListComponent(IMatches matches) {

		return new JScrollPane(createMatchesList(matches));
	}

	private GList<CIdentity> createMatchesList(IMatches matches) {

		GList<CIdentity> list = new GList<CIdentity>(!matches.ranked());

		list.addSelectionListener(new MatchDisplayer());

		for (CIdentity match : matches.getAllMatches()) {

			list.addEntity(match, new GCellDisplay(match.getLabel()));
		}

		return list;
	}
}
