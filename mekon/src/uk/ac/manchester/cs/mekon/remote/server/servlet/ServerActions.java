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

package uk.ac.manchester.cs.mekon.remote.server.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;

import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
abstract class ServerActions {

	private List<Action<?>> actions = new ArrayList<Action<?>>();

	abstract class Action<T extends Enum<T>> {

		Action() {

			actions.add(this);
		}

		abstract T getType();

		abstract void perform(ServerIO io) throws ServletException, IOException;
	}

	boolean checkPerformAction(ServerIO io) throws ServletException, IOException {

		ServerActionSpec spec = io.getActionSpec();

		if (spec.hasCategory(getCategory())) {

			findAction(spec).perform(io);

			return true;
		}

		return false;
	}

	abstract RActionCategory getCategory();

	private Action<?> findAction(ServerActionSpec spec) throws ServletException {

		for (Action<?> action : actions) {

			if (spec.hasType(action.getType())) {

				return action;
			}
		}

		throw spec.getBadSpecException();
	}
}
