/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.icon.*;

/**
 * @author Colin Puleston
 */
class MekonAppIcons {

	static private final Color ASSERTION_FRAME_CLR = Color.CYAN;
	static private final Color QUERY_FRAME_CLR = Color.GREEN;
	static private final Color DATA_CLR = Color.BLACK;
	static private final Color AUTO_DESCRIPTOR_CLR = Color.GRAY;
	static private final Color USER_DESCRIPTOR_CLR = Color.LIGHT_GRAY;
	static private final Color EMPTY_DESCRIPTOR_CLR = Color.WHITE;

	static final Icon ASSERTION_TYPE = createInstanceTypeIcon(ASSERTION_FRAME_CLR);
	static final Icon QUERY_TYPE = createInstanceTypeIcon(QUERY_FRAME_CLR);

	static final Icon ASSERTION_REF = createInstanceRefIcon(ASSERTION_FRAME_CLR);
	static final Icon QUERY_REF = createInstanceRefIcon(QUERY_FRAME_CLR);

	static final Icon DATA_VALUE = createDataValueIcon();

	static final Icon AUTO_DESCRIPTOR = createDescrptorIcon(AUTO_DESCRIPTOR_CLR);
	static final Icon USER_DESCRIPTOR = createDescrptorIcon(USER_DESCRIPTOR_CLR);
	static final Icon EMPTY_DESCRIPTOR = createDescrptorIcon(EMPTY_DESCRIPTOR_CLR);

	static private GIcon createInstanceTypeIcon(Color clr) {

		return new GIcon(new GOvalRenderer(clr, 12));
	}

	static private GIcon createInstanceRefIcon(Color clr) {

		return new GIcon(new GDiamondRenderer(clr, 12));
	}

	static private GIcon createDataValueIcon() {

		return new GIcon(new GRectangleRenderer(DATA_CLR, 10));
	}

	static private GIcon createDescrptorIcon(Color clr) {

		return new GIcon(createDescrptorRenderer(clr));
	}

	static private GIconRenderer createDescrptorRenderer(Color clr) {

		return new GTriangleRenderer(GTriangleRenderer.Type.RIGHTWARD, clr, 12, 12);
	}
}
