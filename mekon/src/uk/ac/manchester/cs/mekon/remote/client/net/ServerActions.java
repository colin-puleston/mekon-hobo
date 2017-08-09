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

package uk.ac.manchester.cs.mekon.remote.client.net;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.client.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
abstract class ServerActions<T extends Enum<T>> {

	private URL serverURL;

	private DocumentRetriever documentRetriever = new DocumentRetriever();
	private BooleanRetriever booleanRetriever = new BooleanRetriever();

	private abstract class Retriever<R> {

		abstract R get(NetLink link) throws IOException;
	}

	private class DocumentRetriever extends Retriever<XDocument> {

		XDocument get(NetLink link) throws IOException {

			return link.readDocument();
		}
	}

	private class BooleanRetriever extends Retriever<Boolean> {

		Boolean get(NetLink link) throws IOException {

			return link.readBoolean();
		}
	}

	ServerActions(URL serverURL) {

		this.serverURL = serverURL;
	}

	XDocument getDocumentResult(T type, XDocument... inputDocs) {

		return perform(type, documentRetriever, inputDocs);
	}

	Boolean getBooleanResult(T type, XDocument... inputDocs) {

		return perform(type, booleanRetriever, inputDocs);
	}

	private <R>R perform(T type, Retriever<R> retriever, XDocument... inputDocs) {

		try {

			NetLink link = new NetLink(getActionRequestURL(type));

			link.writeDocuments(inputDocs);

			R result = retriever.get(link);

			link.close();

			return result;
		}
		catch (IOException e) {

			throw new RServerException(e);
		}
	}

	abstract RActionCategory getCategory();

	private ActionRequestURL getActionRequestURL(T type) {

		return new ActionRequestURL(serverURL, getCategory(), type);
	}
}
