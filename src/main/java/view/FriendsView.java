package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Consumer<String> loadMessagesHandler = id -> {};
    private BiConsumer<String, String> sendMessageHandler = (id, text) -> {};
    private Consumer<String> blockFriendHandler = username -> {};
    private Consumer<String> unblockUserHandler = username -> {};
    private Consumer<String> removeFriendHandler = username -> {};
    private Consumer<String> sendFriendRequestHandler = username -> {};
    private Consumer<String> acceptFriendRequestHandler = username -> {};
    private Consumer<String> rejectFriendRequestHandler = username -> {};

    private final JPanel friendsListPanel = new JPanel();
    private final JPanel blockedUsersPanel = new JPanel();
    private final DefaultListModel<String> messagesModel = new DefaultListModel<>();
    private final JList<String> messagesList = new JList<>(messagesModel);
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    private final List<FriendItem> friends = new ArrayList<>();
    private final List<String> blockedUsers = new ArrayList<>();
    private String selectedFriendId = null;

    private Consumer<Integer> openRecipeHandler = recipeId -> {};
    private final Pattern recipePattern = Pattern.compile("RECIPE: .+? \\(ID: (\\d+)\\)");

    public FriendsView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Set custom cell renderer for recipe messages
        messagesList.setCellRenderer(new RecipeMessageCellRenderer());

        // Add click handling for recipe messages
        messagesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click to open recipe
                    int index = messagesList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String message = messagesModel.getElementAt(index);
                        handleRecipeMessageClick(message);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                int index = messagesList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    String message = messagesModel.getElementAt(index);
                    if (isRecipeMessage(message)) {
                        messagesList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        messagesList.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        });

        // Friends section
        friendsListPanel.setLayout(new BoxLayout(friendsListPanel, BoxLayout.Y_AXIS));
        JScrollPane friendsScroll = new JScrollPane(friendsListPanel);
        friendsScroll.setPreferredSize(new Dimension(280, 200));
        friendsScroll.setBorder(BorderFactory.createTitledBorder("Friends"));

        blockedUsersPanel.setLayout(new BoxLayout(blockedUsersPanel, BoxLayout.Y_AXIS));
        JScrollPane blockedScroll = new JScrollPane(blockedUsersPanel);
        blockedScroll.setPreferredSize(new Dimension(280, 100));
        blockedScroll.setBorder(BorderFactory.createTitledBorder("Blocked Users"));

        leftPanel.add(friendsScroll, BorderLayout.CENTER);
        leftPanel.add(blockedScroll, BorderLayout.SOUTH);

        JPanel messagesPanel = new JPanel(new BorderLayout(6, 6));
        messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        messagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messagesPanel.add(new JScrollPane(messagesList), BorderLayout.CENTER);

        JPanel inputBar = new JPanel(new BorderLayout(6, 6));
        inputBar.add(inputField, BorderLayout.CENTER);
        inputBar.add(sendButton, BorderLayout.EAST);
        messagesPanel.add(inputBar, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, messagesPanel);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        JPanel addFriendPanel = new JPanel(new BorderLayout(6, 6));
        addFriendPanel.setBorder(BorderFactory.createTitledBorder("Add Friend"));

        JTextField usernameField = new JTextField(15);
        JButton addButton = new JButton("Send Request");
        addButton.setBorderPainted(false);
        addButton.setBackground(new Color(0x28A745));
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);

        addFriendPanel.add(usernameField, BorderLayout.CENTER);
        addFriendPanel.add(addButton, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addFriendPanel, BorderLayout.NORTH);
        topPanel.add(split, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);

        sendButton.addActionListener(e -> {
            String txt = inputField.getText().trim();
            if (selectedFriendId == null || txt.isEmpty()) return;
            sendMessageHandler.accept(selectedFriendId, txt);
            messagesModel.addElement("You: " + txt);
            inputField.setText("");
        });

        addButton.addActionListener(e -> {
            String targetUsername = usernameField.getText().trim();
            if (!targetUsername.isEmpty()) {
                sendFriendRequestHandler.accept(targetUsername);
                usernameField.setText("");
            }
        });
    }

    // Custom cell renderer to style recipe messages differently
    private class RecipeMessageCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String message = (String) value;
            if (isRecipeMessage(message)) {
                setText("<html><b>" + message + "</b> 🍳</html>");
                setForeground(new Color(0, 100, 0)); // Dark green for recipe messages
                if (isSelected) {
                    setBackground(new Color(200, 255, 200)); // Light green when selected
                }
            } else {
                setText(message);
                setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            }

            return this;
        }
    }

    // Check if a message is a recipe message
    private boolean isRecipeMessage(String message) {
        return message != null && message.contains("RECIPE:") && message.contains("(ID:");
    }

    // Extract recipe ID from recipe message
    private Integer extractRecipeId(String message) {
        if (!isRecipeMessage(message)) return null;

        Matcher matcher = recipePattern.matcher(message);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Handle click on recipe message
    private void handleRecipeMessageClick(String message) {
        Integer recipeId = extractRecipeId(message);
        if (recipeId != null) {
            openRecipeHandler.accept(recipeId);
        }
    }

    // Add handler for opening recipes
    public void onOpenRecipe(Consumer<Integer> handler) {
        this.openRecipeHandler = handler != null ? handler : recipeId -> {};
    }

    public void showFriendRequestPopup(String requesterUsername) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    requesterUsername + " wants to be your friend. Accept?",
                    "Friend Request",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                acceptFriendRequestHandler.accept(requesterUsername);
            } else {
                rejectFriendRequestHandler.accept(requesterUsername);
            }
        });
    }

    public void onSendFriendRequest(Consumer<String> handler) {
        this.sendFriendRequestHandler = handler != null ? handler : username -> {};
    }

    public void onAcceptFriendRequest(Consumer<String> handler) {
        this.acceptFriendRequestHandler = handler != null ? handler : username -> {};
    }

    public void onRejectFriendRequest(Consumer<String> handler) {
        this.rejectFriendRequestHandler = handler != null ? handler : username -> {};
    }

    public void onLoadMessages(Consumer<String> handler) {
        this.loadMessagesHandler = handler != null ? handler : id -> {};
    }

    public void onSendMessage(BiConsumer<String, String> handler) {
        this.sendMessageHandler = handler != null ? handler : (id, txt) -> {};
    }

    public void setFriends(List<FriendItem> items) {
        friends.clear();
        if (items != null) friends.addAll(items);
        rebuildFriendsList();
    }

    public void setBlockedUsers(List<String> blockedUsersList) {
        blockedUsers.clear();
        if (blockedUsersList != null) blockedUsers.addAll(blockedUsersList);
        rebuildBlockedUsersList();
    }

    public void setMessages(List<String> msgs) {
        messagesModel.clear();
        if (msgs != null) msgs.forEach(messagesModel::addElement);
    }

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
            row.add(name, BorderLayout.WEST);

            if (f.following) {
                JButton removeButton = new JButton("Remove Friend");
                removeButton.setBackground(new Color(220, 53, 69));
                removeButton.setForeground(Color.WHITE);
                removeButton.setOpaque(true);
                removeButton.setBorderPainted(false);

                removeButton.addActionListener(e -> {
                    System.out.println("Remove button clicked for: " + f.name);

                    String[] options = {"Remove Friend", "Block User"};
                    int choice = JOptionPane.showOptionDialog(
                            this,
                            "Do you want to remove this user as a friend or block them? \n" +
                                    "Blocked users will not be able to send friend requests again",
                            "Remove Friend",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    System.out.println("Choice: " + choice);

                    if (choice == 0) {
                        System.out.println("Removing friend: " + f.id);
                        removeFriendHandler.accept(f.id);
                    } else if (choice == 1) {
                        System.out.println("Blocking user: " + f.id);
                        blockFriendHandler.accept(f.id);
                    }
                });

                row.add(removeButton, BorderLayout.EAST);
            }

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

    private void rebuildBlockedUsersList() {
        blockedUsersPanel.removeAll();

        for (String blockedUser : blockedUsers) {
            JPanel row = new JPanel(new BorderLayout(6, 6));
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            JLabel name = new JLabel(blockedUser);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            name.setForeground(Color.GRAY);
            row.add(name, BorderLayout.WEST);

            JButton unblockButton = new JButton("Unblock");
            unblockButton.addActionListener(e -> {
                unblockUserHandler.accept(blockedUser);
            });
            row.add(unblockButton, BorderLayout.EAST);

            blockedUsersPanel.add(row);
        }

        blockedUsersPanel.add(Box.createVerticalGlue());
        blockedUsersPanel.revalidate();
        blockedUsersPanel.repaint();
    }

    public void onBlockFriend(Consumer<String> handler) {
        this.blockFriendHandler = handler != null ? handler : username -> {};
    }

    public void onUnblockUser(Consumer<String> handler) {
        this.unblockUserHandler = handler != null ? handler : username -> {};
    }

    public void onRemoveFriend(Consumer<String> handler) {
        this.removeFriendHandler = handler != null ? handler : username -> {};
    }
}