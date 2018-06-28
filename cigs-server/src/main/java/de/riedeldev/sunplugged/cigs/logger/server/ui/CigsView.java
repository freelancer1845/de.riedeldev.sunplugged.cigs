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
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataCSVService;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import de.riedeldev.sunplugged.cigs.logger.server.ui.tabs.LogConfigurationTab;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringView(name = "cigs")
public class CigsView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7756494991676578253L;
	private Label header;
	private TabSheet tabsheet;
	private Tab dataTab;

	private DataLoggingService logService;

	private DataCSVService csvService;

	private final SessionEditor editor;

	private DateField startDate;

	private DateField endDate;

	private ListDataProvider<LogSession> dataProvider;
	private VerticalLayout formLayout;
	private Component dataComponent;

	private LogConfigurationTab logConfigurationTab;
	private HorizontalLayout logStateLayout;

	@Autowired
	public CigsView(DataLoggingService service, DataCSVService csvService, SessionEditor editor,
			LogConfigurationTab logConfigurationTab) {
		this.logService = service;
		this.csvService = csvService;
		this.editor = editor;
		this.logConfigurationTab = logConfigurationTab;
		editor.setVisible(false);
	}

	@Override
	public void enter(ViewChangeEvent event) {

		header = new Label("<h2>Cigs Data</h2>", ContentMode.HTML);

		addComponent(header);

		tabsheet = new TabSheet();
		addComponent(tabsheet);

		dataComponent = createDataComponent();
		dataTab = tabsheet.addTab(dataComponent, "Data", VaadinIcons.DATABASE);
		dataTab = tabsheet.addTab(logConfigurationTab, "Configuration", VaadinIcons.PENCIL);

		createLogStateLayout();

		View.super.enter(event);
	}

	private void createLogStateLayout() {
		Label horizontalSperator = new Label("<hr />", ContentMode.HTML);
		// horizontalSperator.setSizeUndefined();
		horizontalSperator.setWidth("100%");
		addComponent(horizontalSperator);

		logStateLayout = new HorizontalLayout();
		logStateLayout.setWidth("100%");
		addComponent(logStateLayout);

		Label stateLabel = new Label();
		stateLabel.setContentMode(ContentMode.HTML);
		stateLabel.addStyleName(ValoTheme.LABEL_H2);
		stateLabel.setValue(VaadinIcons.POWER_OFF.getHtml() + " Currently not logging");
		stateLabel.setWidth("100%");
		logStateLayout.addComponent(stateLabel);
		logStateLayout.setExpandRatio(stateLabel, 1.0f);

		Label currentSessionLabel = new Label();
		currentSessionLabel.setValue("Current Session: Unkown");
		currentSessionLabel.addStyleName(ValoTheme.LABEL_H2);
		currentSessionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		logStateLayout.addComponent(currentSessionLabel);
		logStateLayout.setExpandRatio(currentSessionLabel, 0.0f);

	}

	private Component createDataComponent() {

		GridLayout gridLayout = new GridLayout(2, 1);
		gridLayout.setSizeFull();
		gridLayout.setColumnExpandRatio(0, 2.0f);
		gridLayout.setColumnExpandRatio(1, 1.0f);

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

		gridLayout.addComponent(firstComponent, 0, 0);

		gridLayout.addComponent(editor, 1, 0);
		return gridLayout;
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

		// grid.addColumn(session -> logService.getCountOfDataPointsBySession(session))
		// .setCaption("Data Points");

		grid.addComponentColumn(this::createActionsForSession)
				.setCaption("Actions");

		dataProvider = new ListDataProvider<>(logService.streamSessionsFromLast()
				.collect(Collectors.toList()));
		dataProvider.addFilter(session -> {
			if (endDate.isEmpty() == false) {
				return session.getEndDate()
						.toLocalDate()
						.isBefore(endDate.getValue()
								.plusDays(1));
			} else {
				return true;
			}

		});
		dataProvider.addFilter(session -> {
			if (startDate.isEmpty() == false) {
				return session.getStartDate()
						.toLocalDate()
						.isAfter(startDate.getValue()
								.minusDays(1));
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
		editor.editSession(event.getFirstSelectedItem()
				.orElse(null));
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

		}, String.format("cigs-%s.csv", session.getStartDate()
				.format(DateTimeFormatter.ofPattern("yyyy-dd-MM_HH-mm"))));
	}

	private CssLayout createActionsForSession(LogSession session) {
		CssLayout layout = new CssLayout();
		layout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		Button viewButton = new Button("");
		viewButton.setIcon(VaadinIcons.CHART);
		viewButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		viewButton.addClickListener(click -> {

			Tab tab = tabsheet.addTab(new DataViewComponent(session, logService), session.getStartDate()
					.format(DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm")));
			tab.setClosable(true);
			tabsheet.setSelectedTab(tab);
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
		dataProvider.getItems()
				.remove(session);
		editor.getCurrentSession()
				.ifPresent(activeSession -> {
					if (activeSession.equals(session)) {
						editor.setVisible(false);
					}
				});
		dataProvider.refreshAll();
	}
}
