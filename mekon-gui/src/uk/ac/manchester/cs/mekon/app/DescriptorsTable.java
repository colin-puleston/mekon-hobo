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

/**
 * @author Colin Puleston
 */
class DescriptorsTable extends ActiveTable {

	static private final long serialVersionUID = -1;

	static private final String[] TITLES = new String[]{"Attribute", "Value"};

	private AspectWindow aspectWindow;
	private DescriptorsList list;

	DescriptorsTable(AspectWindow aspectWindow, DescriptorsList list) {

		super(TITLES);

		this.aspectWindow = aspectWindow;
		this.list = list;

		addRows();
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	private void addRows() {

		for (Descriptor descriptor : list.getList()) {

			addRow(toRow(descriptor));
		}
	}

	private Object[] toRow(Descriptor descriptor) {

		DescriptorDisplay display = createDisplay(descriptor);

		return new ActiveTableCell[] {
					display.createIdentityCell(),
					display.createValueCell()};
	}

	private DescriptorDisplay createDisplay(Descriptor descriptor) {

		return new DescriptorDisplay(aspectWindow, descriptor);
	}
}
