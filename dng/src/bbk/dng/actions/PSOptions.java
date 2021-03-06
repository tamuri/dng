/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PSOptions.java
 *
 * Created on 20-Jul-2010, 08:02:42
 */

package bbk.dng.actions;

import bbk.dng.ui.panels.AppFrame;

/**
 *
 * @author roman
 */
public class PSOptions extends javax.swing.JDialog {

  //
  // C O N S T A N T S
  //
  private boolean          cancelled = false;
  private boolean          landscape = false;

    /** Creates new form PSOptions */
    public PSOptions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

  public PSOptions(AppFrame appFrame, boolean modal, boolean landscape) {
    super(appFrame, modal);
    initComponents();

    // Save the default parameters
    this.landscape = landscape;

    // Set the radio buttons according to these parameter values
    setParameters();
  }

  /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    orientationButtonGroup = new javax.swing.ButtonGroup();
    headingLabel = new javax.swing.JLabel();
    portraitRadioButton = new javax.swing.JRadioButton();
    orientationLabel = new javax.swing.JLabel();
    landscapeRadioButton = new javax.swing.JRadioButton();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(bbk.dng.Main.class).getContext().getResourceMap(PSOptions.class);
    setTitle(resourceMap.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N

    headingLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
    headingLabel.setText(resourceMap.getString("headingLabel.text")); // NOI18N
    headingLabel.setName("headingLabel"); // NOI18N

    orientationButtonGroup.add(portraitRadioButton);
    portraitRadioButton.setFont(resourceMap.getFont("portraitRadioButton.font")); // NOI18N
    portraitRadioButton.setSelected(true);
    portraitRadioButton.setText(resourceMap.getString("portraitRadioButton.text")); // NOI18N
    portraitRadioButton.setName("portraitRadioButton"); // NOI18N

    orientationLabel.setFont(resourceMap.getFont("orientationLabel.font")); // NOI18N
    orientationLabel.setText(resourceMap.getString("orientationLabel.text")); // NOI18N
    orientationLabel.setName("orientationLabel"); // NOI18N

    orientationButtonGroup.add(landscapeRadioButton);
    landscapeRadioButton.setFont(resourceMap.getFont("landscapeRadioButton.font")); // NOI18N
    landscapeRadioButton.setText(resourceMap.getString("landscapeRadioButton.text")); // NOI18N
    landscapeRadioButton.setName("landscapeRadioButton"); // NOI18N

    okButton.setFont(resourceMap.getFont("okButton.font")); // NOI18N
    okButton.setText(resourceMap.getString("okButton.text")); // NOI18N
    okButton.setName("okButton"); // NOI18N
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });

    cancelButton.setFont(resourceMap.getFont("cancelButton.font")); // NOI18N
    cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
    cancelButton.setName("cancelButton"); // NOI18N
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(headingLabel)
              .addGroup(layout.createSequentialGroup()
                .addComponent(orientationLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(landscapeRadioButton)
                  .addComponent(portraitRadioButton)))))
          .addGroup(layout.createSequentialGroup()
            .addGap(49, 49, 49)
            .addComponent(okButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(cancelButton)))
        .addContainerGap(49, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(22, 22, 22)
        .addComponent(headingLabel)
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(orientationLabel)
          .addComponent(portraitRadioButton))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(landscapeRadioButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(okButton))
        .addGap(36, 36, 36))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

      // Unset cancelled flag
      cancelled = false;

      // Retrieve the settings of the radio buttons
      landscape = landscapeRadioButton.isSelected();

      // Close the window
      dispose();

    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
      // Cancel selected, so don't plot
      cancelled = true;

      // Close the window
      dispose();

    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PSOptions dialog = new PSOptions(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JLabel headingLabel;
  private javax.swing.JRadioButton landscapeRadioButton;
  private javax.swing.JButton okButton;
  private javax.swing.ButtonGroup orientationButtonGroup;
  private javax.swing.JLabel orientationLabel;
  private javax.swing.JRadioButton portraitRadioButton;
  // End of variables declaration//GEN-END:variables

  // Set all the radio buttons
  private void setParameters() {
    // Plot orientation
    landscapeRadioButton.setSelected(landscape);
    portraitRadioButton.setSelected(!landscape);
  }

  // Return whether dialogue was cancelled
  public boolean cancelled() {
    return cancelled;
  }

  // Return whether landscape orientation selected
  public boolean isLandscape() {
    return landscape;
  }

}
