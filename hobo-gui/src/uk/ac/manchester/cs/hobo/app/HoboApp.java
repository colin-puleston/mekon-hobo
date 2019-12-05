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

package uk.ac.manchester.cs.hobo.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.app.*;

import uk.ac.manchester.cs.hobo.manage.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
public class HoboApp {

	static private IStore getIStore(DBuilder builder) {

		return IDiskStoreManager.getBuilder(builder.getCBuilder()).build();
	}

	private DModel model;
	private MekonApp mekonApp;

	public HoboApp(String title) {

		this(title, DManager.createBuilder());
	}

	public HoboApp(String title, DBuilder builder) {

		this(title, builder.build(), getIStore(builder));
	}

	public HoboApp(String title, DModel model, IStore store) {

		this.model = model;

		mekonApp = new MekonApp(title, model.getCModel(), store);
	}

	public void setCustomiser(Customiser customiser) {

		mekonApp.setCustomiser(customiser);
	}

	public void display(List<Class<? extends DObject>> instanceClasses) {

		mekonApp.display(toFrames(instanceClasses));
	}

	public DModel getModel() {

		return model;
	}

	public IStore getStore() {

		return mekonApp.getStore();
	}

	private List<CFrame> toFrames(List<Class<? extends DObject>> dClasses) {

		List<CFrame> frames = new ArrayList<CFrame>();

		for (Class<? extends DObject> dClass : dClasses) {

			frames.add(toFrame(dClass));
		}

		return frames;
	}

	private CFrame toFrame(Class<? extends DObject> dClass) {

		return model.getConcept(dClass).getFrame();
	}
}
