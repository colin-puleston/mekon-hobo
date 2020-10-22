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

package uk.ac.manchester.cs.hobo.manage;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.*;
import uk.ac.manchester.cs.hobo.model.zlink.*;

/**
 * Point-of-entry for applications using a direct HOBO model,
 * together with an associated frames-based MEKON model to
 * which it is bound.
 * <p>
 * Optionally, both the configuration information for the
 * externally-sourced model section(s), and the mappings between
 * entities in the direct model and those in the external sources,
 * can be provided via the general HOBO/MEKON configuration system.
 *
 * @author Colin Puleston
 */
public class DManager {

	static private final ZDModelAccessor modelAccessor = ZDModelAccessor.get();

	/**
	 * Creates model-builder to build model for which the
	 * specification of both the externally-sourced model section(s)
	 * and the mappings between direct and externally-sourced sections
	 * must be provided via the relevant methods.
	 *
	 * @return Resulting model-builder
	 */
	static public DBuilder createEmptyBuilder() {

		return modelAccessor.createBuilder();
	}

	/**
	 * Creates model-builder to build model for which the
	 * specification of both the externally-sourced model section(s)
	 * and the mappings between direct and externally-sourced sections
	 * will come from a configuration file with the standard MEKON
	 * configuration file-name located somewhere on the classpath.
	 *
	 * @return Resulting model-builder
	 * @throws KSystemConfigException if configuration file does not
	 * exist
	 * @throws XDocumentException if configuration file does not
	 * contain correctly specified configuration information
	 */
	static public DBuilder createBuilder() {

		return createBuilder(new KConfigFile());
	}

	/**
	 * Creates model-builder to build model for which the specification
	 * of both the externally-sourced model section(s) and the mappings
	 * between direct and externally-sourced sections will come from the
	 * specified configuration file.
	 *
	 * @param configFile Relevant configuration file
	 * @return Resulting model-builder
	 * @throws KSystemConfigException if configuration file does not
	 * exist
	 * @throws XDocumentException if configuration file does not
	 * contain correctly specified configuration information
	 */
	static public DBuilder createBuilder(KConfigFile configFile) {

		DBuilder dBuilder = createEmptyBuilder();
		CBuilder cBuilder = dBuilder.getCBuilder();

		CManager.configureBuilder(cBuilder, configFile);
		createConfig(configFile).configure(dBuilder);

		return dBuilder;
	}

	/**
	 * Creates model-builder based on the supplied pre-populated
	 * frames-based MEKON model-builder.
	 *
	 * @param cBuilder Relevant frames-based model-builder
	 * @return Resulting direct model-builder
	 */
	static public DBuilder createBuilder(CBuilder cBuilder) {

		return modelAccessor.createBuilder(cBuilder);
	}

	/**
	 * Creates model-builder based on the builder associated with the
	 * supplied frames-based MEKON model.
	 *
	 * @param cModel Relevant frames-based model
	 * @return Resulting direct model-builder
	 */
	static public DBuilder createBuilder(CModel cModel) {

		return modelAccessor.createBuilder(CManager.getBuilder(cModel));
	}

	/**
	 * Retrieves the model-builder for the specified model.
	 *
	 * @param model Relevant model
	 * @return Model-builder for specified model
	 */
	static public DBuilder getBuilder(DModel model) {

		return modelAccessor.getBuilder(model);
	}

	static private DConfig createConfig(KConfigFile configFile) {

		return new DConfig(configFile.getRootNode());
	}
}
