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

package uk.ac.manchester.cs.mekon.model.serial;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Specifies the manner in which schema information is to be derived
 * when parsing serialised {@link IFrame}/{@link ISlot} networks.
 *
 * @author Colin Puleston
 */
public enum ISchemaParse {

	/**
	 * A "free-instance" is to be created with a minimal schema (see
	 * {@link IFreeInstantiator} for description of free-instances).
	 */
	FREE {

		IFrameParseMechanisms getMechanisms(CModel model) {

			return IFrameFreeParseMechanisms.SINGLETON;
		}
	},

	/**
	 * Static schema information is to be parsed from the serialisation,
	 * along with the frame/slot network instantiation information.
	 */
	STATIC {

		IFrameParseMechanisms getMechanisms(CModel model) {

			return IFrameStaticParseMechanisms.SINGLETON;
		}
	},

	/**
	 * Schema information is to be derived dynamically from the model
	 * as the frame/slot network is instantiated.
	 */
	DYNAMIC {

		IFrameParseMechanisms getMechanisms(CModel model) {

			return new IFrameDynamicParseMechanisms(model);
		}
	};

	abstract IFrameParseMechanisms getMechanisms(CModel model);
}