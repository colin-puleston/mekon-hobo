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
 test_closedWorldSemanticsByMinimalInclusion* THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.owl.generate;

import java.io.*;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;
import org.semanticweb.owlapi.reasoner.structural.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.build.*;

/**
 * @author Colin Puleston
 */
public class OGGeneratorTest {

	static private final String TEST_FILENAME = "owl-generator-test.owl";
	static private final String TEST_NAMESPACE = "http://" + TEST_FILENAME;
	static private final File TEST_FILE = new File(ODemoModel.RESOURCE_DIR, TEST_FILENAME);
	static private final IRI TEST_ONTOLOGY_IRI = IRI.create(TEST_NAMESPACE);
	static private final IRI TEST_ROOT_CONCEPT_IRI = getTestEntityIRI("TestRoot");

	static private final IRI LABEL_ANNOTATION_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();

	static private IRI getTestEntityIRI(String fragment) {

		return IRI.create(TEST_NAMESPACE + '#' + fragment);
	}

	private CIdentifieds<CFrame> inputFrames;
	private CIdentifieds<CFrame> regenFrames;

	private class RegenId {

		private String identifier;

		RegenId(CIdentity inputIdentity) {

			this(inputIdentity.getIdentifier());
		}

		RegenId(CIdentified inputIdentified) {

			this(inputIdentified.getIdentity());
		}

		String getIdentifier() {

			return identifier;
		}

		CIdentity getIdentity() {

			return new CIdentity(identifier);
		}

		IRI getIRI() {

			return IRI.create(identifier);
		}

		String getIRIFragment() {

			return getIRIFragment(getIRI());
		}

		private RegenId(String inputIdentifier) {

			identifier = toRegenIdentifier(inputIdentifier);
		}

		private String toRegenIdentifier(String inputIdentifier) {

			return getTestEntityIRI(getIRIFragment(inputIdentifier)).toString();
		}

		private String getIRIFragment(String iri) {

			return getIRIFragment(IRI.create(iri));
		}

		private String getIRIFragment(IRI iri) {

			return iri.toURI().getFragment();
		}
	}

	private abstract class TestEntityProfile implements OGEntityProfile {

		private IRI iri;

		public IRI getIRI() {

			return iri;
		}

		TestEntityProfile(CIdentified identified) {

			iri = new RegenId(identified).getIRI();
		}

		boolean hasIRI(IRI testIRI) {

			return testIRI.equals(iri);
		}
	}

	private class TestConceptProfile extends TestEntityProfile implements OGConceptProfile {

		private CFrame frame;

		public Set<IRI> getExtraAncestorIRIs(IRI directChildIRI) {

			if (hasIRI(directChildIRI) && subRootFrame(frame)) {

				return Collections.singleton(TEST_ROOT_CONCEPT_IRI);
			}

			return Collections.emptySet();
		}

		TestConceptProfile(CFrame frame) {

			super(frame);

			this.frame = frame;
		}
	}

	private class TestPropertyProfile extends TestEntityProfile implements OGPropertyProfile {

		private CSlot slot;

		public IRI getAncestorIRIOrNull(IRI directChildIRI) {

			return hasIRI(directChildIRI) ? createSuperPropertyIRI() : null;
		}

		TestPropertyProfile(CSlot slot) {

			super(slot);

			this.slot = slot;
		}

		private IRI createSuperPropertyIRI() {

			return getTestEntityIRI(getSubRootName() + "_property");
		}

		private String getSubRootName() {

			return new RegenId(getSubRoot()).getIRIFragment();
		}

		private CFrame getSubRoot() {

			return findAncestorSubRootFrame(slot.getContainer());
		}
	}

	private class TestEntityGenerator implements OGEntityGenerator {

		public OGConceptProfile getConceptProfile(CFrame frame) {

			return new TestConceptProfile(frame);
		}

		public OGPropertyProfile getPropertyProfile(CSlot slot) {

			return new TestPropertyProfile(slot);
		}
	}

	private abstract class EntityRegenTester<E extends CIdentified> {

		void fullTest() {

			testRegenExists();
			testLabelRegen();
		}

		E retrieveTest() {

			testRegenExists();

			return getRegenEntity();
		}

		abstract E getInputEntity();

		abstract E getRegenEntity();

		abstract String describeInput();

		<A>void testAttributeRegen(A input, A regen, String attrName) {

			assertEquals(createAttrRegenFailMsg(describeInput(), attrName), input, regen);
		}

		<A>void failAttributeRegen(A input, A regen, String attrName) {

			fail(createAttrRegenFailMsg(describeInput(), input, regen, attrName));
		}

		private void testRegenExists() {

			assertFalse(
				"Original entity not regenerated: " + describeInput(),
				getRegenEntity() == null);
		}

		private void testLabelRegen() {

			testAttributeRegen(
				getLabel(getInputEntity()),
				getLabel(getRegenEntity()),
				"label");
		}
	}

	private class FrameRegenTester extends EntityRegenTester<CFrame> {

		private CFrame inputFrame;
		private CFrame regenFrame;

		FrameRegenTester(CFrame inputFrame) {

			this.inputFrame = inputFrame;

			regenFrame = getRegenFrameOrNull();
		}

		void fullTest() {

			super.fullTest();

			testSuperFrameGeneratedIfRoot();
		}

		CFrame getInputEntity() {

			return inputFrame;
		}

		CFrame getRegenEntity() {

			return regenFrame;
		}

		String describeInput() {

			return describeFrame(inputFrame);
		}

		private CFrame getRegenFrameOrNull() {

			return regenFrames.getOrNull(new RegenId(inputFrame).getIdentifier());
		}

		private void testSuperFrameGeneratedIfRoot() {

			if (inputFrame.isRoot()) {

				assertTrue(subRootFrame(regenFrame));

				CFrame regenFrameSup = regenFrame.getSupers().get(0);
				String regenFrameSupId = regenFrameSup.getIdentity().getIdentifier();

				assertEquals(regenFrameSupId, TEST_ROOT_CONCEPT_IRI.toString());
			}
		}
	}

	private class SlotRegenTester extends EntityRegenTester<CSlot> {

		private CFrame inputFrame;
		private CSlot inputSlot;

		private CFrame regenFrame;
		private CSlot regenSlot;

		SlotRegenTester(CFrame inputFrame, CSlot inputSlot, CFrame regenFrame) {

			this.inputFrame = inputFrame;
			this.inputSlot = inputSlot;
			this.regenFrame = regenFrame;

			regenSlot = getRegenSlotOrNull();
		}

		void fullTest() {

			super.fullTest();

			testCardinalityRegen();
			testValueTypeRegen();
		}

		CSlot getInputEntity() {

			return inputSlot;
		}

		CSlot getRegenEntity() {

			return regenSlot;
		}

		String describeInput() {

			return describeSlot(inputFrame, inputSlot.getIdentity());
		}

		private void testCardinalityRegen() {

			testAttributeRegen(
				inputSlot.getCardinality(),
				regenSlot.getCardinality(),
				"cardinality");
		}

		private void testValueTypeRegen() {

			CValue<?> input = inputSlot.getValueType();
			CValue<?> regen = regenSlot.getValueType();

			if (!valueTypeRegenOk(input, regen)) {

				failAttributeRegen(input, regen, "value-type");
			}
		}

		private boolean valueTypeRegenOk(CValue<?> input, CValue<?> regen) {

			if (input.getClass() != regen.getClass()) {

				return false;
			}

			input = checkConvertMFrameToCFrame(input);
			regen = checkConvertMFrameToCFrame(regen);

			if (input instanceof CFrame) {

				return frameValueTypeRegenOk((CFrame)input, (CFrame)regen);
			}

			return input.equals(regen);
		}

		private CValue<?> checkConvertMFrameToCFrame(CValue<?> value) {

			return value instanceof MFrame ? ((MFrame)value).getRootCFrame() : value;
		}

		private boolean frameValueTypeRegenOk(CFrame input, CFrame regen) {

			Set<String> inputIds = disjunctIdsAsSet(input, true);
			Set<String> regenIds = disjunctIdsAsSet(regen, false);

			return inputIds.equals(regenIds);
		}

		private CSlot getRegenSlotOrNull() {

			return regenFrame.getSlots().getOrNull(new RegenId(inputSlot).getIdentifier());
		}

		private Set<String> disjunctIdsAsSet(CFrame frame, boolean toRegens) {

			return identifiersAsSet(getIdentities(frame.asDisjuncts()), toRegens);
		}
	}

	private class FrameSlotValuesRegenTester {

		private CFrame inputFrame;
		private CFrame regenFrame;

		FrameSlotValuesRegenTester(CFrame inputFrame) {

			this.inputFrame = inputFrame;

			regenFrame = new FrameRegenTester(inputFrame).retrieveTest();

			test();
		}

		private void test() {

			testValueSlotsRegen();

			for (CIdentity inputSlotId : inputFrame.getSlotValues().getSlotIdentities()) {

				testSlotValuesRegen(inputSlotId);
			}
		}

		private void testValueSlotsRegen() {

			Set<String> inputSlotIds = slotIdsAsSet(inputFrame, true);
			Set<String> regenSlotIds = slotIdsAsSet(regenFrame, false);

			assertEquals(createValueSlotsRegenFailMsg(), inputSlotIds, regenSlotIds);
		}

		private void testSlotValuesRegen(CIdentity inputSlotId) {

			CIdentity regenSlotId = new RegenId(inputSlotId).getIdentity();

			Set<String> inputs = frameSlotValueIdsAsSet(inputFrame, inputSlotId, true);
			Set<String> regens = frameSlotValueIdsAsSet(regenFrame, regenSlotId, false);

			assertEquals(createSlotValuesRegenFailMsg(inputSlotId), inputs, regens);
		}

		private Set<String> slotIdsAsSet(CFrame frame, boolean toRegens) {

			return identifiersAsSet(frame.getSlotValues().getSlotIdentities(), toRegens);
		}

		private Set<String> frameSlotValueIdsAsSet(CFrame frame, CIdentity slotId, boolean toRegens) {

			List<CValue<?>> values = frame.getSlotValues().getValues(slotId);

			return identifiersAsSet(mframeValuesToIdentities(values), toRegens);
		}

		private List<CIdentity> mframeValuesToIdentities(List<CValue<?>> values) {

			List<CIdentity> ids = new ArrayList<CIdentity>();

			for (CValue<?> value : values) {

				ids.add(mframeValueToIdentity(value));
			}

			return ids;
		}

		private CIdentity mframeValueToIdentity(CValue<?> value) {

			return ((MFrame)value).getRootCFrame().getIdentity();
		}

		private String createValueSlotsRegenFailMsg() {

			String frameDesc = describeFrame(inputFrame);

			return createAttrRegenFailMsg(frameDesc, "slots-with-fixed-values");
		}

		private String createSlotValuesRegenFailMsg(CIdentity inputSlotId) {

			String slotDesc = describeSlot(inputFrame, inputSlotId);

			return createAttrRegenFailMsg(slotDesc, "fixed-slot-values");
		}
	}

	@Before
	public void setUp() {

		CModel inputModel = CManager.createBuilder().build();

		generateOntology(inputModel);

		inputFrames = inputModel.getFrames();
		regenFrames = loadCModelFromGeneratedOntology().getFrames();
	}

	@Test
	public void test_generationFromFrames() {

		for (CFrame inputFrame : inputFrames.asList()) {

			new FrameRegenTester(inputFrame).fullTest();
		}

		assertEquals(inputFrames.size() + 1, regenFrames.size());
	}

	@Test
	public void test_generationFromSlots() {

		for (CFrame inputFrame : inputFrames.asList()) {

			testFrameSlotsRegen(inputFrame);
		}
	}

	@Test
	public void test_generationFromSlotValues() {

		for (CFrame inputFrame : inputFrames.asList()) {

			new FrameSlotValuesRegenTester(inputFrame);
		}
	}

	private void testFrameSlotsRegen(CFrame inputFrame) {

		CFrame regenFrame = new FrameRegenTester(inputFrame).retrieveTest();

		for (CSlot inputSlot : inputFrame.getSlots().asList()) {

			new SlotRegenTester(inputFrame, inputSlot, regenFrame).fullTest();
		}

		assertEquals(inputFrame.getSlots().size(), regenFrame.getSlots().size());
	}

	private Set<String> identifiersAsSet(List<CIdentity> identities, boolean toRegens) {

		Set<String> identifiers = new HashSet<String>();

		for (CIdentity identity : identities) {

			identifiers.add(getIdentifier(identity, toRegens));
		}

		return identifiers;
	}

	private List<CIdentity> getIdentities(List<? extends CIdentified> identifieds) {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (CIdentified identified : identifieds) {

			ids.add(identified.getIdentity());
		}

		return ids;
	}

	private String getIdentifier(CIdentity identity, boolean toRegen) {

		return toRegen ? new RegenId(identity).getIdentifier() : identity.getIdentifier();
	}

	private String getLabel(CIdentified identified) {

		return identified.getIdentity().getLabel();
	}

	private String describeFrame(CFrame frame) {

		return "frame " + getLabel(frame);
	}

	private String describeSlot(CFrame frame, CIdentity slotId) {

		return "slot " + getLabel(frame) + "-->" + slotId.getLabel();
	}

	private CFrame findAncestorSubRootFrame(CFrame current) {

		if (current.isRoot()) {

			throw new Error("Unexpected root-frame!");
		}

		CFrame sup = current.getSupers().get(0);

		return subRootFrame(sup) ? sup : findAncestorSubRootFrame(sup);
	}

	private boolean subRootFrame(CFrame frame) {

		List<CFrame> sups = frame.getSupers();

		return sups.size() == 1 && sups.get(0).isRoot();
	}

	private String createAttrRegenFailMsg(
						String inputDesc,
						Object input,
						Object regen,
						String attr) {

		return createAttrRegenFailMsg(inputDesc, attr)
				+ " (original = " + input
				+ ", regenerated = " + regen + ")";
	}

	private String createAttrRegenFailMsg(String inputDesc, String attr) {

		return "Original " + attr + " not regenerated correctly for " + inputDesc;
	}

	private void generateOntology(CModel inputModel) {

		OGGenerator generator = new OGGenerator(TEST_ONTOLOGY_IRI, new TestEntityGenerator());

		generator.generate(inputModel);
		generator.save(TEST_FILE);
	}

	private CModel loadCModelFromGeneratedOntology() {

		CBuilder cBuilder = CManager.createEmptyBuilder();
		OBSectionBuilder obBuilder = new OBSectionBuilder(loadGeneratedOntology());

		obBuilder.setDefaultFrameSlotsPolicy(OBFrameSlotsPolicy.CFRAME_VALUED_IF_NO_STRUCTURE);
		obBuilder.getEntityLabels().addAnnotationProperty(LABEL_ANNOTATION_IRI);

		cBuilder.addSectionBuilder(obBuilder);

		return cBuilder.build();
	}

	private OModel loadGeneratedOntology() {

		return new OModelBuilder(TEST_FILE, new StructuralReasonerFactory()).create(true);
	}
}
