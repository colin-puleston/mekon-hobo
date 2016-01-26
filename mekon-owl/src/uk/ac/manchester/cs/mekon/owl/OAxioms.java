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

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

/**
 * @author Colin Puleston
 */
class OAxioms {

	private OModel model;
	private OWLOntology ontology;

	private abstract class AxiomProcessor {

		void process(OWLAxiom axiom) {

			if (axiom instanceof OWLDeclarationAxiom) {

				processDeclaration((OWLDeclarationAxiom)axiom);
			}

			model.getReasoner().flush();
		}

		abstract <E extends OWLEntity>void updateEntities(OEntities<E> all, E entity);

		private void processDeclaration(OWLDeclarationAxiom axiom) {

			OWLEntity entity = axiom.getEntity();

			if (entity instanceof OWLClass) {

				updateEntities(model.getConcepts(), (OWLClass)entity);
			}
			else if (entity instanceof OWLObjectProperty) {

				updateEntities(model.getObjectProperties(), (OWLObjectProperty)entity);
			}
			else if (entity instanceof OWLDataProperty) {

				updateEntities(model.getDataProperties(), (OWLDataProperty)entity);
			}
		}
	}

	private class AddedAxiomProcessor extends AxiomProcessor {

		<E extends OWLEntity>void updateEntities(OEntities<E> all, E entity) {

			all.add(entity);
		}
	}

	private class RemovedAxiomProcessor extends AxiomProcessor {

		<E extends OWLEntity>void updateEntities(OEntities<E> all, E entity) {

			all.remove(entity);
		}
	}

	OAxioms(OModel model, OWLOntology ontology) {

		this.model = model;
		this.ontology = ontology;
	}

	synchronized void add(OWLAxiom axiom) {

		getManager().addAxiom(ontology, axiom);

		new AddedAxiomProcessor().process(axiom);
	}

	void addAll(Set<? extends OWLAxiom> axioms) {

		for (OWLAxiom axiom : axioms) {

			add(axiom);
		}
	}

	synchronized void remove(OWLAxiom axiom) {

		getManager().removeAxiom(ontology, axiom);

		new RemovedAxiomProcessor().process(axiom);
	}

	void removeAll(Set<? extends OWLAxiom> axioms) {

		for (OWLAxiom axiom : axioms) {

			remove(axiom);
		}
	}

	void retainOnlyDeclarations() {

		for (OWLAxiom axiom : ontology.getAxioms()) {

			if (!(axiom instanceof OWLDeclarationAxiom)) {

				getManager().removeAxiom(ontology, axiom);
			}
		}
	}

	private OWLOntologyManager getManager() {

		return model.getManager();
	}
}
