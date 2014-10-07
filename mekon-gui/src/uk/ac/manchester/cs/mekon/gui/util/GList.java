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

package uk.ac.manchester.cs.mekon.gui.util;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
public class GList<E> extends JList {

	static private final long serialVersionUID = -1;

	private LocalListModel model;
	private E selectedEntity = null;

	private GSelectionListeners<E> selectionListeners = new GSelectionListeners<E>();

	private class SelectionProcessor implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {

			if (!event.getValueIsAdjusting()) {

				checkUpdateSelection();
			}
		}
	}

	private class DisplayElement extends GCellDisplay {

		private E entity;

		DisplayElement(E entity, GCellDisplay display) {

			super(display);

			this.entity = entity;
		}

		E getEntity() {

			return entity;
		}
	}

	private class DisplayList extends GCellDisplaySortedList<DisplayElement> {

		DisplayList(boolean orderAlphabetically) {

			super(orderAlphabetically);
		}

		GCellDisplay getDisplay(DisplayElement element) {

			return element;
		}
	}

	private class LocalListModel extends DefaultListModel {

		static private final long serialVersionUID = -1;

		private DisplayList displayList;
		private boolean upToDate = false;

		public void clear() {

			super.clear();
			displayList.clear();
		}

		public Object getElementAt(int index) {

			checkUpToDate();

			return super.getElementAt(index);
		}

		public int getSize() {

			checkUpToDate();

			return super.getSize();
		}

		LocalListModel(boolean orderAlphabetically) {

			displayList = new DisplayList(orderAlphabetically);
		}

		void add(E entity, GCellDisplay display) {

			displayList.add(new DisplayElement(entity, display));

			upToDate = false;
		}

		void remove(E entity) {

			DisplayElement element = getDisplayElementOrNull(entity);

			if (element != null) {

				displayList.remove(element);

				upToDate = false;
			}
		}

		void applyFilter(GLexicalFilter filter) {

			super.clear();
			addElements(filter);

			checkRestoreSelection();
		}

		DisplayList getDisplayList() {

			return displayList;
		}

		List<E> getEntityList() {

			List<E> entities = new ArrayList<E>();

			for (DisplayElement element : displayList.asList()) {

				entities.add(element.getEntity());
			}

			return entities;
		}

		DisplayElement getDisplayElement(E entity) {

			DisplayElement element = getDisplayElementOrNull(entity);

			if (element == null) {

				throw new KAccessException("Cannot find element: " + element);
			}

			return element;
		}

		private DisplayElement getDisplayElementOrNull(E entity) {

			for (DisplayElement element : displayList.asList()) {

				if (element.getEntity().equals(entity)) {

					return element;
				}
			}

			return null;
		}

		private void checkUpToDate() {

			if (!upToDate) {

				upToDate = true;

				applyFilter(null);
			}
		}

		private void addElements(GLexicalFilter filter) {

			for (DisplayElement element : displayList.asList()) {

				if (filter == null || filter.pass(element.getLabel())) {

					addElement(element);
				}
			}
		}
	}

	public GList(boolean orderAlphabetically) {

		model = new LocalListModel(orderAlphabetically);

		setModel(model);

		GCellRenderers.get().set(this);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectedIndex(-1);
		addListSelectionListener(new SelectionProcessor());
	}

	public void addSelectionListener(GSelectionListener<E> selectionListener) {

		selectionListeners.add(selectionListener);
	}

	public void addEntity(E entity, GCellDisplay display) {

		model.add(entity, display);
	}

	public void removeEntity(E entity) {

		if (selectedEntity == entity) {

			selectedEntity = null;
		}

		model.remove(entity);
	}

	public void applyFilter(GLexicalFilter filter) {

		model.applyFilter(filter);
	}

	public void clearFilter() {

		model.applyFilter(null);
	}

	public void clearList() {

		model.clear();

		selectedEntity = null;
	}

	public void select(E entity) {

		setSelectedValue(model.getDisplayElement(entity), true);
	}

	public List<E> getEntityList() {

		return model.getEntityList();
	}

	public E getEntity(int index) {

		return getEntityList().get(index);
	}

	public boolean containsEntity(E entity) {

		return getEntityList().contains(entity);
	}

	private void checkUpdateSelection() {

		DisplayElement selection = (DisplayElement)getSelectedValue();

		if (selection != null) {

			E entity = selection.getEntity();

			if (entity != selectedEntity) {

				selectedEntity = entity;
				selectionListeners.poll(selectedEntity);
			}
		}
	}

	private void checkRestoreSelection() {

		if (selectedEntity != null) {

			select(selectedEntity);
		}
	}
}
