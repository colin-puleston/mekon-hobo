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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class CExtension extends CExpression {

	static private final String EXPRESSION_TYPE_NAME = "expression";

	private CModelFrame extendedFrame;
	private Set<CFrame> structuredAncestors;
	private CSlotValues slotValues;

	private int hashCode;

	private class DelayedSlotValuesValidator extends InitialisationListener {

		DelayedSlotValuesValidator() {

			getModel().addInitialisationListener(this);
		}

		void onInitialised() {

			slotValues.validateAll(CExtension.this);
			getModel().removeInitialisationListener(this);
		}
	}

	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof CExtension) {

			return equalsExtension((CExtension)other);
		}

		return false;
	}

	public int hashCode() {

		return hashCode;
	}

	public String toString() {

		return new CExtensionDescriber(this).describeForToString();
	}

	public CFrameCategory getCategory() {

		return CFrameCategory.EXTENSION;
	}

	public CModel getModel() {

		return extendedFrame.getModel();
	}

	public CFrame getModelFrame() {

		return extendedFrame;
	}

	public List<CFrame> getSupers(CFrameVisibility visibility) {

		return Collections.<CFrame>singletonList(extendedFrame);
	}

	public List<CFrame> getSubs(CFrameVisibility visibility) {

		return Collections.emptyList();
	}

	public Set<CFrame> getAncestors(CFrameVisibility visibility) {

		Set<CFrame> ancestors = extendedFrame.getAncestors(visibility);

		if (visibility.coversHiddenStatus(hidden())) {

			ancestors.add(extendedFrame);
		}

		return ancestors;
	}

	public Set<CFrame> getStructuredAncestors() {

		return structuredAncestors;
	}

	public Set<CFrame> getDescendants(CFrameVisibility visibility) {

		return Collections.emptySet();
	}

	public CSlotValues getSlotValues() {

		return slotValues;
	}

	public boolean subsumes(CFrame testSubsumed) {

		return testSubsumed instanceof CExtension
				&& super.subsumes(testSubsumed)
				&& slotValues.subsumes(testSubsumed.getSlotValues());
	}

	CExtension(String label, CModelFrame extendedFrame, CSlotValues slotValues) {

		super(label);

		this.extendedFrame = extendedFrame;
		this.slotValues = slotValues;

		structuredAncestors = resolveStructuredAncestors();
		hashCode = createHashCode();

		if (getModel().initialised()) {

			this.slotValues.validateAll(this);
		}
		else {

			new DelayedSlotValuesValidator();
		}

		registerAsReferencerOfSlotValues();
	}

	List<CModelFrame> asDisjuncts() {

		throw new KAccessException(
					"Cannot create disjunction with "
					+ "extension-frame as a disjunct: "
					+ this);
	}

	List<CModelFrame> getSubsumptionTestDisjuncts() {

		return extendedFrame.getSubsumptionTestDisjuncts();
	}

	IReasoner getIReasoner() {

		return extendedFrame.getIReasoner();
	}

	String getExpressionTypeName() {

		return EXPRESSION_TYPE_NAME;
	}

	String getExpressionDescriptionForId() {

		return new CExtensionDescriber(this).describeForId();
	}

	String getExpressionDescriptionForLabel() {

		return new CExtensionDescriber(this).describeForLabel();
	}

	private Set<CFrame> resolveStructuredAncestors() {

		Set<CFrame> resolved = extendedFrame.getStructuredAncestors();

		if (extendedFrame.structured()) {

			resolved = new HashSet<CFrame>(resolved);
			resolved.add(extendedFrame);
		}

		return resolved;
	}

	private int createHashCode() {

		return extendedFrame.hashCode() + slotValues.createHashCode();
	}

	private void registerAsReferencerOfSlotValues() {

		for (CProperty property : slotValues.getSlotProperties()) {

			for (CValue<?> value : slotValues.getValues(property)) {

				value.registerSlotValueReferencingFrame(this);
			}
		}
	}

	private boolean equalsExtension(CExtension other) {

		return extendedFrame.equals(other.extendedFrame)
				&& slotValues.equalSlotValues(other.slotValues);
	}
}
