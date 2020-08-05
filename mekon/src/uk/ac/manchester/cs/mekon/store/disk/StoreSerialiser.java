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
import java.nio.file.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class StoreSerialiser {

	private CModel model;

	private StoreStructure structure;
	private InstanceSerialiser instanceSerialiser;

	private StoreDirectory mainDirectory;
	private Map<String, StoreDirectory> subDirectories = new HashMap<String, StoreDirectory>();

	private List<StoreDirectory> allDirectories = new ArrayList<StoreDirectory>();

	StoreSerialiser(CModel model, StoreStructure structure) {

		this.model = model;
		this.structure = structure;

		instanceSerialiser = new InstanceSerialiser(model);
		mainDirectory = createStoreDirectory(structure.getMainDirectory());

		for (String subStoreName : structure.getSubStoreNames()) {

			subDirectories.put(subStoreName, createSubStoreDirectory(subStoreName));
		}
	}

	void write(IFrame instance, CIdentity identity, int index) {

		InstanceProfile profile = createProfile(instance, identity);
		StoreDirectory storeDir = selectStoreDirectory(instance);

		File pFile = storeDir.getProfileFile(index);
		File iFile = storeDir.getInstanceFile(index);

		ProfileSerialiser.render(profile, pFile);
		instanceSerialiser.render(instance, iFile);
	}

	IRegenInstance read(CIdentity identity, int index, boolean freeInstance) {

		File iFile = selectStoreDirectory(index).getInstanceFile(index);

		return instanceSerialiser.parse(identity, iFile, freeInstance);
	}

	CIdentity readTypeId(int index) {

		File pFile = selectStoreDirectory(index).getProfileFile(index);

		return ProfileSerialiser.parse(pFile).getTypeId();
	}

	void remove(int index) {

		selectStoreDirectory(index).remove(index);
	}

	void clear() {

		for (StoreDirectory directory : allDirectories) {

			directory.clear();
		}
	}

	List<InstanceProfile> resolveStoredProfiles() {

		List<InstanceProfile> profiles = new ArrayList<InstanceProfile>();
		Set<Integer> resolvedIndices = new HashSet<Integer>();

		for (StoreDirectory dir : allDirectories) {

			for (File pFile : dir.getAllProfileFiles()) {

				InstanceProfile profile = resolveStoredProfile(dir, pFile);

				if (resolvedIndices.add(profile.getIndex())) {

					profiles.add(profile);
				}
			}
		}

		return profiles;
	}

	private StoreDirectory createSubStoreDirectory(String subStoreName) {

		return createStoreDirectory(structure.getSubDirectory(subStoreName));
	}

	private StoreDirectory createStoreDirectory(File directory) {

		StoreDirectory serialiser = new StoreDirectory(directory);

		allDirectories.add(serialiser);

		return serialiser;
	}

	private InstanceProfile createProfile(IFrame instance, CIdentity identity) {

		CIdentity typeId = instance.getType().getIdentity();
		List<CIdentity> refedIds = instance.getAllReferenceIds();
		IFrameFunction function = instance.getFunction();

		return new InstanceProfile(identity, typeId, refedIds, function);
	}

	private InstanceProfile resolveStoredProfile(StoreDirectory dir, File pFile) {

		InstanceProfile profile = ProfileSerialiser.parse(pFile);
		int index = dir.getProfileFileIndex(pFile);

		profile.setIndex(index);

		StoreDirectory typeDir = selectStoreDirectory(profile);

		if (typeDir != dir) {

			File toDir = typeDir.getDirectory();

			moveDirectory(toDir, pFile);
			moveDirectory(toDir, dir.getInstanceFile(index));
		}

		return profile;
	}

	private void moveDirectory(File toDir, File fromFile) {

		File toFile = new File(toDir, fromFile.getName());

		try {

			Files.move(toPath(fromFile), toPath(toFile));
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}

	private Path toPath(File file) {

		return Paths.get(file.getPath());
	}

	private StoreDirectory selectStoreDirectory(IFrame instance) {

		return selectStoreDirectory(instance.getType(), instance.getFunction());
	}

	private StoreDirectory selectStoreDirectory(InstanceProfile profile) {

		CFrame type = getFrameType(profile.getTypeId());

		return selectStoreDirectory(type, profile.getFunction());
	}

	private StoreDirectory selectStoreDirectory(CFrame type, IFrameFunction function) {

		String subStoreName = structure.lookForSubStoreName(type, function);

		return subStoreName != null ? subDirectories.get(subStoreName) : mainDirectory;
	}

	private StoreDirectory selectStoreDirectory(int index) {

		for (StoreDirectory subDir : subDirectories.values()) {

			if (subDir.contains(index)) {

				return subDir;
			}
		}

		return mainDirectory;
	}

	private CFrame getFrameType(CIdentity identity) {

		return model.getFrames().get(identity);
	}
}
