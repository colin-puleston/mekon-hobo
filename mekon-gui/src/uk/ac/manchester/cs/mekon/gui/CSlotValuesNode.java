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

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CSlotValuesNode extends GNode {

	private CTree tree;
	private CProperty property;
	private List<CValue<?>> fixedValues;

	private class ValueNodeCreator extends CValueVisitor {

		private GNode created = null;

		protected void visit(CFrame value) {

			created = new CFrameNode(tree, value);
		}

		protected void visit(CNumber value) {

			created = new CNumberNode(tree, value);
		}

		protected void visit(MFrame value) {

			created = new MFrameNode(tree, value);
		}

		GNode create(CValue<?> value) {

			visit(value);

			return created;
		}
	}

	protected void addInitialChildren() {

		ValueNodeCreator creator = new ValueNodeCreator();

		for (CValue<?> fixedValue : fixedValues) {

			addChild(creator.create(fixedValue));
		}
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().forCSlotValues(property);
	}

	CSlotValuesNode(CTree tree, CProperty property, List<CValue<?>> fixedValues) {

		super(tree);

		this.tree = tree;
		this.property = property;
		this.fixedValues = fixedValues;
	}
}
