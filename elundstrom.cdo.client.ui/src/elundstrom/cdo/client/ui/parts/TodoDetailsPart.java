package elundstrom.cdo.client.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import elundstrom.cdo.client.ui.internal.session.SessionController;
import elundstrom.cdo.client.ui.internal.session.SessionController.CDOSessionEvent;
import elundstrom.cdo.client.ui.internal.session.SessionController.EventType;
import elundstrom.cdo.client.ui.internal.session.SessionController.ICDOSessionEventListener;
import library.Author;
import library.Book;
import library.Library;
import library.LibraryFactory;

public class TodoDetailsPart implements ICDOSessionEventListener
{

	@Inject
	protected UISynchronize uiThread;

	private Button generateDefaultContentButton = null;

	private Text connType = null;
	private Text host = null;
	private Text cdoRepoName = null;

	private Label connStatus = null;
	private Button connectButton = null;

	public TodoDetailsPart()
	{
		System.out.println( "TodoDetailsPart alive." );
	}

	@PostConstruct
	public void createControls( Composite parent )
	{
		parent.setLayout( new GridLayout( 2, true ) );

		Label label = new Label( parent, SWT.NONE );
		label.setText( "Connection type" );
		label.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		this.connType = new Text( parent, SWT.BORDER );
		this.connType.setText( SessionController.DEFAULT_CONN_TYPE );
		this.connType.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		label = new Label( parent, SWT.NONE );
		label.setText( "Host" );
		label.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		this.host = new Text( parent, SWT.BORDER );
		this.host.setText( SessionController.DEFAULT_HOST );
		this.host.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		label = new Label( parent, SWT.NONE );
		label.setText( "Repo name" );
		label.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		this.cdoRepoName = new Text( parent, SWT.BORDER );
		this.cdoRepoName.setText( SessionController.DEFAULT_REPO );
		this.cdoRepoName.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		this.connStatus = new Label( parent, SWT.NONE );
		this.connStatus.setText( "Status: not connected to server." );
		this.connStatus.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		this.connectButton = new Button( parent, SWT.PUSH );
		this.connectButton.setText( "Connect" );
		this.connectButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		this.connectButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				CDOSession cdoSession = SessionController.getInstance().getSession( );
				if ( cdoSession == null || cdoSession.isClosed() ) {
					//Attempt to start a new session - may fail if not good params
					SessionController.getInstance().getSession( cdoRepoName.getText().trim(), connType.getText().trim(), host.getText().trim() );
				}
				else {
					try {
						System.out.println( "Closing session..." );
						cdoSession.close();
						System.out.println( "Session closed." );
						//cdoSession = null;
					}
					catch ( Exception e1 ) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				//updateUI( false );
			}
		} );

		Label generateLabel = new Label( parent, SWT.NONE );
		generateLabel.setText( "Generate some default contents " );
		generateLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		this.generateDefaultContentButton = new Button( parent, SWT.PUSH );
		this.generateDefaultContentButton.setText( "Generate" );
		this.generateDefaultContentButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		this.generateDefaultContentButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				CDOSession cdoSession = SessionController.getInstance().getSession( );
				try {

					if ( cdoSession == null ) {
						return;
					}
					CDOTransaction transaction = cdoSession.openTransaction();
					CDOResource resource = transaction.getOrCreateResource( "/myResource" );
					Library library = LibraryFactory.eINSTANCE.createLibrary();

					Book book = LibraryFactory.eINSTANCE.createBook();
					book.setTitle( "Eclipse Modeling Framework (2nd edition)" );
					library.getListBook().add( book );

					Author author = LibraryFactory.eINSTANCE.createAuthor();
					author.setName( "Ed" );
					author.setSurname( "Merks" );
					library.getListAuthor().add( author );
					book.getAuthors().add( author );

					author = LibraryFactory.eINSTANCE.createAuthor();
					author.setName( "Marcelo" );
					author.setSurname( "Paternostro" );
					library.getListAuthor().add( author );
					book.getAuthors().add( author );

					author = LibraryFactory.eINSTANCE.createAuthor();
					author.setName( "Frank" );
					author.setSurname( "Budinsky" );
					library.getListAuthor().add( author );
					book.getAuthors().add( author );

					author = LibraryFactory.eINSTANCE.createAuthor();
					author.setName( "David" );
					author.setSurname( "Steinberg" );
					library.getListAuthor().add( author );
					book.getAuthors().add( author );

					resource.getContents().add( library );
					transaction.commit();
					//cdoSession.close();

				}
				catch ( CommitException exc ) {
					exc.printStackTrace();
				}
				finally {
					//cdoSession.close();
				}
				//updateUI( false );
			}
		} );

		//this.updateUI( true );
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
					connStatus.setText( "Status: not connected to server." );
					connectButton.setText( "Connect" );
					generateDefaultContentButton.setEnabled( false );
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
					connStatus.setText( "Status: connected to server." );
					connectButton.setText( "Disconnect" );
					generateDefaultContentButton.setEnabled( true );
				}
			} );

		}

	}

//	void updateUI( boolean initial )
//	{
//		CDOSession cdoSession = initial ? SessionController.getInstance().getSession( true )
//				: SessionController.getInstance().getSession( false );
//
//		if ( cdoSession == null || cdoSession.isClosed() ) {
//			this.connStatus.setText( "Status: not connected to server." );
//			this.connectButton.setText( "Connect" );
//			this.generateDefaultContentButton.setEnabled( false );
//		}
//		else {
//			this.connStatus.setText( "Status: connected to server." );
//			this.connectButton.setText( "Disconnect" );
//			this.generateDefaultContentButton.setEnabled( true );
//		}
//	}

	// tracks the active part
	@Inject
	@Optional
	public void receiveActivePartyPooper( @Named( IServiceConstants.ACTIVE_PART ) MPart activePart,
			IEclipseContext context )
	{
		if ( activePart != null ) {
			// IEclipseContext activePartContext = activePart.getContext();
			// System.out.println("Are contexts the same? + " +
			// Objects.equals(context, activePartContext));
			//System.out.println( "Active part changed to " + activePart.getElementId() ); // +
			// ".
			// Former:
			// "
			// +
			// context.get("myactivePartId"));

			//context.set("myactivePartId", "com.example.e4.rcp.todo.part.todooverview");
		}
	}

	// tracks the active shell
	@Inject
	@Optional
	public void receiveActiveShell( @Named( IServiceConstants.ACTIVE_SHELL ) Shell shell )
	{
		if ( shell != null ) {
			// System.out.println("Active shell (Window) changed");
		}
	}
}
