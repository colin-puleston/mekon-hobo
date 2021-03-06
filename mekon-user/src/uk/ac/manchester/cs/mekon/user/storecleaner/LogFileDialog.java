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

package uk.ac.manchester.cs.mekon.user.storecleaner;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class LogFileDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String MAIN_TITLE = "Log file: Instance regeneration issues";
	static private final String FILE_LOCATION_TITLE = "File location: ";

	static private final Dimension WINDOW_SIZE = new Dimension(700, 700);

	LogFileDialog(JComponent parent, File file) {

		super(parent, MAIN_TITLE, true);

		setPreferredSize(WINDOW_SIZE);

		display(createMainPanel(file));
	}

	private JPanel createMainPanel(File file) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createContentsComponent(file), BorderLayout.CENTER);
		panel.add(createLocationComponent(file), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createContentsComponent(File file) {

		JTextArea area = new JTextArea();

		readContents(area, file);

		return createTextComponent(area);
	}

	private JComponent createLocationComponent(File file) {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(new TitledBorder(FILE_LOCATION_TITLE));
		panel.add(createPathComponent(file));

		return panel;
	}

	private JComponent createPathComponent(File file) {

		return createTextComponent(new JTextArea(file.getPath()));
	}

	private JComponent createTextComponent(JTextArea area) {

		setAreaFont(area);
		area.setEditable(false);

		return new JScrollPane(area);
	}

	private void setAreaFont(JTextArea area) {

		area.setFont(GFonts.toMedium(area.getFont()));
	}

	private void readContents(JTextArea area, File file) {

		try {

			BufferedReader reader = new BufferedReader(new FileReader(file));

			area.read(reader, null);
			reader.close();
		}
		catch (IOException e) {

			reportReadError(e.getMessage());
		}
	}

	private void reportReadError(String message) {

		JOptionPane.showMessageDialog(null, "Error reading log-file: " + message);
	}
}
