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

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * @author Colin Puleston
 */
public class TestCModel {

	static private CModel createEmptyModel() {

		return ZCModelAccessor.get().createModel();
	}

	public final CModel model;
	public final IReasoner iReasoner;
	public final IEditor iEditor;

	public final TestCFrames cFrames;
	public final TestIFrames iFrameAssertions;
	public final TestIFrames iFrameQueries;

	public TestCModel() {

		this(createEmptyModel(), null);
	}

	public TestCModel(IReasoner iReasoner) {

		this(createEmptyModel(), iReasoner);
	}

	public TestCModel(CModel model) {

		this(model, null);
	}

	public void setQueriesEnabled(boolean enabled) {

		model.setQueriesEnabled(enabled);
	}

	public void normaliseCFramesHierarchy() {

		new CHierarchyNormaliser(model);
	}

	public TestInstances createTestInstances() {

		return new TestInstances(cFrames);
	}

	private TestCModel(CModel model, IReasoner iReasoner) {

		this.model = model;
		this.iReasoner = iReasoner;

		iEditor = model.getIEditor();

		cFrames = new TestCFrames(model, iReasoner);
		iFrameAssertions = new TestIFrames(cFrames, IFrameFunction.ASSERTION);
		iFrameQueries = new TestIFrames(cFrames, IFrameFunction.QUERY);
	}
}