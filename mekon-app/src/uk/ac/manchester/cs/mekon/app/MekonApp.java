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

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
public class MekonApp extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String EXIT_LABEL = "EXIT";

	static private final int FRAME_WIDTH = 600;
	static private final int FRAME_HEIGHT = 600;

	static private final String INSTANCES_TAB_TITLE = "Instances";
	static private final String QUERIES_TAB_TITLE = "Queries";
	static private final String MATCHES_TAB_TITLE = "Query Matches";

	static private IStore getIStore(CBuilder builder) {

		return IDiskStoreManager.getBuilder(builder).build();
	}

	private CModel model;
	private IStore store;

	private Customiser customiser = null;

	private class ExitButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		ExitButton() {

			super(EXIT_LABEL);
		}
	}

	private class InstanceTypesPanelCreator {

		private Store appStore = new Store(store);
		private Controller controller = new Controller(appStore, resolveCustomiser());

		JComponent create(List<CFrame> types) {

			JTabbedPane panel = new JTabbedPane();

			for (CFrame type : types) {

				panel.addTab(getTypePanelTitle(type), createTypePanel(type));
			}

			return panel;
		}

		private JComponent createTypePanel(CFrame type) {

			return new InstanceTypePanel(appStore, controller.addInstanceType(type));
		}

		private String getTypePanelTitle(CFrame type) {

			return type.getDisplayLabel() + 's';
		}
	}

	public MekonApp(String title) {

		this(title, CManager.createBuilder());
	}

	public MekonApp(String title, CBuilder builder) {

		this(title, builder.build(), getIStore(builder));
	}

	public MekonApp(String title, CModel model, IStore store) {

		super(title, FRAME_WIDTH, FRAME_HEIGHT);

		this.model = model;
		this.store = store;

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void setCustomiser(Customiser customiser) {

		this.customiser = customiser;
	}

	public void display(List<CFrame> instanceTypes) {

		display(createMainPanel(instanceTypes));
	}

	public CModel getModel() {

		return model;
	}

	public IStore getStore() {

		return store;
	}

	private JPanel createMainPanel(List<CFrame> instanceTypes) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createInstanceTypesPanel(instanceTypes), BorderLayout.CENTER);
		panel.add(new ExitButton(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createInstanceTypesPanel(List<CFrame> instanceTypes) {

		return new InstanceTypesPanelCreator().create(instanceTypes);
	}

	private Customiser resolveCustomiser() {

		if (customiser == null) {

			customiser = new DefaultCustomiser(store);
		}

		return customiser;
	}
}
