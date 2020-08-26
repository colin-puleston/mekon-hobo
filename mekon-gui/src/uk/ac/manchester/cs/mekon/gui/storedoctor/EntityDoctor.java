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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public abstract class EntityDoctor implements FSerialiserVocab {

	final NodeDoctor entityNodeDoctor = new NodeDoctor();
	final NodeDoctor entityIdNodeDoctor = new NodeDoctor();

	private String entityId;

	public void setNewId(String value) {

		entityIdNodeDoctor.addNewValue(IDENTIFIER_ATTR, value);
	}

	public void setNewLabel(String value) {

		entityIdNodeDoctor.addNewValue(LABEL_ATTR, value);
	}

	EntityDoctor(String entityId) {

		this.entityId = entityId;
	}

	void setModel(CModel model) {
	}

	boolean checkDoctor(XNode entityNode) {

		XNode idNode = getEntityIdNodeOrNull(entityNode);

		if (idNode != null && entityId.equals(lookForId(idNode))) {

			System.out.println("  SLOT-MATCH: " + entityId);
			entityNodeDoctor.doctor(entityNode);
			entityIdNodeDoctor.doctor(idNode);

			return true;
		}

		return false;
	}

	String getId(XNode node) {

		return node.getString(IDENTIFIER_ATTR);
	}

	String lookForId(XNode node) {

		return node.getString(IDENTIFIER_ATTR, null);
	}

	abstract String[] getXMLTags();

	abstract XNode getEntityIdNodeOrNull(XNode entityNode);
}
