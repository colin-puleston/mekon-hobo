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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class IDiskStore implements IStore {

	private CModel model;

	private FileStore fileStore;
	private List<IMatcher> matchers = new ArrayList<IMatcher>();
	private IDirectMatcher defaultMatcher = new IDirectMatcher();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private InstanceIndexes indexes = new InstanceIndexes();

	public CModel getModel() {

		return model;
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);

		fileStore.write(instance, identity, index);
		checkAddToMatcher(instance, identity);

		return previous;
	}

	public synchronized boolean remove(CIdentity identity) {

		return checkRemove(identity) != null;
	}

	public synchronized void clear() {

		for (CIdentity identity : getAllIdentities()) {

			remove(identity);
		}
	}

	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	public synchronized IFrame get(CIdentity identity) {

		return fileStore.read(indexes.getIndex(identity), false);
	}

	public synchronized List<CIdentity> getAllIdentities() {

		return new ArrayList<CIdentity>(identities);
	}

	public synchronized IMatches match(IFrame query) {

		query = createFreeCopy(query);

		IMatches matches = getMatcher(query).match(query);

		indexes.ensureOriginalLabelsInMatches(matches);

		return matches;
	}

	public synchronized boolean matches(IFrame query, IFrame instance) {

		query = createFreeCopy(query);
		instance = createFreeCopy(instance);

		IMatcher matcher = getMatcher(query);

		if (matcher != getMatcher(instance)) {

			return false;
		}

		return matcher.matches(query, instance);
	}

	IDiskStore(CModel model) {

		this.model = model;

		fileStore = new FileStore(model, this);
	}

	void setStoreDirectory(File directory) {

		fileStore.setDirectory(directory);
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void removeMatcher(IMatcher matcher) {

		matchers.remove(matcher);
	}

	void insertMatcher(IMatcher matcher, int index) {

		matchers.add(index, matcher);
	}

	void replaceMatcher(IMatcher oldMatcher, IMatcher newMatcher) {

		matchers.set(matchers.indexOf(oldMatcher), newMatcher);
	}

	void initialisePostRegistration() {

		initialiseMatchers();

		fileStore.reloadAll();
		indexes.reinitialiseFreeIndexes();
	}

	void reload(InstanceProfile profile, int index) {

		CIdentity identity = profile.getIdentity();

		identities.add(identity);
		indexes.assignIndex(identity, index);

		IMatcher matcher = getMatcher(profile.getType());

		if (matcher.rebuildOnStartup()) {

			matcher.add(fileStore.read(index, true), identity);
		}
	}

	void stop() {

		for (IMatcher matcher : matchers) {

			matcher.stop();
		}

		matchers.clear();
	}

	List<IMatcher> getMatchers() {

		return new ArrayList<IMatcher>(matchers);
	}

	private void initialiseMatchers() {

		for (IMatcher matcher : matchers) {

			matcher.initialise(indexes);
		}
	}

	private IFrame checkRemove(CIdentity identity) {

		IFrame removed = null;

		if (indexes.hasIndex(identity)) {

			identities.remove(identity);

			int index = indexes.getIndex(identity);

			checkRemoveFromMatcher(fileStore.readType(index), identity);
			fileStore.remove(index);
			indexes.freeIndex(identity);
		}

		return removed;
	}

	private void checkAddToMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).add(createFreeCopy(instance), identity);
	}

	private void checkRemoveFromMatcher(CFrame type, CIdentity identity) {

		getMatcher(type).remove(identity);
	}

	private IMatcher getMatcher(IFrame frame) {

		return getMatcher(frame.getType());
	}

	private IMatcher getMatcher(CFrame frameType) {

		for (IMatcher matcher : matchers) {

			if (matcher.handlesType(frameType)) {

				return matcher;
			}
		}

		return defaultMatcher;
	}

	private IFrame createFreeCopy(IFrame instance) {

		return IFreeCopier.get().createFreeCopy(instance);
	}
}
