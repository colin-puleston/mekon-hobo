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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class InstantiationGFrame extends GFrame implements AspectWindow {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s";

	static private final int FRAME_WIDTH = 700;

	static String createTitle(InstanceType instanceType, String suffix) {

		return String.format(TITLE_FORMAT, getTypeLabel(instanceType), suffix);
	}

	static private String getTypeLabel(InstanceType instanceType) {

		return instanceType.getType().getIdentity().getLabel();
	}

	private Instantiator instantiator;

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	public Window getRootWindow() {

		return this;
	}

	public Instantiator getInstantiator() {

		return instantiator;
	}

	public void displayCopy() {

		createCopy().display();
	}

	InstantiationGFrame(Instantiator instantiator, String title) {

		super(title);

		this.instantiator = instantiator;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	void display() {

		display(createDisplay());
	}

	abstract InstantiationGFrame createCopy();

	abstract JComponent createMainComponent(JComponent instantiationComponent);

	abstract JComponent createControlsComponent();

	IFrame getInstantiation() {

		return instantiator.getInstantiation();
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createMainComponent(), BorderLayout.CENTER);
		panel.add(createControlsComponent(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createMainComponent() {

		return createMainComponent(new JScrollPane(createDescriptorsTable()));
	}

	private DescriptorsTable createDescriptorsTable() {

		return new DescriptorsTable(this, createDescriptorsList());
	}

	private DescriptorsList createDescriptorsList() {

		return new DescriptorsList(instantiator, getInstantiation());
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}
