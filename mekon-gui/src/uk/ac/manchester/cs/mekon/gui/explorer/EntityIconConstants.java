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

package uk.ac.manchester.cs.mekon.gui.explorer;

import java.awt.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
interface EntityIconConstants {

	static final Color INTERNAL_SOURCE_CLR = Color.blue;
	static final Color EXTERNAL_SOURCE_CLR = Color.red;
	static final Color UNSPECIFIED_SOURCE_CLR = Color.gray;
	static final Color DATA_VALUE_CLR = Color.green.darker().darker();
	static final Color HIDDEN_FRAME_MARKER_CLR = Color.darkGray.darker();
	static final Color CURBED_SLOT_MARKER_CLR = Color.darkGray.darker();
	static final Color FREED_SLOT_MARKER_CLR = Color.white;

	static final int ENTITY_INNER_SIZE = 8;
	static final int ENTITY_RIM_SIZE = 3;
	static final int ENTITY_SIZE = ENTITY_INNER_SIZE + (ENTITY_RIM_SIZE * 2);
	static final int HIDDEN_FRAME_MARKER_WIDTH = ENTITY_SIZE;
	static final int HIDDEN_FRAME_MARKER_HEIGHT = 4;
	static final int CURBED_SLOT_MARKER_WIDTH = 4;
	static final int CURBED_SLOT_MARKER_HEIGHT = ENTITY_SIZE;
	static final int FREED_SLOT_MARKER_SIZE = 8;
}
