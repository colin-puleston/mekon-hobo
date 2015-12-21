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

package uk.ac.manchester.cs.hobo.model.motor;

import java.util.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Represents a set of mappings between an Object Model (OM)
 * class (a {@link DObject}-derived class) and entities in one or
 * more external sources. Mappings can involve either the OM class
 * itself or certain fields on the OM class (or both).
 *
 * @author Colin Puleston
 */
public class DClassMap {

	private Class<? extends DObject> dClass;
	private String externalId;
	private Set<DFieldMap> fieldMaps = new HashSet<DFieldMap>();

	/**
	 * Adds a mapping between a field on the OM class and a
	 * correponding entity from an external source.
	 *
	 * @param fieldName Name of OM field
	 * @param externalId Identifier for mapped entity in external
	 * source
	 */
	public void addFieldMap(String fieldName, String externalId) {

		fieldMaps.add(new DFieldMap(fieldName, externalId));
	}

	/**
	 * Provides the mapped OM class.
	 *
	 * @return Mapped OM class
	 */
	public Class<? extends DObject> getDClass() {

		return dClass;
	}

	/**
	 * Provides the identifier for the entity from the external source
	 * that is mapped to the OM class, if applicable.
	 *
	 * @return Identifier for entity from external source mapped to
	 * OM class, of null if no such mapping
	 */
	public String getExternalId() {

		return externalId != null ? externalId : dClass.getName();
	}

	/**
	 * Specifies whether the OM class itself is mapped to an entity
	 * from the external source.
	 *
	 * @return True if OM class mapped to entity from external source
	 */
	public boolean mappedClass() {

		return externalId != null;
	}

	/**
	 * Provides all OM field-maps associated with the OM class.
	 *
	 * @return All OM field-maps
	 */
	public Set<DFieldMap> getFieldMaps() {

		return fieldMaps;
	}

	DClassMap(Class<? extends DObject> dClass, String externalId) {

		this.dClass = dClass;
		this.externalId = externalId;
	}
}
