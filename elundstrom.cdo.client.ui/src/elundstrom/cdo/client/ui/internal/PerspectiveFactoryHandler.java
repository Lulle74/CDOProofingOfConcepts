package elundstrom.cdo.client.ui.internal;

import java.util.List;
import java.util.Objects;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MAdvancedFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.Selector;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Copies all snippet perspectives to perspective stack called
 * "MainPerspectiveStack" In order to register/reset perspective and not have to
 * sync two copies in e4xmi.
 * 
 */
public class PerspectiveFactoryHandler {
	private static final String PERSP_TODO = "com.example.e4.rcp.todo.perspective.todo"; //$NON-NLS-1$
	private static final String PERSP_TODO_STACK = "com.example.e4.rcp.todo.partstack.0";

	// For all part ids: same id is used for PartDescriptor, SharedElement,
	// Placeholder
	private static final String OVERVIEW_PART_ID = "com.example.e4.rcp.todo.part.overview"; //$NON-NLS-1$
	
	private static final String PLACEHOLDER_SUFFIX = ".ph";

	@Execute
	public void execute(EModelService modelService, EPartService partService,
			MApplication application/* , MWindow window */) {
		MWindow window = (MWindow) modelService.find(ModelProcessor.WINDOW_NAME, application); // $NON-NLS-1$
		if (window == null) {
			return;
		}
		MPerspective perspective = modelService.getActivePerspective(window);

		if (perspective == null) {
			return;
		}

		String perspId = perspective.getElementId();
		if (Objects.equals(perspId, PERSP_TODO)) {
			MPerspective todoPersp = perspective;
			// Part stacks should be well-defined in the Perspective definition
			// (in the snippet)
			List<MPlaceholder> placeholders = modelService.findElements(todoPersp, MPlaceholder.class, EModelService.ANYWHERE, new Selector() {
				
				@Override
				public boolean select(MApplicationElement element) {
					return element instanceof MPlaceholder;
				}
			});
			MPartStack stack = (MPartStack) modelService.find(PERSP_TODO_STACK, todoPersp);
			if (stack == null) {
				return;
			}
			// Find the placeholder!
			MPlaceholder overviewPH = (MPlaceholder) modelService.find(OVERVIEW_PART_ID + PLACEHOLDER_SUFFIX, todoPersp);
			//if (overviewPH == null) {
				// Need to create it!
				// First, get the Part. Is it created?
				MPart part = (MPart) modelService.find(OVERVIEW_PART_ID, window);
				if (part == null) {
					// Is it already in shared elements?
					for (MUIElement sharedPart : window.getSharedElements()) {
						if (Objects.equals(OVERVIEW_PART_ID, sharedPart.getElementId())) {
							part = (MPart) sharedPart;
							break;
						}
					}
					if (part == null) {
						// Create it from part descriptor!
						part = partService.createPart(OVERVIEW_PART_ID);
						if (part != null) {
							part.setElementId(OVERVIEW_PART_ID);
							// Add to shared elements
							window.getSharedElements().add(part);

							// Add it to a container
						}
					}

				}
				
				// The part exists now. 
				
				boolean created = false;
				if (overviewPH==null){
					created = true;
					//Now, create the placeholder.
					overviewPH = MAdvancedFactory.INSTANCE.createPlaceholder();
					overviewPH.setElementId(OVERVIEW_PART_ID + PLACEHOLDER_SUFFIX);
				}
				
				overviewPH.setRef(part);
				overviewPH.setToBeRendered(true);
				
				if (created){
					stack.getChildren().add(overviewPH);
				}

			// } else {
			//
			// }
		}

	}
}