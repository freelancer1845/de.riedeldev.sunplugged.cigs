package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Grid.SelectionMode;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;

@SpringComponent
@UIScope
public class LogSessionsComponent extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8763555323942205501L;

	private DataLoggingService service;

	private DateField startDate;

	private DateField endDate;

	private ListDataProvider<LogSession> dataProvider;

	@Autowired
	public LogSessionsComponent(DataLoggingService service) {
		this.service = service;

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

		grid.addColumn(session -> service.getCountOfDataPointsBySession(session)).setCaption("Data Points");

		grid.addComponentColumn(this::createActionsForSession).setCaption("Actions");

		dataProvider = new ListDataProvider<>(service.streamSessionsFromLast().collect(Collectors.toList()));
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

	private void downloadSession(LogSession session) {
		System.out.println("Trying to download + " + session.getId());
	}

	private HorizontalLayout createActionsForSession(LogSession session) {
		HorizontalLayout layout = new HorizontalLayout();
		Button downloadButton = new Button("Downlaod");
		downloadButton.setIcon(VaadinIcons.DOWNLOAD);
		downloadButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		downloadButton.addClickListener(click -> downloadSession(session));

		layout.addComponent(downloadButton);
		
		Button deleteButton = new Button("Delete");
		deleteButton.setIcon(VaadinIcons.DEL);
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
		service.deleteLogSession(session);
	}

}
