package elundstrom.cdo.client.ui.internal;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Copies all snippet perspectives to perspective stack called
 * "MainPerspectiveStack" In order to register/reset perspective and not have to
 * sync two copies in e4xmi.
 * 
 */
public class ModelProcessor {
	public static final String WINDOW_NAME = "com.example.e4.rcp.todo.trimmedwindow.taskmanager"; //$NON-NLS-1$
	public static final String WINDOW_PERSPECTIVE_STACK_NAME = "com.example.e4.rcp.todo.perspectivestack.0"; //$NON-NLS-1$

	@Execute
	public void execute(EModelService modelService, EPartService partService, MApplication application,
			IEclipseContext context) {
		MWindow existingFromLoad = (MWindow) modelService.find(WINDOW_NAME, application);
		if (existingFromLoad!=null){
			System.out.println("So the model reconciler did the job. Not creating...");
			return;
		}
		
		// The window will not exist as we get here - it is defined as a
		// "snippet".
		MWindow window = (MWindow) modelService.cloneSnippet(application, WINDOW_NAME, null); // $NON-NLS-1$
		if (window == null) {
			return;
		}

		MPerspectiveStack perspectiveStack = (MPerspectiveStack) modelService.find(WINDOW_PERSPECTIVE_STACK_NAME,
				window); // $NON-NLS-1$

		// clone each snippet that is a perspective and add the cloned
		// perspective into the main PerspectiveStack
		MPerspective perspToSelect = null;
		for (MUIElement snippet : application.getSnippets()) {
			if (snippet instanceof MPerspective) {
				MPerspective perspectiveClone = (MPerspective) modelService.cloneSnippet(application,
						snippet.getElementId(), null);
				perspectiveStack.getChildren().add(perspectiveClone);
				if (perspToSelect == null) {
					perspToSelect = perspectiveClone;
				}
			}
		}

		application.getChildren().add(window);
		// modelService.bringToTop( window );

		if (perspToSelect != null) {
			perspectiveStack.setSelectedElement(perspToSelect);
			// or: ?
			// partService.switchPerspective(perspToSelect);

			// Run our "perspective factory"
			// PerspectiveFactoryHandler handler =
			// ContextInjectionFactory.make(PerspectiveFactoryHandler.class,
			// context);
			// ContextInjectionFactory.invoke(handler, Execute.class, context);
			// Now the new perspective copy should be ok!
		}
	}

}