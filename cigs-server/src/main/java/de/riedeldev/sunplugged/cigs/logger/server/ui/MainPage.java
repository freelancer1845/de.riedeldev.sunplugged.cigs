package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
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

    @Autowired
    public MainPage(DataLoggingService service) {
        this.service = service;
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        Grid<LogSession> grid = createGrid();

        layout.addComponent(grid);

        setContent(layout);

    }

    private Grid<LogSession> createGrid() {
        Grid<LogSession> grid = new Grid<>();
        grid.setCaption("Sessions");
        grid.addColumn(session -> session.getStartDate()
                                         .format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm")))
            .setCaption("Start Time");
        grid.addColumn(session -> session.getEndDate()
                                         .format(DateTimeFormatter.ofPattern("yyyy-MM-dd : HH:mm")))
            .setCaption("End Time");
        List<LogSession> sessions = service.getSessions();
        grid.setDataProvider(new ListDataProvider<>(sessions));
        // grid.setDataProvider((sortOrder, offset, limit) ->
        // service.streamSessionsFromLast()
        // .skip(offset)
        // .limit(limit),
        // () -> service.sessionsCount()
        // .intValue());

        return grid;
    }

}
