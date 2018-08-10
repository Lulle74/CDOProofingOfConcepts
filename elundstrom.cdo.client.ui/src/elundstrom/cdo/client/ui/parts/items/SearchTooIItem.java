package elundstrom.cdo.client.ui.parts.items;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SearchTooIItem {

	@Inject
	public SearchTooIItem() {

	}

	@PostConstruct
	public void createGui(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		Text text = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH | SWT.CANCEL | SWT.BORDER);
		text.setMessage("Search");
		GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).applyTo(text);

	}
}