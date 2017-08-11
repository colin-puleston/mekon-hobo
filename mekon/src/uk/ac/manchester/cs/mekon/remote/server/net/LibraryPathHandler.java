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

package uk.ac.manchester.cs.mekon.remote.server.net;

import java.lang.reflect.*;
import javax.servlet.*;

import uk.ac.manchester.cs.mekon.remote.server.*;

/**
 * @author Colin Puleston
 */
class LibraryPathHandler {

	static private final String PATHS_PROPERTY = "java.library.path";
	static private final String APP_LIB_RELATIVE_PATH = "WEB-INF/lib";
	static private final String SYS_PATHS_FIELD_NAME = "sys_paths";

	static void setLibraryPath(ServletContext context) {

		setPathsSystemProperty(context);
		flushExistingPaths();
	}

	static private void setPathsSystemProperty(ServletContext context) {

		System.setProperty(PATHS_PROPERTY, getAppLibraryPath(context));
	}

	static private String getAppLibraryPath(ServletContext context) {

		return context.getRealPath(APP_LIB_RELATIVE_PATH);
	}

	static private void flushExistingPaths() {

		try {

			Field sysPaths = ClassLoader.class.getDeclaredField(SYS_PATHS_FIELD_NAME);

			sysPaths.setAccessible(true);
			sysPaths.set(null, null);
		}
		catch (NoSuchFieldException e) {

			handleException(e);
		}
		catch (SecurityException e) {

			handleException(e);
		}
		catch (IllegalArgumentException e) {

			handleException(e);
		}
		catch (IllegalAccessException e) {

			handleException(e);
		}
	}

	static private void handleException(Exception exception) {

		throw new RServerException(
					"Error setting library path: "
					+ exception.getMessage());
	}
}