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

package uk.ac.manchester.cs.mekon.user.storedoctor;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon_util.config.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class ConfigFileParser extends FSerialiser {

	static private final String STORE_DIR_ID = "StoreDirectory";
	static private final String MEKON_CONFIG_FILE_ID = "MekonConfigFile";
	static private final String CFRAME_DOCTOR_ID = "CFrameDoctor";
	static private final String ISLOT_DOCTOR_ID = "ISlotDoctor";
	static private final String ROOT_CONTAINER_TYPE_ID = "RootContainerType";
	static private final String UPDATES_ID = "Updates";
	static private final String VALUE_TYPE_UPDATE_ID = "ValueType";

	static private final String RESOURCE_PATH_ATTR = "path";
	static private final String PATH_FROM_CLASSPATH_ATTR = "pathFromClasspath";
	static private final String INCLUDE_SUB_DIRS_ATTR = "includeSubDirectories";

	private MekonStoreDoctor doctor;
	private XNode rootNode;

	private abstract class EntityDoctorParser<D extends EntityDoctor> {

		EntityDoctorParser() {

			for (XNode docNode : rootNode.getChildren(getTag())) {

				doctor.addEntityDoctor(parse(docNode));
			}
		}

		abstract String getTag();

		abstract D create(XNode docNode, String id);

		void parseUpdates(D doc, XNode updatesNode) {

			String id = updatesNode.getString(IDENTIFIER_ATTR, null);
			String label = updatesNode.getString(LABEL_ATTR, null);

			if (id != null) {

				doc.setNewId(id);
			}

			if (label != null) {

				doc.setNewLabel(label);
			}
		}

		private D parse(XNode docNode) {

			D doc = create(docNode);

			parseUpdates(doc, docNode.getChild(UPDATES_ID));

			return doc;
		}

		private D create(XNode docNode) {

			return create(docNode, docNode.getString(IDENTIFIER_ATTR));
		}
	}

	private class CFrameDoctorParser extends EntityDoctorParser<CFrameDoctor> {

		String getTag() {

			return CFRAME_DOCTOR_ID;
		}

		CFrameDoctor create(XNode docNode, String id) {

			return new CFrameDoctor(id);
		}
	}

	private class ISlotDoctorParser extends EntityDoctorParser<ISlotDoctor> {

		String getTag() {

			return ISLOT_DOCTOR_ID;
		}

		ISlotDoctor create(XNode docNode, String id) {

			return new ISlotDoctor(getRootContainerId(docNode), id);
		}

		void parseUpdates(ISlotDoctor doc, XNode updNode) {

			super.parseUpdates(doc, updNode);

			CCardinality card = updNode.getEnum(CARDINALITY_ATTR, CCardinality.class, null);
			CActivation actv = updNode.getEnum(ACTIVATION_ATTR, CActivation.class, null);
			IEditability edit = updNode.getEnum(EDITABILITY_ATTR, IEditability.class, null);

			if (card != null) {

				doc.setNewCardinality(card);
			}

			if (actv != null) {

				doc.setNewActivation(actv);
			}

			if (edit != null) {

				doc.setNewEditability(edit);
			}

			checkParseValueTypeUpdate(doc, updNode);
		}

		private void checkParseValueTypeUpdate(ISlotDoctor doc, XNode updNode) {

			XNode typeUpdNode = updNode.getChildOrNull(VALUE_TYPE_UPDATE_ID);

			if (typeUpdNode == null) {

				return;
			}

			XNode mFrameNode = typeUpdNode.getChildOrNull(MFRAME_ID);

			if (mFrameNode != null) {

				doc.setNewMFrameValueType(parseMFrameAsDisjunctIds(mFrameNode));

				return;
			}

			XNode cFrameNode = typeUpdNode.getChildOrNull(CFRAME_ID);

			if (cFrameNode != null) {

				doc.setNewCFrameValueType(parseCFrameAsDisjunctIds(cFrameNode));

				return;
			}

			XNode cNumberNode = typeUpdNode.getChildOrNull(CNUMBER_ID);

			if (cNumberNode != null) {

				doc.setNewCNumberValueType(parseCNumber(cNumberNode));

				return;
			}
		}

		private String getRootContainerId(XNode docNode) {

			return docNode.getChild(ROOT_CONTAINER_TYPE_ID).getString(IDENTIFIER_ATTR);
		}

		private CIdentity getIdentity(XNode node) {

			return new CIdentity(node.getString(IDENTIFIER_ATTR), node.getString(LABEL_ATTR));
		}
	}

	ConfigFileParser(MekonStoreDoctor doctor, File configFile) {

		this.doctor = doctor;

		rootNode = new XDocument(configFile).getRootNode();

		setStoreDirs();
		checkSetModel();

		new CFrameDoctorParser();
		new ISlotDoctorParser();
	}

	private void setStoreDirs() {

		XNode dirsNode = rootNode.getChild(STORE_DIR_ID);
		boolean includeSubs = dirsNode.getBoolean(INCLUDE_SUB_DIRS_ATTR, true);

		doctor.setStoreDir(getResource(dirsNode, true));
		doctor.setIncludeSubDirs(includeSubs);
	}

	private void checkSetModel() {

		File mekonCfgFile = lookForMekonConfigFile();

		if (mekonCfgFile != null) {

			doctor.setModel(mekonCfgFile);
		}
	}

	private File lookForMekonConfigFile() {

		XNode fileNode = rootNode.getChildOrNull(MEKON_CONFIG_FILE_ID);

		return fileNode != null ? getResource(fileNode, false) : null;
	}

	private File getResource(XNode node, boolean expectDir) {

		String path = node.getString(RESOURCE_PATH_ATTR);
		boolean fromClasspath = node.getBoolean(PATH_FROM_CLASSPATH_ATTR);

		return getResourceFinder(fromClasspath, expectDir).getResource(path);
	}

	private KConfigResourceFinder getResourceFinder(boolean fromClasspath, boolean expectDir) {

		if (fromClasspath) {

			return expectDir ? KConfigResourceFinder.DIRS : KConfigResourceFinder.FILES;
		}

		return new KConfigResourceFinder(expectDir);
	}
}
