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

package uk.ac.manchester.cs.mekon.app;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class AspectValueNode extends InstantiationNode {

	private String label;
	private DescriptorsList aspectDescriptors;

	protected void addInitialChildren() {

		InstantiationTree tree = getInstantiationTree();

		for (Descriptor descriptor : aspectDescriptors.getList()) {

			addChild(new DescriptorNode(tree, descriptor));
		}
	}

	protected boolean autoExpand() {

		return true;
	}

	protected boolean orderedChildren() {

		return false;
	}

	AspectValueNode(InstantiationTree tree, IFrame topLevelAspect) {

		this(tree, "", topLevelAspect);
	}

	AspectValueNode(InstantiationTree tree, Descriptor parentDescriptor) {

		this(
			tree,
			parentDescriptor.getValueLabel(),
			(IFrame)parentDescriptor.getCurrentValue());
	}

	String getDisplayLabel() {

		return label;
	}

	Icon getIcon() {

		return queryInstance() ? MekonAppIcons.QUERY_TYPE : MekonAppIcons.ASSERTION_TYPE;
	}

	private AspectValueNode(InstantiationTree tree, String label, IFrame aspect) {

		super(tree);

		this.label = label;

		aspectDescriptors = new DescriptorsList(getInstantiator(), aspect);
	}
}
