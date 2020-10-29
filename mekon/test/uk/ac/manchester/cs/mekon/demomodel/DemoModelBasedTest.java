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

import java.util.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.*;

/**
 * @author Colin Puleston
 */
public abstract class DemoModelBasedTest extends DemoModelIds {

	private CModel clientModel;
	private IStore clientStore;

	private CModel serverModel;
	private IStore serverStore;

	public CBuilder buildModel(CSectionBuilder sectionBuilder) {

		CBuilder cBuilder = CManager.createEmptyBuilder();

		cBuilder.setQueriesEnabled(true);
		cBuilder.addSectionBuilder(sectionBuilder);

		serverModel = cBuilder.build();
		serverStore = IDiskStoreManager.getBuilder(cBuilder).build();

		if (testingRemoteModel()) {

			MekonRemoteTestModel remote = new MekonRemoteTestModel(serverModel, serverStore);

			clientModel = remote.clientModel;
			clientStore = remote.clientStore;
		}
		else {

			clientModel = serverModel;
			clientStore = serverStore;
		}

		return cBuilder;
	}

	public CModel getClientModel() {

		return clientModel;
	}

	public CModel getServerModel() {

		return serverModel;
	}

	public IStore getClientStore() {

		return clientStore;
	}

	public IStore getServerStore() {

		return serverStore;
	}

	public Set<CFrame> getCFrames(CIdentity... ids) {

		Set<CFrame> frames = new HashSet<CFrame>();

		for (CIdentity id : ids) {

			frames.add(getCFrame(id));
		}

		return frames;
	}

	public boolean isCFrame(CIdentity id) {

		return clientModel.getFrames().containsValueFor(id);
	}

	public CFrame getCFrame(CIdentity id) {

		return clientModel.getFrames().get(id);
	}

	public IFrame createIFrame(CIdentity typeId) {

		return instantiate(typeId, IFrameFunction.ASSERTION);
	}

	public IFrame createQueryIFrame(CIdentity typeId) {

		return instantiate(typeId, IFrameFunction.QUERY);
	}

	public IFrame createRefIFrame(CIdentity typeId, CIdentity refId) {

		return instantiateRef(typeId, refId, IFrameFunction.ASSERTION);
	}

	public IFrame createRefQueryIFrame(CIdentity typeId, CIdentity refId) {

		return instantiateRef(typeId, refId, IFrameFunction.QUERY);
	}

	public ISlot getISlot(IFrame container, CIdentity slotId) {

		return container.getSlots().get(slotId);
	}

	public void addISlotValue(IFrame container, CIdentity slotId, IValue value) {

		getISlot(container, slotId).getValuesEditor().add(value);
	}

	protected boolean testingRemoteModel() {

		return false;
	}

	private IFrame instantiate(CIdentity typeId, IFrameFunction function) {

		return getFrame(typeId).instantiate(function);
	}

	private IFrame instantiateRef(CIdentity typeId, CIdentity refId, IFrameFunction function) {

		return getFrame(typeId).instantiate(refId, function);
	}

	private CFrame getFrame(CIdentity id) {

		return clientModel.getFrames().get(id);
	}
}
