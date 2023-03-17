package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import java.awt.Color;
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
class TextEntityQueryInputter extends TextInputter<String> {

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

	static private final Color ENBLED_FIELD_COLOUR = Color.WHITE;
	static private final Color DISABLED_FIELD_COLOUR = UIManager.getColor("Panel.background");

	static private final int MIN_DISPLAYED_CONJUNCTS_COUNT = 3;
	static private final int MIN_DISPLAYED_DISJUNCTS_COUNT = 2;

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

					if (!valuePresent && redundantDisplay()) {

						resetExpressionDisplay();
					}

					disjunctAddButton.updateEnabling();
					expressionPanel.updateEnabling();
				}
			}

			DisjunctHandler(String disjunct) {

				super(TextEntityQueryInputter.this);

				if (!disjunct.isEmpty()) {

					setInitialValue(disjunct);
				}

				disjunctHandlers.add(this);
			}

			void setInitialValue(String disjunct) {

				setValueAsText(disjunct);

				valuePresent = true;
			}

			void setFieldEnabled(boolean enable) {

				getField().setEnabled(enable);
				getField().setBackground(getFieldColour(enable));
			}

			private Color getFieldColour(boolean enabled) {

				return enabled ? ENBLED_FIELD_COLOUR : DISABLED_FIELD_COLOUR;
			}
		}

		private class DisjunctAddButton extends GButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				shiftDisplayedDisjunctsCount(1);
				updateExpressionDisplay();
			}

			DisjunctAddButton() {

				super(DISJUNCT_ADD_LABEL);

				setHorizontalMargin(DISJUNCT_ADD_BUTTON_BORDER_SIZE);

				updateEnabling();
			}

			void updateEnabling() {

				setEnabled(allDisjunctFieldsPopulated());
			}
		}

		ConjunctPanel(TextDisjunction conjunct, int conjunctIndex) {

			super(new BorderLayout());

			this.conjunctIndex = conjunctIndex;

			createDisjunctHandlers(conjunct);

			disjunctAddButton = new DisjunctAddButton();

			add(createDisjunctsPanel(), BorderLayout.CENTER);
			add(disjunctAddButton, BorderLayout.EAST);
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

		boolean updateFieldEnabling(boolean allPreviousPopulated) {

			boolean populated = anyDisjunctFieldsPopulated();

			enableFields(populated || allPreviousPopulated);

			return populated && allPreviousPopulated;
		}

		boolean anyDisjunctFieldsPopulated() {

			for (DisjunctHandler handler : disjunctHandlers) {

				if (handler.hasTextValue()) {

					return true;
				}
			}

			return false;
		}

		private void createDisjunctHandlers(TextDisjunction conjunct) {

			Iterator<String> djs = conjunct.getDisjuncts().iterator();

			for (int i = 0 ; i < getDisplayedDisjunctsCount() ; i++) {

				new DisjunctHandler(djs.hasNext() ? djs.next() : "");
			}
		}

		private JPanel createDisjunctsPanel() {

			JPanel panel = new JPanel();
			int i = 0;

			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			for (DisjunctHandler handler : disjunctHandlers) {

				panel.add(createDisjunctPanel(handler, i++ == 0));
			}

			return panel;
		}

		private JPanel createDisjunctPanel(DisjunctHandler handler, boolean firstDisjunct) {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createDisjunctLabel(firstDisjunct), BorderLayout.WEST);
			panel.add(handler.getField(), BorderLayout.CENTER);

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

		private void enableFields(boolean allowEnabledUnpopulated) {

			boolean enabledUnpopulated = false;

			for (DisjunctHandler handler : disjunctHandlers) {

				boolean populated = handler.hasTextValue();

				handler.setFieldEnabled(populated || allowEnabledUnpopulated);

				allowEnabledUnpopulated &= populated;
			}
		}

		private boolean redundantDisplay() {

			return redundantDisjunctFields() || expressionPanel.redundantConjunctPanels();
		}

		private boolean redundantDisjunctFields() {

			return !minDisplayedDisjuncts() && multiUnpopulatedDisjunctFields();
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

		private boolean minDisplayedDisjuncts() {

			return getDisplayedDisjunctsCount() == MIN_DISPLAYED_DISJUNCTS_COUNT;
		}

		private void shiftDisplayedDisjunctsCount(int shiftBy) {

			setDisplayedDisjunctsCount(getDisplayedDisjunctsCount() + shiftBy);
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
				displayedDisjunctsCounts.add(MIN_DISPLAYED_DISJUNCTS_COUNT);

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

			updateEnabling();
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

		void updateEnabling() {

			conjunctAddButton.updateEnabling();

			updateFieldEnabling();
		}

		boolean redundantConjunctPanels() {

			return !minDisplayedConjuncts() && multiUnpopulatedConjunctPanels();
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

		private void updateFieldEnabling() {

			boolean allPopulated = true;

			for (ConjunctPanel conjunctPanel : conjunctPanels) {

				allPopulated = conjunctPanel.updateFieldEnabling(allPopulated);
			}
		}

		private boolean allConjunctPanelsPopulated() {

			return !unpopulatedConjunctPanels(1);
		}

		private boolean multiUnpopulatedConjunctPanels() {

			return unpopulatedConjunctPanels(2);
		}

		private boolean unpopulatedConjunctPanels(int required) {

			int count = 0;

			for (ConjunctPanel conjunctPanel : conjunctPanels) {

				if (!populatedConjunctPanel(conjunctPanel) && ++count == required) {

					return true;
				}
			}

			return false;
		}

		private boolean populatedConjunctPanel(ConjunctPanel conjunctPanel) {

			return conjunctPanel.anyDisjunctFieldsPopulated();
		}

		private boolean minDisplayedConjuncts() {

			return displayedConjunctsCount == MIN_DISPLAYED_CONJUNCTS_COUNT;
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

	TextEntityQueryInputter(JComponent parent, TextEntity currentValueObj) {

		super(parent, TITLE, true, true);

		setNewExpressionDisplay(getInitialExpression(currentValueObj));
	}

	private void resetExpressionDisplay() {

		displayedDisjunctsCounts.clear();

		setNewExpressionDisplay(expressionPanel.getCurrentExpression());
	}

	private void updateExpressionDisplay() {

		setExpressionDisplay(expressionPanel.getCurrentExpression());
	}

	private void setNewExpressionDisplay(TextExpression expression) {

		displayedConjunctsCount = getNewDisplayedConjunctsCount(expression);

		setNewDisplayedDisjunctsCounts(expression);
		setExpressionDisplay(expression);
	}

	private void setExpressionDisplay(TextExpression expression) {

		expressionPanel = new ExpressionPanel(expression);

		expressionScrollPane.getViewport().setView(expressionPanel);
	}

	private TextExpression getInitialExpression(TextEntity currentValueObj) {

		return currentValueObj != null
				? currentValueObj.getQueryExpression()
				: EMPTY_EXPRESSION;
	}

	private int getNewDisplayedConjunctsCount(TextExpression expression) {

		int inExpr = expression.getConjuncts().size();

		return Math.max(inExpr, MIN_DISPLAYED_CONJUNCTS_COUNT);
	}

	private void setNewDisplayedDisjunctsCounts(TextExpression expression) {

		Iterator<TextDisjunction> cjs = expression.getConjuncts().iterator();

		for (int i = 0 ; i < displayedConjunctsCount ; i++) {

			displayedDisjunctsCounts.add(getNewDisplayedDisjunctsCount(cjs));
		}
	}

	private int getNewDisplayedDisjunctsCount(Iterator<TextDisjunction> conjuncts) {

		if (conjuncts.hasNext()) {

			return getNewDisplayedDisjunctsCount(conjuncts.next());
		}

		return MIN_DISPLAYED_DISJUNCTS_COUNT;
	}

	private int getNewDisplayedDisjunctsCount(TextDisjunction conjunct) {

		int inConjunct = conjunct.getDisjuncts().size();

		return Math.max(inConjunct, MIN_DISPLAYED_DISJUNCTS_COUNT);
	}

	private void setComponentBorder(JComponent component, int size) {

		component.setBorder(new EmptyBorder(size, size, size, size));
	}
}
