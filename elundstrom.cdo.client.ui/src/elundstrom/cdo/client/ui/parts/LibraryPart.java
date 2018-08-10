package elundstrom.cdo.client.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import elundstrom.cdo.client.ui.internal.session.SessionController;
import elundstrom.cdo.client.ui.internal.session.SessionController.CDOSessionEvent;
import elundstrom.cdo.client.ui.internal.session.SessionController.EventType;
import elundstrom.cdo.client.ui.internal.session.SessionController.ICDOSessionEventListener;
import library.Author;
import library.Book;
import library.Library;

public class LibraryPart implements ICDOSessionEventListener
{
	@Inject
	protected ESelectionService selectionService;

	@Inject
	protected UISynchronize uiThread;

	protected TreeViewer viewer = null;

	public LibraryPart()
	{
		System.out.println( "OverviewPart alive." );
	}

	@PostConstruct
	public void createControls( Composite parent, EMenuService menuService )
	{
		viewer = new TreeViewer( parent, SWT.MULTI );

		viewer.setLabelProvider( new LabelProvider() {
			@Override
			public String getText( Object element )
			{
				String label = "";
				if ( element instanceof Library ) {
					Library lib = (Library)element;
					label = "Library [" + lib.getListBook().size() + " books]";
				}
				else if ( element instanceof Book ) {
					Book book = (Book)element;
					label = "Book [" + book.getTitle() + "]";
				}
				else if ( element instanceof Author ) {
					Author author = (Author)element;
					label = "Author [" + author.getName() + " " + author.getSurname() + "]";
				}
				return label;
			}
		} );

		viewer.setContentProvider( new ITreeContentProvider() {

			@Override
			public Object[] getElements( Object inputElement )
			{
				List<EObject> rootElements = new ArrayList<>();
				if ( inputElement instanceof Resource ) {
					rootElements.addAll( ((Resource)inputElement).getContents() );
				}
				return rootElements.toArray();
			}

			@Override
			public boolean hasChildren( Object element )
			{
				return this.getChildren( element ).length > 0;
			}

			@Override
			public Object getParent( Object element )
			{
				Object parent = null;
				if ( element instanceof EObject ) {
					parent = ((EObject)element).eContainer();
				}
				return parent;
			}

			@Override
			public Object[] getChildren( Object parentElement )
			{
				List<EObject> children = new ArrayList<>();
				if ( parentElement instanceof Library ) {
					children.addAll( ((Library)parentElement).getListBook() );
					children.addAll( ((Library)parentElement).getListAuthor() );
				}
				return children.toArray();
			}
		} );

		viewer.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged( SelectionChangedEvent event )
			{
				ISelection viewerSelection = event.getSelection();
				Object selectionToPost = "<NONE>";
				if ( viewerSelection instanceof StructuredSelection ) {
					List<Object> allSelected = new ArrayList<>();
					allSelected.addAll( ((StructuredSelection)viewerSelection).toList() );
					selectionToPost = allSelected;
				}

				//Post selection to service
				LibraryPart.this.selectionService.setSelection( selectionToPost );
			}
		} );

//		CDOSession session = SessionController.getInstance().getSession( true );
//
//		Object input = null;
//		if ( session != null && !session.isClosed() ) {
//			CDOView view = session.openView();
//			CDOResource resource = view.getResource( "/myResource" );
//			if ( resource != null ) {
//				input = resource;
//			}
//		}
//		viewer.setInput( input );

		// register context menu on the table
		menuService.registerContextMenu( viewer.getControl(), "com.example.e4.rcp.todo.popupmenu.table" );
		
		SessionController.getInstance().addCDOSessionListener( this );
	}

	@PreDestroy
	void destroy()
	{
		SessionController.getInstance().removeCDOSessionListener( this );
	}

	@Override
	public void sessionEvent( CDOSessionEvent event )
	{
		if ( event.getEventType() == EventType.RECOVERY_STARTED || event.getEventType() == EventType.DEACTIVATED ) {
			//We are disconnected
			uiThread.asyncExec( new Runnable() {
				@Override
				public void run()
				{
					viewer.setInput( null );
				}
			} );

		}
		else if ( event.getEventType() == EventType.RECOVERY_FINISHED || event.getEventType() == EventType.ACTIVATED ) {
			final CDOSession theSession = event.getSession();
			//Online (again, if reconnecting/recovery)
			uiThread.asyncExec( new Runnable() {
				@Override
				public void run()
				{
					Object input = null;
					if ( theSession != null && !theSession.isClosed() ) {
						CDOView view = theSession.openView();
						CDOResource resource = view.getResource( "/myResource" );
						if ( resource != null ) {
							input = resource;
						}
					}
					viewer.setInput( input );
				}
			} );

		}

	}

}
