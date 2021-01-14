/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.remote;

import java.util.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
abstract class EntityManager<E> {

	private EntitiesPanel<E> panel;

	private List<E> entities = new ArrayList<E>();
	private EntityComparator entityComparator = new EntityComparator();

	private class EntityComparator implements Comparator<E> {

		public int compare(E first, E second) {

			return getSorterName(first).compareTo(getSorterName(second));
		}
	}

	private class UpdateProcess implements Runnable {

		public void run() {

			while (true) {

				waitABit();
				updateFromServer();
			}
		}

		UpdateProcess() {

			new Thread(this).start();
		}

		private void waitABit() {

			try {

				Thread.sleep(10000);
			}
			catch (InterruptedException e) {

				throw new RuntimeException(e);
			}
		}
	}

	EntityManager(EntitiesPanel<E> panel) {

		this.panel = panel;

		new UpdateProcess();
	}

	synchronized void updateFromServer() {

		List<E> serverEntities = getServerEntities();

		if (!entitySetsEqual(entities, serverEntities)) {

			entities = serverEntities;

			onUpdate(true);
		}
	}

	synchronized void addEntity() {

		E entity = addServerEntity();

		if (entity != null) {

			entities.add(entity);

			onUpdate(true);
		}
	}

	synchronized void editEntity(E entity) {

		E editedEntity = editServerEntity(entity);

		if (editedEntity != null) {

			entities.remove(entity);
			entities.add(editedEntity);

			onUpdate(true);
		}
	}

	synchronized void deleteEntity(E entity) {

		if (confirmDeletion(entity)) {

			deleteServerEntity(entity);
			entities.remove(entity);

			onUpdate(false);
		}
	}

	synchronized List<E> getEntities() {

		return entities;
	}

	synchronized E getEntity(int index) {

		return entities.get(index);
	}

	abstract E addServerEntity();

	abstract E editServerEntity(E entity);

	abstract boolean deleteServerEntity(E entity);

	abstract List<E> getServerEntities();

	abstract String describe(E entity);

	abstract String getSorterName(E entity);

	private void onUpdate(boolean sort) {

		if (sort) {

			sortEntities();
		}

		panel.updateDisplay();
	}

	private void sortEntities() {

		TreeSet<E> sorter = new TreeSet<E>(entityComparator);

		sorter.addAll(entities);

		entities.clear();
		entities.addAll(sorter);
	}

	private boolean entitySetsEqual(List<E> list1, List<E> list2) {

		return entitiesAsSet(list1).equals(entitiesAsSet(list2));
	}

	private Set<E> entitiesAsSet(List<E> list) {

		return new HashSet<E>(list);
	}

	private boolean confirmDeletion(E entity) {

		return obtainConfirmation("Deleting " + describe(entity));
	}

	private boolean obtainConfirmation(String msg) {

		return obtainConfirmationOption(msg) == JOptionPane.OK_OPTION;
	}

	private int obtainConfirmationOption(String msg) {

		return JOptionPane.showConfirmDialog(
					null,
					msg,
					"Confirm?",
					JOptionPane.OK_CANCEL_OPTION);
	}
}
