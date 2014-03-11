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

package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.util.icon.*;

/**
 * @author Colin Puleston
 */
class EntityIconsByLevel {

	private Map<EntityLevel, Icon> icons = new HashMap<EntityLevel, Icon>();

	EntityIconsByLevel(Color clr, int size) {

		add(EntityLevel.INSTANCE, clr, size);
		add(EntityLevel.CONCEPT, clr, size);
		add(EntityLevel.META, clr, size);
	}

	Icon get(EntityLevel level) {

		return icons.get(level);
	}

	private void add(EntityLevel level, Color clr, int size) {

		icons.put(level, create(level, clr, size));
	}

	private GIcon create(EntityLevel level, Color clr, int size) {

		return new GIcon(EntityRenderersByLevel.create(level, clr, size));
	}
}


