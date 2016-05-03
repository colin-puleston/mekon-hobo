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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY FC ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES FC MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION FC CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT FC OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Abstract base class for classes responsible for converting
 * {@link IFrame}/{@link ISlot} networks into other formats.
 *
 * @author Colin Puleston
 */
public abstract class IFrameConverter<FC, SC> {

	private TypeISlotConverter<IFrame> iFrameSlotConverter;
	private TypeISlotConverter<CFrame> cFrameSlotConverter;
	private TypeISlotConverter<INumber> iNumberSlotConverter;
	private TypeISlotConverter<IString> iStringSlotConverter;

	private Map<IFrame, FC> frameConversions = new HashMap<IFrame, FC>();

	/**
	 * Abstract base class for slot-conversion.
	 */
	protected abstract class TypeISlotConverter<IV> {

		/**
		 * Converts specified slot together with specified set of slot-values.
		 *
		 * @param frameConversion Entity to which slot-conversions are to be added
		 * @param slot Slot being converted
		 * @param iValues Slot-values to be converted
		 */
		protected abstract void convert(FC frameConversion, ISlot slot, List<IV> iValues);

		/**
		 * Converts specified slot together with specified set of slot-values.
		 *
		 * @param frameConversion Entity to which slot-conversions are to be added
		 * @param slotId Identity of slot being converted
		 * @param iValues Slot-values to be converted
		 */
		protected abstract void convert(FC frameConversion, CIdentity slotId, List<IV> iValues);
	}

	private class TypeISlotNonConverter<IV> extends TypeISlotConverter<IV> {

		protected void convert(FC frameConversion, ISlot slot, List<IV> iValues) {
		}

		protected void convert(FC frameConversion, CIdentity slotId, List<IV> iValues) {
		}
	}

	private class ISlotConverter extends ISlotValuesVisitor {

		private ISlot slot;
		private FC frameConversion;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotConverter.convert(frameConversion, slot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			iNumberSlotConverter.convert(frameConversion, slot, values);
		}

		protected void visit(CString valueType, List<IString> values) {
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotConverter.convert(frameConversion, slot, values);
		}

		ISlotConverter(ISlot slot, FC frameConversion) {

			this.slot = slot;
			this.frameConversion = frameConversion;

			visit(slot);
		}
	}

	private class CSlotValuesConverter extends CValueVisitor {

		private CSlotValues cSlotValues;
		private CIdentity slotId;
		private FC frameConversion;

		protected void visit(CFrame value) {

			cFrameSlotConverter.convert(frameConversion, slotId, getValues(CFrame.class));
		}

		protected void visit(CNumber value) {

			iNumberSlotConverter.convert(frameConversion, slotId, getCNumberValuesAsINumbers());
		}

		protected void visit(CString value) {
		}

		protected void visit(MFrame value) {

			cFrameSlotConverter.convert(frameConversion, slotId, getMFrameValuesAsCFrames());
		}

		CSlotValuesConverter(CSlotValues cSlotValues, CIdentity slotId, FC frameConversion) {

			this.cSlotValues = cSlotValues;
			this.slotId = slotId;
			this.frameConversion = frameConversion;

			visit(cSlotValues.getValues(slotId).get(0));
		}

		private List<CFrame> getMFrameValuesAsCFrames() {

			List<CFrame> cFrames = new ArrayList<CFrame>();

			for (MFrame mFrame : getValues(MFrame.class)) {

				cFrames.add(mFrame.getRootCFrame());
			}

			return cFrames;
		}

		private List<INumber> getCNumberValuesAsINumbers() {

			List<INumber> iNumbers = new ArrayList<INumber>();

			for (CNumber cNumber : getValues(CNumber.class)) {

				iNumbers.add(cNumber.asINumber());
			}

			return iNumbers;
		}

		private <V extends CValue<?>>List<V> getValues(Class<V> valueClass) {

			return cSlotValues.getValues(slotId, valueClass);
		}
	}

	/**
	 * Converts the specified frame/link network into a network of
	 * the relevant type.
	 *
	 * @param rootFrame Root-frame of network to be converted
	 * @return Root-entity of created network
	 */
	public FC convert(IFrame rootFrame) {

		return getFrameConversion(rootFrame);
	}

	/**
	 * Constructor.
	 */
	protected IFrameConverter() {

		iFrameSlotConverter = createIFrameSlotConverter();
		cFrameSlotConverter = createCFrameSlotConverter();
		iNumberSlotConverter = createINumberSlotConverter();
		iStringSlotConverter = createIStringSlotConverter();
	}

	/**
	 * Creates a converter for {@link IFrame}-valued slots.
	 *
	 * @return Created converter
	 */
	protected abstract TypeISlotConverter<IFrame> createIFrameSlotConverter();

	/**
	 * Creates a converter for {@link CFrame}-valued slots.
	 *
	 * @return Created converter
	 */
	protected abstract TypeISlotConverter<CFrame> createCFrameSlotConverter();

	/**
	 * Creates a converter for {@link INumber}-valued slots.
	 *
	 * @return Created converter
	 */
	protected abstract TypeISlotConverter<INumber> createINumberSlotConverter();

	/**
	 * Creates a converter for {@link IString}-valued slots.
	 *
	 * @return Created converter
	 */
	protected abstract TypeISlotConverter<IString> createIStringSlotConverter();

	/**
	 * Creates a converter for slots with specified value-type that
	 * are not required to appear in the output network.
	 *
	 * @return Created converter
	 */
	protected <T>TypeISlotConverter<T> createTypeISlotNonConverter(Class<T> type) {

		return new TypeISlotNonConverter<T>();
	}

	/**
	 * Provides a conversion of the specified frame, including
	 * any required structure representing frame-type and/or slots.
	 * The provided conversion will be a previously created entity if
	 * this method has already been invoked for the specified frame.
	 * Otherwise it will be newly created and configured.
	 *
	 * @param frame Frame whose conversion is required
	 * @return Converted and configured frame
	 */
	protected FC getFrameConversion(IFrame frame) {

		FC conversion = frameConversions.get(frame);

		if (conversion == null) {

			conversion = createUnconfiguredFrameConversion(frame);

			frameConversions.put(frame, conversion);

			configureFrameConversionType(conversion, frame.getType());
			convertSlots(frame, conversion);
		}

		return conversion;
	}

	/**
	 * Creates a converted version of the specified frame, without
	 * creating any structure representing frame-type or slots.
	 *
	 * @param frame Frame whose conversion is required
	 * @return Converted but unconfigured frame
	 */
	protected abstract FC createUnconfiguredFrameConversion(IFrame frame);

	/**
	 * Performs any required frame-type related configurations for
	 * a newly-created frame-conversion.
	 *
	 * @param conversion Newly-created frame-conversion
	 * @return Type of converted frame
	 */
	protected void configureFrameConversionType(FC conversion, CFrame frameType) {

		if (frameType.getCategory().extension()) {

			configureExtensionFrameConversion(conversion, frameType);
		}
	}

	private void configureExtensionFrameConversion(FC conversion, CFrame frameType) {

		CSlotValues slotValues = frameType.getSlotValues();

		for (CIdentity slotId : slotValues.getSlotIdentities()) {

			new CSlotValuesConverter(slotValues, slotId, conversion);
		}
	}

	private void convertSlots(IFrame frame, FC frameConversion) {

		for (ISlot slot : frame.getSlots().asList()) {

			new ISlotConverter(slot, frameConversion);
		}
	}
}
