package elundstrom.cdo.client.ui.parts;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PlaygroundPart {

	public PlaygroundPart() {
		System.out.println("Playground alive.");
	}

	private Text text;
	private Browser browser;

	@Inject
	private MPart part;

	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		text = new Text(parent, SWT.BORDER);
		text.setMessage("Enter City");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Search");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String city = text.getText();
				if (city.isEmpty()) {
					return;
				}
				try {
					System.out.println("Snubben söker efter: " + city);
					System.out.println("Whats my part? " + PlaygroundPart.this.part);

					IEclipseContext context = PlaygroundPart.this.part.getContext();
					if (context.get("kalle") == null) {
						context.set("kalle", "roger pontare");
					} else {
						System.out.println("value in context of kalle: " + context.get("kalle"));
					}
					// not supported at the moment by Google
					// browser.setUrl("http://maps.google.com/maps?q="
					// + URLEncoder.encode(city, "UTF-8")
					// + "&output=embed");
					browser.setUrl(
							"https://www.google.com/maps/place/" + URLEncoder.encode(city, "UTF-8") + "/&output=embed");

				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		});

		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

	}

	@PreDestroy
	public void janneKask() {
		System.out.println("About to destroy this son of a gun");
	}

	@PreDestroy
	public void janneKask2() {
		System.out.println("About to destroy this son of a gun 2");
	}

	@Persist
	public void janneKask3() {
		System.out.println("Save request by E framework?");
	}

	@PersistState
	public void janneKask4() {
		System.out.println("Model object about to be disposed; part may save its instance state.");
	}

	@Focus
	public void onFocus() {
		System.out.println("Playground part got focus. Setting on the text box...");
		text.setFocus();
	}
}
