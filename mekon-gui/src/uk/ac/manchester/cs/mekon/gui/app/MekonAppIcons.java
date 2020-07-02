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

package uk.ac.manchester.cs.mekon.gui.app;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.icon.*;

/**
 * @author Colin Puleston
 */
class MekonAppIcons {

	static private final Color ASSERTION_VALUE_CLR = new Color(49,130,189);
	static private final Color QUERY_VALUE_CLR = new Color(49,163,84);
	static private final Color NO_VALUE_CLR = Color.WHITE;

	static private final int ICON_DIMENSION = 12;

	static final Icon ASSERTION_VALUE = createDefaultValueIcon(ASSERTION_VALUE_CLR);
	static final Icon QUERY_VALUE = createDefaultValueIcon(QUERY_VALUE_CLR);

	static final Icon ASSERTION_ARRAY = createArrayIcon(ASSERTION_VALUE_CLR);
	static final Icon QUERY_ARRAY = createArrayIcon(QUERY_VALUE_CLR);

	static final Icon ASSERTION_REF = createInstanceRefIcon(ASSERTION_VALUE_CLR);
	static final Icon QUERY_REF = createInstanceRefIcon(QUERY_VALUE_CLR);

	static final Icon NO_VALUE = createNoValueIcon(NO_VALUE_CLR);

	static private GIcon createDefaultValueIcon(Color clr) {

		return new GIcon(new GDiamondRenderer(clr, ICON_DIMENSION));
	}

	static private GIcon createArrayIcon(Color clr) {

		return new GIcon(new GDiamondRenderer(clr, ICON_DIMENSION));
	}

	static private GIcon createInstanceRefIcon(Color clr) {

		return new GIcon(new GOvalRenderer(clr, ICON_DIMENSION));
	}

	static private GIcon createNoValueIcon(Color clr) {

		return new GIcon(createNoValueRenderer(clr));
	}

	static private GIconRenderer createNoValueRenderer(Color clr) {

		return new GTriangleRenderer(
						GTriangleRenderer.Type.RIGHTWARD,
						clr,
						ICON_DIMENSION,
						ICON_DIMENSION);
	}
}
