package bbk;

import org.jdesktop.application.Task;
import org.jdesktop.application.Application;

/**
 * Date: 15-Aug-2008 18:28:23
 */
public class DoNothingTask extends Task<Void,Void> {

    public DoNothingTask(Application application) {
        super(application);
    }

    @Override protected Void doInBackground() throws InterruptedException {
        
        for(int i = 0; i < 10; i++) {
            System.out.printf("Working... [" + i + "]\n");
            Thread.sleep(150L);
            setProgress(i, 0, 9);
        }
        Thread.sleep(150L);
        return null;
    }
    
}
