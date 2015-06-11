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

import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBFrames {

	private OModel model;
	private IReasoner iReasoner = null;

	private OBConcepts concepts;
	private OBProperties properties;
	private OBEntityLabels labels;
	private OBValues values = null;

	private Map<OWLEntity, OBAtomicFrame> frames
				= new HashMap<OWLEntity, OBAtomicFrame>();

	OBFrames(
		OModel model,
		OBConcepts concepts,
		OBProperties properties,
		OBEntityLabels labels) {

		this.model = model;
		this.concepts = concepts;
		this.properties = properties;
		this.labels = labels;
	}

	void setValues(OBValues values) {

		this.values = values;
	}

	void setIReasoner(IReasoner iReasoner) {

		this.iReasoner = iReasoner;
	}

	void createAll() {

		createAllForConcepts();
		createAllForProperties();
		addAllDefinitions();
	}

	Collection<OBAtomicFrame> getAll() {

		return frames.values();
	}

	OBAtomicFrame get(OWLEntity sourceEntity) {

		OBAtomicFrame frame = frames.get(sourceEntity);

		if (frame == null) {

			throw new Error("Cannot find frame for: " + sourceEntity);
		}

		return frame;
	}

	private void createAllForConcepts() {

		for (OWLClass concept : concepts.getAll()) {

			createFrame(concept, iReasoner, hidden(concept));
		}
	}

	private void createAllForProperties() {

		for (OWLProperty<?, ?> property : properties.getAll()) {

			if (properties.getAttributes(property).frameSource()) {

				createFrame(property, null, false);
			}
		}
	}

	private void addAllDefinitions() {

		for (OWLClass concept : concepts.getAll()) {

			addDefinitions(concept);
		}
	}

	private void addDefinitions(OWLClass concept) {

		OBAtomicFrame frame = get(concept);

		for (OWLClassExpression equiv : getEquivalents(concept)) {

			OBValue<?> definition = values.checkCreateValue(equiv, true);

			if (definition instanceof OBExpressionFrame) {

				frame.addDefinition((OBExpressionFrame)definition);
			}
		}
	}

	private OBAtomicFrame createFrame(
							OWLEntity source,
							IReasoner iReasoner,
							boolean hidden) {

		String label = labels.getLabel(source);
		OBAtomicFrame frame = new OBAtomicFrame(source, label, hidden, iReasoner);

		frames.put(source, frame);

		return frame;
	}

	private boolean hidden(OWLClass concept) {

		return concepts.getAttributes(concept).hidden();
	}

	private Set<OWLClassExpression> getEquivalents(OWLClass concept) {

		Set<OWLClassExpression> equivs = new HashSet<OWLClassExpression>();

		for (OWLOntology ontology : model.getAllOntologies()) {

			for (OWLClassAxiom axiom : ontology.getAxioms(concept)) {

				if (axiom instanceof OWLEquivalentClassesAxiom) {

					OWLEquivalentClassesAxiom eqAxiom
						= (OWLEquivalentClassesAxiom)axiom;

					equivs.addAll(eqAxiom.getClassExpressionsMinus(concept));
				}
			}
		}

		return equivs;
	}
}
