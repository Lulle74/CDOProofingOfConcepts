package elundstrom.cdo.client.ui.handlers;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;

public class SaveAllHandler {
	@Inject
	protected ESelectionService selectionService;

	// private boolean assignedListener = false;
	//private ISelectionListener listener = null;
	private boolean canExecuteDueToSelection = false;

//	@PostConstruct
//	public void postConstruct() {
//		System.out.println((this.getClass().getSimpleName()
//				+ ".postConstruct. Assigning selection listener to Selection Service."));
//		this.listener = new MySelectionListener();
//		// this.selectionService.addSelectionListener(this.listener);
//	}
//
//	@PreDestroy
//	public void preDestroy() {
//		System.out.println(
//				(this.getClass().getSimpleName() + ".preDestroy. Kill off selection listener to Selection Service."));
//		// this.selectionService.removeSelectionListener(this.listener);
//		this.listener = null;
//	}

	@Execute
	public void execute() {
		System.out.println((this.getClass().getSimpleName() + " called - selection checks ok!"));
	}

	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		// if (!this.assignedListener) {
		// if (this.selectionService != null) {
		// this.selectionService.addSelectionListener(new
		// MySelectionListener());
		// this.assignedListener = true;
		// }
		// }
		boolean canExecute = false;
		// String activePartId = (String) context.get("myactivePartId");
		// if (Objects.equals("com.example.e4.rcp.todo.part.todooverview",
		// activePartId)) {
		// canExecute = true;
		// }
		System.out.println("Can exec invoked in SaveAll. We're giving " + this.canExecuteDueToSelection);
		return this.canExecuteDueToSelection;
		// return canExecute;
	}

	class MySelectionListener implements ISelectionListener {
		@Override
		public void selectionChanged(MPart part, Object selection) {
			SaveAllHandler.this.canExecuteDueToSelection = false;
			if (part != null && Objects.equals(part.getElementId(), "com.example.e4.rcp.todo.part.todooverview")) {
				if (selection != null) {
					SaveAllHandler.this.canExecuteDueToSelection = true;
				}
			}
		}
	}

	@Inject
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection,
			@Named(IServiceConstants.ACTIVE_PART) MPart activePart) {
		//System.out.println("SetSelection. From active part: " + (activePart!=null ? activePart.getElementId() : "null") + " , selection is : " +selection);

		// For instance, say "ok" if part is the overview AND we have a nonempty
		// selection
		this.canExecuteDueToSelection = false;
		if (activePart != null
				&& Objects.equals(activePart.getElementId(), "com.example.e4.rcp.todo.part.todooverview")) {
			if (selection != null) {
				this.canExecuteDueToSelection = true;
			}
		}
	}

}