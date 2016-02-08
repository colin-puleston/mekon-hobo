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
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
public class IFrameSerialiseTest {

	private TestCModel model = new TestCModel();
	private TestISlots slots = model.iFrameAssertions.repeatTypesSlots;
	private TestInstances instances = model.createTestInstances();

	private boolean freeParser = false;
	private ISchemaLevel schemaLevel = ISchemaLevel.NONE;
	private boolean renderAsTree = false;
	private boolean includeEmptySlots = false;

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
	public void test_renderWithBasicSchemaAndParse() {

		schemaLevel = ISchemaLevel.BASIC;

		testRenderAndParse();
	}

	@Test
	public void test_renderWithFullSchemaAndParse() {

		schemaLevel = ISchemaLevel.FULL;

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
	public void test_renderWithBasicSchemaAndFreeParse() {

		freeParser = true;
		schemaLevel = ISchemaLevel.BASIC;

		testRenderAndParse();
	}

	@Test
	public void test_renderWithBasicSchemaAndFreeParseWithEmptySlots() {

		freeParser = true;
		schemaLevel = ISchemaLevel.BASIC;
		includeEmptySlots = true;

		testRenderAndParse();
	}

	@Test
	public void test_renderWithBasicSchemaAndFreeParseWithDynamicSlotInsertion() {

		freeParser = true;
		schemaLevel = ISchemaLevel.BASIC;

		instances.setDynamicSlotInsertion();

		testRenderAndParse();
	}

	private void testRenderAndParse() {

		IFrame original = createTestInstance();
		IFrame reconstituted = parse(render(original));

		assertTrue(reconstituted.equalStructures(original));
	}

	private XDocument render(IFrame frame) {

		return createRenderer().render(frame);
	}

	private IFrame parse(XDocument rendering) {

		return createParser().parse(rendering);
	}

	private IFrameParserAbstract createParser() {

		CModel cModel = model.model;

		return freeParser
				? new IFrameFreeParser(cModel, IFrameFunction.ASSERTION)
				: new IFrameParser(cModel, IFrameFunction.ASSERTION);
	}

	private IFrameRenderer createRenderer() {

		IFrameRenderer renderer = new IFrameRenderer();

		renderer.setRenderAsTree(renderAsTree);
		renderer.setSchemaLevel(schemaLevel);

		return renderer;
	}

	private IFrame createTestInstance() {

		IFrame instance = instances.getBasic();

		if (includeEmptySlots) {

			addEmptySlot(instance);
		}

		return instance;
	}

	private void addEmptySlot(IFrame instance) {

		slots.create(instance, "emptySlot", instance.getType());
	}
}
