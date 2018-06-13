package de.riedeldev.sunplugged.cigs.logger.server.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
