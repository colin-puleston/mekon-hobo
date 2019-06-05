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
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.regen.zlink.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class IDiskStore implements IStore {

	static private final ZIRegenAccessor regenAccessor = ZIRegenAccessor.get();

	private CModel model;

	private FileStore fileStore;
	private List<IMatcher> matchers;
	private IDirectMatcher defaultMatcher = new IDirectMatcher();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IRegenType> types = new HashMap<CIdentity, IRegenType>();
	private InstanceIndexes indexes = new InstanceIndexes();

	public CModel getModel() {

		return model;
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);
		types.put(identity, createRegenType(instance));

		fileStore.write(instance, identity, index);
		checkAddToMatcher(instance, identity);

		return previous;
	}

	public synchronized boolean remove(CIdentity identity) {

		return checkRemove(identity) != null;
	}

	public synchronized boolean clear() {

		if (identities.isEmpty()) {

			return false;
		}

		for (CIdentity identity : getAllIdentities()) {

			remove(identity);
		}

		return true;
	}

	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	public synchronized IRegenType getType(CIdentity identity) {

		return types.get(identity);
	}

	public synchronized IRegenInstance get(CIdentity identity) {

		if (indexes.hasIndex(identity)) {

			return fileStore.read(identity, indexes.getIndex(identity), false);
		}

		return null;
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

		this(model, new ArrayList<IMatcher>(), null);
	}

	IDiskStore(CModel model, List<IMatcher> matchers, File directory) {

		this.model = model;
		this.matchers = matchers;

		fileStore = new FileStore(model, directory);
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void initialisePostRegistration() {

		initialiseMatchers();

		for (StoredProfile storedProfile : fileStore.getStoredProfiles()) {

			reload(storedProfile.getProfile(), storedProfile.getIndex());
		}

		indexes.reinitialiseFreeIndexes();
	}

	void stop() {

		for (IMatcher matcher : matchers) {

			matcher.stop();
		}

		matchers.clear();
	}

	private void initialiseMatchers() {

		for (IMatcher matcher : matchers) {

			matcher.initialise(indexes);
		}
	}

	private void reload(InstanceProfile profile, int index) {

		CIdentity identity = profile.getInstanceId();
		IRegenType type = createRegenType(profile.getTypeId());

		identities.add(identity);
		types.put(identity, type);
		indexes.assignIndex(identity, index);

		if (type.validRootType()) {

			IMatcher matcher = getMatcher(type.getRootType());

			if (matcher.rebuildOnStartup()) {

				IRegenInstance regen = fileStore.read(identity, index, true);

				matcher.add(regen.getRootFrame(), identity);
			}
		}
	}

	private IFrame checkRemove(CIdentity identity) {

		IFrame removed = null;

		if (indexes.hasIndex(identity)) {

			identities.remove(identity);
			types.remove(identity);

			int index = indexes.getIndex(identity);
			CFrame type = model.getFrames().get(fileStore.readTypeId(index));

			checkRemoveFromMatcher(type, identity);
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

	private IRegenType createRegenType(IFrame instance) {

		CFrame type = instance.getType();

		return regenAccessor.createRegenType(type.getIdentity(), type);
	}

	private IRegenType createRegenType(CIdentity typeId) {

		CFrame type = model.getFrames().getOrNull(typeId);

		return regenAccessor.createRegenType(typeId, type);
	}

	private IFrame createFreeCopy(IFrame instance) {

		return IFreeCopier.get().createFreeCopy(instance);
	}
}
