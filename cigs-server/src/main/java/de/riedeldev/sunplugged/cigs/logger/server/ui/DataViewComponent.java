package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.ReflectionUtils;

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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;

import de.riedeldev.sunplugged.cigs.logger.server.model.CreateChart;
import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataViewComponent extends GridLayout {

	private static final int MAX_DATA_POINTS = 300;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1773821077458705610L;

	private LogSession session;

	private DataLoggingService dataService;

	private List<ChartJs> charts = new LinkedList<>();

	public DataViewComponent(LogSession session, DataLoggingService dataService) {
		this.session = session;
		this.setCaption("Session Data View");
		this.dataService = dataService;
		this.setColumns(3);
		this.setWidth("100%");
		this.setHeight("100%");
		this.setResponsive(true);
		this.setSpacing(true);
		ReflectionUtils.doWithFields(DataPoint.class, this::addChartForField, this::filterField);

	}

	public static DataViewComponent createWithLiveListening(LogSession session, DataLoggingService dataService) {
		final DataViewComponent component = new DataViewComponent(session, dataService);
		dataService.registerAsListener(new ChartUpdateListener(component));

		return component;
	}

	private final static class ChartUpdateListener implements DataLoggingService.LiveListener {
		private DataViewComponent component;

		public ChartUpdateListener(DataViewComponent component) {
			this.component = component;
		}

		@Override
		public void newDataPoint(DataPoint point) {

			if (component.getUI() != null && component.getUI()
					.isAttached()) {
				component.getUI()
						.accessSynchronously(() -> {
							component.charts.forEach(chart -> {
								LineChartConfig config = (LineChartConfig) chart.getConfig();
								TimeLineDataset set = (TimeLineDataset) config.data()
										.getFirstDataset();

								set.addData(point.getDateTime(), getValueByField(point, (Field) chart.getData()));
							});
							// for (ChartJs chart : component.charts) {
							// chart.update();
							// }
						});
			}

		}
	}

	private void addChartForField(Field field) {

		int timeStepSize = 1;
		TimeScaleOptions.Unit timeStepUnit = TimeScaleOptions.Unit.HOUR;
		long hoursDifference = session.getStartDate()
				.until(session.getEndDate(), ChronoUnit.HOURS);
		if (hoursDifference <= 1) {
			timeStepSize = 20;
			timeStepUnit = TimeScaleOptions.Unit.MINUTE;
			long minutesDifference = session.getStartDate()
					.until(session.getEndDate(), ChronoUnit.MINUTES);
			if (minutesDifference < 30) {
				timeStepSize = 5;
			}
		}

		LineChartConfig lineConfig = new LineChartConfig();
		lineConfig.data()
				.addDataset(new TimeLineDataset().label(field.getName())
						.fill(false))
				.and()
				.options()
				.responsive(true)
				.animation()
				.and()
				.title()
				.display(true)
				.text(field.getName() + " Data")
				.and()
				.tooltips()
				.mode(InteractionMode.INDEX)
				.intersect(false)
				.and()
				.hover()
				.mode(InteractionMode.NEAREST)
				.and()
				.scales()
				.add(Axis.X, new TimeScale().time()
						.min(session.getStartDate())
						.max(session.getEndDate())
						.stepSize(timeStepSize)
						.unit(timeStepUnit)
						.displayFormats()
						.hour("DD.MM HH:mm:ss") // german
												// date/time
												// format
						.and()
						.and())
				.add(Axis.Y, new LinearScale().display(true)
						.scaleLabel()
						.display(true)
						.labelString("Value")
						.and()
						.ticks()
						.and()
						.position(Position.RIGHT))
				.and()
				// .pan()
				// .mode(XYMode.X)
				// .speed(20)
				// .threshold(10)
				// .and()
				.done();
		TimeLineDataset ds = (TimeLineDataset) lineConfig.data()
				.getFirstDataset();

		ds.borderColor(ColorUtils.randomColor(.4));
		ds.backgroundColor(ColorUtils.randomColor(.1));
		ds.pointBorderColor(ColorUtils.randomColor(.7));
		ds.pointBackgroundColor(ColorUtils.randomColor(.5));
		ds.pointBorderWidth(1);
		List<DataPoint> data = dataService.getDatapointsOfSession(session);

		int points = data.size();

		int stepSize = points / MAX_DATA_POINTS;
		if (stepSize == 0) {
			stepSize = 1;
		}
		for (int i = 0; i < data.size(); i += stepSize) {
			if (i < data.size()) {
				DataPoint point = data.get(i);

				ds.addData(point.getDateTime(), getValueByField(point, field));
			}
		}
		// data.forEach(point -> ds.addData(point.getDateTime(), getValueByField(point,
		// field)));
		lineConfig.options()
				.maintainAspectRatio(false);
		ChartJs chart = new ChartJs(lineConfig);
		// chart.setSizeFull();
		chart.setWidth("100%");
		chart.setHeight("350px");
		// chart.setJsLoggingEnabled(true);
		addComponent(chart);
		setComponentAlignment(chart, Alignment.TOP_CENTER);
		chart.setData(field);
		charts.add(chart);
		// addChartToLayout(chart);

	}

	private boolean filterField(Field field) {
		return Arrays.stream(field.getAnnotations())
				.anyMatch(annotation -> annotation.annotationType()
						.equals(CreateChart.class));
	}

	private static Double getValueByField(DataPoint point, Field field) {

		try {
			Double value = Double.valueOf(BeanUtils.getProperty(point, field.getName()));
			return value;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			log.debug("Error accessing field for chart.", e);
			return Double.NaN;
		}
	}

}
