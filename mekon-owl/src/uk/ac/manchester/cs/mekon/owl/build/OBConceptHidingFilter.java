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

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Represents a filter that will be applied to the concepts
 * specified by the scope of a concept-hiding specification.
 *
 * @author Colin Puleston
 */
public enum OBConceptHidingFilter {

	/**
	 * Represents a filter that allows any concept to be hidden.
	 */
	ANY {

		boolean passesFilter(OModel model, OWLClass concept) {

			return true;
		}
	},

	/**
	 * Represents a filter that allows only "defined" concepts to be
	 * hidden.
	 */
	DEFINIED_CONCEPTS_ONLY {

		boolean passesFilter(OModel model, OWLClass concept) {

			return isDefined(model, concept);
		}
	},

	/**
	 * Represents a filter that allows only "defined" concepts with no
	 * no non-defined descendants to be hidden.
	 */
	DEFINIED_SUB_TREES_ONLY {

		boolean passesFilter(OModel model, OWLClass concept) {

			return isDefinedSubTree(model, concept);
		}
	};

	static private boolean isDefinedSubTree(OModel model, OWLClass concept) {

		return isDefined(model, concept) && !hasPrimitiveDescendant(model, concept);
	}

	static private boolean isDefined(OModel model, OWLClass concept) {

		return OWLAPIVersion.isDefined(concept, model.getAllOntologies());
	}

	static private boolean hasPrimitiveDescendant(OModel model, OWLClass concept) {

		for (OWLClass subConcept : getAllDescendants(model, concept)) {

			if (!isDefined(model, subConcept)) {

				return true;
			}
		}

		return false;
	}

	static private Set<OWLClass> getAllDescendants(OModel model, OWLClass concept) {

		return model.getInferredSubs(concept, false);
	}

	abstract boolean passesFilter(OModel model, OWLClass concept);
}
