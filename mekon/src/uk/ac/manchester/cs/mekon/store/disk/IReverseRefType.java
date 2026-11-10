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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Interface implemented by classes representing specific types of
 * reverse-direction references between instances of specific types,
 * occurences of which are to be maintained as relevant updates are
 * made to the instance-store. In general, the instances containing the
 * forward-direction references may contain multiple such references,
 * whilst the corresponding instances containing the reverse-direction
 * references will each contain only one such reference.
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
	 * Provides all slot values within a particular instance of the
	 * relevant type that represent the actual forward-direction references.
	 *
	 * @param forwardRefer Instance of type that may contain forward-direction
	 * references
	 * @return All relevant reference values within instance
	 */
	public Collection<IFrame> getAllForwardRefValues(IFrame forwardRefer);

	/**
	 * Checks whether the slot for the reverse-ref values is currently present
	 * within a particular reverse-referencer type instance.
	 *
	 * @param reverseRefer Instance of type that may contain reverse-direction
	 * references
	 * @return True if slot for relevant reverse-ref values is currently
	 * present
	 */
	public boolean reverseRefSlotPresent(IFrame reverseRefer);

	/**
	 * Adds a particular reverse-direction reference value to the relevant
	 * slot within a particular instance, if that slot currently exists.
	 *
	 * @param forwardRefer Instance containing forward-direction reference
	 * @param reverseRefer Instance from which reverse-direction reference
	 * value is to be added
	 * @param reverseRef Reference value to be added
	 */
	public boolean checkAddReverseRef(
						IFrame forwardRefer,
						IFrame reverseRefer,
						CIdentity reverseRef);

	/**
	 * Removes a particular reverse-direction reference value from the
	 * relevant slot within a particular instance, if that slot currently
	 * exists.
	 *
	 * @param forwardRefer Instance containing forward-direction reference
	 * @param reverseRefer Instance from which reverse-direction reference
	 * value is to be removed
	 * @param reverseRef Reference value to be removed
	 */
	public boolean checkRemoveReverseRef(
						IFrame forwardRefer,
						IFrame reverseRefer,
						CIdentity reverseRef);

	/**
	 * Aligns the reverse-direction references within a new instance of
	 * the relevant type with those of an instance for which it is a direct
	 * replacement.
	 *
	 * @param oldReverseRefer Instance to be aligned with
	 * @param newReverseRefer Instance to be aligned
	 */
	public void alignReverseRefs(IFrame oldReverseRefer, IFrame newReverseRefer);
}
