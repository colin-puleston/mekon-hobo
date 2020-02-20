/**
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
package uk.ac.manchester.cs.mekon.explorer;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class CFrameAnnotationsTree extends CTree {

	static private final long serialVersionUID = -1;

	private abstract class AnnotationsTreeNode extends GNode {

		private String label;

		protected GCellDisplay getDisplay() {

			return EntityDisplays.get().get(label, null, getTextDisplay());
		}

		AnnotationsTreeNode() {

			this("");
		}

		AnnotationsTreeNode(String label) {

			super(CFrameAnnotationsTree.this);

			this.label = label;
		}

		NodeTextDisplay getTextDisplay() {

			return NodeTextDisplay.ANNOTATION_KEY;
		}
	}

	private class RootNode extends AnnotationsTreeNode {

		private CAnnotations annotations;

		protected void addInitialChildren() {

			for (Object key : annotations.getKeys()) {

				addChild(new AnnotationNode(key, annotations.getAll(key)));
			}
		}

		RootNode(CAnnotations annotations) {

			this.annotations = annotations;
		}
	}

	private class AnnotationNode extends AnnotationsTreeNode {

		private List<Object> values;

		protected void addInitialChildren() {

			for (Object value : values) {

				addChild(createAnnotationValueNode(value));
			}
		}

		AnnotationNode(Object key, List<Object> values) {

			super(key.toString());

			this.values = values;
		}
	}

	private class ValuesNode extends AnnotationsTreeNode {

		ValuesNode(String label) {

			super(label);
		}

		NodeTextDisplay getTextDisplay() {

			return NodeTextDisplay.ANNOTATION_VALUE;
		}
	}

	private abstract class CValuesNode extends ValuesNode {

		private Collection<CValue<?>> cValues;

		protected void addInitialChildren() {

			for (CValue<?> cValue : cValues) {

				addChild(createCValueNode(cValue));
			}
		}

		CValuesNode(Class<?> type, Collection<CValue<?>> cValues) {

			super(type.getSimpleName());

			this.cValues = cValues;
		}
	}

	private class CValueSetNode extends CValuesNode {

		CValueSetNode(Set<CValue<?>> cValues) {

			super(Set.class, cValues);
		}
	}

	private class CValueListNode extends CValuesNode {

		CValueListNode(List<CValue<?>> cValues) {

			super(List.class, cValues);
		}
	}

	CFrameAnnotationsTree(CFrame frame, CFrameSelectionListener reselectionListener) {

		setRootVisible(false);
		setShowsRootHandles(true);

		initialise(new RootNode(frame.getAnnotations()));

		setNonVisibleSelection();
		addSelectionListener(reselectionListener);
	}

	void addCFrameChildren(CFrameNode parent) {
	}

	private GNode createAnnotationValueNode(Object value) {

		if (value instanceof CValue) {

			return createCValueNode((CValue<?>)value);
		}

		if (value instanceof Set) {

			Set<CValue<?>> cValues = toCValueSetOrNull((Set<?>)value);

			if (cValues != null) {

				return new CValueSetNode(cValues);
			}
		}

		if (value instanceof List) {

			List<CValue<?>> cValues = toCValueListOrNull((List<?>)value);

			if (cValues != null) {

				return new CValueListNode(cValues);
			}
		}

		return new ValuesNode(value.toString());
	}

	private Set<CValue<?>> toCValueSetOrNull(Collection<?> elements) {

		List<CValue<?>> list = toCValueListOrNull(elements);

		return list != null ? new HashSet<CValue<?>>(list) : null;
	}

	private List<CValue<?>> toCValueListOrNull(Collection<?> elements) {

		List<CValue<?>> cValues = new ArrayList<CValue<?>>();

		for (Object element : elements) {

			if (!(element instanceof CValue)) {

				return null;
			}

			cValues.add((CValue<?>)element);
		}

		return cValues;
	}
}
