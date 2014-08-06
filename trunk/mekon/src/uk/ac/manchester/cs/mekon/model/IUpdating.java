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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents configuration information concerning the nature
 * of the updates to be performed on instance-level frames as
 * the result of reasoning.
 *
 * @author Colin Puleston
 */
public class IUpdating {

	private IEditor iEditor;

	private boolean autoUpdate = true;
	private Set<IUpdateOp> defaultOps = getAllUpdateOpsAsSet();

	/**
	 * Specifies whether automatic updating of instance-level
	 * frames will occur.
	 *
	 * @return True if auto-update is enabled
	 */
	public boolean autoUpdate() {

		return autoUpdate;
	}

	/**
	 * Checks whether the specified update operation is one of
	 * the default operations.
	 *
	 * @param Relevant update operation
	 * @return True if update operation is a default operation
	 */
	public boolean defaultOp(IUpdateOp updateOp) {

		return defaultOps.contains(updateOp);
	}

	/**
	 * Specifies the default of update operations that will be
	 * performed on instance-level frames.
	 *
	 * @return Relevant set of update operations
	 */
	public Set<IUpdateOp> getDefaultOps() {

		return new HashSet<IUpdateOp>(defaultOps);
	}

	IUpdating(IEditor iEditor) {

		this.iEditor = iEditor;
	}

	void setAutoUpdate(boolean autoUpdate) {

		this.autoUpdate = autoUpdate;
	}

	void setDefaultOp(IUpdateOp op, boolean enabled) {

		if (enabled) {

			defaultOps.add(op);
		}
		else {

			defaultOps.remove(op);
		}
	}

	void checkAutoUpdate(IFrame instance) {

		if (autoUpdate) {

			checkUpdateDefault(instance);
		}
	}

	void checkManualUpdate(IFrame instance) {

		if (!autoUpdate) {

			checkUpdateDefault(instance);
		}
	}

	void checkManualUpdate(IFrame instance, Set<IUpdateOp> ops) {

		if (!autoUpdate) {

			checkUpdate(instance, getNonDefaultOps(ops));
		}
	}

	private void checkUpdateDefault(IFrame instance) {

		checkUpdate(instance, getDefaultOps());
	}

	private void checkUpdate(IFrame instance, Set<IUpdateOp> ops) {

		purgeOpsForQuery(instance, ops);

		if (!ops.isEmpty()) {

			IReasoner reasoner = instance.getType().getIReasoner();

			reasoner.updateFrame(iEditor, instance, ops);
		}
	}

	private void purgeOpsForQuery(IFrame instance, Set<IUpdateOp> ops) {

		if (instance.queryInstance()) {

			ops.remove(IUpdateOp.SLOT_VALUES);
		}
	}

	private Set<IUpdateOp> getNonDefaultOps(Set<IUpdateOp> ops) {

		Set<IUpdateOp> nonDefaultOps = new HashSet<IUpdateOp>(ops);

		nonDefaultOps.removeAll(defaultOps);

		return nonDefaultOps;
	}

	private Set<IUpdateOp> getAllUpdateOpsAsSet() {

		return new HashSet<IUpdateOp>(Arrays.asList(IUpdateOp.values()));
	}
}
