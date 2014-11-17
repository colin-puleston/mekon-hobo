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

import java.lang.reflect.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
abstract class FSerialiser {

	static final String MFRAME_ID = getClassId(MFrame.class);
	static final String CFRAME_ID = getClassId(CFrame.class);
	static final String CNUMBER_ID = getClassId(CNumber.class);
	static final String CSLOT_ID = getClassId(CSlot.class);
	static final String IFRAME_ID = getClassId(IFrame.class);
	static final String INUMBER_ID = getClassId(INumber.class);
	static final String ISLOT_ID = getClassId(ISlot.class);

	static final String IDENTITY_ATTR = "id";
	static final String LABEL_ATTR = "label";

	static void renderIdentity(CIdentity id, XNode node) {

		node.addValue(IDENTITY_ATTR, id.getIdentifier());
		node.addValue(LABEL_ATTR, id.getLabel());
	}

	static void renderIdentity(CIdentified id, XNode node) {

		renderIdentity(id.getIdentity(), node);
	}

	static void renderClassId(Class<?> leafClass, XNode node, String attr) {

		node.addValue(attr, getClassId(leafClass));
	}

	static CIdentity parseIdentity(XNode node) {

		String id = node.getString(IDENTITY_ATTR);
		String label = node.getString(LABEL_ATTR);

		return new CIdentity(id, label);
	}

	static private String getClassId(Class<?> leafClass) {

		return findPublicClass(leafClass).getSimpleName();
	}

	static private Class<?> findPublicClass(Class leafClass) {

		return isPublicClass(leafClass)
					? leafClass
					: findPublicClass(leafClass.getSuperclass());
	}

	static private boolean isPublicClass(Class testClass) {

		return Modifier.isPublic(testClass.getModifiers());
	}
}
