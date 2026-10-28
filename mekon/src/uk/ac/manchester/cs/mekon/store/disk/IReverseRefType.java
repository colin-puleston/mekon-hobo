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

package uk.ac.manchester.cs.mekon.store.disk;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Interface implemented by classes representing specific types of
 * reverse-direction references between instances of specific types,
 * occurences of which are to be maintained as relevant updates are
 * made to the instance-store.
 *
 * @author Colin Puleston
 */
public interface IReverseRefType {

	/**
	 * Provides the type of the instances containing the
	 * forward-direction references whose reverse-direction references
	 * are to be maintained.
	 *
	 * @return Type of the instances containing the forward-direction
	 * references
	 */
	public CFrame getForwardReferType();

	/**
	 * Provides the type of the instances containing the
	 * reverse-direction references that are to be maintained.
	 *
	 * @return Type of the instances containing the reverse-direction
	 * references
	 */
	public CFrame getReverseReferType();

	/**
	 * Provides the frame-value of the specific slot within a particular
	 * instance of the forward-direction reference type that represents
	 * the value of the actual forward-direction reference, if such a
	 * value currently exists.
	 *
	 * @param forwardRefer Forward-direction reference type instance
	 * @return Relevant reference value within instance, or null if value
	 * does not currently exist
	 */
	public IFrame lookForForwardRefValue(IFrame forwardRefer);

	/**
	 * Adds a particular reverse-direction reference value to the relevant
	 * slot within a particular instance, if that slot currently exists.
	 *
	 * @param forwardRefer Instance to be added as reference value
	 * @param reverseRefer Instance to which reference value is to be added
	 */
	public boolean checkAddReverseRef(IFrame forwardRefer, IFrame reverseRefer);

	/**
	 * Removes a particular reverse-direction reference value from the
	 * relevant slot within a particular instance, if that slot currently
	 * exists.
	 *
	 * @param forwardRefer Instance to be removed as reference value
	 * @param reverseRefer Instance from which reference value is to be
	 * removed
	 */
	public boolean checkRemoveReverseRef(IFrame forwardRefer, IFrame reverseRefer);

	/**
	 * Aligns the reverse-direction references within a new instance of
	 * the relevant type with those of an instance for which it a direct
	 * replacement.
	 *
	 * @param oldReverseRefer Instance to be aligned with
	 * @param newReverseRefer Instance to be aligned
	 */
	public void alignReverseRefs(IFrame oldReverseRefer, IFrame newReverseRefer);
}
