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
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
public class IFrameSerialiseTest extends MekonTest {

	private IFrameRenderer renderer = null;

	@Before
	public void setUp() {

		renderer = new IFrameRenderer();
	}

	@Test
	public void test_renderAndParse() {

		testRenderAndParse(false);
	}

	@Test
	public void test_renderAsTreeAndParse() {

		renderer.setRenderAsTree(true);

		testRenderAndParse(false);
	}

	@Test
	public void test_renderWithSchemaAndParse() {

		renderer.setRenderSchema(true);

		testRenderAndParse(false);
	}

	@Test
	public void test_renderWithFlattenedMetaLevelAndParse() {

		renderer.setFlattenMetaLevel(true);

		testRenderAndParse(false);
	}

	@Test
	public void test_renderAndParseWithDynamicSlotInsertion() {

		testRenderAndParse(true);
	}

	private void testRenderAndParse(boolean dynamicSlotInsertion) {

		IFrame original = createComplexInstance(dynamicSlotInsertion);
		IFrame reconstituted = parseInstance(renderer.render(original));

		assertTrue(reconstituted.matches(original));
	}

	private IFrame parseInstance(XDocument rendering) {

		return getModel().parseIFrame(rendering, IFrameCategory.ASSERTION);
	}
}
