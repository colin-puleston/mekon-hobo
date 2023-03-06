package uk.ac.manchester.cs.hobo.user.app.basic;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockQueryInputter extends TextInputter<String> {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Query Expression";

	static private final String FIRST_CONJUNCT_LABEL = "CONTAINS";
	static private final String FOLLOWING_CONJUNCT_LABEL = "AND";
	static private final String FIRST_DISJUNCT_LABEL = "";
	static private final String FOLLOWING_DISJUNCT_LABEL = "OR";

	static private final String CONJUNCT_ADD_LABEL = "+ +";
	static private final String DISJUNCT_ADD_LABEL = "+";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 350);

	static private final int EXPRESSION_PANEL_BORDER_SIZE = 4;
	static private final int CONJUNCT_LABEL_BORDER_SIZE = 4;
	static private final int CONJUNCT_ADD_BUTTON_BORDER_SIZE = 0;
	static private final int CONJUNCT_ADD_BUTTON_SEPARATOR_SIZE = 10;
	static private final int DISJUNCT_LABEL_BORDER_SIZE = 4;
	static private final int DISJUNCT_ADD_BUTTON_BORDER_SIZE = 0;

	static private final int DEFAULT_DISPLAYED_CONJUNCTS_COUNT = 3;
	static private final int DEFAULT_DISPLAYED_DISJUNCTS_COUNT = 2;

	static private final TextExpression EMPTY_EXPRESSION = new TextExpression();
	static private final TextDisjunction EMPTY_DISJUNCTION = new TextDisjunction();

	private ExpressionPanel expressionPanel;
	private JScrollPane expressionScrollPane = new JScrollPane();

	private int displayedConjunctsCount;
	private List<Integer> displayedDisjunctsCounts = new ArrayList<Integer>();

	private class ConjunctPanel extends JPanel {

		static private final long serialVersionUID = -1;

		private int conjunctIndex;

		private List<DisjunctHandler> disjunctHandlers = new ArrayList<DisjunctHandler>();
		private DisjunctAddButton disjunctAddButton;

		private class DisjunctHandler extends TextInputHandler<String> {

			private boolean valuePresent = false;

			protected void onTextChange() {

				boolean nowValuePresent = hasTextValue();

				if (nowValuePresent != valuePresent) {

					valuePresent = nowValuePresent;

					if (!valuePresent) {

						checkPurgeRedundantDisjunctFields();
					}

					disjunctAddButton.updateEnabling();
					expressionPanel.updateButtonEnabling();
				}
			}

			void setInitialValue(String disjunct) {

				setValueAsText(disjunct);

				valuePresent = true;
			}
		}

		private class DisjunctAddButton extends GButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				incrementDisplayedDisjunctsCount();
				updateExpressionDisplay();
			}

			DisjunctAddButton() {

				super(DISJUNCT_ADD_LABEL);

				setHorizontalMargin(DISJUNCT_ADD_BUTTON_BORDER_SIZE);
			}

			void updateEnabling() {

				setEnabled(allDisjunctFieldsPopulated());
			}
		}

		ConjunctPanel(TextDisjunction conjunct, int conjunctIndex) {

			super(new BorderLayout());

			this.conjunctIndex = conjunctIndex;

			disjunctAddButton = new DisjunctAddButton();

			add(createDisjunctsPanel(conjunct), BorderLayout.CENTER);
			add(disjunctAddButton, BorderLayout.EAST);

			disjunctAddButton.updateEnabling();
		}

		TextDisjunction getCurrentConjunct() {

			TextDisjunction conjunct = new TextDisjunction();

			for (DisjunctHandler handler : disjunctHandlers) {

				String disjunct = handler.getValue();

				if (!disjunct.isEmpty()) {

					conjunct.addDisjunct(disjunct);
				}
			}

			return conjunct;
		}

		boolean anyDisjunctFieldsPopulated() {

			for (DisjunctHandler handler : disjunctHandlers) {

				if (handler.hasTextValue()) {

					return true;
				}
			}

			return false;
		}

		private JPanel createDisjunctsPanel(TextDisjunction conjunct) {

			JPanel panel = new JPanel();

			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			Iterator<String> djs = conjunct.getDisjuncts().iterator();

			for (int i = 0 ; i < getDisplayedDisjunctsCount() ; i++) {

				panel.add(createDisjunctPanel(djs.hasNext() ? djs.next() : "", i == 0));
			}

			return panel;
		}

		private JPanel createDisjunctPanel(String disjunct, boolean firstDisjunct) {

			JPanel panel = new JPanel(new BorderLayout());
			DisjunctHandler handler = new DisjunctHandler();

			disjunctHandlers.add(handler);

			panel.add(createDisjunctLabel(firstDisjunct), BorderLayout.WEST);
			panel.add(createInputField(handler), BorderLayout.CENTER);

			if (!disjunct.isEmpty()) {

				handler.setInitialValue(disjunct);
			}

			return panel;
		}

		private JLabel createDisjunctLabel(boolean firstDisjunct) {

			JLabel label = new JLabel(getDisjunctLabelText(firstDisjunct));

			setComponentBorder(label, DISJUNCT_LABEL_BORDER_SIZE);

			return label;
		}

		private String getDisjunctLabelText(boolean firstDisjunct) {

			return firstDisjunct ? FIRST_DISJUNCT_LABEL : FOLLOWING_DISJUNCT_LABEL;
		}

		private void checkPurgeRedundantDisjunctFields() {

			if (multiUnpopulatedDisjunctFields()) {

				removeAllUnpopulatedDisjunctFields();
				incrementDisplayedDisjunctsCount();

				updateExpressionDisplay();
			}
		}

		private void removeAllUnpopulatedDisjunctFields() {

			for (DisjunctHandler handler : new ArrayList<DisjunctHandler>(disjunctHandlers)) {

				if (!handler.hasTextValue()) {

					disjunctHandlers.remove(handler);
				}
			}

			setDisplayedDisjunctsCount(disjunctHandlers.size());
		}

		private boolean allDisjunctFieldsPopulated() {

			return !unpopulatedDisjunctFields(1);
		}

		private boolean multiUnpopulatedDisjunctFields() {

			return unpopulatedDisjunctFields(2);
		}

		private boolean unpopulatedDisjunctFields(int required) {

			int count = 0;

			for (DisjunctHandler handler : disjunctHandlers) {

				if (!handler.hasTextValue() && ++count == required) {

					return true;
				}
			}

			return false;
		}

		private void incrementDisplayedDisjunctsCount() {

			setDisplayedDisjunctsCount(getDisplayedDisjunctsCount() + 1);
		}

		private void setDisplayedDisjunctsCount(int count) {

			displayedDisjunctsCounts.set(conjunctIndex, count);
		}

		private int getDisplayedDisjunctsCount() {

			return displayedDisjunctsCounts.get(conjunctIndex);
		}
	}

	private class ExpressionPanel extends JPanel {

		static private final long serialVersionUID = -1;

		private List<ConjunctPanel> conjunctPanels = new ArrayList<ConjunctPanel>();
		private ConjunctAddButton conjunctAddButton = new ConjunctAddButton();

		private class ConjunctAddButton extends GButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				displayedConjunctsCount++;
				displayedDisjunctsCounts.add(DEFAULT_DISPLAYED_DISJUNCTS_COUNT);

				updateExpressionDisplay();
			}

			ConjunctAddButton() {

				super(CONJUNCT_ADD_LABEL);

				setVerticalMargin(CONJUNCT_ADD_BUTTON_BORDER_SIZE);
			}

			void updateEnabling() {

				setEnabled(allConjunctPanelsPopulated());
			}
		}

		ExpressionPanel(TextExpression expression) {

			setComponentBorder(this, EXPRESSION_PANEL_BORDER_SIZE);

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			Iterator<TextDisjunction> cjs = expression.getConjuncts().iterator();

			for (int i = 0 ; i < displayedConjunctsCount ; i++) {

				TextDisjunction cj = cjs.hasNext() ? cjs.next() : EMPTY_DISJUNCTION;

				add(createConjunctLabelPanel(i == 0));
				add(createConjunctPanel(cj, i));
			}

			add(Box.createVerticalStrut(CONJUNCT_ADD_BUTTON_SEPARATOR_SIZE));
			add(createConjunctAddButtonPanel());

			conjunctAddButton.updateEnabling();
		}

		void updateButtonEnabling() {

			conjunctAddButton.updateEnabling();
		}

		boolean currentExpression() {

			return conjunctPanels.size() > 1;
		}

		TextExpression getCurrentExpression() {

			TextExpression expression = new TextExpression();

			for (ConjunctPanel conjunctPanel : conjunctPanels) {

				TextDisjunction conjunct = conjunctPanel.getCurrentConjunct();

				if (conjunct.anyDisjuncts()) {

					expression.addConjunct(conjunct);
				}
			}

			return expression;
		}

		private ConjunctPanel createConjunctPanel(TextDisjunction conjunct, int index) {

			ConjunctPanel panel = new ConjunctPanel(conjunct, index);

			conjunctPanels.add(panel);

			return panel;
		}

		private JPanel createConjunctLabelPanel(boolean firstConjunct) {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createConjunctLabel(firstConjunct), BorderLayout.WEST);

			return panel;
		}

		private JPanel createConjunctAddButtonPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(conjunctAddButton, BorderLayout.SOUTH);

			return panel;
		}

		private JLabel createConjunctLabel(boolean firstConjunct) {

			JLabel label = new JLabel(getConjunctLabelText(firstConjunct));

			setComponentBorder(label, CONJUNCT_LABEL_BORDER_SIZE);
			GFonts.setMedium(label);

			return label;
		}

		private String getConjunctLabelText(boolean firstConjunct) {

			return firstConjunct ? FIRST_CONJUNCT_LABEL : FOLLOWING_CONJUNCT_LABEL;
		}

		private boolean allConjunctPanelsPopulated() {

			for (ConjunctPanel conjunctPanel : conjunctPanels) {

				if (!conjunctPanel.anyDisjunctFieldsPopulated()) {

					return false;
				}
			}

			return true;
		}
	}

	protected JComponent getInputComponent() {

		return expressionScrollPane;
	}

	protected Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	protected String resolveInput() {

		return expressionPanel.getCurrentExpression().toQueryString();
	}

	protected boolean validInputText(String text) {

		for (char c : TextExpression.RESERVED_QUERY_CHARS.toCharArray()) {

			if (text.indexOf(c) != -1) {

				return false;
			}
		}

		return true;
	}

	protected boolean potentiallyValidInputText(String text) {

		return validInputText(text);
	}

	protected String convertInputValue(String text) {

		return text;
	}

	protected boolean validCurrentInput() {

		return expressionPanel.currentExpression();
	}

	protected boolean validCompletedInput() {

		return expressionPanel.currentExpression();
	}

	TextBlockQueryInputter(JComponent parent, TextBlock currentValueObj) {

		super(parent, TITLE, true, true);

		TextExpression expression = getInitialExpression(currentValueObj);

		displayedConjunctsCount = getInitialDisplayedConjunctsCount(expression);

		setInitialDisplayedDisjunctsCounts(expression);
		setExpressionDisplay(expression);
	}

	private int getInitialDisplayedConjunctsCount(TextExpression expression) {

		int inExpr = expression.getConjuncts().size();

		return Math.max(inExpr, DEFAULT_DISPLAYED_CONJUNCTS_COUNT);
	}

	private void setInitialDisplayedDisjunctsCounts(TextExpression expression) {

		Iterator<TextDisjunction> cjs = expression.getConjuncts().iterator();

		for (int i = 0 ; i < displayedConjunctsCount ; i++) {

			int c = cjs.hasNext()
						? getInitialDisplayedDisjunctsCount(cjs.next())
						: DEFAULT_DISPLAYED_DISJUNCTS_COUNT;

			displayedDisjunctsCounts.add(c);
		}
	}

	private int getInitialDisplayedDisjunctsCount(TextDisjunction conjunct) {

		int inConjunct = conjunct.getDisjuncts().size();

		return Math.max(inConjunct, DEFAULT_DISPLAYED_DISJUNCTS_COUNT);
	}

	private void updateExpressionDisplay() {

		setExpressionDisplay(expressionPanel.getCurrentExpression());
	}

	private void setExpressionDisplay(TextExpression expression) {

		expressionPanel = new ExpressionPanel(expression);

		expressionScrollPane.getViewport().setView(expressionPanel);
	}

	private TextExpression getInitialExpression(TextBlock currentValueObj) {

		return currentValueObj != null
				? currentValueObj.getQueryExpression()
				: EMPTY_EXPRESSION;
	}

	private void setComponentBorder(JComponent component, int size) {

		component.setBorder(new EmptyBorder(size, size, size, size));
	}
}
