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
 * Represents the editability from the client perspective of a
 * particular {@link ISlot}. Covers slot editability status for
 * both concrete and abstract values (see {@link
 * IValue#abstractValue})
 *
 * @author Colin Puleston
 */
public enum IEditability {

	/**
	 * Slot is not editable by the client.
	 */
	NONE,

	/**
	 * Slot can be given concrete values only by the client.
	 */
	CONCRETE_ONLY,

	/**
	 * Slot can be given both concrete and abstract values by the
	 * client.
	 */
	FULL;

	/**
	 * Specifies whether slot is editable in any way by the client,
	 * which will be the case if and only if value is either {@link
	 * #CONCRETE_ONLY} or {@link #FULL}.
	 *
	 * @return True is slot is editable
	 */
	public boolean editable() {

		return this != NONE;
	}

	/**
	 * Specifies whether the slot can be given abstract values by the
	 * client, which will be the case if and only if value is {@link
	 * #FULL}.
	 *
	 * @return True is slot is abstract-editable
	 */
	public boolean abstractEditable() {

		return this == FULL;
	}
}
