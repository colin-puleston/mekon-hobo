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

package uk.ac.manchester.cs.mekon.gui.util.icon;

import java.awt.*;

/**
 * @author Colin Puleston
 */
public abstract class GIconRenderer {

	static private final Color LINE_CLR = Color.BLACK;

	private Color fillColour;
	private int width;
	private int height;
	private int xOffset = 0;
	private int yOffset = 0;
	private boolean drawEnabled = true;
	private boolean fillEnabled = true;

	public GIconRenderer(Color fillColour, int width, int height) {

		this.fillColour = fillColour;
		this.width = width;
		this.height = height;
	}

	public void setXOffset(int xOffset) {

		this.xOffset = xOffset;
	}

	public void setYOffset(int yOffset) {

		this.yOffset = yOffset;
	}

	public void setDrawEnabled(boolean drawEnabled) {

		this.drawEnabled = drawEnabled;
	}

	public void setFillEnabled(boolean fillEnabled) {

		this.fillEnabled = fillEnabled;
	}

	int getWidth() {

		return width;
	}

	int getHeight() {

		return height;
	}

	int getXOffset() {

		return xOffset;
	}

	int getYOffset() {

		return yOffset;
	}

	void paint(Graphics2D g, int x, int y) {

		setRenderingHints(g);

		x += getXOffset();
		y += getYOffset();

		Color oldColor = g.getColor();

		if (fillEnabled) {

			g.setColor(fillColour);
			fill(g, x, y, width, height);
		}

		if (drawEnabled) {

			g.setColor(LINE_CLR);
			draw(g, x, y, width, height);
		}

		g.setColor(oldColor);
	}

	abstract void draw(Graphics2D g, int x, int y, int width, int height);

	abstract void fill(Graphics2D g, int x, int y, int width, int height);

	private void setRenderingHints(Graphics2D g) {

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
	}
}


