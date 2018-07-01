package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataAquisitionService;

@SpringComponent
@UIScope
public class LogStateComponent extends HorizontalLayout implements DataAquisitionService.StateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3859515126368407806L;

	private DataAquisitionService dataAquiService;

	private Label stateLabel;

	private Label currentSessionLabel;

	@Autowired
	public LogStateComponent(DataAquisitionService dataAquiService) {

		this.dataAquiService = dataAquiService;

		createContent();

	}

	private void createContent() {
		this.setMargin(false);
		this.setWidth("100%");

		stateLabel = new Label();
		stateLabel.setContentMode(ContentMode.HTML);
		stateLabel.addStyleName(ValoTheme.LABEL_H2);

		stateLabel.setWidth("100%");
		this.addComponent(stateLabel);
		this.setExpandRatio(stateLabel, 1.0f);

		currentSessionLabel = new Label();
		currentSessionLabel.addStyleName(ValoTheme.LABEL_H2);
		currentSessionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		this.addComponent(currentSessionLabel);
		this.setExpandRatio(currentSessionLabel, 0.0f);

		handleNewState(dataAquiService.isLogging(), dataAquiService.getActiveSession());

		dataAquiService.addStateListener(this);

	}

	private void handleNewState(Boolean state, LogSession session) {
		if (state == true) {
			stateLabel.setValue(VaadinIcons.CHECK.getHtml() + " Logging");
			currentSessionLabel.setValue("Current Session: " + session.getStartDate()
					.format(DateTimeFormatter.ofPattern("HH:mm")));
		} else {
			stateLabel.setValue(VaadinIcons.POWER_OFF.getHtml() + " Not logging");
			currentSessionLabel.setValue("");
		}
	}

	@Override
	public void detach() {
		dataAquiService.removeListener(this);
		super.detach();
	}

	@Override
	public void newState(Boolean currentState, LogSession currentSession) {
		if (getUI() == null) {
			return;
		} else if (getUI().isAttached() == false) {
			return;
		}
		getUI().access(() -> {
			handleNewState(currentState, currentSession);
		});

	}

}
