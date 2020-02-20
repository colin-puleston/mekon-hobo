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

package uk.ac.manchester.cs.mekon.gui;

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

		DisplayList getDisplayList() {

			return displayList;
		}

		List<E> getEntityList() {

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

				if (filter == null || filter.pass(element.getText())) {

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

	public void addListListener(GListListener<E> listListener) {

		listListeners.add(listListener);
	}

	public void addSelectionListener(GSelectionListener<E> selectionListener) {

		selectionListeners.add(selectionListener);
	}

	public void addEntity(E entity, GCellDisplay display) {

		model.add(entity, display);
		revalidate();

		pollListListenersForAdded(entity);
	}

	public void removeEntity(E entity) {

		currentSelections.remove(entity);

		model.remove(entity);
		revalidate();

		pollListListenersForRemoved(entity);
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

		model.clear();
		revalidate();

		currentSelections.clear();
	}

	public void select(E entity) {

		setSelectedValue(model.getElement(entity), true);
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

	public E getSelectedEntity() {

		return currentSelections.isEmpty() ? null : currentSelections.get(0);
	}

	public List<E> getSelectedEntities() {

		return new ArrayList<E>(currentSelections);
	}

	private void updateSelections() {

		List<E> newSelections = extractEntities(getSelectedValuesList());
		List<E> addedSelections = new ArrayList<E>(newSelections);
		List<E> removedSelections = new ArrayList<E>(currentSelections);

		addedSelections.removeAll(currentSelections);
		removedSelections.removeAll(newSelections);

		currentSelections = newSelections;

		pollForAddedSelections(removedSelections);
		pollForAddedSelections(addedSelections);
	}

	private void restoreSelections() {

		for (E selection : currentSelections) {

			select(selection);
		}
	}

	private void clearSelections() {

		List<E> clearedSelections = new ArrayList<E>(currentSelections);

		currentSelections.clear();

		for (E selection : currentSelections) {

			selectionListeners.pollForDeselected(selection);
		}
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

	private List<E> extractEntities(List<GListElement<E>> elements) {

		List<E> entities = new ArrayList<E>();

		for (GListElement<E> element : elements) {

			entities.add(element.getEntity());
		}

		return entities;
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
