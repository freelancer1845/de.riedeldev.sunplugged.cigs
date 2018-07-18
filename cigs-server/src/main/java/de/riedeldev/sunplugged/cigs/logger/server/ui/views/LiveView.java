package de.riedeldev.sunplugged.cigs.logger.server.ui.views;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.ReflectionUtils;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSettings;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataAquisitionService;
import de.riedeldev.sunplugged.cigs.logger.server.ui.Views;

@SpringView(name = Views.LIVE_VIEW)
@UIScope
public class LiveView extends VerticalLayout implements View, Consumer<DataPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5954558455045193409L;

	private final DataAquisitionService aquiService;

	private Binder<DataPoint> binder = new Binder<>(DataPoint.class);

	private int layoutSortIndex = 0;

	public LiveView(DataAquisitionService logService) {
		this.aquiService = logService;
		Button startButton = new Button();
		startButton.setCaption("Start Logging");
		startButton.addClickListener(this::handleStartClick);
		addComponent(startButton);

		Button stopButton = new Button();
		stopButton.setCaption("Stop Logging");
		stopButton.addClickListener(this::handleStopClick);
		addComponent(stopButton);

		Button newPointButton = new Button();
		newPointButton.setCaption("New Point");
		newPointButton.addClickListener(event -> {
			aquiService.testGetDataPoint();
		});

		addComponent(newPointButton);

		HorizontalLayout formWrapper = new HorizontalLayout();
		FormLayout layoutLeft = new FormLayout();
		FormLayout layoutRight = new FormLayout();

		formWrapper.addComponent(layoutLeft);
		formWrapper.addComponent(layoutRight);
		DataPoint.class.getFields();

		ReflectionUtils.doWithFields(DataPoint.class, field -> {
			TextField textField = new TextField(field.getAnnotation(LogSettings.class)
					.nameToDisplay());
			if (layoutSortIndex == 0) {
				layoutLeft.addComponent(textField);
				layoutSortIndex = 1;
			} else {
				layoutRight.addComponent(textField);
				layoutSortIndex = 0;
			}
			binder.bind(textField, (point) -> {
				return getValueByField(point, field);
			}, null);
		}, this::filterField);
		binder.setReadOnly(true);

		addComponent(formWrapper);
		aquiService.registerDataPointListener(this);
	}

	private void handleStartClick(ClickEvent event) {
		aquiService.startLogging();
	}

	private void handleStopClick(ClickEvent event) {
		aquiService.stopLogging();
	}

	private boolean filterField(Field field) {
		if (Arrays.stream(field.getAnnotations())
				.anyMatch(annotation -> annotation.annotationType()
						.equals(LogSettings.class))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		aquiService.removeDataPointListener(this);
		super.detach();
	}

	@Override
	public void accept(DataPoint t) {
		if (getUI() != null) {
			if (getUI().isAttached() == true) {
				getUI().access(() -> {
					binder.setBean(t);
					this.markAsDirtyRecursive();
				});
			}
		}
	}

	private static String getValueByField(DataPoint point, Field field) {

		try {
			if (field.getType() == Double.class) {
				String stringValue = BeanUtils.getProperty(point, field.getName());
				if (stringValue == null) {
					return "N/A";
				}
				Double value = Double.valueOf(stringValue);

				return String.format("%.2f", value);
			} else if (field.getType() == LocalDateTime.class) {
				LocalDateTime value = LocalDateTime.parse(BeanUtils.getProperty(point, field.getName()));
				return value.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy"));
			} else if (field.getType() == Integer.class) {
				String stringValue = BeanUtils.getProperty(point, field.getName());
				if (stringValue == null) {
					return "N/A";
				}
				return stringValue;
			}
			return BeanUtils.getProperty(point, field.getName());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {

			return "";
		}
	}
}
