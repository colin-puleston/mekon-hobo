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

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Light-weight representation of the hierarchical relationships in a
 * {@link CFrame}-hierarchy, with frames being specified solely by the
 * relevant {@link CIdentity} objects.
 *
 * @author Colin Puleston
 */
public class CHierarchy {

	private CIdentity rootFrameId;
	private Map<CIdentity, Node> nodes = new HashMap<CIdentity, Node>();

	private class Node {

		final List<CIdentity> subIds = new ArrayList<CIdentity>();
		final KListMap<Object, Object> annotations = new KListMap<Object, Object>();

		Node(CIdentity frameId) {

			nodes.put(frameId, this);
		}

		void addSub(CIdentity subId) {

			subIds.add(subId);
		}

		void addAnnotation(Object key, Object value) {

			annotations.add(key, value);
		}

		void addAnnotations(Object key, List<Object> values) {

			annotations.addAll(key, values);
		}
	}

	/**
	 * Constructs representation of hierarchy under the specified
	 * root-frame (which is not necessarily the root-frame of the entire
	 * frame-hierarchy for the relevat model).
	 *
	 * @param rootFrame Root-frame for hierarchy to be represented
	 */
	public CHierarchy(CFrame rootFrame) {

		this(rootFrame.getIdentity());

		addSubTree(rootFrame);
	}

	/**
	 * Provides the identity of the root-frame
	 *
	 * @return Identity of the root-frame
	 */
	public CIdentity getRootFrameId() {

		return rootFrameId;
	}

	/**
	 * Provides the identities of all sub-frames of the specified frame.
	 *
	 * @param frameId Identity of relevant frame
	 * @return Identities of all sub-frames of specified frame
	 * @throws KAccessException if specified frame not present in hierarchy
	 */
	public List<CIdentity> getSubFrameIds(CIdentity frameId) {

		return getNode(frameId).subIds;
	}

	/**
	 * Provides the keys for all annotations on the specified frame.
	 *
	 * @param frameId Identity of relevant frame
	 * @return Keys for all annotations on specified frame
	 * @throws KAccessException if specified frame not present in hierarchy
	 */
	public Set<Object> getAnnotationKeys(CIdentity frameId) {

		return getNode(frameId).annotations.keySet();
	}

	/**
	 * Provides all values of the specified annotation on the specified
	 * frame.
	 *
	 * @param frameId Identity of relevant frame
	 * @param key Key for relevant annotation
	 * @return All values of specified annotation on specified frame
	 * @throws KAccessException if specified frame not present in hierarchy
	 */
	public List<Object> getAnnotationValues(CIdentity frameId, Object key) {

		return getNode(frameId).annotations.getList(key);
	}

	CHierarchy(CIdentity rootFrameId) {

		this.rootFrameId = rootFrameId;

		new Node(rootFrameId);
	}

	boolean addSub(CIdentity frameId, CIdentity subFrameId) {

		Node node = getNode(frameId);
		Node subNode = nodes.get(subFrameId);
		boolean newSub = (subNode == null);

		if (newSub) {

			subNode = new Node(subFrameId);
		}

		node.addSub(subFrameId);

		return newSub;
	}

	void addAnnotation(CIdentity frameId, Object key, Object value) {

		getNode(frameId).addAnnotation(key, value);
	}

	private void addSubTree(CFrame subRoot) {

		Node subRootNode = new Node(subRoot.getIdentity());

		addAnnotations(subRoot, subRootNode);

		for (CFrame sub : subRoot.getSubs()) {

			subRootNode.addSub(sub.getIdentity());
			addSubTree(sub);
		}
	}

	private void addAnnotations(CFrame frame, Node node) {

		CAnnotations annos = frame.getAnnotations();

		for (Object key : annos.getKeys()) {

			node.addAnnotations(key, annos.getAll(key));
		}
	}

	private Node getNode(CIdentity frameId) {

		Node node = nodes.get(frameId);

		if (node == null) {

			throw new KAccessException("Frame not present in hierarchy: " + frameId);
		}

		return node;
	}
}
