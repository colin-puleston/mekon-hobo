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

package uk.ac.manchester.cs.mekon.gui.storedoctor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class ISlotDoctor extends EntityDoctor {

	static private Set<String> valueTypeTags = new HashSet<String>();

	static {

		valueTypeTags.add(MFRAME_ID);
		valueTypeTags.add(CFRAME_ID);
		valueTypeTags.add(CNUMBER_ID);
	}

	private String frameId;
	private ValueTypeDoctor valueTypeDoctor = null;

	private abstract class ValueTypeDoctor extends NodeDoctor {

		void doctor(XNode entityNode) {

			removeOldTypeNode(entityNode);
			renderNewType(entityNode.addChild(getXMLTag()));
		}

		abstract String getXMLTag();

		abstract void renderNewType(XNode typeNode);

		private void removeOldTypeNode(XNode entityNode) {

			for (String tag : valueTypeTags) {

				XNode typeNode = entityNode.getChildOrNull(tag);

				if (typeNode != null) {

					entityNode.removeChild(typeNode);

					break;
				}
			}
		}
	}

	private abstract class FrameValueTypeDoctor extends ValueTypeDoctor {

		private CIdentity newTypeId;

		FrameValueTypeDoctor(CIdentity newTypeId) {

			this.newTypeId = newTypeId;
		}

		void renderNewType(XNode typeNode) {

			FSerialiser.renderIdentity(newTypeId, typeNode);
		}
	}

	private class MFrameValueTypeDoctor extends FrameValueTypeDoctor {

		MFrameValueTypeDoctor(CIdentity newTypeId) {

			super(newTypeId);
		}

		String getXMLTag() {

			return MFRAME_ID;
		}
	}

	private class CFrameValueTypeDoctor extends FrameValueTypeDoctor {

		CFrameValueTypeDoctor(CIdentity newTypeId) {

			super(newTypeId);
		}

		String getXMLTag() {

			return MFRAME_ID;
		}
	}

	private class CNumberValueTypeDoctor extends ValueTypeDoctor {

		private CNumber newType;

		CNumberValueTypeDoctor(CNumber newType) {

			this.newType = newType;
		}

		String getXMLTag() {

			return CNUMBER_ID;
		}

		void renderNewType(XNode typeNode) {

			FSerialiser.renderCNumber(newType, typeNode);
		}
	}

	public ISlotDoctor(String frameId, String slotId) {

		super(slotId);

		this.frameId = frameId;
	}

	public void setNewMFrameValueType(CIdentity newTypeId) {

		valueTypeDoctor = new MFrameValueTypeDoctor(newTypeId);
	}

	public void setNewCFrameValueType(CIdentity newTypeId) {

		valueTypeDoctor = new CFrameValueTypeDoctor(newTypeId);
	}

	public void setNewCNumberValueType(CNumber newType) {

		valueTypeDoctor = new CNumberValueTypeDoctor(newType);
	}

	public void setNewCardinality(CCardinality value) {

		entityIdNodeDoctor.addNewValue(CARDINALITY_ATTR, value);
	}

	public void setNewActivation(CActivation value) {

		entityIdNodeDoctor.addNewValue(ACTIVATION_ATTR, value);
	}

	public void setNewEditability(IEditability value) {

		entityNodeDoctor.addNewValue(EDITABILITY_ATTR, value);
	}

	boolean checkDoctor(XNode entityNode) {

		if (super.checkDoctor(entityNode)) {

			if (valueTypeDoctor != null) {

				valueTypeDoctor.doctor(entityNode);
			}

			return true;
		}

		return false;
	}

	String[] getXMLTags() {

		return new String[]{ISLOT_ID};
	}

	XNode getEntityIdNodeOrNull(XNode entityNode) {

		return frameIdMatch(entityNode) ? entityNode.getChild(CSLOT_ID) : null;
	}

	private boolean frameIdMatch(XNode entityNode) {

		return idMatch(getFrameTypeNode(entityNode), frameId);
	}

	private XNode getFrameTypeNode(XNode entityNode) {

		return entityNode.getParent().getChild(CFRAME_ID);
	}
}
