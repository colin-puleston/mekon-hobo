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

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class InstanceDoctor {

	private List<EntityDoctor> entityDoctors = new ArrayList<EntityDoctor>();

	private KListMap<String, EntityDoctor> entityDoctorsByXMLTag
								= new KListMap<String, EntityDoctor>();

	void addEntityDoctor(EntityDoctor entityDoctor) {

		entityDoctors.add(entityDoctor);

		for (String tag : entityDoctor.getXMLTags()) {

			entityDoctorsByXMLTag.add(tag, entityDoctor);
		}
	}

	void setModel(CModel model) {

		for (EntityDoctor entityDoctor : entityDoctors) {

			entityDoctor.setModel(model);
		}
	}

	void checkDoctor(File file) {

		XDocument doc = new XDocument(file);

		if (checkDoctorFrom(doc.getRootNode())) {

			doc.writeToFile(file);
		}
	}

	private boolean checkDoctorFrom(XNode node) {

		boolean doctored = false;

		for (XNode child : node.getAllChildren()) {

			doctored |= checkDoctor(child);
			doctored |= checkDoctorFrom(child);
		}

		return doctored;
	}

	private boolean checkDoctor(XNode node) {

		boolean doctored = false;

		for (EntityDoctor entDoc : entityDoctorsByXMLTag.getList(node.getId())) {

			doctored |= entDoc.checkDoctor(node);
		}

		return doctored;
	}
}
