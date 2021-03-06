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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

/**
 * Specifies which concepts or properties are to be selected from
 * of within a particular section of hierarchy.
 *
 * @author Colin Puleston
 */
public enum OBEntitySelection {

	/**
	 * Represents the selection of no entities from the relevant
	 * section of hierarchy.
	 */
	NONE(),

	/**
	 * Represents the selection of all entities from the relevant
	 * section of hierarchy.
	 */
	ALL(EntityLocation.ROOT, EntityLocation.INTERMEDIATE, EntityLocation.LEAF),

	/**
	 * Represents the selection of only the root-entity from the
	 * relevant section of hierarchy.
	 */
	ROOTS_ONLY(EntityLocation.ROOT),

	/**
	 * Represents the selection of only the non-root-entities from
	 * the relevant section of hierarchy.
	 */
	NON_ROOTS_ONLY(EntityLocation.INTERMEDIATE, EntityLocation.LEAF),

	/**
	 * Represents the selection of only the leaf-entities from the
	 * relevant section of hierarchy.
	 */
	LEAFS_ONLY(EntityLocation.LEAF);

	boolean includesRoot() {

		return includes(EntityLocation.ROOT);
	}

	boolean includesAnyNonRoots() {

		return includesIntermediates() || includesLeafs();
	}

	boolean includesIntermediates() {

		return includes(EntityLocation.INTERMEDIATE);
	}

	boolean includesLeafs() {

		return includes(EntityLocation.LEAF);
	}

	boolean includes(EntityLocation location) {

		return includedLocations.contains(location);
	}

	private List<EntityLocation> includedLocations;

	private OBEntitySelection(EntityLocation... includedLocations) {

		this.includedLocations = Arrays.asList(includedLocations);
	}
}
