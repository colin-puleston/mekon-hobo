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
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.icon.*;

/**
 * @author Colin Puleston
 */
abstract class EntityIconsBySource implements EntityIconConstants {

	private Map<CSource, Icon> icons = new HashMap<CSource, Icon>();

	void initialise() {

		add(CSource.INTERNAL, INTERNAL_SOURCE_CLR, null);
		add(CSource.EXTERNAL, EXTERNAL_SOURCE_CLR, null);
		add(CSource.DUAL, INTERNAL_SOURCE_CLR, EXTERNAL_SOURCE_CLR);
		add(CSource.UNSPECIFIED, UNSPECIFIED_SOURCE_CLR, null);
	}

	Icon get(CSource source) {

		return icons.get(source);
	}

	GIcon create(Color mainClr, Color innerClr) {

		GIcon icon = new GIcon();

		icon.addRenderer(createMainRenderer(mainClr));

		if (innerClr != null) {

			icon.addRenderer(createInnerRenderer(innerClr));
		}

		return icon;
	}

	abstract GIconRenderer createRenderer(Color clr, int size);

	private void add(CSource source, Color mainClr, Color innerClr) {

		icons.put(source, create(mainClr, innerClr));
	}

	private GIconRenderer createMainRenderer(Color clr) {

		return createRenderer(clr, ENTITY_SIZE);
	}

	private GIconRenderer createInnerRenderer(Color clr) {

		GIconRenderer r = createRenderer(clr, ENTITY_INNER_SIZE);

		r.setDrawEnabled(false);
		r.setXOffset(ENTITY_RIM_SIZE);
		r.setYOffset(ENTITY_RIM_SIZE);

		return r;
	}
}


