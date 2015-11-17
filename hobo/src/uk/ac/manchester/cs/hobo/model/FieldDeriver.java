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

package uk.ac.manchester.cs.hobo.model;

/**
 * @author Colin Puleston
 */
class FieldDeriver {

	static private abstract class TypeFieldDeriver
									<SV,
									SVT extends DValueType<SV>,
									DV,
									DVT extends DValueType<DV>> {

		final DModel model;
		final DCell<SV> sourceCell;

		TypeFieldDeriver(DModel model, DCell<SV> sourceCell) {

			this.model = model;
			this.sourceCell = sourceCell;
		}

		DCell<DV> derive() {

			DCell<DV> cell = new DCell<DV>(model, deriveValueType());

			sourceCell.onFieldDerived(cell);

			return cell;
		}

		abstract SVT getSourceValueType();

		abstract DVT deriveValueType(SVT sourceValueType);

		private DVT deriveValueType() {

			return deriveValueType(getSourceValueType());
		}
	}

	static private class DisjunctionCellDeriver<D extends DObject>
							extends
								TypeFieldDeriver
									<D,
									DObjectValueType<D>,
									DDisjunction<D>,
									DDisjunctionValueType<D>> {

		DisjunctionCellDeriver(DModel model, DCell<D> sourceCell) {

			super(model, sourceCell);
		}

		DObjectValueType<D> getSourceValueType() {

			return (DObjectValueType<D>)sourceCell.getValueType();
		}

		DDisjunctionValueType<D> deriveValueType(DObjectValueType<D> sourceValueType) {

			return new DDisjunctionValueType<D>(model, sourceValueType);
		}
	}

	static private class NumberRangeCellDeriver<N extends Number>
							extends
								TypeFieldDeriver
									<N,
									DNumberValueType<N>,
									DNumberRange<N>,
									DNumberRangeValueType<N>> {

		NumberRangeCellDeriver(DModel model, DCell<N> sourceCell) {

			super(model, sourceCell);
		}

		DNumberValueType<N> getSourceValueType() {

			return (DNumberValueType<N>)sourceCell.getValueType();
		}

		DNumberRangeValueType<N> deriveValueType(DNumberValueType<N> sourceValueType) {

			return new DNumberRangeValueType<N>(sourceValueType);
		}
	}

	static <D extends DObject>DCell<DDisjunction<D>> deriveDisjunctionCell(
														DModel model,
														DCell<D> objectCell) {

		return new DisjunctionCellDeriver<D>(model, objectCell).derive();
	}

	static <N extends Number>DCell<DNumberRange<N>> deriveNumberRangeCell(
														DModel model,
														DCell<N> numberCell) {

		return new NumberRangeCellDeriver<N>(model, numberCell).derive();
	}
}