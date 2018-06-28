package de.riedeldev.sunplugged.cigs.logger.server.ui;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.UI;

@SpringUI
@Push
@PushStateNavigation
@SpringViewDisplay
public class MainPage extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7064501814567652976L;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Cigs RPi");
	}

}
