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

package uk.ac.manchester.cs.mekon.owl;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.sanctions.*;

/**
 * @author Colin Puleston
 */
public class OTest extends FramesTestUtils {

	static private final String TEST_NAMESPACE = "http://mekon/test.owl#";

	private CModel model;

	public void buildModel(OSSectionBuilder sectionBuilder) {

		CBuilder cBuilder = CManager.createEmptyBuilder();

		cBuilder.addSectionBuilder(sectionBuilder);

		model = cBuilder.build();
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

	public boolean isCProperty(String name) {

		return model.getProperties().containsKey(nameToIdentifier(name));
	}

	public CProperty getCProperty(String name) {

		return model.getProperties().get(nameToIdentifier(name));
	}

	public CNumber getCInteger(Integer min, Integer max) {

		return getCNumber(CIntegerDef.range(min, max));
	}

	public CNumber getCNumber(CNumberDef def) {

		return getCNumber(def.getNumberType(), def.getMin(), def.getMax());
	}

	public IFrame createIFrame(String name) {

		return model.instantiate(nameToIdentity(name));
	}

	public ISlot getISlot(IFrame container, String name) {

		return container.getSlots().get(nameToIdentity(name));
	}

	public IRI nameToIRI(String name) {

		return IRI.create(nameToIdentifier(name));
	}

	public CIdentity nameToIdentity(String name) {

		return new CIdentity(nameToIdentifier(name), name);
	}

	public String nameToIdentifier(String name) {

		return TEST_NAMESPACE + name;
	}
}
