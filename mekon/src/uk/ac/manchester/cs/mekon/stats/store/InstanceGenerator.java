package uk.ac.manchester.cs.mekon.stats.store;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class InstanceGenerator {

	static private final int BIG_NUMBER = 1000000;

	private CFrame type;
	private IFrameFunction function;
	private boolean enableStrings;
	private int branchingFactor;
	private int maxNodes;

	private int nodeCount = 1;
	private int maxDepth = 1;

	private Random random = new Random();

	private Map<CValue<?>, SlotPopulator<?>> slotPopulators
					= new HashMap<CValue<?>, SlotPopulator<?>>();

	private class CFrameValuesSelector {

		private List<CFrame> valueOptions = new ArrayList<CFrame>();

		CFrameValuesSelector(CFrame rootValue) {

			addLeafsAsValueOptions(rootValue, new HashSet<CFrame>());
		}

		List<CFrame> getValues(CCardinality cardinality) {

			List<CFrame> values = new ArrayList<CFrame>();
			List<CFrame> options = new ArrayList<CFrame>(valueOptions);

			for (int branch = 0 ; branch < branchingFactor ; branch++) {

				CFrame value = getRandomValueOption(options);

				values.add(value);

				if (cardinality.singleValue()) {

					break;
				}

				if (cardinality.uniqueTypes()) {

					options.remove(value);

					if (options.isEmpty()) {

						break;
					}
				}
			}

			return values;
		}

		private void addLeafsAsValueOptions(CFrame frame, Set<CFrame> visited) {

			List<CFrame> subs = frame.getSubs(CVisibility.EXPOSED);

			if (subs.isEmpty()) {

				valueOptions.add(frame);
			}
			else {

				for (CFrame sub : subs) {

					if (visited.add(sub)) {

						addLeafsAsValueOptions(sub, visited);
					}
				}
			}
		}

		private CFrame getRandomValueOption(List<CFrame> options) {

			return options.get(random.nextInt(options.size()));
		}
	}

	private abstract class SlotPopulator<V extends IValue> {

		void populate(ISlot slot, int depth) {

			List<V> values = getValues(slot.getType().getCardinality());

			slot.getValuesEditor().addAll(values);
			populateValues(values, depth);
		}

		abstract List<V> getValues(CCardinality cardinality);

		void populateValues(List<V> values, int depth) {
		}
	}

	private abstract class FrameValuedSlotPopulator
								<F extends IValue>
								extends SlotPopulator<F> {

		private CFrameValuesSelector cFramesSelector;

		FrameValuedSlotPopulator(CFrame rootCFrame) {

			cFramesSelector = new CFrameValuesSelector(rootCFrame);
		}

		List<F> getValues(CCardinality cardinality) {

			List<F> values = new ArrayList<F>();

			for (CFrame cFrame : cFramesSelector.getValues(cardinality)) {

				if (!incrementNodeCount()) {

					break;
				}

				values.add(getValue(cFrame));
			}

			return values;
		}

		abstract F getValue(CFrame cFrame);
	}

	private class CFrameValuedSlotPopulator extends FrameValuedSlotPopulator<CFrame> {

		private CFrameValuesSelector valuesSelector;

		CFrameValuedSlotPopulator(MFrame valueType) {

			super(valueType.getRootCFrame());
		}

		CFrame getValue(CFrame cFrame) {

			return cFrame;
		}
	}

	private class IFrameValuedSlotPopulator extends FrameValuedSlotPopulator<IFrame> {

		IFrameValuedSlotPopulator(CFrame valueType) {

			super(valueType);
		}

		IFrame getValue(CFrame cFrame) {

			return cFrame.instantiate(function);
		}

		void populateValues(List<IFrame> values, int depth) {

			for (IFrame value : values) {

				populateFrame(value, depth);
			}
		}
	}

	private abstract class IDataValuedSlotPopulator<V extends IDataValue> extends SlotPopulator<V> {

		List<V> getValues(CCardinality cardinality) {

			if (dataTypeEnabled() && incrementNodeCount()) {

				return Collections.singletonList(createValue());
			}

			return Collections.<V>emptyList();
		}

		boolean dataTypeEnabled() {

			return true;
		}

		abstract V createValue();
	}

	private class INumberValuedSlotPopulator extends IDataValuedSlotPopulator<INumber> {

		private CNumber valueType;

		INumberValuedSlotPopulator(CNumber valueType) {

			this.valueType = valueType;

			checkIntegerValued();
		}

		INumber createValue() {

			return new INumber(getRandomValue());
		}

		private void checkIntegerValued() {

			if (valueType.getNumberType() != Integer.class) {

				throw new RuntimeException("Unexpected value-type for INumber-valued slots");
			}
		}

		private int getRandomValue() {

			int min = valueType.hasMin() ? valueType.getMin().asInteger() : 0;
			int max = valueType.hasMax() ? valueType.getMax().asInteger() : BIG_NUMBER;

			return random.nextInt(max - min + 1) + min;
		}
	}

	private class IStringValuedSlotPopulator extends IDataValuedSlotPopulator<IString> {

		private int valueIndex = 0;

		boolean dataTypeEnabled() {

			return enableStrings;
		}

		IString createValue() {

			return CString.FREE.instantiate("String-" + valueIndex++);
		}
	}

	private class SlotPopulatorCreator extends CValueVisitor {

		private SlotPopulator<?> populator = null;

		protected void visit(CFrame valueType) {

			populator = new IFrameValuedSlotPopulator(valueType);
		}

		protected void visit(CNumber valueType) {

			populator = new INumberValuedSlotPopulator(valueType);
		}

		protected void visit(CString valueType) {

			populator = new IStringValuedSlotPopulator();
		}

		protected void visit(MFrame valueType) {

			populator = new CFrameValuedSlotPopulator(valueType);
		}

		SlotPopulator<?> create(CValue<?> valueType) {

			visit(valueType);

			return populator;
		}
	}

	public InstanceGenerator(
				CFrame type,
				IFrameFunction function,
				boolean enableStrings,
				int branchingFactor,
				int maxNodes) {

		this.type = type;
		this.function = function;
		this.enableStrings = enableStrings;
		this.branchingFactor = branchingFactor;
		this.maxNodes = maxNodes;
	}

	public IFrame generate() {

		IFrame instance = type.instantiate(function);

		populateFrame(instance, 0);

		return instance;
	}

	public int generatedNodeCount() {

		return nodeCount;
	}

	public int maxGeneratedDepth() {

		return maxDepth;
	}

	private void populateFrame(IFrame frame, int depth) {

		if (++depth > maxDepth) {

			maxDepth = depth;
		}

		for (ISlot slot : frame.getSlots().activesAsList()) {

			if (slot.getEditability().editable()) {

				getSlotPopulator(slot).populate(slot, depth);
			}
		}
	}

	private SlotPopulator<?> getSlotPopulator(ISlot slot) {

		CValue<?> valueType = slot.getValueType();
		SlotPopulator<?> populator = slotPopulators.get(valueType);

		if (populator == null) {

			populator = createSlotPopulator(valueType);
			slotPopulators.put(valueType, populator);
		}

		return populator;
	}

	private SlotPopulator<?> createSlotPopulator(CValue<?> valueType) {

		return new SlotPopulatorCreator().create(valueType);
	}

	private boolean incrementNodeCount() {

		if (nodeCount == maxNodes) {

			return false;
		}

		nodeCount++;

		return true;
	}
}
