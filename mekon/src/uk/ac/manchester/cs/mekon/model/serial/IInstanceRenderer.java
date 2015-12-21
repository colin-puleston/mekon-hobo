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

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Renderer for the standard XML serialisation of an instance,
 * comprising an instance-identifier, and a frame representation
 * of the instance.
 *
 * @author Colin Puleston
 */
public class IInstanceRenderer extends ISerialiser {

	private File instanceFile;

	private XDocument document = new XDocument(INSTANCE_ID);

	/**
	 * Constructor.
	 *
	 * @param instanceFile Serialisation file
	 */
	public IInstanceRenderer(File instanceFile) {

		this.instanceFile = instanceFile;
	}

	/**
	 * Renders the instance to the relevant file.
	 *
	 * @param identity Identity of instance
	 * @param instance Frame representation of instance
	 */
	public void render(IFrame instance, CIdentity identity) {

		XNode rootNode = document.getRootNode();

		renderInstance(instance, rootNode);
		renderIdentity(identity, rootNode);

		document.writeToFile(instanceFile);
	}

	private void renderInstance(IFrame instance, XNode rootNode) {

		IFrameRenderer frameRenderer = new IFrameRenderer();

		frameRenderer.setSchemaLevel(ISchemaLevel.BASIC);
		frameRenderer.render(instance, rootNode);
	}
}
