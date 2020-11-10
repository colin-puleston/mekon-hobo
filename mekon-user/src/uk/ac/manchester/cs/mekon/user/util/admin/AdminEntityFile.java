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

package uk.ac.manchester.cs.mekon.user.util.admin;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
abstract class AdminEntityFile<E, K> {

	private XNode rootNode;

	AdminEntityFile(File adminDirectory, String filename) {

		rootNode = readDocument(adminDirectory, filename).getRootNode();
	}

	Map<K, E> parseAll() {

		Map<K, E> entityMap = new HashMap<K, E>();

		for (XNode entityNode : rootNode.getChildren(getEntityTag())) {

			E entity = parseEntity(entityNode);

			entityMap.put(getEntityMapKey(entity), entity);
		}

		return entityMap;
	}

	abstract String getEntityTag();

	abstract E parseEntity(XNode entityNode);

	abstract K getEntityMapKey(E entity);

	private XDocument readDocument(File adminDirectory, String filename) {

		return new XDocument(new File(adminDirectory, filename));
	}
}
