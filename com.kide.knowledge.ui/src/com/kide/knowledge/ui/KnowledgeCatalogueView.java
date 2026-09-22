package com.kide.knowledge.ui;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.kide.knowledge.EmbeddedKnowledgeRepository;
import com.kide.knowledge.KnowledgeCatalogueItem;
import com.kide.knowledge.KnowledgeCatalogueQuery;
import com.kide.knowledge.KnowledgeCatalogueService;
import com.kide.knowledge.KnowledgeVocabulary;

public final class KnowledgeCatalogueView extends ViewPart {
    private static final String[] TYPE_LABELS = {
            "All concepts", "Capability", "Device", "Workflow",
            "Interface", "Behavior", "Interaction"
    };

    private Text queryText;
    private Combo typeCombo;
    private Label projectLabel;
    private Label statusLabel;
    private Table table;
    private Text details;
    private List<KnowledgeCatalogueItem> visibleItems = List.of();

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        projectLabel = new Label(parent, SWT.WRAP);
        projectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite controls = new Composite(parent, SWT.NONE);
        controls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        controls.setLayout(new GridLayout(3, false));

        queryText = new Text(controls, SWT.SEARCH | SWT.ICON_SEARCH | SWT.CANCEL | SWT.BORDER);
        queryText.setMessage("Search capability, device, role or property");
        queryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        typeCombo = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
        typeCombo.setItems(TYPE_LABELS);
        typeCombo.select(0);

        Button search = new Button(controls, SWT.PUSH);
        search.setText("Search");
        search.addListener(SWT.Selection, event -> refreshCatalogue());
        queryText.addListener(SWT.DefaultSelection, event -> refreshCatalogue());

        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addColumn("Label", 220);
        addColumn("Type", 150);
        addColumn("Authority", 140);
        addColumn("Source", 240);

        details = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        GridData detailData = new GridData(SWT.FILL, SWT.FILL, true, false);
        detailData.heightHint = 110;
        details.setLayoutData(detailData);

        statusLabel = new Label(parent, SWT.WRAP);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        table.addListener(SWT.Selection, event -> showSelectedDetails());
        refreshCatalogue();
    }

    @Override
    public void setFocus() {
        if (queryText != null && !queryText.isDisposed()) queryText.setFocus();
    }

    private void refreshCatalogue() {
        IProject project = activeProject();
        table.removeAll();
        visibleItems = List.of();
        details.setText("");
        if (project == null || project.getLocation() == null) {
            projectLabel.setText("No open project selected.");
            statusLabel.setText("Open or select a project to browse its knowledge catalogue.");
            return;
        }

        projectLabel.setText("Project: " + project.getName());
        try {
            Path root = project.getLocation().toFile().toPath();
            KnowledgeCatalogueService catalogue =
                    new KnowledgeCatalogueService(new EmbeddedKnowledgeRepository(root));
            var result = catalogue.query(new KnowledgeCatalogueQuery(
                    queryText.getText(),
                    selectedTypes(),
                    500));
            visibleItems = result.items();
            for (KnowledgeCatalogueItem item : visibleItems) {
                TableItem row = new TableItem(table, SWT.NONE);
                row.setText(new String[] {
                        item.label(),
                        item.types().stream().map(KnowledgeCatalogueView::compact).sorted()
                                .reduce((a, b) -> a + ", " + b).orElse(""),
                        item.authority(),
                        item.provenanceSource()
                });
            }
            for (TableColumn column : table.getColumns()) {
                if (column.getWidth() < 100) column.pack();
            }
            statusLabel.setText(
                    visibleItems.size() + " result(s) · knowledge revision " + result.revision()
                            + (result.cached() ? " · cached" : ""));
        } catch (RuntimeException e) {
            statusLabel.setText("Knowledge catalogue unavailable: " + safe(e.getMessage()));
        }
    }

    private void showSelectedDetails() {
        int index = table.getSelectionIndex();
        if (index < 0 || index >= visibleItems.size()) {
            details.setText("");
            return;
        }
        KnowledgeCatalogueItem item = visibleItems.get(index);
        StringBuilder value = new StringBuilder();
        value.append(item.iri()).append("\n");
        item.properties().forEach((predicate, values) -> value
                .append(compact(predicate)).append(": ")
                .append(String.join(", ", values)).append("\n"));
        details.setText(value.toString());
    }

    private Set<String> selectedTypes() {
        return switch (typeCombo.getSelectionIndex()) {
            case 1 -> Set.of(KnowledgeVocabulary.CAPABILITY);
            case 2 -> Set.of(KnowledgeVocabulary.DEVICE);
            case 3 -> Set.of(KnowledgeVocabulary.WORKFLOW);
            case 4 -> Set.of(KnowledgeVocabulary.INTERFACE);
            case 5 -> Set.of(KnowledgeVocabulary.BEHAVIOR);
            case 6 -> Set.of(KnowledgeVocabulary.INTERACTION);
            default -> Set.of();
        };
    }

    private IProject activeProject() {
        try {
            var window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null) {
                var selection = window.getSelectionService().getSelection();
                if (selection instanceof IStructuredSelection structured) {
                    Object first = structured.getFirstElement();
                    if (first instanceof IResource resource && resource.getProject().isOpen()) {
                        return resource.getProject();
                    }
                }
            }
        } catch (RuntimeException ignored) {
            // Fall back to the first open workspace project.
        }
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (project.isOpen() && project.getLocation() != null) return project;
        }
        return null;
    }

    private void addColumn(String title, int width) {
        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText(title);
        column.setWidth(width);
    }

    private static String compact(String iri) {
        int split = Math.max(Math.max(iri.lastIndexOf('#'), iri.lastIndexOf('/')),
                iri.lastIndexOf(':'));
        return split >= 0 && split + 1 < iri.length() ? iri.substring(split + 1) : iri;
    }

    private static String safe(String message) {
        if (message == null || message.isBlank()) return "repository error";
        return message.replace('\r', ' ').replace('\n', ' ');
    }
}
