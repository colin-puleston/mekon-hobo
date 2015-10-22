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

package uk.ac.manchester.cs.mekon.mechanism.core;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Point-of-entry for the MEKON mechanisms, and the mechanisms of
 * any extensions of the MEKON framework.
 *
 * @author Colin Puleston
 */
public class ZMekonManager {

	static private final String MODEL_CLASS_NAME
			= "uk.ac.manchester.cs.mekon.model.CModel";

	static private ZMekonBootstrapper bootstrapper = null;

	static private Map<CModel, ZMekonAccessor> accessors
						= new HashMap<CModel, ZMekonAccessor>();

	/**
	 * Initialises manager with the bootstrapper, as provided by
	 * {@link CModel} class upon loading.
	 *
	 * @param booter Bootstrapper provided by model class
	 */
	static public void initialise(ZMekonBootstrapper booter) {

		bootstrapper = booter;
	}

	/**
	 * Creates an empty model with the default customiser.
	 *
	 * @return Accessor for model
	 */
	static public ZMekonAccessor start() {

		return start(new CustomiserDefault());
	}

	/**
	 * Creates an empty model with the specified customiser.
	 *
	 * @param customiser Customiser for model
	 * @return Accessor for model
	 */
	static public ZMekonAccessor start(ZMekonCustomiser customiser) {

		ZMekonAccessor accessor = getBootstrapper().start(customiser);

		register(accessor);

		return accessor;
	}

	/**
	 * Retrieves the accessor for the model.
	 *
	 * @param model Relevant model
	 * @return Accessor for model
	 */
	static public ZMekonAccessor access(CModel model) {

		ZMekonAccessor accessor = accessors.get(model);

		if (accessor == null) {

			throw new Error("MEKON model has not been registered!");
		}

		return accessor;
	}

	static private ZMekonBootstrapper getBootstrapper() {

		loadModelClassToInitialiseBootstrapper();

		if (bootstrapper == null) {

			throw new Error("MEKON bootstrapper has not been registered!");
		}

		return bootstrapper;
	}

	static private void loadModelClassToInitialiseBootstrapper() {

		try {

			Class.forName(MODEL_CLASS_NAME);
		}
		catch (ClassNotFoundException e) {

			throw new Error("MEKON model class not found!");
		}
	}

	static private synchronized void register(ZMekonAccessor accessor) {

		accessors.put(accessor.getModel(), accessor);
	}
}
