package bbk.dng.utils;

import javax.swing.*;
import java.util.Enumeration;

public class SwingUtils {

    public static JRadioButton getSelection(ButtonGroup group) {
        for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if (b.getModel() == group.getSelection()) {
                return b;
            }
        }
        return null;
    }

}
