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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * Represents all required mappings between specific entities
 * in the Object Model (OM) and correponding entities in any
 * external sources that will also be contributing towards the
 * Frames Model (FM).
 *
 * @author Colin Puleston
 */
public class DModelMap {

	private boolean labelsFromDirectClasses = false;
	private boolean labelsFromDirectFields = false;

	private Map<Class<? extends DObject>, DClassMap> classMaps
				= new HashMap<Class<? extends DObject>, DClassMap>();

	/**
	 * Sets the value of the {@link #labelsFromDirectClasses}
	 * attribute. If not specified, the attribute value will
	 * default to false.
	 *
	 * @param value True if frame-labels are to come from the OM
	 * classes
	 */
	public void setLabelsFromDirectClasses(boolean value) {

		labelsFromDirectClasses = value;
	}

	/**
	 * Sets the value of the {@link #labelsFromDirectFields}
	 * attribute. If not specified, the attribute value will
	 * default to false.
	 *
	 * @param value True if slot-labels are to come from the OM
	 * fields
	 */
	public void setLabelsFromDirectFields(boolean value) {

		labelsFromDirectFields = value;
	}

	/**
	 * Provides an object for defining mappings between the fields of
	 * an OM class and correponding entities from external sources.
	 *
	 * @param dClass OM class
	 * @return Resulting class-map object
	 */
	public DClassMap addClassMap(Class<? extends DObject> dClass) {

		return addClassMap(dClass, null);
	}

	/**
	 * Adds a mapping between an OM class and a correponding entity
	 * from an external source, and provides an object for defining
	 * mappings between the fields of that OM class and correponding
	 * from external sources.
	 *
	 * @param dClass OM class
	 * @param externalId Identifier for mapped entity in external source
	 * @return Resulting class-map object
	 */
	public DClassMap addClassMap(
						Class<? extends DObject> dClass,
						String externalId) {

		DClassMap classMap = new DClassMap(dClass, externalId);

		classMaps.put(dClass, classMap);

		return classMap;
	}

	/**
	 * Specifies whether, for those OM classes that are mapped to
	 * entities from external sources, the bound {@link CFrame}
	 * objects will obtain their labels from the relevant OM
	 * class-names, rather than from the mapped entities. A label taken
	 * from an OM class-name will be a heuristically-modified version
	 * of the leaf class-name.
	 *
	 * @return True if frame-labels will come from the OM classes
	 */
	public boolean labelsFromDirectClasses() {

		return labelsFromDirectClasses;
	}

	/**
	 * Specifies whether, for those OM fields that are mapped to
	 * entities from external sources, the bound {@link CSlot} objects
	 * will obtain their labels from the relevant OM field-names,
	 * rather than from the mapped entities. A label taken from an
	 * OM field-name will be a heuristically-modified version of the
	 * field-name.
	 *
	 * @return True if slot-labels will come from the OM fields
	 */
	public boolean labelsFromDirectFields() {

		return labelsFromDirectFields;
	}

	/**
	 * Retrieves a set of mappings between a specified OM class and
	 * correponding entities from external sources, if such mappings
	 * exist.
	 *
	 * @param dClass OM class for which mapping is required
 	 * @return Required class-map object, or null if not found
	 */
	public DClassMap getClassMap(Class<? extends DObject> dClass) {

		return classMaps.get(dClass);
	}
}
