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

package uk.ac.manchester.cs.mekon.owl;

import java.io.File;
import java.net.URL;

import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

/**
 * @author Colin Puleston
 */
public class TestOModel {

	static private final String OWL_FILE = "demo.owl";
	static private final String NUMERIC_PROPERTY = "numericValue";

	static private final Class<FaCTPlusPlusReasonerFactory>
							REASONER_FACTORY_CLASS
								= FaCTPlusPlusReasonerFactory.class;

	static public OModel create() {

		OModel model = new OModel(getOWLFile(), REASONER_FACTORY_CLASS, true);

		model.setIndirectNumericProperty(new OTest().nameToIRI(NUMERIC_PROPERTY));

		return model;
	}

	static private File getOWLFile() {

		URL url = getClassLoader().getResource(OWL_FILE);

		if (url == null) {

			throw new RuntimeException("Cannot access OWL file: " + OWL_FILE);
		}

		return new File(url.getFile());
	}

	static private ClassLoader getClassLoader() {

		return Thread.currentThread().getContextClassLoader();
	}
}
