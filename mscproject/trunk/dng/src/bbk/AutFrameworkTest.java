package bbk;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.EventObject;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 * Date: 15-Aug-2008 17:53:36
 */
public class AutFrameworkTest extends SingleFrameApplication {
    @Override
    protected void startup() {

        /*ExitListener confirmExit = new ExitListener() {
            public boolean canExit(EventObject e) {
                int option = JOptionPane.showConfirmDialog(null, "Really Exit?");
                return option == JOptionPane.YES_OPTION;
            }
            public void willExit(EventObject e) { }
        };
        addExitListener(confirmExit);
        */

        JLabel label = new JLabel();
        label.setName("myVariable");
        label.setFont(new Font("LucidaSans", Font.PLAIN, 32));

        JButton button = new JButton();
        button.setName("button");
        button.setAction(getAction("doNothing"));

        JPanel panel = new JPanel();
        panel.add( label );
        panel.add( button );

        show(panel);
    }


    @Action
    public void startCounting() {
        System.out.printf("1 2 3 4 5...");
    }

    @Action(block= Task.BlockingScope.ACTION)
    public DoNothingTask doNothing() {
        return new DoNothingTask(this);
    }


    private javax.swing.Action getAction(String actionName) {
        return getContext().getActionMap().get(actionName);
    }


    public static void main(String[] args) {
        Application.launch(AutFrameworkTest.class, args);
    }

}


