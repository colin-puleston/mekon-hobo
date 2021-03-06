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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Renderer for the standard XML serialisation of section of
 * {@link CFrame}-hierarchy.
 *
 * @author Colin Puleston
 */
public class CHierarchyRenderer extends FSerialiser {

	private CVisibility visibilityFilter = CVisibility.ALL;
	private Set<Object> annotationKeys = new HashSet<Object>();

	/**
	 * Sets the filter that determines by visibility-status which
	 * frames will be included in the hierarchy.
	 *
	 * @param visibilityFilter Visibility-filter to set
	 */
	public void setVisibilityFilter(CVisibility visibilityFilter) {

		this.visibilityFilter = visibilityFilter;
	}

	/**
	 * Sets rendering of all annotations on frames being rendered.
	 */
	public void setRenderAllAnnotations() {

		annotationKeys = null;
	}

	/**
	 * Sets rendering of specified annotation on frames being
	 * rendered.
	 *
	 * @param key Key for annotations to be rendered
	 */
	public void setRenderAnnotations(Object key) {

		if (annotationKeys != null) {

			annotationKeys.add(key);
		}
	}

	/**
	 * Renders the hierarchy under the specified root-frame to
	 * produce an XML document.
	 *
	 * @param rootFrame Root-frame of hierarchy to render
	 * @return Rendered document
	 */
	public XDocument render(CFrame rootFrame) {

		XDocument document = new XDocument(CFRAME_ID);

		renderDetails(rootFrame, document.getRootNode());

		return document;
	}

	/**
	 * Renders the hierarchy under the specified root-frame to
	 * the specified parent-node.
	 *
	 * @param rootFrame Root-frame of hierarchy to render
	 * @param parentNode Parent-node for rendering
	 */
	public void render(CFrame rootFrame, XNode parentNode) {

		renderDetails(rootFrame, parentNode.addChild(CFRAME_ID));
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

			node.setValue(ANNOTATION_KEY_ATTR, key);
			node.setValue(ANNOTATION_VALUE_ATTR, value);
		}
	}

	private boolean requireAnnotations(Object key) {

		return annotationKeys == null || annotationKeys.contains(key);
	}
}
