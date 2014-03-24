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

package uk.ac.manchester.cs.hobo.mechanism;

import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * Point-of-entry for applications using a direct HOBO model,
 * together with an associated frames-based MEKON model to
 * which it is bound.
 * <p>
 * Optionally, both the configuration information for the
 * indirect model section(s), and the mappings between entities
 * in the direct and indirect model sections, can be provided via
 * the general HOBO/MEKON configuration system.
 *
 * @author Colin Puleston
 */
public class DManager {

	private class DModelLocal extends DModel {

		protected DBuilder createBuilder() {

			return super.createBuilder();
		}

		DModelLocal() {
		}
	}

	/**
	 * Creates model-builder to build model for which the
	 * specification of both the indirect model section(s) and the
	 * mappings between direct and indirect sections must be
	 * provided via the relevant methods.
	 *
	 * @return Resulting model-builder
	 */
	public DBuilder createEmptyBuilder() {

		return new DModelLocal().createBuilder();
	}

	/**
	 * Creates model-builder to build model for which the
	 * specification of both the indirect model section(s) and the
	 * mappings between direct and indirect sections will come from
	 * a configuration file with the standard MEKON configuration
	 * file-name located somewhere on the classpath.
	 *
	 * @return Resulting model-builder
	 * @throws KConfigFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public DBuilder createBuilder() {

		return createBuilder(new KConfigFile());
	}

	/**
	 * Creates model-builder to build model for which the
	 * specification of both the indirect model section(s) and the
	 * mappings between direct and indirect sections will come from
	 * the specified configuration file.
	 *
	 * @param configFile Relevant configuration file
	 * @return Resulting model-builder
	 * @throws KConfigFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public DBuilder createBuilder(KConfigFile configFile) {

		DBuilder dBuilder = createEmptyBuilder();
		CBuilder cBuilder = dBuilder.getCBuilder();

		CManager.configureBuilder(cBuilder, configFile);
		createConfig(configFile).configure(dBuilder);

		return dBuilder;
	}

	private DBuilderConfig createConfig(KConfigFile configFile) {

		return new DBuilderConfig(configFile.getRootNode());
	}
}
