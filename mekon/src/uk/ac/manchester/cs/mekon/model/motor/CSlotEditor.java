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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mechanisms for editing a specific {@link CSlot} object.
 *
 * @author Colin Puleston
 */
public interface CSlotEditor {

	/**
	 * Sets the source for the slot.
	 *
	 * @param source Source for slot
	 */
	public void setSource(CSource source);

	/**
	 * Resets the label for the slot (as represented within the
	 * associated {@link CIdentity} object).
	 *
	 * @param newLabel New-label for slot
	 */
	public void resetLabel(String newLabel);

	/**
	 * Sets the slot value-type to be whichever is more-specific
	 * between the current value-type and the other specified
	 * value-type (as defined by {@link CValue#subsumedBy(CValue)}).
	 *
	 * @param otherValueType Other candidate value-type for slot
	 * @throws KModelException if neither value-type subsumes the
	 * other
	 */
	public void absorbValueType(CValue<?> otherValueType);

	/**
	 * Sets the slot cardinality to be whichever is the more
	 * restrictive between the current cardinality and the other
	 * specified cardinality (as defined by
	 * {@link CCardinality#moreRestrictiveThan(CCardinality)}).
	 *
	 * @param otherCardinality Other candidate cardinality for slot
	 */
	public void absorbCardinality(CCardinality otherCardinality);

	/**
	 * Updates the activation of the slot to incorporate the specified
	 * additional activation value. Resulting status determined via the
	 * {@link CActivation#getWeakest} method.
	 *
	 * @param otherActive Additional activation to incorporate
	 */
	public void absorbActivation(CActivation otherActivation);

	/**
	 * Updates the editability of the slot to incorporate the specified
	 * additional editability value. Resulting status determined via the
	 * {@link CEditability#getStrongest} method.
	 *
	 * @param otherEditability Additional editability to incorporate
	 */
	public void absorbEditability(CEditability otherEditability);
}
