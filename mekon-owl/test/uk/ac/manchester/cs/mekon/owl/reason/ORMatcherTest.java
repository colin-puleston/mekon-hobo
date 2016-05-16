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

package uk.ac.manchester.cs.mekon.owl.reason;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.demomodel.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.build.*;

/**
 * @author Colin Puleston
 */
public abstract class ORMatcherTest extends IMatcherTest {

	static private final String NON_OWL_TYPE = "NON-OWL-TYPE";
	static private final String NON_OWL_BUT_OWL_SUBSUMED_TYPE = "NON-OWL-BUT-OWL-SUBSUMED-TYPE";

	private class ORMatcherSectionBuilder extends OBSectionBuilder {

		private class ModelEnhancer extends DemoModelBuilder {

			ModelEnhancer(CBuilder builder) {

				super(builder);

				addFrame(NON_OWL_TYPE);
				addFrame(NON_OWL_BUT_OWL_SUBSUMED_TYPE);
				addSuperFrame(NON_OWL_BUT_OWL_SUBSUMED_TYPE, JOB);
			}
		}

		public void build(CBuilder builder) {

			super.build(builder);
			new ModelEnhancer(builder);
		}

		ORMatcherSectionBuilder() {

			super(oModel);

			setIReasoner(new ORClassifier(oModel));
		}
	}

	private OModel oModel = ODemoModel.create();

	@Test
	public void test_handlesType() {

		testHandlesType(JOB, true);
		testHandlesType(NON_OWL_TYPE, false);
		testHandlesType(NON_OWL_BUT_OWL_SUBSUMED_TYPE, true);
	}

	protected CSectionBuilder createSectionBuilder() {

		return new ORMatcherSectionBuilder();
	}

	protected IMatcher createMatcher() {

		return createMatcher(oModel);
	}

	protected abstract ORMatcher createMatcher(OModel oModel);

	private void testHandlesType(String typeId, boolean shouldHandle) {

		assertTrue(getMatcher().handlesType(getCFrame(typeId)) == shouldHandle);
	}
}

