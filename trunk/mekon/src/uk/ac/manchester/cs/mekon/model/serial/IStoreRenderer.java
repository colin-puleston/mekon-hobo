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
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
public class IStoreRenderer extends ISerialiser {

	private File file;

	private XDocument document = new XDocument(STORE_ID);
	private IFrameRenderer iFrameRenderer = new IFrameRenderer();

	/**
	 */
 	public IStoreRenderer(File file) {

		this.file = file;
	}

 	/**
	 */
 	public void render(IStore store) {

		for (CIdentity id : store.getAllIdentities()) {

			renderInstance(id, store.get(id));
		}

		document.writeToFile(file);
	}

	private void renderInstance(CIdentity id, IFrame frame) {

		XNode instNode = getRootNode().addChild(INSTANCE_ID);

		renderIdentity(id, instNode);
		iFrameRenderer.render(frame, instNode);
	}

	private XNode getRootNode() {

		return document.getRootNode();
	}
}
