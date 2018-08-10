
package elundstrom.cdo.client.ui.parts.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

public class MyCoolCommandHandler {
	private int counter = 0;

	@Execute
	public void execute(IWorkbench workbench) {
		System.out.println("Commmand handler invoked - closing shit down");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		workbench.close();
	}

	@CanExecute
	public boolean canDoIt() {
		System.out.println("Can exec invoked");
		this.counter++;
		boolean candoit = false;
		if (this.counter > 1) {
			candoit = true;
		}
		return candoit;

	}
}