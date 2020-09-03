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

package uk.ac.manchester.cs.mekon.user.storedoctor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class CFrameDoctor extends EntityDoctor {

	static private final List<String> XML_TAGS = Arrays.asList(CFRAME_ID, MFRAME_ID);

	private String frameId;

	private String newFrameId = null;
	private String newFrameLabel = null;

	public CFrameDoctor(String frameId) {

		super(frameId);

		this.frameId = frameId;
	}

	public void setNewId(String value) {

		super.setNewId(value);

		newFrameId = value;
	}

	public void setNewLabel(String value) {

		super.setNewLabel(value);

		newFrameLabel = value;
	}

	IInstanceProfile checkDoctorProfile(IInstanceProfile profile) {

		if (newFrameId != null || newFrameLabel != null) {

			CIdentity typeIdentity = profile.getTypeIdentity();

			if (typeIdentity.getIdentifier().equals(frameId)) {

				return profile.updateType(getNewIdentity(typeIdentity.getLabel()));
			}
		}

		return null;
	}

	List<String> getXMLTags() {

		return XML_TAGS;
	}

	String getEntityTypeName() {

		return CFRAME_ID;
	}

	String getEntityDescription() {

		return frameId;
	}

	XNode getEntityIdNodeOrNull(XNode entityNode) {

		return entityNode;
	}

	private CIdentity getNewIdentity(String oldLabel) {

		String id = newFrameId != null ? newFrameId : frameId;
		String label = newFrameLabel != null ? newFrameLabel : oldLabel;

		return new CIdentity(id, label);
	}
}
