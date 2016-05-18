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
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Represents the input-data for a specific {@link IFrame}/{@link ISlot}
 * network parsing operation to be performed by a {@link IFrameParseer},
 * including structures into which certain output-data may be written.
 *
 * @author Colin Puleston
 */
public class IFrameParseInput {

	private XNode parentNode = null;
	private XNode containerNode = null;

	private Map<String, IFrame> framesByXDocId = new HashMap<String, IFrame>();

	/**
	 * Constructor.
	 *
	 * @param document Document containing serialised frame/slot network
	 */
	public IFrameParseInput(XDocument document) {

		containerNode = document.getRootNode();
	}

	/**
	 * Constructor.
	 *
	 * @param parentNode Parent of top-level node of serialised frame/slot
	 * network (identified via either "ITree" or "IGraph" tag, depending on
	 * serialisation format)
	 */
	public IFrameParseInput(XNode parentNode) {

		this.parentNode = parentNode;
	}

	/**
	 * Sets the map into which the document-specific frame-identifiers,
	 * as used in the parsed-document, corresponding to the generated component
	 * frames are to be written.
	 *
	 * @param framesByXDocId Map of document-specific frame-identifiers to frames
	 */
	public void setFramesByXDocId(Map<String, IFrame> framesByXDocId) {

		this.framesByXDocId = framesByXDocId;
	}

	/**
	 * Provides the map into which the document-specific frame-identifiers,
	 * as used in the parsed-document, corresponding to the generated component
	 * frames, are to be written.
	 *
	 * @return Map of document-specific frame-identifiers to frames
	 */
	public Map<String, IFrame> getFramesByXDocId() {

		return framesByXDocId;
	}

	/**
	 * Provides a transposed version of the map into which the document-specific
	 * frame-identifiers, as used in the parsed-document, corresponding to the
	 * generated component frames, have been written.
	 *
	 * @return Map of frames to document-specific frame-identifiers
	 */
	public Map<IFrame, String> getFrameXDocIds() {

		Map<IFrame, String> frameXDocIds = new HashMap<IFrame, String>();

		for (Map.Entry<String, IFrame> entry : framesByXDocId.entrySet()) {

			frameXDocIds.put(entry.getValue(), entry.getKey());
		}

		return frameXDocIds;
	}

	XNode getParentNode() {

		return parentNode;
	}

	XNode getContainerNode() {

		return containerNode;
	}
}
