package de.riedeldev.sunplugged.cigs.logger.server.ui;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.repository.LogSessionRepository;

@SpringComponent
@UIScope
public class SessionEditor extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 882570456611829896L;

	private final LogSessionRepository repository;

	private LogSession session;

	private TextArea comment = new TextArea("Comment");

	private Button save = new Button("Save", VaadinIcons.SAFE);

	private Button cancel = new Button("Cancel");

	private CssLayout actions = new CssLayout(save, cancel);

	private Binder<LogSession> binder = new Binder<>(LogSession.class);

	@Autowired
	public SessionEditor(LogSessionRepository repository) {
		this.repository = repository;

		addComponents(comment, actions);

		binder.bindInstanceFields(this);

		comment.setWidth("100%");

		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);

		save.addClickListener(e -> repository.save(session));
		cancel.addClickListener(e -> editSession(session));
	}

	public interface ChangeHandler {
		void onChange();
	}

	public Optional<LogSession> getCurrentSession() {

		return Optional.ofNullable(session);
	}

	public void editSession(LogSession s) {
		if (s == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = s.getId() != null;
		if (persisted) {
			session = repository.findById(s.getId())
					.get();
		}
		cancel.setVisible(persisted);

		binder.setBean(session);
		setVisible(true);

		save.focus();

	}

	public void setChangeHandler(ChangeHandler h) {
		save.addClickListener(e -> h.onChange());
	}

}
