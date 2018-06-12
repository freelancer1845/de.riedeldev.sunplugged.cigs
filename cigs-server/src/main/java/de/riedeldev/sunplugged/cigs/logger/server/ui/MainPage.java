package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;

@SpringUI
public class MainPage extends UI {

    /**
     * 
     */
    private static final long serialVersionUID = 7064501814567652976L;

    private DataLoggingService service;

    private DateField startDate;

    private DateField endDate;

    private ListDataProvider<LogSession> dataProvider;

    @Autowired
    public MainPage(DataLoggingService service) {
        this.service = service;
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout dateLayout = new HorizontalLayout();

        createDatePickerStart();
        createDatePickerEnd();

        dateLayout.addComponent(startDate);
        dateLayout.addComponent(endDate);

        layout.addComponent(dateLayout);

        Grid<LogSession> grid = createGrid();

        grid.setWidth("100%");

        layout.addComponent(grid);

        setContent(layout);

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

        grid.addColumn(session -> service.getCountOfDataPointsBySession(session))
            .setCaption("Data Points");

        dataProvider = new ListDataProvider<>(service.streamSessionsFromLast()
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
        grid.setSelectionMode(SelectionMode.NONE);

        return grid;
    }

}
