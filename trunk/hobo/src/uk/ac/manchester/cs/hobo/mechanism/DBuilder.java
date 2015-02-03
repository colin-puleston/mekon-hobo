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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * Provides mechanisms for building the HOBO direct model,
 * together with the associated Frames Model (FM) to which it is
 * bound (represented respectively via {@link DModel} and
 * {@link CModel} objects).
 * <p>
 * Provides methods for registering the {@link DObject}-derived
 * classes that will represent the concepts in the Object Model
 * (OM). Also enables the specification of required mappings
 * between entities in the OM and corresponding entities in any
 * external sources for the FM.
 *
 * @author Colin Puleston
 */
public interface DBuilder {

	/**
	 * Registers an {@link DObject}-derived OM class.
	 *
	 * @param dClassName Fully-qualified name of OM class to be
	 * registered
	 */
	public void addDClass(String dClassName);

	/**
	 * Registers an {@link DObject}-derived OM class.
	 *
	 * @param dClass OM class to be registered
	 */
	public void addDClass(Class<? extends DObject> dClass);

	/**
	 * Registers OM all {@link DObject}-derived classes in the
	 * specified group of packages.
	 *
	 * @param basePackageName Base-name for packages to be registered
	 */
	public void addDClasses(String basePackageName);

	/**
	 * Builds the FM, which will incorporate entities (frames and
	 * slots) derived from both direct model (OM) and external sources,
	 * with ome entity-specifications coming from both. Bindings to the
	 * relevant OM entities will be created where applicable.
	 *
	 * @return Built direct model (from which FM can be retrieved)
	 */
	public DModel build();

	/**
	 * Provides the builder for the FM, which this model-builder wraps.
	 *
	 * @return Frames-based model builder
	 */
	public CBuilder getCBuilder();

	/**
	 * Provides the mappings between direct model and external sources.
	 *
	 * @return Mappings between direct and external sources
	 */
	public DModelMap getModelMap();
}
