 
package elundstrom.cdo.client.ui.parts.items;

import org.eclipse.e4.core.di.annotations.Execute;

public class DirectMenuItem {
	@Execute
	public void execute() {
		System.out.println("Execute me, please");
	}
		
}