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
import com.vaadin.ui.TabSheet;
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


	private LogSessionsComponent logSessionsComponent;


	private TabSheet tabSheet;

    @Autowired
    public MainPage(LogSessionsComponent logSessionsComponent) {
        this.logSessionsComponent = logSessionsComponent;
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        tabSheet = new TabSheet();
        layout.addComponent(tabSheet);
        
        
        tabSheet.addTab(logSessionsComponent, "Sessions");
        
        
        setContent(layout);

    }

    

}
