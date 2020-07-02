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

/**
 * Visitor for {@link IValue} objects.
 *
 * @author Colin Puleston
 */
public abstract class IValueVisitor extends FVisitor<IValue> {

	private class ValueTypeVisitor extends CValueVisitor {

		private IValue value;

		protected void visit(CFrame valueType) {

			IValueVisitor.this.visit(castValue(valueType));
		}

		protected void visit(CNumber valueType) {

			IValueVisitor.this.visit(castValue(valueType));
		}

		protected void visit(CString valueType) {

			IValueVisitor.this.visit(castValue(valueType));
		}

		protected void visit(MFrame valueType) {

			IValueVisitor.this.visit(castValue(valueType));
		}

		ValueTypeVisitor(IValue value) {

			this.value = value;

			visit(value.getType());
		}

		private <V extends IValue>V castValue(CValue<V> valueType) {

			return valueType.castValue(value);
		}
	}

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(IFrame value);

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(INumber value);

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(IString value);

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(CFrame value);

	void performVisit(IValue acceptor) throws Exception {

		new ValueTypeVisitor(acceptor);
	}
}
