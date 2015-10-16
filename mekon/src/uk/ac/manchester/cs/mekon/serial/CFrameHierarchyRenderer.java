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

package uk.ac.manchester.cs.mekon.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
public class CFrameHierarchyRenderer extends CSerialiser {

	private CVisibility visibilityFilter = CVisibility.ALL;
	private Set<Object> annotationKeys = new HashSet<Object>();

	/**
	 */
	public void setVisibilityFilter(CVisibility visibilityFilter) {

		this.visibilityFilter = visibilityFilter;
	}

	/**
	 */
	public void setRenderAllAnnotations() {

		annotationKeys = null;
	}

	/**
	 */
	public void setRenderAnnotations(Object key) {

		if (annotationKeys != null) {

			annotationKeys.add(key);
		}
	}

	/**
	 */
	public XDocument render(CFrame frame) {

		XDocument document = new XDocument(CFRAME_ID);

		renderDetails(frame, document.getRootNode());

		return document;
	}

	/**
	 */
	public void render(CFrame frame, XNode parentNode) {

		renderDetails(frame, parentNode.addChild(CFRAME_ID));
	}

	private void renderDetails(CFrame frame, XNode node) {

		renderIdentity(frame, node);
		renderAnnotations(frame.getAnnotations(), node);

		for (CFrame sub : frame.getSubs(visibilityFilter)) {

			render(sub, node);
		}
	}

	private void renderAnnotations(CAnnotations annos, XNode node) {

		for (Object key : annos.getKeys()) {

			if (requireAnnotations(key)) {

				renderAnnotations(key, annos.getAll(key), node);
			}
		}
	}

	private void renderAnnotations(Object key, List<Object> values, XNode parentNode) {

		for (Object value : values) {

			XNode node = parentNode.addChild(ANNOTATION_ID);

			node.addValue(ANNOTATION_KEY_ATTR, key);
			node.addValue(ANNOTATION_VALUE_ATTR, value);
		}
	}

	private boolean requireAnnotations(Object key) {

		return annotationKeys == null || annotationKeys.contains(key);
	}
}
