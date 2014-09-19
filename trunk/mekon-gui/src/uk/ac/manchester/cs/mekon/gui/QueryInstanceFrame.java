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
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class QueryInstanceFrame extends InstantiationFrame {

	static private final long serialVersionUID = -1;

	static private final JPanel BLANK_PANEL = new JPanel();

	static private final String QUERY_LABEL = "Query";
	static private final String MATCHES_TITLE = "Query Matches";
	static private final String EXECUTE_ACTION_LABEL = "Execute";

	private int matchesTabIndex = 0;

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

			protected boolean isTargetSlot(ISlot slot) {

				return true;
			}

			Propagator(IFrame frame) {

				super(MatchesClearer.this);

				propagateFrom(frame);
			}
		}

		public void onUpdated() {

			clearMatchesList();
		}

		MatchesClearer(IFrame frame) {

			new Propagator(frame);
		}
	}

	private class MatchDisplayer extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity entity) {

			showMatch(entity);
		}
	}

	QueryInstanceFrame(CFramesTree modelTree, IFrame frame) {

		super(modelTree, frame);

		matchesTabIndex = addAspectTab(MATCHES_TITLE, BLANK_PANEL);

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

		IMatches matches = getIStore().match(getFrame());

		if (matches.anyMatches()) {

			showMatchesList(matches);
		}
		else {

			showMessage("No matches for supplied query");
		}
	}

	private void showMatchesList(IMatches matches) {

		JComponent comp = createMatchesComponent(matches);

		getAspectTabs().setComponentAt(matchesTabIndex, comp);
		getAspectTabs().setSelectedIndex(matchesTabIndex);
	}

	private void clearMatchesList() {

		getAspectTabs().setComponentAt(matchesTabIndex, BLANK_PANEL);
	}

	private JComponent createMatchesComponent(IMatches matches) {

		return new JScrollPane(createMatchesList(matches));
	}

	private GList<CIdentity> createMatchesList(IMatches matches) {

		GList<CIdentity> list = new GList<CIdentity>(!matches.ranked());

		list.addSelectionListener(new MatchDisplayer());

		for (CIdentity match : matches.getMatches()) {

			list.addEntity(match, new GCellDisplay(match.getLabel()));
		}

		return list;
	}

	private void showMatch(CIdentity id) {

		ConcreteInstanceFrame.display(getModelTree(), getIStore(), id);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
