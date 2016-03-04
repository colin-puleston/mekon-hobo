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

package uk.ac.manchester.cs.mekon.model;

/**
 * Listener for dynamic operations on a {@link ISlot} object.
 *
 * @author Colin Puleston
 */
public interface ISlotListener {

	/**
	 * Method invoked after the slot's value-type has been updated.
	 *
	 * @param valueType New value-type for slot
	 */
	public void onUpdatedValueType(CValue<?> valueType);

	/**
	 * Method invoked after the slot's cardinality has been updated.
	 *
	 * @param cardinality New cardinality
	 */
	public void onUpdatedCardinality(CCardinality cardinality);

	/**
	 * Method invoked after the slot's "active" status has been updated.
	 *
	 * @param active New active status
	 */
	public void onUpdatedActiveStatus(boolean active);

	/**
	 * Method invoked after the slot's editability has been updated.
	 *
	 * @param editability New editability
	 */
	public void onUpdatedEditability(CEditability editability);
}
