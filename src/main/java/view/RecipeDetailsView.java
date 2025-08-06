package view;

import entity.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RecipeDetailsView extends JDialog {
    private final JButton backButton = new JButton("Back");

    public RecipeDetailsView(JFrame owner, SearchResult recipe) {
        super(owner, recipe.getTitle(), true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 700));

        // --- NORTH: Title + summary ---
        JPanel north = new JPanel(new BorderLayout());
        north.add(new JLabel("<html><h1>" + recipe.getTitle() + "</h1></html>"), BorderLayout.CENTER);
        north.add(new JLabel(recipe.getReadyInMinutes() + " min  •  Serves " + recipe.getServings()),
                BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // --- CENTER: Tabs for Ingredients / Nutrition / Instructions ---
        JTabbedPane tabs = new JTabbedPane();

        // Ingredients tab
        DefaultListModel<String> ingrModel = new DefaultListModel<>();
        for (var i : recipe.getIngredients()) {
            ingrModel.addElement(String.format("%s: %.1f %s", i.getName(), i.getMetricValue(), i.getMetricUnit()));
        }
        tabs.add("Ingredients", new JScrollPane(new JList<>(ingrModel)));

        // Nutrition tab
        DefaultListModel<String> nutModel = new DefaultListModel<>();
        for (var n : recipe.getNutrition()) {
            nutModel.addElement(String.format("%s: %.1f %s (%.0f%%)",
                    n.getName(), n.getAmount(), n.getUnit(), n.getPercentOfDailyNeeds()));
        }
        tabs.add("Nutrition", new JScrollPane(new JList<>(nutModel)));

        // Instructions tab
        DefaultListModel<String> instrModel = new DefaultListModel<>();
        for (var s : recipe.getInstructions()) {
            instrModel.addElement(s.getNumber() + ". " + s.getStep());
        }
        tabs.add("Instructions", new JScrollPane(new JList<>(instrModel)));

        add(tabs, BorderLayout.CENTER);

        // --- SOUTH: Back button ---
        JPanel south = new JPanel();
        south.add(backButton);
        add(south, BorderLayout.SOUTH);

        backButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }
}
