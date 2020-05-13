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
public class GTriangleRenderer extends GIconRenderer {

	private Type type;

	public enum Type {

		UPWARD {

			int[] getXs(int x, int width) {

				return new int[]{x, x + (width / 2), x + width};
			}

			int[] getYs(int y, int height) {

				return new int[]{y + height, y, y + height};
			}
		},

		DOWNWARD {

			int[] getXs(int x, int width) {

				return new int[]{x, x + (width / 2), x + width};
			}

			int[] getYs(int y, int height) {

				return new int[]{y, y + height, y};
			}
		},

		RIGHTWARD {

			int[] getXs(int x, int width) {

				return new int[]{x, x, x + width};
			}

			int[] getYs(int y, int height) {

				return new int[]{y, y + height, y + (height / 2)};
			}
		},

		LEFTWARD {

			int[] getXs(int x, int width) {

				return new int[]{x, x + width, x + width};
			}

			int[] getYs(int y, int height) {

				return new int[]{y + (height / 2), y, y + height};
			}
		};

		abstract int[] getXs(int x, int width);

		abstract int[] getYs(int y, int height);
	}

	public GTriangleRenderer(Type type, Color color, int dimension) {

		this(type, color, dimension, dimension);
	}

	public GTriangleRenderer(Type type, Color color, int width, int height) {

		super(color, width, height);

		this.type = type;
	}

	void draw(Graphics2D g, int x, int y, int width, int height) {

		g.drawPolygon(type.getXs(x, width), type.getYs(y, height), 3);
	}

	void fill(Graphics2D g, int x, int y, int width, int height) {

		g.fillPolygon(type.getXs(x, width), type.getYs(y, height), 3);
	}
}


