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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Colin Puleston
 */
public class GList<E> extends JList<GListElement<E>> {

	static private final long serialVersionUID = -1;

	private LocalListModel model;
	private List<E> currentSelections = new ArrayList<E>();

	private List<GListListener<E>> listListeners = new ArrayList<GListListener<E>>();
	private GSelectionListeners<E> selectionListeners = new GSelectionListeners<E>();

	private class SelectionProcessor implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {

			if (!event.getValueIsAdjusting()) {

				updateSelections();
			}
		}
	}

	private class DisplayList extends GCellDisplaySortedList<GListElement<E>> {

		DisplayList(boolean orderAlphabetically) {

			super(orderAlphabetically);
		}

		GCellDisplay getDisplay(GListElement<E> element) {

			return element;
		}
	}

	private class LocalListModel extends DefaultListModel<GListElement<E>> {

		static private final long serialVersionUID = -1;

		private DisplayList displayList;
		private boolean upToDate = false;

		public void clear() {

			super.clear();

			displayList.clear();
		}

		public GListElement<E> getElementAt(int index) {

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

			displayList.add(new GListElement<E>(entity, display));

			upToDate = false;
		}

		void remove(E entity) {

			GListElement<E> element = getElementOrNull(entity);

			if (element != null) {

				displayList.remove(element);

				upToDate = false;
			}
		}

		void applyFilter(GLexicalFilter filter) {

			super.clear();

			addElements(filter);
			restoreSelections();
		}

		List<E> getEntities() {

			return extractEntities(displayList.asList());
		}

		GListElement<E> getElement(E entity) {

			GListElement<E> element = getElementOrNull(entity);

			if (element == null) {

				throw new RuntimeException("Cannot find element for: " + entity);
			}

			return element;
		}

		private GListElement<E> getElementOrNull(E entity) {

			for (GListElement<E> element : displayList.asList()) {

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

			for (GListElement<E> element : displayList.asList()) {

				if (filter == null || filter.pass(element.getFilterText())) {

					addElement(element);
				}
			}
		}
	}

	public GList(boolean multiSelect, boolean orderAlphabetically) {

		model = new LocalListModel(orderAlphabetically);

		setModel(model);
		setSelectionMode(getSelectionMode(multiSelect));
		setSelectedIndex(-1);

		GCellRenderers.get().set(this);

		addListSelectionListener(new SelectionProcessor());
	}

	public void addListListener(GListListener<E> listListener) {

		listListeners.add(listListener);
	}

	public void addSelectionListener(GSelectionListener<E> selectionListener) {

		selectionListeners.add(selectionListener);
	}

	public void addEntity(E entity) {

		addEntity(entity, new GCellDisplay(entity.toString()));
	}

	public void addEntity(E entity, GCellDisplay display) {

		model.add(entity, display);
		revalidate();

		pollListListenersForAdded(entity);
	}

	public void removeEntity(E entity) {

		boolean wasSelected = currentSelections.remove(entity);

		model.remove(entity);
		revalidate();

		pollListListenersForRemoved(entity);

		if (wasSelected) {

			selectionListeners.pollForDeselected(entity);
		}
	}

	public void applyFilter(GLexicalFilter filter) {

		model.applyFilter(filter);
		revalidate();
	}

	public void clearFilter() {

		model.applyFilter(null);
		revalidate();
	}

	public void clearList() {

		List<E> entities = getEntities();
		List<E> selections = getSelectedEntities();

		model.clear();
		revalidate();

		currentSelections.clear();

		for (E entity : entities) {

			pollListListenersForRemoved(entity);
		}

		pollForRemovedSelections(selections);
	}

	public void select(E entity) {

		setSelectedValue(model.getElement(entity), true);
	}

	public boolean anyElements() {

		return model.getSize() != 0;
	}

	public boolean anySelections() {

		return !currentSelections.isEmpty();
	}

	public List<E> getEntities() {

		return model.getEntities();
	}

	public E getEntity(int index) {

		return getEntities().get(index);
	}

	public boolean containsEntity(E entity) {

		return getEntities().contains(entity);
	}

	public boolean selectedEntity(E entity) {

		return currentSelections.contains(entity);
	}

	public E getSelectedEntity() {

		return currentSelections.isEmpty() ? null : currentSelections.get(0);
	}

	public List<E> getSelectedEntities() {

		return new ArrayList<E>(currentSelections);
	}

	private int getSelectionMode(boolean multiSelect) {

		return multiSelect
				? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
				: ListSelectionModel.SINGLE_SELECTION;
	}

	private void updateSelections() {

		List<E> newSelections = extractEntities(getSelectedValuesList());
		List<E> addedSelections = new ArrayList<E>(newSelections);
		List<E> removedSelections = new ArrayList<E>(currentSelections);

		addedSelections.removeAll(currentSelections);
		removedSelections.removeAll(newSelections);

		currentSelections = newSelections;

		pollForRemovedSelections(removedSelections);
		pollForAddedSelections(addedSelections);
	}

	private void restoreSelections() {

		for (E selection : new ArrayList<E>(currentSelections)) {

			if (containsEntity(selection)) {

				select(selection);
			}
			else {

				currentSelections.remove(selection);
			}
		}
	}

	private void clearSelections() {

		List<E> clearedSelections = new ArrayList<E>(currentSelections);

		currentSelections.clear();
		pollForRemovedSelections(currentSelections);
	}

	private List<E> extractEntities(List<GListElement<E>> elements) {

		List<E> entities = new ArrayList<E>();

		for (GListElement<E> element : elements) {

			entities.add(element.getEntity());
		}

		return entities;
	}

	private void pollForAddedSelections(List<E> entities) {

		for (E entity : entities) {

			selectionListeners.pollForSelected(entity);
		}
	}

	private void pollForRemovedSelections(List<E> entities) {

		for (E entity : entities) {

			selectionListeners.pollForDeselected(entity);
		}
	}

	private void pollListListenersForAdded(E entity) {

		for (GListListener<E> listener : copyListListeners()) {

			listener.onAdded(entity);
		}
	}

	private void pollListListenersForRemoved(E entity) {

		for (GListListener<E> listener : copyListListeners()) {

			listener.onRemoved(entity);
		}
	}

	private List<GListListener<E>> copyListListeners() {

		return new ArrayList<GListListener<E>>(listListeners);
	}
}
