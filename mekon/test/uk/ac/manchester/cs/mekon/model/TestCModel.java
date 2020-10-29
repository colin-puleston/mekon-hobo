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
import uk.ac.manchester.cs.mekon.store.disk.*;

/**
 * @author Colin Puleston
 */
public class TestCModel {

	static private CModel createEmptyModel() {

		CModel model = new CModel();

		model.startInitialisation();
		model.completeInitialisation();

		return model;
	}

	public final CModel serverModel;
	public final TestCFrames serverCFrames;

	public final IReasoner iReasoner;

	private CModel clientModel;
	private TestCFrames clientCFrames;

	public TestCModel() {

		this(createEmptyModel());
	}

	public TestCModel(IReasoner iReasoner) {

		this(createEmptyModel(), iReasoner);
	}

	public TestCModel(CModel model) {

		this(model, (IReasoner)null);
	}

	public TestCModel(CModel model, IReasoner iReasoner) {

		serverModel = model;
		clientModel = model;

		this.iReasoner = iReasoner;

		serverCFrames = new TestCFrames(serverModel, iReasoner);
		clientCFrames = serverCFrames;

		serverModel.setQueriesEnabled(true);
		clientModel.setQueriesEnabled(true);
	}

	public void setClientModel(CModel clientModel) {

		this.clientModel = clientModel;

		clientCFrames = new TestCFrames(clientModel, null);
	}

	public TestIFrames createAssertionIFrames() {

		return new TestIFrames(clientCFrames, IFrameFunction.ASSERTION);
	}

	public TestIFrames createQueryIFrames() {

		return new TestIFrames(clientCFrames, IFrameFunction.QUERY);
	}

	public boolean remoteModel() {

		return clientCFrames != serverCFrames;
	}

	public CModel getClientModel() {

		return clientModel;
	}

	public TestCFrames getClientCFrames() {

		return clientCFrames;
	}

	public IEditor getIEditor() {

		return clientModel.getIEditor();
	}
}
