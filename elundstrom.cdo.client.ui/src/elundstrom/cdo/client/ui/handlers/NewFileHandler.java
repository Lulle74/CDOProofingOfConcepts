/*
 * Created on 2003-mar-18
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package elundstrom.cdo.client.ui.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;

public class NewFileHandler {

	/**
	 * Ideally, we want to create a new MWindow here, and apply the app model.
	 * 
	 * @param application
	 */
	/*
	 * @Execute public void execute(MApplication application) { MWindow mWindow
	 * = MBasicFactory.INSTANCE.createTrimmedWindow(); mWindow.setX(0);
	 * mWindow.setY(0); mWindow.setHeight(200); mWindow.setWidth(400);
	 * mWindow.getChildren().add(MBasicFactory.INSTANCE.createPart());
	 * application.getChildren().add(mWindow); }
	 */

	/**
	 * But I will start out by cheating, and creating a new workspace.
	 * 
	 * @param application
	 */
	@Execute
	public void execute(MWindow window) {
		System.out.println("New File Handler, window is: " + window);
	}

	@CanExecute
	public boolean canExecute() {

		return true;
	}

}
