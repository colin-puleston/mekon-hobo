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

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.demomodel.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
public class ODemoModel extends DemoModelIds {

	static public final File OWL_FILE = getFileFromClasspath("demo.owl");
	static public final File RESOURCE_DIR = OWL_FILE.getParentFile();

	static private final Class<? extends OWLReasonerFactory> REASONER_FACTORY_CLASS = FaCTPlusPlusReasonerFactory.class;

	static public OModel create() {

		OModelBuilder bldr = createBuilder();

		bldr.setIndirectNumericProperty(toIRI(NUMERIC_PROPERTY));

		return bldr.create(true);
	}

	static private OModelBuilder createBuilder() {

		return new OModelBuilder(OWL_FILE, REASONER_FACTORY_CLASS);
	}

	static private File getFileFromClasspath(String name) {

		URL url = getClassLoader().getResource(name);

		if (url == null) {

			throw new RuntimeException("Cannot access file: " + name);
		}

		return new File(url.getFile());
	}

	static private ClassLoader getClassLoader() {

		return Thread.currentThread().getContextClassLoader();
	}

	static private IRI toIRI(CIdentity id) {

		return IRI.create(id.getIdentifier());
	}
}