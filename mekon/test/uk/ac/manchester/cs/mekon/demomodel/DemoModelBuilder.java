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

package uk.ac.manchester.cs.mekon.demomodel;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
public class DemoModelBuilder {

	private CBuilder builder;

	public DemoModelBuilder(CBuilder builder) {

		this.builder = builder;
	}

	public CFrame addFrame(CIdentity frame) {

		return builder.addFrame(frame, false);
	}

	public void addSuperFrame(CIdentity sub, CIdentity sup) {

		getFrameEditor(sub).addSuper(getFrame(sup));
	}

	public void addSlot(CIdentity container, CIdentity property, CIdentity valueType) {

		addSlot(container, property, getFrame(valueType));
	}

	public void addSlot(CIdentity container, CIdentity property, CValue<?> valueType) {

		CFrameEditor contEd = getFrameEditor(container);

		contEd.addSlot(property, valueType, CCardinality.REPEATABLE_TYPES);
	}

	public CFrame getFrame(CIdentity frameId) {

		return builder.getFrames().get(frameId);
	}

	private CFrameEditor getFrameEditor(CIdentity frameId) {

		return builder.getFrameEditor(getFrame(frameId));
	}
}
