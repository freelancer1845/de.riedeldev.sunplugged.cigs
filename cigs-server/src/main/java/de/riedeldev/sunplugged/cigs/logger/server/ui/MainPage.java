package de.riedeldev.sunplugged.cigs.logger.server.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.cigs.logger.server.service.DataAquisitionService;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;

@SpringUI
@Push
public class MainPage extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7064501814567652976L;

	private LogSessionsComponent logSessionsComponent;

	@Autowired
	private DataAquisitionService aquService;

	@Autowired
	private DataLoggingService logService;

	private TabSheet tabSheet;

	@Autowired
	public MainPage(LogSessionsComponent logSessionsComponent) {
		this.logSessionsComponent = logSessionsComponent;
	}

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		tabSheet.addTab(logSessionsComponent, "Sessions");

		HorizontalLayout buttonLayout = new HorizontalLayout();

		Button button = new Button();
		button.setCaption("Start Logging");
		button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		button.addClickListener(click -> aquService.startLogging());
		buttonLayout.addComponent(button);

		Button stopbutton = new Button();
		stopbutton.setCaption("Stop Logging");
		stopbutton.setStyleName(ValoTheme.BUTTON_DANGER);
		stopbutton.addClickListener(click -> aquService.stopLogging());
		buttonLayout.addComponent(stopbutton);

		Button liveSession = new Button();
		liveSession.setCaption("Live Tab");
		liveSession.setStyleName(ValoTheme.BUTTON_QUIET);
		liveSession.addClickListener(click -> {
			Tab tab = tabSheet.addTab(
					DataViewComponent.createWithLiveListening(aquService.getActiveSession(), logService), "Live Data");
			tab.setClosable(true);

			tabSheet.setSelectedTab(tab);
		});

		buttonLayout.addComponent(liveSession);

		layout.addComponent(buttonLayout);

		setContent(layout);

	}

}
