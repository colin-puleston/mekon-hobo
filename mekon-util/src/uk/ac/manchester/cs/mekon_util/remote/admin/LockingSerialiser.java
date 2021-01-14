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

package uk.ac.manchester.cs.mekon_util.remote.admin;

import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class LockingSerialiser {

	static private final String LOCK_TAG = "lock";

	static private final String OWNER_NAME_ATTR = "ownerName";
	static private final String RESOURCE_ID_ATTR = "resourceId";

	static void renderLocks(List<RLock> locks, XNode locksNode) {

		for (RLock lock : locks) {

			renderLock(lock, locksNode.addChild(LOCK_TAG));
		}
	}

	static void renderLock(RLock lock, XNode lockNode) {

		lockNode.setValue(OWNER_NAME_ATTR, lock.getOwnerName());
		lockNode.setValue(RESOURCE_ID_ATTR, lock.getResourceId());
	}

	static void renderLockingResult(RLockingResult result, XNode resultNode) {

		if (!result.lockObtained()) {

			resultNode.setValue(OWNER_NAME_ATTR, result.getCurrentOwnerName());
		}
	}

	static void renderLockResourceId(String resourceId, XNode idNode) {

		idNode.setValue(RESOURCE_ID_ATTR, resourceId);
	}

	static List<RLock> parseLocks(XNode locksNode) {

		List<RLock> locks = new ArrayList<RLock>();

		for (XNode lockNode : locksNode.getChildren(LOCK_TAG)) {

			locks.add(parseLock(lockNode));
		}

		return locks;
	}

	static RLock parseLock(XNode lockNode) {

		return new RLock(
					lockNode.getString(OWNER_NAME_ATTR),
					lockNode.getString(RESOURCE_ID_ATTR));
	}

	static RLockingResult parseLockingResult(XNode resultNode) {

		String currentOwner = resultNode.getString(OWNER_NAME_ATTR, null);

		return currentOwner != null
					? RLockingResult.currentlyLocked(currentOwner)
					: RLockingResult.LOCK_OBTAINED;
	}

	static String parseLockResourceId(XNode idNode) {

		return idNode.getString(RESOURCE_ID_ATTR);
	}
}
