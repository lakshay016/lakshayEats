package view.search;

import entity.FilterOptions;
import entity.SearchResult;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchController;
import view.RecipeDetailsView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class SearchView extends JPanel implements PropertyChangeListener {
    private final SearchViewModel viewModel;
    private final SearchController controller;
    private final FilterOptions defaultFilters;

    private final JTextField searchField = new JTextField(20);
    private final JButton searchButton = new JButton("Search");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> resultList = new JList<>(listModel);

    public SearchView(SearchViewModel vm,
                      SearchController ctl,
                      FilterOptions defaultFilters) {
        this.viewModel      = vm;
        this.controller     = ctl;
        this.defaultFilters = defaultFilters;

        setLayout(new BorderLayout(8,8));

        // Top: search bar + button
        JPanel top = new JPanel();
        top.add(searchField);
        top.add(searchButton);
        add(top, BorderLayout.NORTH);

        // Center: results list
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(resultList), BorderLayout.CENTER);

        // Listen to VM changes
        viewModel.addPropertyChangeListener(this);

        // Wire button
        searchButton.addActionListener(e -> {
            String q = searchField.getText().trim();
            controller.handleSearch(q, defaultFilters);
        });

        // Double-click a result to open details
        resultList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = resultList.getSelectedIndex();
                    if (idx >= 0) {
                        SearchResult sr = viewModel.getState().getResults().get(idx);
                        new RecipeDetailsView(
                                (JFrame) SwingUtilities.getWindowAncestor(SearchView.this),
                                sr
                        ).setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // whenever VM state changes, repopulate the list
        List<SearchResult> results = viewModel.getState().getResults();
        listModel.clear();
        for (SearchResult r : results) {
            listModel.addElement(r.getTitle());
        }
    }
}
