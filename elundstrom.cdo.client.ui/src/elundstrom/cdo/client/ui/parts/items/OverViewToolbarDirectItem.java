 
package elundstrom.cdo.client.ui.parts.items;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class OverViewToolbarDirectItem {
	@Execute
	public void execute() {
		System.out.println("Time to build a submenu here...");
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}