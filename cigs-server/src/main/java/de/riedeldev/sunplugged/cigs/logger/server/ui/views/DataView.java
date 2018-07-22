package de.riedeldev.sunplugged.cigs.logger.server.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import de.riedeldev.sunplugged.cigs.logger.server.ui.ConfirmationDialog;
import de.riedeldev.sunplugged.cigs.logger.server.ui.SessionDataChart;
import de.riedeldev.sunplugged.cigs.logger.server.ui.SessionEditor;
import de.riedeldev.sunplugged.cigs.logger.server.ui.Views;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringView(name = Views.DATA_VIEW)
@UIScope
public class DataView extends GridLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -121026732458969523L;

	private DataLoggingService logService;
	private SessionEditor editor;
	private SessionDataChart chartComponent;

	private DateField endDate;
	private DateField startDate;

	private ListDataProvider<LogSession> dataProvider;

	public DataView(SessionEditor editor, DataLoggingService logService,
			SessionDataChart chartComponent) {
		super(2, 1);
		this.editor = editor;
		this.logService = logService;
		this.chartComponent = chartComponent;
		this.editor.setVisible(false);
		createContent();

	}

	@Override
	public void enter(ViewChangeEvent event) {
		dataProvider.refreshAll();
	}

	private void createContent() {
		this.setSizeFull();
		this.setColumnExpandRatio(0, 1.2f);
		this.setColumnExpandRatio(1, 1.0f);

		VerticalLayout firstComponent = new VerticalLayout();

		HorizontalLayout dateLayout = new HorizontalLayout();

		createDatePickerStart();
		createDatePickerEnd();

		dateLayout.addComponent(startDate);
		dateLayout.addComponent(endDate);

		firstComponent.addComponent(dateLayout);

		Grid<LogSession> grid = createGrid();

		grid.setWidth("100%");
		grid.setHeight("650px");

		firstComponent.addComponent(grid);
		firstComponent.setWidth("100%");

		this.addComponent(firstComponent, 0, 0);

		// this.addComponent(editor, 1, 0);
		// this.addComponent(chartComponent, 1, 1);

	}

	private void createDatePickerEnd() {
		endDate = new DateField();

		endDate.setCaption("Filter Date End");

		endDate.addValueChangeListener(event -> {
			if (event.getValue() != null) {
				LocalDate endDate = event.getValue();
				startDate.setRangeEnd(endDate);
			}
			dataProvider.refreshAll();
		});

	}

	private void createDatePickerStart() {
		startDate = new DateField();

		startDate.setCaption("Filter Date Start");

		startDate.addValueChangeListener(event -> {
			if (event.getValue() != null) {
				LocalDate startDate = event.getValue();
				endDate.setRangeStart(startDate);

			}
			dataProvider.refreshAll();
		});

	}

	private Grid<LogSession> createGrid() {
		Grid<LogSession> grid = new Grid<>();
		grid.setCaption("Sessions");
		grid.addColumn(session -> session.getStartDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm:ss")))
				.setCaption("Start Time");
		grid.addColumn(session -> session.getEndDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm:ss")))

				.setCaption("End Time");

		// grid.addColumn(session ->
		// logService.getCountOfDataPointsBySession(session))
		// .setCaption("Data Points");

		grid.addComponentColumn(this::createActionsForSession)
				.setCaption("Actions");

		dataProvider = new ListDataProvider<>(logService
				.streamSessionsFromLast().collect(Collectors.toList()));
		dataProvider.addFilter(session -> {
			if (endDate.isEmpty() == false) {
				return session.getEndDate().toLocalDate()
						.isBefore(endDate.getValue().plusDays(1));
			} else {
				return true;
			}

		});
		dataProvider.addFilter(session -> {
			if (startDate.isEmpty() == false) {
				return session.getStartDate().toLocalDate()
						.isAfter(startDate.getValue().minusDays(1));
			} else {
				return true;
			}

		});
		grid.setDataProvider(dataProvider);
		// grid.setDataProvider((sortOrder, offset, limit) ->
		// service.streamSessionsFromLast()
		// .skip(offset)
		// .limit(limit),
		// () -> service.sessionsCount()
		// .intValue());
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.addSelectionListener(this::changeSelectedSession);

		return grid;
	}

	private void changeSelectedSession(SelectionEvent<LogSession> event) {
		editor.editSession(event.getFirstSelectedItem().orElse(null));
		removeComponent(chartComponent);
		removeComponent(editor);
		addComponent(editor, 1, 0);
	}

	private FileDownloader getSessionDownloader(LogSession session) {
		FileDownloader downloader = new FileDownloader(
				createSessionDownloadResource(session));
		return downloader;
	}

	private Resource createSessionDownloadResource(LogSession session) {

		return new StreamResource(new StreamSource() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5231933199482993327L;

			@Override
			public InputStream getStream() {
				File file = new File(session.getLogFilePath());
				try {
					return logService
							.getDownloadStreamForLogSession(session);
				} catch (FileNotFoundException e) {
					log.error("Failed to find file " + file.getPath(), e);
					throw new IllegalStateException("Error download file.", e);
				}
			}
		}, Paths.get(session.getLogFilePath()).getFileName().toString());
	}

	private CssLayout createActionsForSession(LogSession session) {
		CssLayout layout = new CssLayout();
		layout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		Button viewButton = new Button("");
		viewButton.setIcon(VaadinIcons.CHART);
		viewButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		viewButton.addClickListener(click -> {
			try {
				chartComponent.showSession(session);
			} catch (Exception e) {
				Notification error = new Notification("Error creating chart",
						e.getMessage(), Type.ERROR_MESSAGE);
				error.show(getUI().getPage());
			}
			removeComponent(editor);
			removeComponent(chartComponent);
			addComponent(chartComponent, 1, 0);
		});
		layout.addComponent(viewButton);
		Button downloadButton = new Button("");
		downloadButton.setIcon(VaadinIcons.DOWNLOAD);
		downloadButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		getSessionDownloader(session).extend(downloadButton);

		layout.addComponent(downloadButton);

		Button deleteButton = new Button("");
		deleteButton.setIcon(VaadinIcons.TRASH);
		deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);
		deleteButton.addClickListener(click -> {
			ConfirmationDialog.confirmAndDo(getUI(), "Delete Session",
					"Are you sure you want to delete this session?", answer -> {
						if (answer) {
							deleteSession(session);
						}
					});
		});
		layout.addComponent(deleteButton);

		return layout;
	}

	private void deleteSession(LogSession session) {
		logService.deleteLogSession(session);
		dataProvider.getItems().remove(session);
		editor.getCurrentSession().ifPresent(activeSession -> {
			if (activeSession.equals(session)) {
				editor.setVisible(false);
			}
		});
		dataProvider.refreshAll();
	}

}
