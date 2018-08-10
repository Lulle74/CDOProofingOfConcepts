package elundstrom.cdo.client.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

public class ExitHandler {
    
    @Execute
	public void execute(IWorkbench workbench) {
    	System.out.println((this.getClass().getSimpleName() + " called"));
    	System.out.println("Commmand handler invoked - closing shit down");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		workbench.close();
	}
}