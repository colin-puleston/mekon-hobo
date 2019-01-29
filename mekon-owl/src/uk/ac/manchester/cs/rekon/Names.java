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

package uk.ac.manchester.cs.rekon;

import java.util.*;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class Names {

	private ClassNames classNames;
	private ObjectPropertyNames objectPropertyNames;
	private DataPropertyNames dataPropertyNames;

	private RefGenerator refs = new RefGenerator();

	Names(Assertions assertions) {

		classNames = new ClassNames(assertions, refs);
		objectPropertyNames = new ObjectPropertyNames(assertions, refs);
		dataPropertyNames = new DataPropertyNames(assertions, refs);
	}

	void resolveAllLinksPostClassification() {

		classNames.resolveAllLinksPostClassification(true);
		objectPropertyNames.resolveAllLinksPostClassification(false);
		dataPropertyNames.resolveAllLinksPostClassification(false);
	}

	Collection<ClassName> getAllClassNames() {

		return classNames.names.values();
	}

	ClassName get(OWLClass entity) {

		return classNames.names.get(entity);
	}

	ObjectPropertyName get(OWLObjectProperty entity) {

		return objectPropertyNames.names.get(entity);
	}

	DataPropertyName get(OWLDataProperty entity) {

		return dataPropertyNames.names.get(entity);
	}
}

