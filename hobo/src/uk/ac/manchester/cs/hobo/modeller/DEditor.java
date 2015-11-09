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

package uk.ac.manchester.cs.hobo.modeller;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * Provides the OM classes with access to mechanisms for performing
 * internal model-instantiation updates that cannot be performed by
 * the client code.
 *
 * @author Colin Puleston
 */
public interface DEditor {

	/**
	 * Provides the {@link IEditor} object for the model, which provides
	 * access to mechanisms for editing the {@link CModel}-instantiation,
	 * which are not accessible to the client code.
	 *
	 * @return Frames-based model instantiation editor
	 */
	public IEditor getIEditor();

	/**
	 * Retrieves the OM field being viewed by the specified viewer.
	 *
	 * @param <V> Generic version of field value-type
	 * @param <F> Generic version of field-type
	 * @param viewer Viewer for which field is required
	 * @return Viewed field
	 */
	public <V, F extends DField<V>>F getField(DFieldViewer<V, F> viewer);

	/**
	 * Retrieves the OM cell being viewed by the specified viewer.
	 *
	 * @param <V> Generic version of cell value-type
	 * @param viewer Viewer for which cell is required
	 * @return Viewed cell
	 */
	public <V>DCell<V> getCell(DCellViewer<V> viewer);

	/**
	 * Retrieves the OM number-cell being viewed by the specified viewer.
	 *
	 * @param <N> Generic version of cell value-type
	 * @param viewer Viewer for which cell is required
	 * @return Viewed cell
	 */
	public <N extends Number>DNumberCell<N> getCell(DNumberCellViewer<N> viewer);

	/**
	 * Retrieves the OM array being viewed by the specified viewer.
	 *
	 * @param <V> Generic version of array value-type
	 * @param viewer Viewer for which array is required
	 * @return Viewed array
	 */
	public <V>DArray<V> getArray(DArrayViewer<V> viewer);
}