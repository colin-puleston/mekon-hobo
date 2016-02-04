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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.hobo.*;

/**
 * A <code>DNumberRange</code>-valued cell bound to the same slot as
 * a specific <code>Number</code>-valued "source" cell, enabling the
 * setting of numeric-range values for the slot.
 * <p>
 * NOTE: Objects of this type can only be created for source-cells
 * whose associated slots are abstract-editable (i.e. have editabilily
 * status of {@link IEditability.FULL}).
 *
 * @author Colin Puleston
 */
public class DNumberRangeCell<N extends Number> extends DCell<DNumberRange<N>> {

	/**
	 * Constructor.
	 *
	 * @param source Source cell
	 * @throws KAccessException if relevant slot is not abstract-editable
	 */
	public DNumberRangeCell(DCell<N> sourceCell) {

		super(sourceCell.getModel(), new DNumberRangeValueType<N>(sourceCell));

		initialiseAbstractField(sourceCell);
	}

	/**
	 * Constructor.
	 *
	 * @param sourceCellViewer Viewer for source cell
	 * @throws KAccessException if relevant slot is not abstract-editable
	 */
	public DNumberRangeCell(DCellViewer<N> sourceCellViewer) {

		this(sourceCellViewer.getField());
	}
}
