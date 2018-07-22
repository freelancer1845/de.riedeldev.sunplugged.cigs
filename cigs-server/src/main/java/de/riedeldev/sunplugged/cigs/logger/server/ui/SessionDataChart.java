package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.TimeLineDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.options.scale.TimeScale;
import com.byteowls.vaadin.chartjs.options.scale.TimeScaleOptions;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.DataPointUtils.DataPointField;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import lombok.extern.slf4j.Slf4j;

@SpringComponent
@UIScope
@Slf4j
public class SessionDataChart extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7482020498752025139L;

	private LogSession session;

	private List<DataPoint> dataPoints;

	private DataLoggingService service;

	private List<DataPointField> fieldsToShow = new ArrayList<>();

	private DataPointField dateField = DataPointField.getDateField();

	private ChartJs chart;

	@Autowired
	public SessionDataChart(DataLoggingService service) {
		this.service = service;
		this.setVisible(false);
		setHeight("823");

		TwinColSelect<DataPointField> select = new TwinColSelect<>(
				"Choose What to plot");

		select.setItems(DataPointField.getAllDataPointFieldsSorted().stream()
				.filter(field -> field.field.getType() != LocalDateTime.class));
		select.addSelectionListener(event -> {

			fieldsToShow.clear();
			fieldsToShow.addAll(event.getNewSelection());
			try {
				updateChart();
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| ParseException e) {
				Notification error = new Notification("Error creating chart",
						e.getMessage(), Type.ERROR_MESSAGE);
				error.show(getUI().getPage());
			}
		});
		select.setItemCaptionGenerator(field -> field.name);
		select.setWidth("100%");
		select.setHeight("100%");
		addComponent(select);
	}

	public void showSession(LogSession session) throws Exception {
		this.session = service.getLiveLogSession(session);
		if (this.session == null) {
			setVisible(false);
		}
		setVisible(true);
		try {
			dataPoints = service.getDatapointsOfSession(session);
		} catch (Exception e) {
			log.error("Failed load datapoints.", e);
			throw e;
		}

		updateChart();

	}

	private void updateChart()
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {
		if (chart != null) {
			removeComponent(chart);
		}
		chart = createChart();
		addComponent(chart, 0);
	}

	private ChartJs createChart()
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {
		LineChartConfig config = new LineChartConfig();

		for (DataPointField field : fieldsToShow) {

			TimeLineDataset lds = new TimeLineDataset().label(field.name)
					.fill(false);

			lds.borderColor(ColorUtils.randomColor(.4));
			lds.backgroundColor(ColorUtils.randomColor(.1));
			lds.pointBorderColor(ColorUtils.randomColor(.7));
			lds.pointBackgroundColor(ColorUtils.randomColor(.5));
			lds.pointBorderWidth(1);

			getData(dataPoints, field).forEach(
					pair -> lds.addData(pair.getFirst(), pair.getSecond()));
			config.data().addDataset(lds);
		}

		config.options().responsive(true).title().display(true)
				.text(session.getStartDate().format(
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
				.and().tooltips().mode(InteractionMode.INDEX).intersect(false)
				.and().hover().mode(InteractionMode.NEAREST).intersect(true)
				.and().scales().add(Axis.X, createTimeScaleForSession(session))
				.add(Axis.Y,
						new LinearScale().display(true).scaleLabel()
								.display(true).labelString("Value").and()
								.ticks().and().position(Position.RIGHT))
				.and().maintainAspectRatio(false).responsive(true)
				// .pan()
				// .enabled(true).speed(10).threshold(1).rangeMax()
				// .x(session.getEndDate().plusHours(1)
				// .toEpochSecond(ZoneOffset.UTC))
				// .and().and().zoom().enabled(true).mode(XYMode.Y).sensitivity(3)
				// .and()
				.done();
		ChartJs chart = new ChartJs(config);
		// chart.setSizeFull();
		chart.setWidth("100%");
		chart.setHeight("100%");
		return chart;
	}

	private List<Pair<LocalDateTime, Double>> getData(
			List<DataPoint> dataPoints, DataPointField field)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {
		List<Pair<LocalDateTime, Double>> values = new ArrayList<>();
		for (DataPoint dataPoint : dataPoints) {
			Number number = NumberFormat.getNumberInstance(Locale.US)
					.parse(PropertyUtils
							.getPropertyDescriptor(dataPoint,
									field.field.getName())
							.getReadMethod().invoke(dataPoint, new Object[0])
							.toString());
			values.add(Pair.of(dataPoint.getDateTime(), number.doubleValue()));
		}

		List<Pair<LocalDateTime, Double>> finalValues = new ArrayList<>();

		finalValues.add(values.get(0));

		int idxOfPrevious = 0;

		for (int i = 1; i < values.size(); i++) {
			if ((Math
					.abs(values.get(idxOfPrevious).getSecond()
							- values.get(i).getSecond())
					/ Math.abs(values.get(idxOfPrevious).getSecond())) > 0.1) {
				if (i - 1 > idxOfPrevious) {
					if (Math.abs((values.get(i - 1).getSecond()
							- values.get(i).getSecond()))
							/ Math.abs(values.get(i).getSecond()) > 0.1) {
						finalValues.add(values.get(i - 1));
					}

				}
				finalValues.add(values.get(i));
				idxOfPrevious = i;
			} else if (finalValues.get(finalValues.size() - 1).getFirst()
					.plusMinutes(2).isBefore(values.get(i).getFirst())) {
				finalValues.add(values.get(i));
				idxOfPrevious = i;
			} else {
				if (i == values.size() - 1) {
					finalValues.add(values.get(i));
				}
			}
		}

		return finalValues;

	}

	private TimeScale createTimeScaleForSession(LogSession session) {

		if (session.getStartDate().plusHours(1)
				.isBefore(session.getEndDate())) {

			return new TimeScale().time()
					.min(session.getStartDate().minusMinutes(5))
					.max(session.getEndDate().truncatedTo(ChronoUnit.HOURS)
							.plusMinutes(20))
					.stepSize(30).unit(TimeScaleOptions.Unit.MINUTE)
					.displayFormats().minute("DD.MM HH:mm").and().and();

		} else if (session.getStartDate().plusMinutes(1)
				.isBefore(session.getEndDate())) {

			return new TimeScale().time()
					.min(session.getStartDate().truncatedTo(ChronoUnit.MINUTES))
					.max(session.getEndDate().plusMinutes(2)
							.truncatedTo(ChronoUnit.MINUTES))
					.stepSize(5).unit(TimeScaleOptions.Unit.MINUTE)
					.displayFormats().hour("DD.MM HH:mm").and().and();

		} else {
			return new TimeScale().time()
					.min(session.getStartDate().truncatedTo(ChronoUnit.SECONDS))
					.max(session.getEndDate().plusSeconds(10)
							.truncatedTo(ChronoUnit.SECONDS))
					.stepSize(5).unit(TimeScaleOptions.Unit.SECOND)
					.displayFormats().hour("DD.MM HH:mm:ss").and().and();
		}

	}

}
