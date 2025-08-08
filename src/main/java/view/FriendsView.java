package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class FriendsView extends JPanel {

    public static class FriendItem {
        public final String id;
        public final String name;
        public boolean following;


        public FriendItem(String id, String name, boolean following) {
            this.id = id;
            this.name = name;
            this.following = following;
        }
    }

    private Consumer<String> followHandler = id -> {
    };
    private Consumer<String> unfollowHandler = id -> {
    };
    private Consumer<String> loadMessagesHandler = id -> {
    };
    private BiConsumer<String, String> sendMessageHandler = (id, text) -> {
    };

    // UI
    private final JPanel friendsListPanel = new JPanel();
    private final DefaultListModel<String> messagesModel = new DefaultListModel<>();
    private final JList<String> messagesList = new JList<>(messagesModel);
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    // State
    private final List<FriendItem> friends = new ArrayList<>();
    private String selectedFriendId = null;

    public FriendsView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: friends list (scrollable)
        friendsListPanel.setLayout(new BoxLayout(friendsListPanel, BoxLayout.Y_AXIS));
        JScrollPane friendsScroll = new JScrollPane(friendsListPanel);
        friendsScroll.setPreferredSize(new Dimension(280, 0));
        friendsScroll.setBorder(BorderFactory.createTitledBorder("Friends"));

        // Right: messages + input
        JPanel messagesPanel = new JPanel(new BorderLayout(6, 6));
        messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        messagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messagesPanel.add(new JScrollPane(messagesList), BorderLayout.CENTER);

        JPanel inputBar = new JPanel(new BorderLayout(6, 6));
        inputBar.add(inputField, BorderLayout.CENTER);
        inputBar.add(sendButton, BorderLayout.EAST);
        messagesPanel.add(inputBar, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, friendsScroll, messagesPanel);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        JPanel addFriendPanel = new JPanel(new BorderLayout(6, 6));
        addFriendPanel.setBorder(BorderFactory.createTitledBorder("Add Friend"));

        JTextField usernameField = new JTextField(15);
        JButton addButton = new JButton("Send Request");

        addFriendPanel.add(usernameField, BorderLayout.CENTER);
        addFriendPanel.add(addButton, BorderLayout.EAST);

// Add the panel to the top of the main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addFriendPanel, BorderLayout.NORTH);
        topPanel.add(split, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);

        // Send logic
        sendButton.addActionListener(e -> {
            String txt = inputField.getText().trim();
            if (selectedFriendId == null || txt.isEmpty()) return;
            sendMessageHandler.accept(selectedFriendId, txt);
            messagesModel.addElement("You: " + txt);
            inputField.setText("");
        });
    }

    public void onFollow(Consumer<String> handler) {
        this.followHandler = handler != null ? handler : id -> {
        };
    }

    public void onUnfollow(Consumer<String> handler) {
        this.unfollowHandler = handler != null ? handler : id -> {
        };
    }

    public void onLoadMessages(Consumer<String> handler) {
        this.loadMessagesHandler = handler != null ? handler : id -> {
        };
    }

    public void onSendMessage(BiConsumer<String, String> handler) {
        this.sendMessageHandler = handler != null ? handler : (id, txt) -> {
        };
    }

    // Populate/refresh friends list
    public void setFriends(List<FriendItem> items) {
        friends.clear();
        if (items != null) friends.addAll(items);
        rebuildFriendsList();
    }

    public void setMessages(List<String> msgs) {
        messagesModel.clear();
        if (msgs != null) msgs.forEach(messagesModel::addElement);
    }

    // Helpers
    private void rebuildFriendsList() {
        friendsListPanel.removeAll();

        for (FriendItem f : friends) {
            JPanel row = new JPanel(new BorderLayout(6, 6));
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            JLabel name = new JLabel(f.name);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            row.add(name, BorderLayout.CENTER);

            JButton action = new JButton(f.following ? "Unfollow" : "Follow");
            action.addActionListener(e -> {
                if (f.following) {
                    unfollowHandler.accept(f.id);
                    f.following = false;
                    action.setText("Follow");
                } else {
                    followHandler.accept(f.id);
                    f.following = true;
                    action.setText("Unfollow");
                }
            });
            row.add(action, BorderLayout.EAST);

            // Select friend to load messages
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            row.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedFriendId = f.id;
                    messagesModel.clear();
                    loadMessagesHandler.accept(f.id);
                }
            });

            friendsListPanel.add(row);
        }

        friendsListPanel.add(Box.createVerticalGlue());
        friendsListPanel.revalidate();
        friendsListPanel.repaint();
    }
}