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

import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GIcon implements Icon {

	static private final int BORDER = 2;

	private int width = 0;
	private int height = 0;
	private List<GIconRenderer> renderers = new ArrayList<GIconRenderer>();

	public GIcon() {
	}

	public GIcon(GIconRenderer... renderers) {

		for (GIconRenderer renderer : renderers) {

			addRenderer(renderer);
		}
	}

	public void addRenderer(GIconRenderer renderer) {

		renderers.add(renderer);

		int w = renderer.getXOffset() + renderer.getWidth();
		int h = renderer.getYOffset() + renderer.getHeight();

		if (w > width) {

			width = w;
		}

		if (h > height) {

			height = h;
		}
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {

		Graphics2D g2d = asGraphics2D(g);

		x += BORDER;
		y += BORDER;

		for (GIconRenderer renderer : renderers) {

			renderer.paint(g2d, x, y);
		}
	}

	public int getIconWidth() {

		return width + BORDER;
	}

	public int getIconHeight() {

		return height + BORDER;
	}

	private Graphics2D asGraphics2D(Graphics g) {

		if (g instanceof Graphics2D) {

			return (Graphics2D)g;
		}

		throw new Error("Graphics object not of type Graphics2D: " + g.getClass());
	}
}


