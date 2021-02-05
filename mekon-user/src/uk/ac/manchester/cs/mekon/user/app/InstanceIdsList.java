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

package uk.ac.manchester.cs.mekon.user.app;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceIdsList extends GList<CIdentity> {

	static private final long serialVersionUID = -1;

	static private final String TYPE_ENHANCED_LABEL_FORMAT = "[%s] %s";

	private InstanceGroup group;

	InstanceIdsList(InstanceGroup group) {

		super(false, true);

		this.group = group;
	}

	void update(Collection<CIdentity> ids) {

		clearList();
		addIds(ids);
	}

	void updateDisplay() {

		update(getEntities());
	}

	void addIds(Collection<CIdentity> ids) {

		for (CIdentity id : ids) {

			addId(id);
		}
	}

	void addId(CIdentity id) {

		addEntity(id, getCellDisplay(id));
	}

	void checkAddId(CIdentity id) {

		if (!containsEntity(id)) {

			addId(id);
		}
	}

	void replaceId(CIdentity id, CIdentity newId) {

		removeEntity(id);
		addId(newId);
	}

	GCellDisplay getCellDisplay(CIdentity id) {

		if (displayTypes()) {

			return createTypeEnhancedCellDisplay(id);
		}

		return createCellDisplay(id, id.getLabel());
	}

	abstract Icon getInstanceIcon();

	boolean displayInstanceInBold(CIdentity id) {

		return false;
	}

	private GCellDisplay createTypeEnhancedCellDisplay(CIdentity id) {

		GCellDisplay display = createCellDisplay(id, createTypeEnhancedLabel(id));

		display.setFilterText(id.getLabel());

		return display;
	}

	private GCellDisplay createCellDisplay(CIdentity id, String label) {

		GCellDisplay display = new GCellDisplay(label, getInstanceIcon());

		if (displayInstanceInBold(id)) {

			display.setFontStyle(Font.BOLD);
		}

		return display;
	}

	private String createTypeEnhancedLabel(CIdentity id) {

		return String.format(TYPE_ENHANCED_LABEL_FORMAT, getTypeLabel(id), id.getLabel());
	}

	private String getTypeLabel(CIdentity id) {

		return group.getType(id).getIdentity().getLabel();
	}

	private boolean displayTypes() {

		return !group.getRootType().getSubs(CVisibility.EXPOSED).isEmpty();
	}
}