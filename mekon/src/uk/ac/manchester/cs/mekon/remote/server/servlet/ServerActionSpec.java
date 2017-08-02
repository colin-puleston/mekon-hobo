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

import javax.servlet.*;

import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class ServerActionSpec {

	private String category;
	private String type;

	ServerActionSpec(ServletRequest request) throws ServletException {

		category = getAspect(request, RActionAspect.CATEGORY);
		type = getAspect(request, RActionAspect.TYPE);
	}

	boolean hasCategory(RActionCategory category) {

		return this.category.equals(category.name());
	}

	boolean hasType(Enum<?> type) {

		return this.type.equals(type.name());
	}

	ServletException getBadSpecException() {

		return new ServletException(
						"Unrecognised server action: "
						+ "\"" + category + ":" + type + "\"");
	}

	private String getAspect(
						ServletRequest request,
						RActionAspect aspect)
						throws ServletException {

		String param = request.getParameter(aspect.name());

		if (param != null) {

			return param;
		}

		throw new ServletException(
						"Server action parameter not specified: "
						+ "\"" + aspect + "\"");
	}
}
