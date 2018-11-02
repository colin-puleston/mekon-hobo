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

import uk.ac.manchester.cs.mekon.owl.util.*;

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

	private abstract class Purger {

		void purge() {

			for (OWLAxiom axiom : OWLAPIVersion.getAxioms(ontology)) {

				if (!retain(axiom)) {

					OWLAPIVersion.removeAxiom(ontology, axiom);
				}
			}
		}

		abstract boolean retainDeclaration(OWLDeclarationAxiom axiom);

		abstract boolean retainNonDeclaration(OWLAxiom axiom);

		private boolean retain(OWLAxiom axiom) {

			return axiom instanceof OWLDeclarationAxiom
					? retainDeclaration((OWLDeclarationAxiom)axiom)
					: retainNonDeclaration(axiom);
		}
	}

	private class DeclarationPurger extends Purger {

		private OAxiomPurgeSpec spec;

		DeclarationPurger(OAxiomPurgeSpec spec) {

			this.spec = spec;
		}

		boolean retainDeclaration(OWLDeclarationAxiom axiom) {

			OWLEntity entity = axiom.getEntity();
			IRI iri = entity.getIRI();

			if (entity instanceof OWLClass) {

				return spec.retainConcept(iri);
			}

			if (entity instanceof OWLProperty) {

				return spec.retainProperty(iri);
			}

			return false;
		}

		boolean retainNonDeclaration(OWLAxiom axiom) {

			return true;
		}
	}

	private class NonDeclarationPurger extends Purger {

		private boolean retainConceptHierarchy;

		NonDeclarationPurger(OAxiomPurgeSpec spec) {

			retainConceptHierarchy = spec.retainConceptHierarchy();
		}

		boolean retainDeclaration(OWLDeclarationAxiom axiom) {

			return true;
		}

		boolean retainNonDeclaration(OWLAxiom axiom) {

			return retainConceptHierarchy && axiom instanceof OWLSubClassOfAxiom;
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

		OWLAPIVersion.addAxiom(ontology, axiom);

		new AddedAxiomProcessor().process(axiom);
	}

	void addAll(Set<? extends OWLAxiom> axioms) {

		for (OWLAxiom axiom : axioms) {

			add(axiom);
		}
	}

	synchronized void remove(OWLAxiom axiom) {

		OWLAPIVersion.removeAxiom(ontology, axiom);

		new RemovedAxiomProcessor().process(axiom);
	}

	void removeAll(Set<? extends OWLAxiom> axioms) {

		for (OWLAxiom axiom : axioms) {

			remove(axiom);
		}
	}

	void purge(OAxiomPurgeSpec purgeSpec) {

		new DeclarationPurger(purgeSpec).purge();
		new NonDeclarationPurger(purgeSpec).purge();
	}

	private OWLOntologyManager getManager() {

		return model.getManager();
	}
}
