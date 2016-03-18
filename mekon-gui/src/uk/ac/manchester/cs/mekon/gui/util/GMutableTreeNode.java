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
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
public abstract class GMutableTreeNode implements MutableTreeNode {

	static private final long serialVersionUID = -1;

	public Enumeration<GNode> children() {

		return Collections.enumeration(ensureChildren());
	}

	public boolean getAllowsChildren() {

		return true;
	}

	public TreeNode getChildAt(int childIndex) {

		return ensureChildren().get(childIndex);
	}

	public int getChildCount() {

		return ensureChildren().size();
	}

	public int getIndex(TreeNode node) {

		return ensureChildren().indexOf(GNode.cast(node));
	}

	public boolean isLeaf() {

		Boolean leaf = leafNodeFastCheck();

		return leaf != null ? leaf : ensureChildren().isEmpty();
	}

	public void insert(MutableTreeNode child, int index) {

		throwInertMethodError();
	}

	public void remove(int index) {

		throwInertMethodError();
	}

	public void remove(MutableTreeNode node) {

		throwInertMethodError();
	}

	public void removeFromParent() {

		throwInertMethodError();
	}

	public void setParent(MutableTreeNode newParent) {

		throwInertMethodError();
	}

	public void setUserObject(Object object) {

		throwInertMethodError();
	}

	protected Boolean leafNodeFastCheck() {

		return null;
	}

	GMutableTreeNode() {
	}

	abstract List<GNode> ensureChildren();

	private void throwInertMethodError() {

		throw new Error("Method should never be invoked!");
	}
}
