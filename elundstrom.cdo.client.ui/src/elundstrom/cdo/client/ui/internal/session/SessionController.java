package elundstrom.cdo.client.ui.internal.session;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.internal.net4j.ReconnectingCDOSessionImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.net4j.ReconnectingCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;

public class SessionController
{
	private CDOSession cdoSession;
	public static String DEFAULT_CONN_TYPE = "tcp";
	public static String DEFAULT_HOST = "localhost";
	public static String DEFAULT_REPO = "marcus";

	private static SessionController INSTANCE = null;

	/**
	 * List of registered menu listeners (element type: <code>IMenuListener</code>).
	 */
	private ListenerList<ICDOSessionEventListener> listeners = new ListenerList<>();

	private IListener sessionListener = null;

	public static SessionController getInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new SessionController( true );
		}
		return INSTANCE;
	}

	private SessionController( boolean createSessionWithDefaultParams )
	{
		this.sessionListener = new Listener();
		if ( createSessionWithDefaultParams ) {
			System.out.println( "Creating session with default params..." );
			cdoSession = createSession( DEFAULT_REPO, DEFAULT_CONN_TYPE, DEFAULT_HOST );
			System.out.println( "Session created!" );
		}
	}

	/**
	 * The listener added will receive "ACTIVATED" or "DEACTIVATED" event upon adding themselves.
	 * 
	 * @param listener
	 */
	public void addCDOSessionListener( ICDOSessionEventListener listener )
	{
		listeners.add( listener );
		if ( listener != null && this.cdoSession != null ) {
			if ( this.cdoSession.isClosed() ) {
				SessionController.this.fireCDOSessionEvent( new CDOSessionEvent( this.cdoSession, EventType.DEACTIVATED ) );
			}
			else {
				SessionController.this.fireCDOSessionEvent( new CDOSessionEvent( this.cdoSession, EventType.ACTIVATED ) );
			}
		}
	}

	public void removeCDOSessionListener( ICDOSessionEventListener listener )
	{
		listeners.remove( listener );
	}

	private void fireCDOSessionEvent( CDOSessionEvent event )
	{
		for ( ICDOSessionEventListener listener : this.listeners ) {
			listener.sessionEvent( event );
		}
	}

	/**
	 * Gets the current session.
	 * @return
	 */
	public CDOSession getSession()
	{
		if ( cdoSession == null || cdoSession.isClosed() ) {
//			if ( cdoSession != null ) {
//				//Remove the listener
//				cdoSession.removeListener( this.sessionListener );
//			}
//			if ( attemptConnectWithDefaults ) {
//				cdoSession = createSession( DEFAULT_REPO, DEFAULT_CONN_TYPE, DEFAULT_HOST );
//			}
		}
		return cdoSession;
	}

	public CDOSession getSession( String repoName, String connectionType, String hostString )
	{
		if ( cdoSession != null ) {
			if ( cdoSession != null ) {
				cdoSession.close();
				//Remove the listener
				cdoSession.removeListener( this.sessionListener );
			}
		}
		cdoSession = this.createSession( repoName, connectionType, hostString );
		return cdoSession;
	}

	private CDOSession createSession( String repoName, String connectionType, String hostString )
	{
		CDOSession newSession = null;
		try {
			System.out.println( "Creating session..." );
			//newSession = createDefaultSession( repoName, connectionType, hostString );
			newSession = createReconnectingSession( repoName, connectionType, hostString );
			if ( !newSession.isClosed() ) {
				SessionController.this.fireCDOSessionEvent( new CDOSessionEvent( newSession, EventType.ACTIVATED ) );
			}
			System.out.println( "New Session created!" );
		}
		catch ( Exception e1 ) {
			System.out.println( "Could not connect using type=" + connectionType + ", host="
					+ hostString + ", repo=" + repoName );
			System.out.println( "Exception is " + e1 );
		}

		return newSession;
	}

	private CDONet4jSession createDefaultSession( String repoName, String typeString, String hostString )
	{
		//The following lines are not needed if the extension 
		//registry (OSGi/Equinox) is running
		Net4jUtil.prepareContainer( IPluginContainer.INSTANCE );
		TCPUtil.prepareContainer( IPluginContainer.INSTANCE );

		final IConnector connector = (IConnector)IPluginContainer.INSTANCE.getElement( //
				"org.eclipse.net4j.connectors", // Product group
				typeString, // Type
				hostString ); // Description

		CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
		config.setConnector( connector );
		config.setRepositoryName( repoName );

		CDONet4jSession newSession = config.openNet4jSession();

		newSession.addListener( new LifecycleEventAdapter() {
			@Override
			protected void onDeactivated( ILifecycle lifecycle )
			{
				System.out.println( "Deactivated event!" );
				connector.close();
			}
		} );

		return newSession;
	}

	@SuppressWarnings( "restriction" )
	private CDONet4jSession createReconnectingSession( String repoName, String typeString, String hostString )
	{

		// Repository
		ReconnectingCDOSessionConfiguration config = CDONet4jUtil.createReconnectingSessionConfiguration( hostString, repoName, IPluginContainer.INSTANCE );

		// Create credentials
		//PasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(username, password);
		PasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider();
		config.setCredentialsProvider( credentialsProvider );
		config.setActivateOnOpen( true );
		config.setPassiveUpdateEnabled( true );
		config.setPassiveUpdateMode( PassiveUpdateMode.ADDITIONS );
		config.setHeartBeatEnabled( true );
	
		//On first connection attempt: if server is NOT up, it will go on forever if this is not set:
		int defaultVal = config.getMaxReconnectAttempts();
		config.setMaxReconnectAttempts( 2 );
		
		System.out.println( "heartbeat period:" + config.getHeartBeatPeriod() );
		System.out.println( "heartbeat timeout:" + config.getHeartBeatTimeout() );
		System.out.println( "connector timeout:" + config.getConnectorTimeout());
		System.out.println( "connector maxReconnectAttempts:" + config.getMaxReconnectAttempts());
		System.out.println( "connector reconnect interval:" + config.getReconnectInterval());
		System.out.println( "connector signal timeout:" + config.getSignalTimeout());
		//System.out.println( "connector signal timeout:" + config.getSignalTimeout());
	
		//If server is not up - it will attempt a couple of times, and failing with an Exception.
		CDONet4jSession newSession = config.openNet4jSession();
		//If successfull, reset the "maxReconnectAttempt"
		if (newSession instanceof ReconnectingCDOSessionImpl) {
			((ReconnectingCDOSessionImpl)newSession).setMaxReconnectAttempts( defaultVal );
		}
		newSession.addListener( this.sessionListener );

		return newSession;

	}

	private class Listener implements IListener
	{
		@Override
		public void notifyEvent( final IEvent event )
		{
			CDOSessionEvent newEvent = null;
			if ( event instanceof ILifecycleEvent ) {
				ILifecycleEvent lEvent = (ILifecycleEvent)event;
				CDOSession sess = null;
				switch ( lEvent.getKind() ) {
					case ABOUT_TO_ACTIVATE:
						newEvent = new CDOSessionEvent( sess, EventType.ABOUT_TO_ACTIVATE );
						break;
					case ACTIVATED:
						newEvent = new CDOSessionEvent( sess, EventType.ACTIVATED );
						break;
					case ABOUT_TO_DEACTIVATE:
						newEvent = new CDOSessionEvent( sess, EventType.ABOUT_TO_DEACTIVATE );
						break;
					case DEACTIVATED:
						newEvent = new CDOSessionEvent( sess, EventType.DEACTIVATED );
						break;
				}
			}
			else if ( event instanceof CDOSessionRecoveryEvent ) {
				final CDOSessionRecoveryEvent recoveryEvent = (CDOSessionRecoveryEvent)event;
				CDOSession sess = recoveryEvent.getSource();
				switch ( recoveryEvent.getType() ) {
					case STARTED:
						newEvent = new CDOSessionEvent( sess, EventType.RECOVERY_STARTED );
						break;
					case FINISHED:
						newEvent = new CDOSessionEvent( sess, EventType.RECOVERY_FINISHED );
						break;
				}
			}
			else {
				System.out.println( "Other event: " + event );
			}
			if ( newEvent != null ) {
				//System.out.println( "Should broadcast event here with " + newEvent.getEventType() );
				SessionController.this.fireCDOSessionEvent( newEvent );
			}
		}
	}

	public static enum EventType
	{
		ABOUT_TO_ACTIVATE,
		ACTIVATED,
		ABOUT_TO_DEACTIVATE,
		DEACTIVATED,
		RECOVERY_STARTED,
		RECOVERY_FINISHED
	}

	public static class CDOSessionEvent
	{
		private CDOSession session = null;
		private EventType type = null;

		public CDOSessionEvent( CDOSession sess, EventType stat )
		{
			this.session = sess;
			this.type = stat;
		}

		public CDOSession getSession()
		{
			return this.session;
		}

		public EventType getEventType()
		{
			return this.type;
		}

	}

	public static interface ICDOSessionEventListener
	{
		public void sessionEvent( CDOSessionEvent event );
	}
}
