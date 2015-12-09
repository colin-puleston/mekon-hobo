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

package uk.ac.manchester.cs.mekon.mechanism;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Point-of-entry for applications using a pure frames-based
 * MEKON model.
 * <p>
 * Optionally, the configuration information for the model
 * section(s) can be provided via the general MEKON configuration
 * system.
 *
 * @author Colin Puleston
 */
public class CManager {

	static private final ZCModelAccessor modelAccessor = ZCModelAccessor.get();
	static private final ZIStoreAccessor storeAccessor = ZIStoreAccessor.get();

	/**
	 * Creates model-builder to build model for which the
	 * specification of the model section(s) must be provided via
	 * the relevant methods.
	 *
	 * @return Resulting model-builder
	 */
	static public CBuilder createEmptyBuilder() {

		return modelAccessor.createBuilder(modelAccessor.createModel());
	}

	/**
	 * Creates model-builder to build model for which the
	 * specification of the model section(s) will come from a
	 * configuration file with the standard MEKON configuration
	 * file-name located somewhere on the classpath.
	 *
	 * @return Resulting model-builder
	 * @throws KSystemConfigException if configuration file does not
	 * exist
	 * @throws XDocumentException if configuration file does not
	 * contain correctly specified configuration information
	 */
	static public CBuilder createBuilder() {

		return createBuilder(new KConfigFile());
	}

	/**
	 * Creates model-builder to build model for which the
	 * specification of the model section(s) will come from the
	 * specified configuration file.
	 *
	 * @param configFile Relevant configuration file
	 * @return Resulting model-builder
	 * @throws KSystemConfigException if configuration file does not
	 * exist
	 * @throws XDocumentException if configuration file does not
	 * contain correctly specified configuration information
	 */
	static public CBuilder createBuilder(KConfigFile configFile) {

		CBuilder builder = createEmptyBuilder();

		configureBuilder(builder, configFile);

		return builder;
	}

	/**
	 * Configures previously created model-builder to build model
	 * with the specification of the model section(s) coming from the
	 * specified configuration file.
	 *
	 * @param builder Relevant model-builder
	 * @param configFile Relevant configuration file
	 * @throws KSystemConfigException if configuration file does not
	 * exist
	 * @throws XDocumentException if configuration file does not
	 * contain correctly specified configuration information
	 */
	static public void configureBuilder(CBuilder builder, KConfigFile configFile) {

		createConfig(configFile).configure(builder);
	}

	/**
	 * Provides an instance-store initialiser for the model associated
	 * with the specified builder.
	 *
	 * @param builder Relevant builder
	 * @return Instance-store initialiser for model associated with
	 * builder
	 */
	static public IStoreInitialiser getIStoreInitialiser(CBuilder builder) {

		CModel model = modelAccessor.getModel(builder);

		return storeAccessor.getStoreInitialiser(model);
	}

	/**
	 * Performs any necessary clear-ups after all access of specified
	 * model has terminated.
	 *
	 * @param model Relevant model
	 */
	static public void clearUp(CModel model) {

		storeAccessor.checkStopStore(model);
	}

	static private CBuilderConfig createConfig(KConfigFile configFile) {

		return new CBuilderConfig(configFile.getRootNode());
	}
}
