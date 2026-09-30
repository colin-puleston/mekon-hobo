/*
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

package uk.ac.manchester.cs.mekon_util.gui;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Colin Puleston
 */
public class GListReorderDialog<E> extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";

	static private final String UP_LABEL = "Up";
	static private final String DOWN_LABEL = "Down";

	private List<E> initialOrder;
	private List<E> currentOrder = new ArrayList<E>();

	private E currentSelection = null;

	private DisplayList displayList = new DisplayList();

	private OkButton okButton = new OkButton();

	private UpButton upButton = new UpButton();
	private DownButton downButton = new DownButton();

	private abstract class DialogButton extends GButton {

		static private final long serialVersionUID = -1;

		DialogButton(String label) {

			super(label);
		}

		JComponent createButtonComponent() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(this, getLocationWithinButtonComponent());

			return panel;
		}

		String getLocationWithinButtonComponent() {

			return BorderLayout.CENTER;
		}
	}

	private class OkButton extends DialogButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		OkButton() {

			super(OK_LABEL);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(reordered());
		}
	}

	private class CancelButton extends DialogButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			currentOrder = initialOrder;

			dispose();
		}

		CancelButton() {

			super(CANCEL_LABEL);
		}
	}

	private abstract class NavigationButton extends DialogButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			int fromIndex = currentOrder.indexOf(currentSelection);
			int toIndex = fromIndex + directionShiftValue();

			currentOrder.remove(fromIndex);
			currentOrder.add(toIndex, currentSelection);

			displayList.repopulate(toIndex);
		}

		NavigationButton(String label) {

			super(label);

			setEnabled(false);
		}

		JComponent createButtonComponent() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(this, getLocationWithinButtonComponent());

			return panel;
		}

		void updateEnabling() {

			setEnabled(canMoveInDirection());
		}

		abstract int directionFinalIndex();

		abstract int directionShiftValue();

		abstract String getLocationWithinButtonComponent();

		private boolean canMoveInDirection() {

			return currentSelection != null && finalDirectionEntitySelected();
		}

		private boolean finalDirectionEntitySelected() {

			return currentSelection != currentOrder.get(directionFinalIndex());
		}
	}

	private class UpButton extends NavigationButton {

		static private final long serialVersionUID = -1;

		UpButton() {

			super(UP_LABEL);
		}

		int directionFinalIndex() {

			return 0;
		}

		int directionShiftValue() {

			return -1;
		}

		String getLocationWithinButtonComponent() {

			return BorderLayout.SOUTH;
		}
	}

	private class DownButton extends NavigationButton {

		static private final long serialVersionUID = -1;

		DownButton() {

			super(DOWN_LABEL);
		}

		int directionFinalIndex() {

			return currentOrder.size() - 1;
		}

		int directionShiftValue() {

			return 1;
		}

		String getLocationWithinButtonComponent() {

			return BorderLayout.NORTH;
		}
	}

	private class DisplayList extends GList<E> {

		static private final long serialVersionUID = -1;

		private class CurrentSelectionListener extends GSelectionListener<E> {

			protected void onSelected(E selected) {

				currentSelection = selected;

				upButton.updateEnabling();
				downButton.updateEnabling();

				okButton.updateEnabling();
			}

			protected void onDeselected(E selected) {
			}

			CurrentSelectionListener() {

				addSelectionListener(this);
			}
		}

		DisplayList() {

			super(false, false);
		}

		void initialise() {

			populate();

			new CurrentSelectionListener();
		}

		void repopulate(int selectedIndex) {

			clearList();
			populate();

			setSelectedIndex(selectedIndex);
		}

		private void populate() {

			for (E entity : currentOrder) {

				addEntity(entity, createEntityDisplay(entity));
			}
		}
	}

	public GListReorderDialog(String title, List<E> initialOrder) {

		super(title, true);

		this.initialOrder = initialOrder;

		currentOrder.addAll(initialOrder);
	}

	public void display() {

		displayList.initialise();

		display(creatMainPanel());
	}

	public boolean reordered() {

		return !currentOrder.equals(initialOrder);
	}

	public List<E> getCurrentOrder() {

		return currentOrder;
	}

	protected GCellDisplay createEntityDisplay(E entity) {

		return new GCellDisplay(entity.toString());
	}

	private JComponent creatMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createReorderPanel(), BorderLayout.CENTER);
		panel.add(createExitButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createReorderPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(displayList), BorderLayout.CENTER);
		panel.add(createNavigationButtonsPanel(), BorderLayout.EAST);

		return panel;
	}

	private JComponent createExitButtonsPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(okButton.createButtonComponent());
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new CancelButton().createButtonComponent());

		return panel;
	}

	private JComponent createNavigationButtonsPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(upButton.createButtonComponent());
		panel.add(Box.createVerticalStrut(10));
		panel.add(downButton.createButtonComponent());

		return panel;
	}

	private JComponent createButtonComponent(String panelLocation) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(this, panelLocation);

		return panel;
	}
}
