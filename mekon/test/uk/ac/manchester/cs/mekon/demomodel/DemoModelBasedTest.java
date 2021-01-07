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

/**
 * @author Colin Puleston
 */
public abstract class DemoModelBasedTest extends DemoModelIds {

	private CModel serverModel;
	private IStore serverStore;

	private CModel clientModel = null;
	private IStore clientStore = null;

	public CBuilder buildModel(CSectionBuilder sectionBuilder) {

		CBuilder cBuilder = CManager.createEmptyBuilder();

		cBuilder.setQueriesEnabled(true);
		cBuilder.addSectionBuilder(sectionBuilder);

		serverModel = cBuilder.build();
		serverStore = IDiskStoreManager.getBuilder(cBuilder).build();

		return cBuilder;
	}

	public CModel getServerModel() {

		return serverModel;
	}

	public IStore getServerStore() {

		return serverStore;
	}

	public CModel getClientModel() {

		if (clientModel == null) {

			clientModel = resolveClientModel(serverModel, serverStore);
		}

		return clientModel;
	}

	public IStore getClientStore() {

		if (clientStore == null) {

			clientStore = resolveClientStore(serverModel, serverStore);
		}

		return clientStore;
	}

	public Set<CFrame> getCFrames(CIdentity... ids) {

		Set<CFrame> frames = new HashSet<CFrame>();

		for (CIdentity id : ids) {

			frames.add(getCFrame(id));
		}

		return frames;
	}

	public boolean isCFrame(CIdentity id) {

		return getClientModel().getFrames().containsValueFor(id);
	}

	public CFrame getCFrame(CIdentity id) {

		return getClientModel().getFrames().get(id);
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

	protected CModel resolveClientModel(CModel serverModel, IStore serverStore) {

		return serverModel;
	}

	protected IStore resolveClientStore(CModel serverModel, IStore serverStore) {

		return serverStore;
	}

	private IFrame instantiate(CIdentity typeId, IFrameFunction function) {

		return getClientFrame(typeId).instantiate(function);
	}

	private IFrame instantiateRef(CIdentity typeId, CIdentity refId, IFrameFunction function) {

		return getClientFrame(typeId).instantiateReference(refId, function);
	}

	private CFrame getClientFrame(CIdentity id) {

		return getClientModel().getFrames().get(id);
	}
}
