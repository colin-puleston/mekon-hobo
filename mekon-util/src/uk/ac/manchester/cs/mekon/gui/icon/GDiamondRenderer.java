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

package uk.ac.manchester.cs.mekon.gui.icon;

import java.awt.*;

/**
 * @author Colin Puleston
 */
public class GDiamondRenderer extends GIconRenderer {

	private int diagonal;
	private int side;

	public GDiamondRenderer(Color color, int diagonal) {

		super(color, diagonal, diagonal);

		this.diagonal = diagonal;
		side = getSideLength();
	}

	int getXOffset() {

		return shiftOffset(super.getXOffset());
	}

	int getYOffset() {

		return shiftOffset(super.getYOffset());
	}

	void draw(Graphics2D g, int x, int y, int width, int height) {

		rotate(g, x, y, 1);
		g.drawRect(x, y, side, side);
		rotate(g, x, y, -1);
	}

	void fill(Graphics2D g, int x, int y, int width, int height) {

		rotate(g, x, y, 1);
		g.fillRect(x, y, side, side);
		rotate(g, x, y, -1);
	}

	private int getSideLength() {

		double dSq = diagonal * diagonal;

		return Math.round((float)Math.sqrt(dSq / 2.0));
	}

	private void rotate(Graphics2D g, int x, int y, int direction) {

		double theta = (Math.PI * direction) / 4.0;
		int offset = side / 2;

		g.rotate(theta, x + offset, y + offset);
	}

	private int shiftOffset(int rawOffset) {

		return rawOffset + Math.round((float)(diagonal - side) / 2);
	}
}
