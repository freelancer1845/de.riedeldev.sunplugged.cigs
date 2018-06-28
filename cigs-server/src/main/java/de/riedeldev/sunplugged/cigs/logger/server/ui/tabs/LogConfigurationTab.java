package de.riedeldev.sunplugged.cigs.logger.server.ui.tabs;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class LogConfigurationTab extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4449057219924494866L;
	private FormLayout settingsLayout;
	private TextField intervalSpeed;
	private CheckBox automaticLogging;

	private Binder<LogConfigurationTab> binder = new Binder<>();

	public LogConfigurationTab() {
		binder.setBean(this);
		createSettingsLayout();

	}

	private void createSettingsLayout() {
		settingsLayout = new FormLayout();
		addComponent(settingsLayout);

		intervalSpeed = new TextField("Interval [ms]");
		settingsLayout.addComponent(intervalSpeed);
		binder.forField(intervalSpeed)
				.withConverter(Integer::valueOf, String::valueOf, "Must be an Integer!")
				.withValidator((text, context) -> {
					int value = Integer.valueOf(text);
					if (value < 500) {
						return ValidationResult.error("Value is too samll < 500");
					} else if (value > 60000) {
						return ValidationResult.error("Value is too big > 60000");
					} else
						return ValidationResult.ok();

				})
				.bind(LogConfigurationTab::getCurrentLogSpeed, LogConfigurationTab::setCurrentLogSpeed);

		automaticLogging = new CheckBox("Automatic Logging");
		binder.forField(automaticLogging)
				.bind(LogConfigurationTab::getAutomaticLogging, LogConfigurationTab::setAutomaticLogging);

		settingsLayout.addComponent(automaticLogging);
	}

	private Integer getCurrentLogSpeed() {
		return 0;
	}

	private void setCurrentLogSpeed(Integer logspeed) {
		System.out.println("Log Speed: " + logspeed);
	}

	private Boolean getAutomaticLogging() {
		return false;
	}

	private void setAutomaticLogging(Boolean value) {
	}

}
