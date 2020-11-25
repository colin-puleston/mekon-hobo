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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
class UserRegistrationInfoDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String MAIN_TITLE = "Registration Info";

	static private final String NAME_TITLE = "Name";
	static private final String ROLE_TITLE = "Role";
	static private final String REG_TOKEN_TITLE = "Registration Token";

	static private final Dimension WINDOW_SIZE = new Dimension(250, 130);

	UserRegistrationInfoDialog(JComponent parent, RUserProfile profile) {

		super(parent, MAIN_TITLE, true);

		setPreferredSize(WINDOW_SIZE);

		display(new JScrollPane(createInfoArea(profile)));
	}

	private JTextArea createInfoArea(RUserProfile profile) {

		JTextArea area = new JTextArea();

		configureInfoArea(area);

		addInfoLine(area, NAME_TITLE, profile.getName());
		addInfoLine(area, ROLE_TITLE, profile.getRoleName());
		addInfoLine(area, REG_TOKEN_TITLE, profile.getRegistrationToken());

		return area;
	}

	private void configureInfoArea(JTextArea area) {

		area.setEditable(false);
		area.setFont(GFonts.toMedium(area.getFont()));
	}

	private void addInfoLine(JTextArea area, String title, String value) {

		area.append(title + ": \"" + value + "\"\n");
	}
}
