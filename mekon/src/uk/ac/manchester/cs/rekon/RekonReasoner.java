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
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.Version;

/**
 * @author Colin Puleston
 */
public class RekonReasoner extends StructuralReasoner {

	static final String REASONER_NAME = "REKON";
	static final Version REASONER_VERSION = new Version(1, 0, 0, 0);

	private Names names;
	private Classifier classifier;

	public RekonReasoner(OWLOntology rootOntology) {

		super(rootOntology, new SimpleConfiguration(), BufferingMode.BUFFERING);

		Assertions assertions = new Assertions(rootOntology);

		names = new Names(assertions);
		classifier = new Classifier(assertions, names);
	}

	public String getReasonerName() {

		return REASONER_NAME;
	}

	public Version getReasonerVersion() {

		return REASONER_VERSION;
	}

	public Node<OWLClass> getEquivalentClasses(OWLClassExpression expr) {

		return new OWLClassNode(getEquivalentClassSet(expr));
	}

	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression expr, boolean directOnly) {

		return toNodeSet(getSuperClassSet(expr, directOnly));
	}

	public NodeSet<OWLClass> getSubClasses(OWLClassExpression expr, boolean directOnly) {

		return toNodeSet(getSubClassSet(expr, directOnly));
	}

	private Set<OWLClass> getEquivalentClassSet(OWLClassExpression expr) {

		if (expr instanceof OWLClass) {

			return names.get((OWLClass)expr).getEquivalentEntities(OWLClass.class);
		}

		return toClasses(classifier.getEquivalents(expr));
	}

	private Set<OWLClass> getSuperClassSet(OWLClassExpression expr, boolean directOnly) {

		if (expr instanceof OWLClass) {

			return names.get((OWLClass)expr).getSuperEntities(OWLClass.class, directOnly);
		}

		return toClasses(classifier.getSupers(expr, directOnly));
	}

	private Set<OWLClass> getSubClassSet(OWLClassExpression expr, boolean directOnly) {

		if (expr instanceof OWLClass) {

			return names.get((OWLClass)expr).getSubEntities(OWLClass.class, directOnly);
		}

		return Collections.emptySet();
	}

	private OWLClassNodeSet toNodeSet(Set<OWLClass> classes) {

		Set<Node<OWLClass>> nodes = new HashSet<Node<OWLClass>>();

		for (OWLClass cls : classes) {

			nodes.add(new OWLClassNode(cls));
		}

		return new OWLClassNodeSet(nodes);
	}

	private Set<OWLClass> toClasses(Set<ClassName> names) {

		Set<OWLClass> classes = new HashSet<OWLClass>();

		for (ClassName name : names) {

			classes.add(name.getCls());
		}

		return classes;
	}
}
