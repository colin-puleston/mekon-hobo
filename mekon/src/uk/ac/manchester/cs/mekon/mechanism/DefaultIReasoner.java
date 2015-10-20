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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides the default version of the reasoning mechanisms
 * defined by {@link IReasoner}. Initialises each instance-level
 * frame with a set of slots derived from the relevant
 * concept-level frame and it's ancestors. Performs no dynamic
 * updating of the slot-sets.
 *
 * @author Colin Puleston
 */
public class DefaultIReasoner implements IReasoner {

	static private final IReasoner singleton = new DefaultIReasoner();

	/**
	 * Provides singleton instance of {@link DefaultIReasoner}
	 *
	 * @return Singleton instance
	 */
	static public IReasoner get() {

		return singleton;
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialiseFrame(IEditor iEditor, IFrame frame) {

		new ISlotSpecs(iEditor, frame.getType()).initialise(frame);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IUpdateOp> updateFrame(
							IEditor iEditor,
							IFrame frame,
							Set<IUpdateOp> ops) {

		return Collections.<IUpdateOp>emptySet();
	}

	/**
	 * Constructor for extension classes.
	 */
	protected DefaultIReasoner() {
	}
}
