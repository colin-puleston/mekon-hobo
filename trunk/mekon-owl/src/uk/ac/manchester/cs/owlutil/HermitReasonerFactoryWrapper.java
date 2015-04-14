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

package uk.ac.manchester.cs.owlutil;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.*;

/**
 * Wrapper for the HermiT implementation of {@link OWLReasonerFactory},
 * which is provided as an inner class within the main HermiT
 * {@link Reasoner} class. Allows construction via reflection of the
 * inner class (which cannot be done directly), enabling the HermiT
 * reasoner to be specified in a MEKON configuration file.
 *
 * @author Colin Puleston
 */
public class HermitReasonerFactoryWrapper implements OWLReasonerFactory {

	private OWLReasonerFactory factory = new Reasoner.ReasonerFactory();

	/**
	 */
	public String getReasonerName() {

		return factory.getReasonerName();
	}

	/**
	 */
	public OWLReasoner createReasoner(OWLOntology ontology) {

		return factory.createReasoner(ontology);
	}

	/**
	 */
	public OWLReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration config) {

		return factory.createReasoner(ontology, config);
	}

	/**
	 */
	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {

		return factory.createNonBufferingReasoner(ontology);
	}

	/**
	 */
	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology, OWLReasonerConfiguration config) {

		return factory.createNonBufferingReasoner(ontology, config);
	}
}
