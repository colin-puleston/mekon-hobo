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

package uk.ac.manchester.cs.mekon.model.motor;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * Responsible for generating "free-instance" versions of existing
 * instance-level frame/slot networks. A free instance is one in which
 * the schema has been loosened in the following ways:
 * <ul>
 *   <li>No effective constraints on slot-values, other than general
 *	 value-category (i.e, {@link IFrame}, {@link CFrame}, {@link INumber}
 *	 or {@link IString})
 *   <li>No automatic updates to slot-sets or slot-values due to
 *	 either generic reasoning mechanisms, or custom procedures
 *	 associated with a mapped object model
 * </ul>
 * <p>
 * Such free-instances are intended for use by any plug-in mechanisms
 * that need to manipulate their instances in a way that (a) is
 * outside the general schema imposed by the model, and/or (b) does not
 * incur the additional, and unneccesary overheads of the reasoning
 * mechanisms kicking in as updates are made.
 *
 * @author Colin Puleston
 */
public abstract class IFreeCopier {

	/**
	 * Provides a free-copier object applicable to any model.
	 *
	 * @return Free-copier object for any model
	 */
	static public IFreeCopier get() {

		return ZCModelAccessor.get().getFreeCopier();
	}

	/**
	 * Generates a free-instance version of the specified source
	 * instance, with the same {@link IFrameFunction} as the source
	 * instance.
	 *
	 * @param sourceInstance Instance of which free-instance version
	 * is required
	 * @return Generated free-instance
	 */
	public abstract IFrame createFreeCopy(IFrame sourceInstance);

	/**
	 * Generates a free-instance version of the specified source
	 * instance.
	 *
	 * @param sourceInstance Instance of which free-instance version
	 * is required
	 * @param copyFunction Required function of generated instance
	 * @return Generated free-instance
	 */
	public abstract IFrame createFreeCopy(
								IFrame sourceInstance,
								IFrameFunction copyFunction);
}
