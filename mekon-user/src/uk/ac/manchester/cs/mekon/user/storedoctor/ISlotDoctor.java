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

	private String slotId;

	private List<String> containerIds = new ArrayList<String>();
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

		final List<CIdentity> newTypeDisjunctIds = new ArrayList<CIdentity>();

		FrameValueTypeDoctor(CIdentity newTypeId) {

			newTypeDisjunctIds.add(newTypeId);
		}

		FrameValueTypeDoctor(Collection<CIdentity> newTypeDisjunctIds) {

			this.newTypeDisjunctIds.addAll(newTypeDisjunctIds);
		}
	}

	private class MFrameValueTypeDoctor extends FrameValueTypeDoctor {

		MFrameValueTypeDoctor(CIdentity newTypeId) {

			super(newTypeId);
		}

		MFrameValueTypeDoctor(Collection<CIdentity> newTypeDisjunctIds) {

			super(newTypeDisjunctIds);
		}

		String getXMLTag() {

			return MFRAME_ID;
		}

		void renderNewType(XNode typeNode) {

			FSerialiser.renderMFrame(newTypeDisjunctIds, typeNode);
		}
	}

	private class CFrameValueTypeDoctor extends FrameValueTypeDoctor {

		CFrameValueTypeDoctor(CIdentity newTypeId) {

			super(newTypeId);
		}

		CFrameValueTypeDoctor(Collection<CIdentity> newTypeDisjunctIds) {

			super(newTypeDisjunctIds);
		}

		String getXMLTag() {

			return CFRAME_ID;
		}

		void renderNewType(XNode typeNode) {

			FSerialiser.renderCFrame(newTypeDisjunctIds, typeNode);
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

	public ISlotDoctor(String rootContainerId, String slotId) {

		super(slotId);

		this.slotId = slotId;

		containerIds.add(rootContainerId);
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

	public void setNewMFrameValueType(CIdentity newTypeId) {

		valueTypeDoctor = new MFrameValueTypeDoctor(newTypeId);
	}

	public void setNewMFrameValueType(Collection<CIdentity> newTypeDisjunctIds) {

		valueTypeDoctor = new MFrameValueTypeDoctor(newTypeDisjunctIds);
	}

	public void setNewCFrameValueType(CIdentity newTypeId) {

		valueTypeDoctor = new CFrameValueTypeDoctor(newTypeId);
	}

	public void setNewCFrameValueType(Collection<CIdentity> newTypeDisjunctIds) {

		valueTypeDoctor = new CFrameValueTypeDoctor(newTypeDisjunctIds);
	}

	public void setNewCNumberValueType(CNumber newType) {

		valueTypeDoctor = new CNumberValueTypeDoctor(newType);
	}

	void setModel(CModel model) {

		for (CFrame type : getDescendantContainerTypes(model)) {

			containerIds.add(type.getIdentity().getIdentifier());
		}
	}

	boolean checkDoctorNode(XNode entityNode) {

		if (super.checkDoctorNode(entityNode)) {

			if (valueTypeDoctor != null) {

				valueTypeDoctor.doctor(entityNode);
			}

			return true;
		}

		return false;
	}

	List<String> getXMLTags() {

		return Collections.singletonList(ISLOT_ID);
	}

	String getEntityTypeName() {

		return ISLOT_ID;
	}

	String getEntityDescription() {

		return getRootContainerId() + "-->" + slotId;
	}

	XNode getEntityIdNodeOrNull(XNode entityNode) {

		return containerIdMatch(entityNode) ? entityNode.getChild(CSLOT_ID) : null;
	}

	private List<CFrame> getDescendantContainerTypes(CModel model) {

		CIdentity rootContIdentity = new CIdentity(getRootContainerId());

		return model.getFrames().get(rootContIdentity).getDescendants();
	}

	private String getRootContainerId() {

		return containerIds.get(0);
	}

	private boolean containerIdMatch(XNode slotNode) {

		XNode containerTypeNode = getContainerTypeNode(slotNode);
		String containerId = lookForId(containerTypeNode);

		if (containerId != null) {

			return containerIds.contains(containerId);
		}

		return containerTypeDisjunctIdsMatch(containerTypeNode);
	}

	private boolean containerTypeDisjunctIdsMatch(XNode containerTypeNode) {

		for (XNode disjunctNode : containerTypeNode.getChildren(CFRAME_ID)) {

			if (!containerIds.contains(getId(disjunctNode))) {

				return false;
			}
		}

		return true;
	}

	private XNode getContainerTypeNode(XNode slotNode) {

		return slotNode.getParent().getChild(CFRAME_ID);
	}
}
