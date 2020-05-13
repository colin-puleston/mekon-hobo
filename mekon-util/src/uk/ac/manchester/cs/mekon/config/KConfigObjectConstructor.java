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

package uk.ac.manchester.cs.mekon.config;

import java.lang.reflect.*;

/**
 * Responsible for constructing objects of a specific type.
 *
 * @author Colin Puleston
 */
public class KConfigObjectConstructor<T> {

	private Class<? extends T> type;

	/**
	 * Constructor.
	 *
	 * @param type Type of objects to be constructed
	 */
	public KConfigObjectConstructor(Class<? extends T> type) {

		this.type = type;
	}

	/**
	 * Constructs an object of the relevant type, where for each of the
	 * specified parameters, the parameter-type expected by the
	 * constructor is simply the class of the specified parameter value
	 * (and not a super-class).
	 *
	 * @param parameters Parameters to be passed to object constructor
	 * (possibly none)
	 * @return Constructed object
	 * @throws KSystemConfigException if required object cannot be constructed
	 * for any reason
	 */
	public T construct(Object... parameters) {

		return construct(new KConfigParameters(parameters));
	}

	/**
	 * Constructs an object of the relevant type.
	 *
	 * @param parameters Parameters plus parameter-types to be passed to
	 * object constructor
	 * @return Constructed object
	 * @throws KSystemConfigException if required object cannot be constructed
	 * for any reason
	 */
	public T construct(KConfigParameters parameters) {

		Class<?>[] paramTypes = parameters.getTypes();
		Object[] paramValues = parameters.getValues();

		try {

			return type.getConstructor(paramTypes).newInstance(paramValues);
		}
		catch (NoSuchMethodException e) {

			throw new KSystemConfigException(e);
		}
		catch (InstantiationException e) {

			throw new KSystemConfigException(e);
		}
		catch (IllegalAccessException e) {

			throw new KSystemConfigException(e);
		}
		catch (InvocationTargetException e) {

			Throwable t = e.getTargetException();

			if (t instanceof Exception) {

				throw new KSystemConfigException((Exception)t);
			}

			if (t instanceof Error) {

				throw (Error)t;
			}

			throw new Error(t);
		}
	}
}
