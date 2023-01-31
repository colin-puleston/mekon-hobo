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

package uk.ac.manchester.cs.mekon.user.app;

import java.awt.event.*;
import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceTree extends GActionTree {

	static private final long serialVersionUID = -1;

	private Instantiator instantiator;
	private RootInstanceNode rootNode;

	private boolean summaryInstance;
	private boolean instanceSubSection;

	private InstanceDisplayMode displayMode;
	private boolean updatingTree = false;

	private class MouseLocator extends MouseMotionAdapter {

		private InstanceNode location = null;

		public void mouseMoved(MouseEvent event) {

			if (location != null) {

				location.onMousePresenceUpdate(false);
			}

			TreePath path = getPathForLocation(event.getX(), event.getY());

			if (path != null) {

				location = (InstanceNode)path.getLastPathComponent();

				location.onMousePresenceUpdate(true);
			}
			else {

				location = null;
			}
		}
	}

	InstanceTree(
		Instantiator instantiator,
		IFrame rootFrame,
		boolean summaryInstance,
		InstanceDisplayMode startDisplayMode) {

		this.instantiator = instantiator;
		this.summaryInstance = summaryInstance;

		displayMode = startDisplayMode;
		rootNode = new RootInstanceNode(this, rootFrame);
		instanceSubSection = instanceSubSection(rootFrame);

		setRootVisible(true);
		setShowsRootHandles(false);

		addMouseMotionListener(new MouseLocator());

		initialise(rootNode);

		rootNode.initialiseExpansion();
	}

	void setDisplayMode(InstanceDisplayMode newDisplayMode) {

		if (newDisplayMode != displayMode) {

			displayMode = newDisplayMode;

			updateTree();
		}
	}

	boolean summaryInstance() {

		return summaryInstance;
	}

	boolean instanceSubSection() {

		return instanceSubSection;
	}

	boolean viewOnly() {

		return displayMode != InstanceDisplayMode.EDIT;
	}

	boolean showQuerySemantics() {

		return displayMode == InstanceDisplayMode.SEMANTICS;
	}

	InstanceDisplayMode getDisplayMode() {

		return displayMode;
	}

	Instantiator getInstantiator() {

		return instantiator;
	}

	boolean updatingTree() {

		return updatingTree;
	}

	private void updateTree() {

		updatingTree = true;
		startCompoundUpdate();

		rootNode.updateFrom();
		rootNode.expand();

		updatingTree = false;
		endCompoundUpdate();
	}

	private boolean instanceSubSection(IFrame rootFrame) {

		return !instantiator.getGroup().groupRootType(rootFrame.getType());
	}
}
