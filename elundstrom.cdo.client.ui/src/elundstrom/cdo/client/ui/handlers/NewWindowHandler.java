 
package elundstrom.cdo.client.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class NewWindowHandler {
	@Execute
	public void execute(MApplication application) {
		MWindow mWindow = MBasicFactory.INSTANCE.createTrimmedWindow();
		mWindow.setX(0);
		mWindow.setY(0);
		mWindow.setHeight(200);
		mWindow.setWidth(400);
		mWindow.getChildren().add(MBasicFactory.INSTANCE.createPart());
		application.getChildren().add(mWindow);
		}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}