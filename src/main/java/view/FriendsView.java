package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    private Consumer<String> loadMessagesHandler = id -> {
    };
    private BiConsumer<String, String> sendMessageHandler = (id, text) -> {
    };

    private Consumer<String> blockFriendHandler = username -> {};
    private Consumer<String> unblockUserHandler = username -> {};

    private final JPanel friendsListPanel = new JPanel();
    private final JPanel blockedUsersPanel = new JPanel();
    private final DefaultListModel<String> messagesModel = new DefaultListModel<>();
    private final JList<String> messagesList = new JList<>(messagesModel);
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    private final List<FriendItem> friends = new ArrayList<>();
    private final List<String> blockedUsers = new ArrayList<>();
    private String selectedFriendId = null;

    public FriendsView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

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

        // Add the panel to the top of the main panel
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

    private Consumer<String> sendFriendRequestHandler = username -> {};

    public void onSendFriendRequest(Consumer<String> handler) {
        this.sendFriendRequestHandler = handler != null ? handler : username -> {};
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

    private Consumer<String> acceptFriendRequestHandler = username -> {};
    private Consumer<String> rejectFriendRequestHandler = username -> {};

    public void onAcceptFriendRequest(Consumer<String> handler) {
        this.acceptFriendRequestHandler = handler != null ? handler : username -> {};
    }

    public void onRejectFriendRequest(Consumer<String> handler) {
        this.rejectFriendRequestHandler = handler != null ? handler : username -> {};
    }

    public void onLoadMessages(Consumer<String> handler) {
        this.loadMessagesHandler = handler != null ? handler : id -> {
        };
    }

    public void onSendMessage(BiConsumer<String, String> handler) {
        this.sendMessageHandler = handler != null ? handler : (id, txt) -> {
        };
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

    private Consumer<String> removeFriendHandler = username -> {};

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
                removeButton.setBackground(new Color(220, 53, 69)); // Red background
                removeButton.setForeground(Color.WHITE); // White text

                removeButton.setOpaque(true);
                removeButton.setBorderPainted(false);

                JButton removeButton2 = new JButton("Remove Friendship");
                JButton blockButton2 = new JButton("Block User");

                removeButton2.setBackground(new Color(0x4682B4));
                blockButton2.setBackground(new Color(220, 53, 69));

                removeButton2.setForeground(Color.WHITE);
                blockButton2.setForeground(Color.WHITE);

                removeButton2.setOpaque(true);
                blockButton2.setOpaque(true);

                removeButton2.setBorderPainted(false);
                blockButton2.setBorderPainted(false);

                blockButton2.setFocusPainted(false);
                removeButton2.setFocusPainted(false);

                removeButton.addActionListener(e -> {
                    int choice = JOptionPane.showOptionDialog(
                            this,
                            "Do you want to remove " + f.name + " as a friend or block this user?",
                            "Remove Friend",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{removeButton2,blockButton2},
                            null
                    );

                    if (choice == 0) {
                        removeFriendHandler.accept(f.id);
                    } else if (choice == 1) {
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
            name.setForeground(Color.GRAY); // Make blocked users appear grayed out
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
        rebuildBlockedUsersList();
    }

    public void onUnblockUser(Consumer<String> handler) {
        this.unblockUserHandler = handler != null ? handler : username -> {};
        rebuildBlockedUsersList();
    }
    public void onRemoveFriend(Consumer<String> handler) {
        this.removeFriendHandler = handler != null ? handler : username -> {};

    }
}