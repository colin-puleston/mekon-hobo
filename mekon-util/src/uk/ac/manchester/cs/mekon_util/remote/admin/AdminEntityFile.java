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

package uk.ac.manchester.cs.mekon_util.remote.admin;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
abstract class AdminEntityFile<E, K> {

	private File file;

	private List<K> keys = new ArrayList<K>();
	private List<E> entities = new ArrayList<E>();

	private Map<K, E> entityMap = new HashMap<K, E>();

	AdminEntityFile(File adminDirectory, String filename) {

		file = new File(adminDirectory, filename);

		parseFile();
	}

	void addEntity(E entity) {

		addToStructures(entity);
		renderFile();
	}

	void removeEntity(K key) {

		removeFromStructures(entityMap.get(key));
		renderFile();
	}

	void replaceEntity(E oldEntity, E newEntity) {

		removeFromStructures(oldEntity);
		addToStructures(newEntity);

		renderFile();
	}

	List<K> getKeys() {

		return keys;
	}

	List<E> getEntities() {

		return entities;
	}

	E lookForEntity(K key) {

		return entityMap.get(key);
	}

	boolean containsEntity(K key) {

		return entityMap.get(key) != null;
	}

	abstract String getRootTag();

	abstract String getEntityTag();

	abstract void renderEntity(E entity, XNode entityNode);

	abstract E parseEntity(XNode entityNode);

	abstract K getEntityKey(E entity);

	private void addToStructures(E entity) {

		K key = getEntityKey(entity);

		keys.add(key);
		entities.add(entity);
		entityMap.put(key, entity);
	}

	private void removeFromStructures(E entity) {

		K key = getEntityKey(entity);

		keys.remove(key);
 		entities.remove(entity);
		entityMap.remove(key);
	}

	private void renderFile() {

		XDocument doc = new XDocument(getRootTag());
		XNode rootNode = doc.getRootNode();

		for (E entity : entities) {

			renderEntity(entity, rootNode.addChild(getEntityTag()));
		}

		doc.writeToFile(file);
	}

	private void parseFile() {

		XNode rootNode = new XDocument(file).getRootNode();

		for (XNode entityNode : rootNode.getChildren(getEntityTag())) {

			addEntity(parseEntity(entityNode));
		}
	}
}
