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

package uk.ac.manchester.cs.mekon.remote.server.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon.remote.server.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
abstract class ServerActions<T extends Enum<T>> {

	private List<Action> actions = new ArrayList<Action>();

	abstract class Action {

		Action() {

			actions.add(this);
		}

		abstract T getActionType();

		abstract void perform(XRequestParser request, XResponseRenderer response);
	}

	boolean checkPerformAction(XRequestParser request, XResponseRenderer response) {

		if (getActionCategory() == request.getActionCategory()) {

			findAction(request).perform(request, response);

			return true;
		}

		return false;
	}

	abstract RActionCategory getActionCategory();

	abstract T getRequestActionType(XRequestParser request);

	private Action findAction(XRequestParser request) {

		T type = getRequestActionType(request);

		for (Action action : actions) {

			if (action.getActionType() == type) {

				return action;
			}
		}

		throw new RServerException(
					"Unrecognised server action type: "
					+ "\"" + getActionCategory() + ":" + type + "\"");
	}
}
