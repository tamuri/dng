package bbk.dng.ui.panels;

import bbk.dng.Constants;
import bbk.dng.actions.SearchPanelActions;
import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
/*
 * Created by JFormDesigner on Tue Aug 19 17:33:06 BST 2008
 */

/**
 * @author SHOCKIE
 */
public class SequenceSearchBoxPanel extends JPanel {

  private JTextField textField1;
  private JButton button1;
  private JLabel label1;
  private JLabel label2;
  private JLabel label3;
  private JLabel label4;
  private JTextField textField2;
  private JTextField textField3;
  private JButton button2;
  private JLabel revUnrevOptionsLabel;
  private JRadioButton allUniProtRadioButton;
  private JRadioButton reviewedUniProtOnlyRadioButton;
  private ButtonGroup reviewedUnreviewedButtonGroup;

  public SequenceSearchBoxPanel() {
    initComponents();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner Open Source Project license - unknown
    label1 = new JLabel();
    textField1 = new JTextField();
    button1 = new JButton();
    /* RAL 14 Jul 09 --> */
    label2 = new JLabel();
    label3 = new JLabel();
    label4 = new JLabel();
    textField2 = new JTextField();
    textField3 = new JTextField();
    button2 = new JButton();
    JLabel dummyLabel1 = new JLabel();
    JLabel dummyLabel2 = new JLabel();
    /* <-- RAL 14 Jul 09 */
    revUnrevOptionsLabel = new JLabel();
    allUniProtRadioButton = new JRadioButton();
    reviewedUniProtOnlyRadioButton = new JRadioButton();
    reviewedUnreviewedButtonGroup = new ButtonGroup();

    //======== this ========
    setBorder(Borders.TABBED_DIALOG_BORDER);
    setLayout(new GridBagLayout());
    ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0, 0, 0};
    ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0};
    ((GridBagLayout) getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
    ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

    //---- label1 ----
    /* RAL 9 Jul 09 -->
    label1.setText("Sequence:"); */
    label1.setText("UniProt code/id:");
    /* <-- RAL 9 Jul 09 */
    add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    //textField1.setText("A0AZ79");
    add(textField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    //---- button1 ----
    button1.setText("Search");
    add(button1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    // JFormDesigner - End of component initialization  //GEN-END:initComponents

    /* RAL 14 Jul 09 --> */
    //---- label3 ----
    label3.setText("<html><b>or</b>");
    add(label3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 10, 0, 5), 0, 0));

    //---- label2 ----
    label2.setText("Pfam identifier:");
    add(label2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    add(textField2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    //---- button2 ----
    button2.setText("Search");
    add(button2, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    //---- dummy label ----
    dummyLabel1.setText(" ");
    add(dummyLabel1, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    //---- label3 ----
    label4.setText("Max architectures:");
    add(label4, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    textField3.setText("" + Constants.MAX_ARCHITECTURES);
    add(textField3, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 3), 0, 0));
  /* <-- RAL 14 Jul 09 */

    dummyLabel2.setText(" ");
    add(dummyLabel2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

    // Display plot options label
    revUnrevOptionsLabel.setName("revUnrevOptionsLabel");
    revUnrevOptionsLabel.setFont(new Font("Serif", Font.BOLD, 14));
    revUnrevOptionsLabel.setText("Reviewed/unreviewed sequences");

    add(revUnrevOptionsLabel, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 0, 5, 0), 0, 0));

    // Put reviewed/unreviewed buttons into same button group
    reviewedUnreviewedButtonGroup.add(allUniProtRadioButton);
    reviewedUnreviewedButtonGroup.add(reviewedUniProtOnlyRadioButton);

    // Initialise buttons
    allUniProtRadioButton.setName("allUniProtRadioButton");
    allUniProtRadioButton.setText("All UniProt sequences");
    allUniProtRadioButton.setSelected(true);

    reviewedUniProtOnlyRadioButton.setName("reviewedUniProtOnlyRadioButton");
    reviewedUniProtOnlyRadioButton.setText("Reviewed UniProt sequences only");


    // Add label and radio buttons to the panel
    add(allUniProtRadioButton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 5, 0, 0), 0, 0));
    add(reviewedUniProtOnlyRadioButton, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 5, 0, 0), 0, 0));
  }
  /* RAL 14 Jul 09 --> */
  /* <-- RAL 14 Jul 09 */
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  public JButton getSearchButton() {
    return button1;
  }

  public JTextField getSequenceTextField() {
    return textField1;
  }

  /* RAL 14 Jul 09 --> */
  public JButton getPfamSearchButton() {
    return button2;
  }

  public JTextField getPfamIdTextField() {
    return textField2;
  }

  public JRadioButton getAllUniProtRadioButton() {
    return allUniProtRadioButton;
  }

  public JRadioButton getReviewedUniProtOnlyRadioButton() {
    return reviewedUniProtOnlyRadioButton;
  }

  public int getMaxArchitectures() {
    int maxArchitectures = 0;

    try {
      maxArchitectures = Integer.parseInt(textField3.getText());
    } catch (Exception e) {
      maxArchitectures = 0;
    }
    return maxArchitectures;
  }

  public void setMaxArchitectures(int maxArchitectures) {
    textField3.setText("" + maxArchitectures);
  }

  public void setLabel2(int domainType) {
    if (domainType == SearchPanelActions.PFAM) {
      label2.setText("Pfam identifier:");
    } else {
      label2.setText("CATH identifier:");
    }
  }
  /* <-- RAL 14 Jul 09 */
}
