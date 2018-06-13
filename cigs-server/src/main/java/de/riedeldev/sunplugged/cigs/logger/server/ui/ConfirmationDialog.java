package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfirmationDialog extends Window{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4339278410786464396L;
	
	private static final int WIDTH = 350;
	private static final int HEIGHT = 200;
	
	private VerticalLayout layout;
	private Label messageLabel;
	
	private Boolean answer;
	private Button yesButton;
	private Button noButton;
	
	private Consumer<Boolean> action;
	private HorizontalLayout buttonLayout;
	
	public static void confirmAndDo(UI ui, String title, String message, Consumer<Boolean> action) {
		new ConfirmationDialog(ui, title, message, action);
	}
	
	private ConfirmationDialog(UI ui, String title, String message, Consumer<Boolean> action) {
		ui.addWindow(this);
		this.action = action;
		setWidth(WIDTH, Unit.PIXELS);
		setHeight(HEIGHT, Unit.PIXELS);
		setPosition((int) getUI().getWidth() / 2 - WIDTH / 2, (int) getUI().getHeight() / 2 - HEIGHT / 2);
		setCaption(title);
		setIcon(VaadinIcons.QUESTION);
		
		
		
		layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);
		
		messageLabel = new Label(message);
		messageLabel.setSizeFull();
		
		layout.addComponent(messageLabel);
		
		yesButton = new Button("Yes", click -> handleAnswer(true));
		noButton = new Button("No", click -> handleAnswer(false));
		
		buttonLayout = new HorizontalLayout();
		buttonLayout.addComponent(yesButton);
		buttonLayout.addComponent(noButton);
		layout.addComponent(buttonLayout);
		layout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		
		setVisible(true);
		setModal(true);
		setClosable(true);
		setResizable(false);
	}
	
	private void handleAnswer(Boolean answer) {
		setVisible(false);
		getUI().removeWindow(this);
		
		action.accept(answer);
		
	}
	
	

}
