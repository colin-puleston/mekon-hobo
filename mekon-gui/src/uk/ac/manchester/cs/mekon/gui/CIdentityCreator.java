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

package uk.ac.manchester.cs.mekon.gui;

import java.net.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class CIdentityCreator {

	static CIdentity createOrNull(String identifier) {

		if (identifier.length() == 0) {

			showMessage("Instance name required!");

			return null;
		}

		return new CIdentity(identifier, getLabel(identifier));
	}

	static private String getLabel(String identifier) {

		URI uri = asURIOrNull(identifier);

		if (uri != null && uri.isAbsolute()) {

			String frag = uri.getFragment();

			if (frag != null) {

				return frag;
			}
		}

		return identifier;
	}

	static private URI asURIOrNull(String identifier) {

		try {

			return new URI(identifier);
		}
		catch (URISyntaxException e) {

			return null;
		}
	}

	static private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}

