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

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class IInstanceSerialiseTest {

	private TestCModel model = new TestCModel();
	private TestISlots slots = model.iFrameAssertions.repeatTypesSlots;
	private TestInstances instances = model.createTestInstances();

	private boolean freeInstances = false;
	private boolean renderAsTree = false;
	private boolean includeEmptySlots = false;
	private boolean includeAbstractValues = false;

	@Test
	public void test_renderAndParse() {

		testRenderAndParse();
	}

	@Test
	public void test_renderAsTreeAndParse() {

		renderAsTree = true;

		testRenderAndParse();
	}

	@Test
	public void test_renderAndParseWithEmptySlots() {

		includeEmptySlots = true;

		testRenderAndParse();
	}

	@Test
	public void test_renderAndParseWithDynamicSlotInsertion() {

		instances.setDynamicSlotInsertion();

		testRenderAndParse();
	}

	@Test
	public void test_renderAndParseWithAbstractValues() {

		includeAbstractValues = true;

		testRenderAndParse();
	}

	private void testRenderAndParse() {

		model.setQueriesEnabled(includeAbstractValues);

		IFrame original = createTestInstance();
		IRegenInstance parseOut = parse(render(original));

		assertEquals(IRegenStatus.FULLY_VALID, parseOut.getStatus());
		assertTrue(parseOut.getRootFrame().equalsStructure(original));
	}

	private XDocument render(IFrame frame) {

		return createRenderer().render(new IInstanceRenderInput(frame));
	}

	private IRegenInstance parse(XDocument rendering) {

		IInstanceParser parser = new IInstanceParser(model.model);

		parser.setFreeInstances(freeInstances);

		return parser.parse(new IInstanceParseInput(rendering));
	}

	private IInstanceRenderer createRenderer() {

		IInstanceRenderer renderer = new IInstanceRenderer();

		renderer.setRenderAsTree(renderAsTree);

		return renderer;
	}

	private IFrame createTestInstance() {

		IFrame instance = createCategoryTestInstance();

		if (includeEmptySlots) {

			addEmptySlot(instance);
		}

		return instance;
	}

	private IFrame createCategoryTestInstance() {

		instances.setFunction(getFunction());

		return includeAbstractValues
				? instances.getAbstractSubsumer()
				: instances.getBasic();
	}

	private void addEmptySlot(IFrame instance) {

		slots.create(instance, "emptySlot", instance.getType());
	}

	private IFrameFunction getFunction() {

		return includeAbstractValues ? IFrameFunction.QUERY : IFrameFunction.ASSERTION;
	}
}
