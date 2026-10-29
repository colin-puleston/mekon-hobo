/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.hobo.store.disk;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.disk.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Abstract HOBO-based implementaion of the {@link IReverseRefType}
 * interface.
 *
 * @author Colin Puleston
 */
public abstract class DReverseRefType
						<F extends DObject,
						R extends DObject>
						implements IReverseRefType {

	private DModel model;

	/**
	 * {@inheritDoc}
	 */
	public CFrame getForwardReferType() {

		return getFrameType(getForwardReferClass());
	}

	/**
	 * {@inheritDoc}
	 */
	public CFrame getReverseReferType() {

		return getFrameType(getReverseReferClass());
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrame lookForForwardRefValue(IFrame forwardRefer) {

		R obj = lookForForwardRefValue(asForwardReferObj(forwardRefer));

		return obj != null ? obj.getFrame() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean checkAddReverseRef(IFrame forwardRefer, IFrame reverseRefer) {

		return checkAddReverseRef(
					asForwardReferObj(forwardRefer),
					asReverseReferObj(reverseRefer));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean checkRemoveReverseRef(IFrame forwardRefer, IFrame reverseRefer) {

		return checkRemoveReverseRef(
					asForwardReferObj(forwardRefer),
					asReverseReferObj(reverseRefer));
	}

	/**
	 * {@inheritDoc}
	 */
	public void alignReverseRefs(IFrame oldReverseRefer, IFrame newReverseRefer) {

		alignReverseRefs(
			asReverseReferObj(oldReverseRefer),
			asReverseReferObj(newReverseRefer));
	}

	/**
	 * Constructor.
	 *
	 * @model Relevant model
	 */
	protected DReverseRefType(DModel model) {

		this.model = model;
	}

	/**
	 * HOBO object-based version of the {@link IReverseRefType#getForwardReferClass}
	 * method.
	 */
	protected abstract Class<F> getForwardReferClass();

	/**
	 * HOBO object-based version of the {@link IReverseRefType#getReverseReferClass}
	 * method.
	 */
	protected abstract Class<R> getReverseReferClass();

	/**
	 * HOBO object-based version of the {@link IReverseRefType#lookForForwardRefValue}
	 * method.
	 */
	protected abstract R lookForForwardRefValue(F forwardRefer);

	/**
	 * HOBO object-based version of the {@link IReverseRefType#checkAddReverseRef}
	 * method.
	 */
	protected abstract boolean checkAddReverseRef(F forwardRefer, R reverseRefer);

	/**
	 * HOBO object-based version of the {@link IReverseRefType#checkRemoveReverseRef}
	 * method.
	 */
	protected abstract boolean checkRemoveReverseRef(F forwardRefer, R reverseRefer);

	/**
	 * HOBO object-based version of the {@link IReverseRefType#alignReverseRefs}
	 * method.
	 */
	protected abstract void alignReverseRefs(R oldReverseRefer, R newReverseRefer);

	private F asForwardReferObj(IFrame forwardRefer) {

		return toDObject(forwardRefer, getForwardReferClass());
	}

	private R asReverseReferObj(IFrame reverseRefer) {

		return toDObject(reverseRefer, getReverseReferClass());
	}

	private <T extends DObject>T toDObject(IFrame instance, Class<T> objType) {

		DObject obj = model.getDObject(instance);

		if (objType.isAssignableFrom(obj.getClass())) {

			return objType.cast(obj);
		}

		throw new RuntimeException(
					"Expected object of type: " + objType
					+ ", found type: " + obj.getClass());
	}

	private CFrame getFrameType(Class<? extends DObject> typeClass) {

		return model.getConcept(typeClass).getFrame();
	}
}
