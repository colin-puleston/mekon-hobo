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

package uk.ac.manchester.cs.hobo.gui.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.gui.app.*;

import uk.ac.manchester.cs.hobo.manage.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
public class HoboApp extends MekonApp {

	static private final long serialVersionUID = -1;

	static public void main(String[] args) {

		configureAndDisplay(new HoboApp(), args);
	}

	static private IStore getIStore(DBuilder builder) {

		return IDiskStoreManager.getBuilder(builder.getCBuilder()).build();
	}

	private DModel model;

	public HoboApp() {

		this(DManager.createBuilder());
	}

	public HoboApp(DBuilder builder) {

		this(builder.build(), getIStore(builder));
	}

	public HoboApp(DModel model, IStore store) {

		super(model.getCModel(), store);

		this.model = model;
	}

	public void addDirectInstanceGroup(
					Class<? extends DObject> rootClass,
					boolean editable) {

		addInstanceGroup(toFrame(rootClass), editable);
	}

	public void addDirectInstanceGroups(
					List<Class<? extends DObject>> rootClasses,
					boolean editable) {

		addInstanceGroups(toFrames(rootClasses), editable);
	}

	public DModel getDModel() {

		return model;
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
