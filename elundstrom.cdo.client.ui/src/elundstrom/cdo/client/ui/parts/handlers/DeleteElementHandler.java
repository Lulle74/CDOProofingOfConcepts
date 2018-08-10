
package elundstrom.cdo.client.ui.parts.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import elundstrom.cdo.client.ui.internal.session.SessionController;
import library.Library;

public class DeleteElementHandler
{
	@Execute
	public void execute( ESelectionService selectionService, MPart part )
	{
		List<EObject> eObjects = this.getEObjects( selectionService, part );
		CDOSession cdoSession = SessionController.getInstance().getSession( );
		if ( cdoSession != null && !cdoSession.isClosed() ) {
			CDOTransaction transaction = cdoSession.openTransaction();

			for ( EObject eObject : eObjects ) {
				EObject eObjectInTransaction = transaction.getObject( eObject );
				if ( eObjectInTransaction.eContainer() != null ) {
					EcoreUtil.delete( eObjectInTransaction );
				}
				else if (eObjectInTransaction instanceof Library) {
					EcoreUtil.delete( eObjectInTransaction );
				}
			}

			try {
				transaction.commit();
			}
			catch ( ConcurrentAccessException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( CommitException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@CanExecute
	public boolean canExecute( ESelectionService selectionService, MPart part )
	{
		List<EObject> eObjects = this.getEObjects( selectionService, part );
		CDOSession cdoSession = SessionController.getInstance().getSession( );

		return (!eObjects.isEmpty() && (cdoSession != null && !cdoSession.isClosed()));
	}

	private List<EObject> getEObjects( ESelectionService selectionService, MPart part )
	{
		List<EObject> eObjects = new ArrayList<>();
		Object rawSelection = selectionService.getSelection( part.getElementId() );
		if ( rawSelection instanceof Collection ) {

			Collection stuff = (Collection)rawSelection;
			for ( Object eObject : stuff ) {
				if ( eObject instanceof EObject ) {
					eObjects.add( (EObject)eObject );
				}
			}
		}
		return eObjects;
	}

}