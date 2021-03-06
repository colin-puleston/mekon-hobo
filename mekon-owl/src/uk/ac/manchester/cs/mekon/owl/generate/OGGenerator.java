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

package uk.ac.manchester.cs.mekon.owl.generate;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.vocab.*;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
public class OGGenerator {

	static private final IRI LABEL_ANNOTATION_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();

	static private OWLOntology createOntology(IRI ontologyIRI) {

		try {

			return OWLManager.createOWLOntologyManager().createOntology(ontologyIRI);
		}
		catch (OWLOntologyCreationException e) {

			throw new KSystemConfigException(e);
		}
	}

	private OWLOntology ontology;
	private OWLDataFactory dataFactory;

	private OGEntityGenerator entityGenerator;
	private ONumberRangeRenderer numberRangeRenderer;
	private OWLDataRange stringDataType;
	private OWLAnnotationProperty labelAnnotationProperty;

	private ConceptResolver concepts = new ConceptResolver();
	private ObjectPropertyResolver objectProperties = new ObjectPropertyResolver();
	private DataPropertyResolver dataProperties = new DataPropertyResolver();

	private Set<OWLClass> conceptsWithExtraSupers = new HashSet<OWLClass>();
	private Set<OWLProperty> propertiesWithSupers = new HashSet<OWLProperty>();

	private abstract class EntityResolver<E extends OWLEntity> {

		private Map<IRI, E> entitiesByIRI = new HashMap<IRI, E>();

		E resolve(IRI iri) {

			return resolve(iri, null);
		}

		E resolve(IRI iri, CIdentified source) {

			E entity = entitiesByIRI.get(iri);

			if (entity == null) {

				entity = create(iri);

				add(entity, source);
				entitiesByIRI.put(iri, entity);
			}

			return entity;
		}

		abstract E create(IRI iri);

		private void add(E entity, CIdentified source) {

			addAxiom(dataFactory.getOWLDeclarationAxiom(entity));

			if (source != null) {

				addAxiom(createLabelAxiom(entity, source.getIdentity().getLabel()));
			}
		}

		private OWLAxiom createLabelAxiom(E entity, String label) {

			return dataFactory
					.getOWLAnnotationAssertionAxiom(
						labelAnnotationProperty,
						entity.getIRI(),
						dataFactory.getOWLLiteral(label));
		}
	}

	private class ConceptResolver extends EntityResolver<OWLClass> {

		OWLClass create(IRI iri) {

			return dataFactory.getOWLClass(iri);
		}
	}

	private class ObjectPropertyResolver extends EntityResolver<OWLObjectProperty> {

		OWLObjectProperty create(IRI iri) {

			return dataFactory.getOWLObjectProperty(iri);
		}
	}

	private class DataPropertyResolver extends EntityResolver<OWLDataProperty> {

		OWLDataProperty create(IRI iri) {

			return dataFactory.getOWLDataProperty(iri);
		}
	}

	private abstract class RestrictionGenerator extends CValueVisitor {

		private OWLClass concept;
		private CSlot slot;

		private OGPropertyProfile propertyProfile;

		private ObjectRestrictions objectRestrictions = new ObjectRestrictions();
		private DataRestrictions dataRestrictions = new DataRestrictions();

		private abstract class TypeRestrictions<P extends OWLProperty, F> {

			void generate(F filler) {

				IRI propertyIRI = propertyProfile.getIRI();
				P property = resolveProperty(propertyIRI);

				addAncestorProperties(property);
				addRestriction(createRestriction(property, filler));
			}

			abstract EntityResolver<P> getPropertyResolver();

			abstract OWLAxiom createSubPropertyAxiom(P sub, P sup);

			abstract OWLRestriction createRestriction(P property, F filler);

			private void addAncestorProperties(P property) {

				if (!propertiesWithSupers.add(property)) {

					return;
				}

				IRI superPropIRI = propertyProfile.getAncestorIRIOrNull(property.getIRI());

				if (superPropIRI != null) {

					P superProperty = resolveProperty(superPropIRI);

					addSuperProperty(property, superProperty);
					addAncestorProperties(superProperty);
				}
			}

			private P resolveProperty(IRI iri) {

				return getPropertyResolver().resolve(iri, slot);
			}

			private void addSuperProperty(P sub, P sup) {

				addAxiom(createSubPropertyAxiom(sub, sup));
			}

			private void addRestriction(OWLRestriction restriction) {

				addSubClassAxiom(concept, restriction);
			}
		}

		private class ObjectRestrictions extends TypeRestrictions<OWLObjectProperty, OWLClassExpression> {

			void generate(CFrame value) {

				generate(toFillerExpression(value));
			}

			EntityResolver<OWLObjectProperty> getPropertyResolver() {

				return objectProperties;
			}

			OWLAxiom createSubPropertyAxiom(OWLObjectProperty sub, OWLObjectProperty sup) {

				return dataFactory.getOWLSubObjectPropertyOfAxiom(sub, sup);
			}

			OWLRestriction createRestriction(OWLObjectProperty property, OWLClassExpression filler) {

				return createObjectRestriction(property, filler);
			}

			private OWLClassExpression toFillerExpression(CFrame frame) {

				return frame.getCategory().disjunction() ? toUnion(frame) : resolveConcept(frame);
			}

			private OWLObjectUnionOf toUnion(CFrame frame) {

				Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();

				for (CFrame disjunct : frame.asDisjuncts()) {

					operands.add(resolveConcept(disjunct));
				}

				return dataFactory.getOWLObjectUnionOf(operands);
			}
		}

		private class DataRestrictions extends TypeRestrictions<OWLDataProperty, OWLDataRange> {

			void generate(CNumber value) {

				generate(numberRangeRenderer.render(value));
			}

			void generate(CString value) {

				generate(stringDataType);
			}

			EntityResolver<OWLDataProperty> getPropertyResolver() {

				return dataProperties;
			}

			OWLAxiom createSubPropertyAxiom(OWLDataProperty sub, OWLDataProperty sup) {

				return dataFactory.getOWLSubDataPropertyOfAxiom(sub, sup);
			}

			OWLRestriction createRestriction(OWLDataProperty property, OWLDataRange filler) {

				return createDataRestriction(property, filler);
			}
		}

		protected void visit(CFrame value) {

			objectRestrictions.generate(value);
		}

		protected void visit(CNumber value) {

			dataRestrictions.generate(numberRangeRenderer.render(value));
		}

		protected void visit(CString value) {

			dataRestrictions.generate(stringDataType);
		}

		protected void visit(MFrame value) {

			objectRestrictions.generate(value.getRootCFrame());
		}

		RestrictionGenerator(OWLClass concept, CSlot slot) {

			this.concept = concept;
			this.slot = slot;

			propertyProfile = entityGenerator.getPropertyProfile(slot);
		}

		void generate(CValue<?> slotValue) {

			visit(slotValue);
		}

		abstract OWLRestriction createObjectRestriction(
									OWLObjectProperty property,
									OWLClassExpression filler);

		abstract OWLRestriction createDataRestriction(
									OWLDataProperty property,
									OWLDataRange filler);
	}

	private class SlotRestrictionGenerator extends RestrictionGenerator {

		private boolean singleValued;

		SlotRestrictionGenerator(OWLClass concept, CSlot slot) {

			super(concept, slot);

			singleValued = slot.getCardinality().singleValue();

			generate(slot.getValueType());
		}

		OWLRestriction createObjectRestriction(
							OWLObjectProperty property,
							OWLClassExpression filler) {

			return singleValued
					? dataFactory.getOWLObjectMaxCardinality(1, property, filler)
					: dataFactory.getOWLObjectAllValuesFrom(property, filler);
		}

		OWLRestriction createDataRestriction(
							OWLDataProperty property,
							OWLDataRange filler) {

			return dataFactory.getOWLDataMaxCardinality(1, property, filler);
		}
	}

	private class SlotValuesRestrictionGenerator extends RestrictionGenerator {

		SlotValuesRestrictionGenerator(OWLClass concept, CSlot slot, CValue<?> value) {

			super(concept, slot);

			generate(value);
		}

		OWLRestriction createObjectRestriction(
							OWLObjectProperty property,
							OWLClassExpression filler) {

			return dataFactory.getOWLObjectSomeValuesFrom(property, filler);
		}

		OWLRestriction createDataRestriction(
							OWLDataProperty property,
							OWLDataRange filler) {

			return dataFactory.getOWLDataSomeValuesFrom(property, filler);
		}
	}

	public OGGenerator(IRI ontologyIRI, OGEntityGenerator entityGenerator) {

		this(createOntology(ontologyIRI), entityGenerator);
	}

	public OGGenerator(OWLOntology ontology, OGEntityGenerator entityGenerator) {

		this.ontology = ontology;
		this.entityGenerator = entityGenerator;

		dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		numberRangeRenderer = new ONumberRangeRenderer(dataFactory);
		stringDataType = getStringDataType();
		labelAnnotationProperty = getLabelAnnotationProperty();
	}

	public void generate() {

		generate(CManager.createBuilder().build());
	}

	public void generate(File mekonConfigFile) {

		generate(CManager.createBuilder(new KConfigFile(mekonConfigFile)).build());
	}

	public void generate(CModel mekonModel) {

		generateForFrames(mekonModel);
		generateForFrameLinks(mekonModel);
	}

	public void save(File ontologyFile) {

		PrintWriter writer = null;

		try {

			writer = new PrintWriter(new FileWriter(ontologyFile));

			new RDFXMLRenderer(ontology, writer).render();
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
		finally {

			if (writer != null) {

				writer.close();
			}
		}
	}

	private void generateForFrames(CModel mekonModel) {

		for (CFrame frame : mekonModel.getFrames().asList()) {

			OGConceptProfile profile = entityGenerator.getConceptProfile(frame);

			addExtraAncestorConcepts(profile, resolveConcept(profile, frame));
		}
	}

	private void generateForFrameLinks(CModel mekonModel) {

		for (CFrame frame : mekonModel.getFrames().asList()) {

			OWLClass concept = resolveConcept(frame);

			generateForSubLinks(frame, concept);
			generateForSlots(frame, concept);
			generateForSlotValues(frame, concept);
		}
	}

	private void generateForSubLinks(CFrame frame, OWLClass concept) {

		for (CFrame sub : frame.getSubs()) {

			addSubClassAxiom(resolveConcept(sub), concept);
		}
	}

	private void generateForSlots(CFrame frame, OWLClass concept) {

		for (CSlot slot : frame.getSlots().asList()) {

			new SlotRestrictionGenerator(concept, slot);
		}
	}

	private void generateForSlotValues(CFrame frame, OWLClass concept) {

		CSlotValues values = frame.getSlotValues();

		for (CIdentity slotId : values.getSlotIdentities()) {

			for (CValue<?> value : values.getValues(slotId)) {

				CSlot slot = findSlot(frame, slotId);

				new SlotValuesRestrictionGenerator(concept, slot, value);
			}
		}
	}

	private void addExtraAncestorConcepts(OGConceptProfile profile, OWLClass concept) {

		if (!conceptsWithExtraSupers.add(concept)) {

			return;
		}

		for (IRI superConceptIRI : profile.getExtraAncestorIRIs(concept.getIRI())) {

			OWLClass superConcept = concepts.resolve(superConceptIRI);

			addSubClassAxiom(concept, superConcept);
			addExtraAncestorConcepts(profile, superConcept);
		}
	}

	private CSlot findSlot(CFrame leafFrame, CIdentity slotId) {

		CSlot slot = findSlot(leafFrame, slotId, new HashSet<CFrame>());

		if (slot != null) {

			return slot;
		}

		throw new Error("Cannot find required slot: " + slotId);
	}

	private CSlot findSlot(CFrame current, CIdentity slotId, Set<CFrame> visited) {

		CSlot slot = current.getSlots().getOrNull(slotId);

		if (slot == null) {

			for (CFrame sup : current.getSupers()) {

				if (visited.add(sup)) {

					slot = findSlot(sup, slotId, visited);

					if (slot != null) {

						break;
					}
				}
			}
		}

		return slot;
	}

	private OWLClass resolveConcept(CFrame frame) {

		return resolveConcept(entityGenerator.getConceptProfile(frame), frame);
	}

	private OWLClass resolveConcept(OGConceptProfile conceptProfile, CFrame frame) {

		return concepts.resolve(conceptProfile.getIRI(), frame);
	}

	private void addSubClassAxiom(OWLClassExpression sub, OWLClassExpression sup) {

		addAxiom(dataFactory.getOWLSubClassOfAxiom(sub, sup));
	}

	private void addAxiom(OWLAxiom axiom) {

		OWLAPIVersion.addAxiom(ontology, axiom);
	}

	private OWLDataRange getStringDataType() {

		return dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
	}

	private OWLAnnotationProperty getLabelAnnotationProperty() {

		return dataFactory.getOWLAnnotationProperty(LABEL_ANNOTATION_IRI);
	}
}
