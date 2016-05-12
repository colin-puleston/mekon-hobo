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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class IFrameXDocIds {

	static private final String ID_FORMAT = "xid-%s";

	static class Resolution {

		private String id;
		private boolean newFrame;

		Resolution(String id, boolean newFrame) {

			this.id = id;
			this.newFrame = newFrame;
		}

		String getId() {

			return id;
		}

		boolean newFrame() {

			return newFrame;
		}
	}

	private Map<IFrame, String> ids = new HashMap<IFrame, String>();
	private Set<IFrame> assignedFrames = new HashSet<IFrame>();

	IFrameXDocIds() {

		this(new HashMap<IFrame, String>());
	}

	IFrameXDocIds(Map<IFrame, String> ids) {

		this.ids = ids;
	}

	Resolution resolve(IFrame frame) {

		String id = ids.get(frame);

		if (id == null) {

			id = createNew(frame);

			ids.put(frame, id);
		}

		return new Resolution(id, assignedFrames.add(frame));
	}

	private String createNew(IFrame frame) {

		String id = null;

		for (int i = 0 ; id == null ; i++) {

			id = tryCreateNew(i);
		}

		return id;
	}

	private String tryCreateNew(int index) {

		String id = create(index);

		return ids.containsKey(id) ? null : id;
	}

	private String create(int index) {

		return String.format(ID_FORMAT, ids.size() + index);
	}
}
