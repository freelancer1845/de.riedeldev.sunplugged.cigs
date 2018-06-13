package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataCSVService;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import lombok.extern.slf4j.Slf4j;

@SpringComponent
@UIScope
@Slf4j
public class LogSessionsComponent extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8763555323942205501L;

	private DataLoggingService logService;

	private DataCSVService csvService;

	private DateField startDate;

	private DateField endDate;

	private ListDataProvider<LogSession> dataProvider;

	@Autowired
	public LogSessionsComponent(DataLoggingService service, DataCSVService csvService) {
		this.logService = service;
		this.csvService = csvService;

		HorizontalLayout dateLayout = new HorizontalLayout();

		createDatePickerStart();
		createDatePickerEnd();

		dateLayout.addComponent(startDate);
		dateLayout.addComponent(endDate);

		addComponent(dateLayout);

		Grid<LogSession> grid = createGrid();

		grid.setWidth("100%");
		grid.setHeight("650px");

		addComponent(grid);

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
		grid.addColumn(session -> session.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm:ss")))
				.setCaption("Start Time");
		grid.addColumn(session -> session.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm:ss")))

				.setCaption("End Time");

		grid.addColumn(session -> logService.getCountOfDataPointsBySession(session)).setCaption("Data Points");

		grid.addComponentColumn(this::createActionsForSession).setCaption("Actions");

		dataProvider = new ListDataProvider<>(logService.streamSessionsFromLast().collect(Collectors.toList()));
		dataProvider.addFilter(session -> {
			if (endDate.isEmpty() == false) {
				return session.getEndDate().toLocalDate().isBefore(endDate.getValue().plusDays(1));
			} else {
				return true;
			}

		});
		dataProvider.addFilter(session -> {
			if (startDate.isEmpty() == false) {
				return session.getStartDate().toLocalDate().isAfter(startDate.getValue().minusDays(1));
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
		grid.setSelectionMode(SelectionMode.NONE);

		return grid;
	}

	private FileDownloader getSessionDownloader(LogSession session) {
		FileDownloader downloader = new FileDownloader(createSessionDownloadResource(session));
		return downloader;
	}

	private Resource createSessionDownloadResource(LogSession session) {
		return new StreamResource(new StreamSource() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1011443990479189684L;

			@Override
			public InputStream getStream() {

				String csvString;
				try {
					csvString = csvService.sessionToCsv(session);
					return new ByteArrayInputStream(csvString.getBytes());
				} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
					log.error("Failed to convert session to csv!", e);
					return new ByteArrayInputStream("Error creating csv...".getBytes());
				}

			}

		}, String.format("cigs-%s.csv",
				session.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-dd-MM_HH-mm"))));
	}

	private HorizontalLayout createActionsForSession(LogSession session) {
		HorizontalLayout layout = new HorizontalLayout();
		Button downloadButton = new Button("Downlaod");
		downloadButton.setIcon(VaadinIcons.DOWNLOAD);
		downloadButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		getSessionDownloader(session).extend(downloadButton);

		layout.addComponent(downloadButton);

		Button deleteButton = new Button("Delete");
		deleteButton.setIcon(VaadinIcons.FILE_REMOVE);
		deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);
		deleteButton.addClickListener(click -> {
			ConfirmationDialog.confirmAndDo(getUI(), "Delete Session", "Are you sure you want to delete this session?",
					answer -> {
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
	}

}
