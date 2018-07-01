package de.riedeldev.sunplugged.cigs.logger.server.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI(path = "/ui")
@Push
@PushStateNavigation
@SpringViewDisplay
public class MainPage extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7064501814567652976L;
	private Label header;
	private VerticalLayout headerLayout;
	private VerticalLayout mainLayout;
	private VerticalLayout footerLayout;
	private VerticalLayout contentLayout;
	private MenuBar mainMenu;
	private LogStateComponent logStateComponent;

	@Autowired
	public MainPage(LogStateComponent logStateComponent) {
		this.logStateComponent = logStateComponent;
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Cigs RPi");
		SpringNavigator navigator = (SpringNavigator) getNavigator();

		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(false);
		mainLayout.setMargin(false);

		headerLayout = new VerticalLayout();
		mainLayout.addComponent(headerLayout);
		mainLayout.addComponent(Utils.createHorizontalSeperator());

		header = new Label("<h2>Cigs Data</h2>", ContentMode.HTML);
		headerLayout.addComponent(header);
		mainMenu = new MenuBar();
		mainMenu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		headerLayout.addComponent(mainMenu);

		mainMenu.addItem("Live", VaadinIcons.LINE_CHART, item -> {
			navigator.navigateTo(Views.LIVE_VIEW);
		});

		mainMenu.addItem("Data", VaadinIcons.DATABASE, item -> navigator.navigateTo(Views.DATA_VIEW));

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(false);
		mainLayout.addComponent(contentLayout);

		mainLayout.addComponent(Utils.createHorizontalSeperator());

		footerLayout = new VerticalLayout();
		mainLayout.addComponent(footerLayout);

		footerLayout.addComponent(logStateComponent);
		setContent(mainLayout);
		navigator.init(this, contentLayout);

	}
}
