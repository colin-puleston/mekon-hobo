/**
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
package uk.ac.manchester.cs.mekon.serial;

import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.*;
import org.xml.sax.*;

/**
 * @author Colin Puleston
 */
class XMLDocument {

	static private final String PRETTY_PRINT_ID = "format-pretty-print";

	static Document read(File file) {

		try {

			return createBuilder().parse(file);
		}
		catch (IOException e) {

			throw new XDocumentException(e);
		}
		catch (SAXException e) {

			throw new XDocumentException(e);
		}
	}

	static void write(Document document, File file) {

		FileOutputStream output = null;

		try {

			output = new FileOutputStream(file);

			write(document, output);
		}
		catch (IOException e) {

			throw new XDocumentException(e);
		}
		finally {

			if (output != null) {

				try {

					output.close();
				}
				catch (IOException e) {

					throw new XDocumentException(e);
				}
			}
		}
	}

	static Document create(String rootElementId) {

		Document document = createBuilder().newDocument();
		Element rootEl = document.createElement(rootElementId);

		document.appendChild(rootEl);

		return document;
	}

	static private DocumentBuilder createBuilder() {

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setNamespaceAware(true);

			return factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {

			throw new XDocumentException(e);
		}
	}

	static private void write(
							Document document,
							FileOutputStream output)
							throws IOException {

		DOMImplementationLS impl = getImplementation(document);
		LSSerializer serializer = impl.createLSSerializer();
		LSOutput lsOutput = impl.createLSOutput();

		setPrettyPrint(serializer);
		lsOutput.setByteStream(output);
		serializer.write(document, lsOutput);
	}

	static private void setPrettyPrint(LSSerializer serializer) {

		DOMConfiguration config = serializer.getDomConfig();

		if (config.canSetParameter(PRETTY_PRINT_ID, true)) {

			config.setParameter(PRETTY_PRINT_ID, true);
		}
	}

	static private DOMImplementationLS getImplementation(Document document) {

		return (DOMImplementationLS)document.getImplementation();
	}
}