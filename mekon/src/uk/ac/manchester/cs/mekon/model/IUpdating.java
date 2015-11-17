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

	static private final Set<IUpdateOp> NO_OPS = Collections.<IUpdateOp>emptySet();

	private CModel model;

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
	 * Checks whether the specified update operation is one of the
	 * default operations.
	 *
	 * @param updateOp Relevant update operation
	 * @return True if update operation is a default operation
	 */
	public boolean defaultOp(IUpdateOp updateOp) {

		return defaultOps.contains(updateOp);
	}

	/**
	 * Specifies the default update operations that will be performed
	 * on instance-level frames.
	 *
	 * @return Relevant set of update operations
	 */
	public Set<IUpdateOp> getDefaultOps() {

		return new HashSet<IUpdateOp>(defaultOps);
	}

	IUpdating(CModel model) {

		this.model = model;
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

	Set<IUpdateOp> checkAutoUpdate(IFrame instance) {

		return autoUpdate ? update(instance) : NO_OPS;
	}

	Set<IUpdateOp> checkManualUpdate(IFrame instance) {

		return autoUpdate ? NO_OPS : update(instance);
	}

	Set<IUpdateOp> checkManualUpdate(IFrame instance, Set<IUpdateOp> ops) {

		if (autoUpdate) {

			ops = removeDefaultOps(ops);
		}

		return update(instance, ops);
	}

	Set<IUpdateOp> update(IFrame instance) {

		return update(instance, getDefaultOps());
	}

	Set<IUpdateOp> update(IFrame instance, Set<IUpdateOp> ops) {

		ops = purgeOpsForQuery(instance, ops);

		if (ops.isEmpty()) {

			return NO_OPS;
		}

		IReasoner reasoner = instance.getType().getIReasoner();

		return reasoner.updateFrame(model.getIEditor(), instance, ops);
	}

	private Set<IUpdateOp> removeDefaultOps(Set<IUpdateOp> ops) {

		Set<IUpdateOp> nonDefaultOps = new HashSet<IUpdateOp>(ops);

		nonDefaultOps.removeAll(defaultOps);

		return nonDefaultOps;
	}

	private Set<IUpdateOp> purgeOpsForQuery(IFrame instance, Set<IUpdateOp> ops) {

		Set<IUpdateOp> purgedOps = new HashSet<IUpdateOp>(ops);

		if (instance.getFunction().query()) {

			purgedOps.remove(IUpdateOp.SLOT_VALUES);
		}

		return purgedOps;
	}

	private Set<IUpdateOp> getAllUpdateOpsAsSet() {

		return new HashSet<IUpdateOp>(Arrays.asList(IUpdateOp.values()));
	}
}

