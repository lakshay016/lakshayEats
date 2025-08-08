package view;


import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppShell extends JPanel {
    public static final String SEARCH  = "Search";
    public static final String SAVED   = "Saved";
    public static final String FEED    = "Feed";
    public static final String FRIENDS = "Friends";
    public static final String ACCOUNT = "Account";

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, JComponent> pages = new LinkedHashMap<>();

    public AppShell(JComponent search, JComponent saved, JComponent feed,
                    JComponent friends, JComponent account) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        pages.put(SEARCH,  search);
        pages.put(SAVED,   saved);
        pages.put(FEED,    feed);
        pages.put(FRIENDS, friends);
        pages.put(ACCOUNT, account);

        for (var e : pages.entrySet()) content.add(e.getValue(), e.getKey());

        add(makeToolbar(), BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        show(SEARCH);
    }

    private JToolBar makeToolbar() {
        var tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

        for (String name : pages.keySet()) {
            var b = new JButton(name);
            b.setFocusable(false);
            b.addActionListener(e -> show(name));
            tb.add(b);
        }

        tb.add(Box.createHorizontalGlue());
        return tb;
    }

    private void show(String name) { cards.show(content, name); }
}
