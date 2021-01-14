/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.remote;

import java.util.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;

/**
 * @author Colin Puleston
 */
class LockManager extends EntityManager<RLock> {

	private RAdminClient adminClient;

	LockManager(LocksPanel panel, RAdminClient adminClient) {

		super(panel);

		this.adminClient = adminClient;
	}

	RLock addServerEntity() {

		throw new Error("Method should never be invoked!");
	}

	RLock editServerEntity(RLock lock) {

		throw new Error("Method should never be invoked!");
	}

	boolean deleteServerEntity(RLock lock) {

		adminClient.releaseLock(lock.getResourceId());

		return true;
	}

	List<RLock> getServerEntities() {

		return adminClient.getActiveLocks();
	}

	String describe(RLock lock) {

		return "lock for resource \"" + lock.getResourceId() + "\"";
	}

	String getSorterName(RLock lock) {

		return lock.getResourceId();
	}
}
