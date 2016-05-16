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
import uk.ac.manchester.cs.mekon.store.disk.*;

/**
 * @author Colin Puleston
 */
public abstract class DemoModelBasedTest implements DemoModelEntities {

	static private final String NAMESPACE = "http://mekon/demo.owl#";

	static public CIdentity nameToIdentity(String name) {

		return new CIdentity(nameToIdentifier(name), name);
	}

	static public String nameToIdentifier(String name) {

		return NAMESPACE + name;
	}

	private CModel model;

	public CBuilder buildModel(CSectionBuilder sectionBuilder) {

		CBuilder cBuilder = CManager.createEmptyBuilder();

		cBuilder.setQueriesEnabled(true);
		cBuilder.addSectionBuilder(sectionBuilder);

		model = cBuilder.build();

		return cBuilder;
	}

	public CModel getModel() {

		return model;
	}

	public Set<CFrame> getCFrames(String... names) {

		Set<CFrame> frames = new HashSet<CFrame>();

		for (String name : names) {

			frames.add(getCFrame(name));
		}

		return frames;
	}

	public boolean isCFrame(String name) {

		return model.getFrames().containsKey(nameToIdentifier(name));
	}

	public CFrame getCFrame(String name) {

		return model.getFrames().get(nameToIdentifier(name));
	}

	public IFrame createIFrame(String name) {

		return instantiate(name, IFrameFunction.ASSERTION);
	}

	public IFrame createQueryIFrame(String name) {

		return instantiate(name, IFrameFunction.QUERY);
	}

	public ISlot getISlot(IFrame container, String name) {

		return container.getSlots().get(nameToIdentity(name));
	}

	public void addISlotValue(IFrame container, String slotName, IValue value) {

		getISlot(container, slotName).getValuesEditor().add(value);
	}

	public IFrame instantiate(String name, IFrameFunction function) {

		return getFrame(name).instantiate(function);
	}

	private CFrame getFrame(String name) {

		return model.getFrames().get(nameToIdentity(name));
	}
}
