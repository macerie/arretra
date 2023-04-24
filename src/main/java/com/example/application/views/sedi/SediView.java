package com.example.application.views.sedi;

import com.example.application.data.entity.Sedi;
import com.example.application.data.service.SediService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Sedi")
@Route(value = "sedi/:sediID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class SediView extends Div implements BeforeEnterObserver {

    private final String SEDI_ID = "sediID";
    private final String SEDI_EDIT_ROUTE_TEMPLATE = "sedi/%s/edit";

    private final Grid<Sedi> grid = new Grid<>(Sedi.class, false);

    private TextField sede;
    private TextField indirizzo;
    private TextField cap;
    private TextField citta;
    private TextField prov;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Sedi> binder;

    private Sedi sedi;

    private final SediService sediService;

    public SediView(SediService sediService) {
        this.sediService = sediService;
        addClassNames("sedi-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("sede").setAutoWidth(true);
        grid.addColumn("indirizzo").setAutoWidth(true);
        grid.addColumn("cap").setAutoWidth(true);
        grid.addColumn("citta").setAutoWidth(true);
        grid.addColumn("prov").setAutoWidth(true);
        grid.setItems(query -> sediService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SEDI_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SediView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Sedi.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sedi == null) {
                    this.sedi = new Sedi();
                }
                binder.writeBean(this.sedi);
                sediService.update(this.sedi);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(SediView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sediId = event.getRouteParameters().get(SEDI_ID).map(Long::parseLong);
        if (sediId.isPresent()) {
            Optional<Sedi> sediFromBackend = sediService.get(sediId.get());
            if (sediFromBackend.isPresent()) {
                populateForm(sediFromBackend.get());
            } else {
                Notification.show(String.format("The requested sedi was not found, ID = %s", sediId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SediView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        sede = new TextField("Sede");
        indirizzo = new TextField("Indirizzo");
        cap = new TextField("Cap");
        citta = new TextField("Citta");
        prov = new TextField("Prov");
        formLayout.add(sede, indirizzo, cap, citta, prov);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Sedi value) {
        this.sedi = value;
        binder.readBean(this.sedi);

    }
}
