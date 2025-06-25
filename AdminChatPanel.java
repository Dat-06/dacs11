package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class AdminChatPanel extends JPanel {
	private int adminId;
	private JPanel messagePanel;
	private JScrollPane scrollPane;
	private JTextField inputField;
	private JButton sendButton;
	private JButton emojiButton;
	private JComboBox<String> userSelector;
	
	private final Map<String, Integer> userMap = new HashMap<>();
	private ChatClientSocket clientSocket;
	
	public AdminChatPanel(int adminId, ChatClientSocket clientSocket) {
		this.adminId = adminId;
		this.clientSocket = clientSocket;
		initUI();
	}
	
	private void initUI() {
		setLayout(new BorderLayout());
		
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(messagePanel);
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		
		userSelector = new JComboBox<>();
		userSelector.addItem("TẤT CẢ");
		bottomPanel.add(userSelector, BorderLayout.WEST);
		
		inputField = new JTextField();
		emojiButton = new JButton("😊");
		emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
		emojiButton.setFocusable(false);
		
		emojiButton.addActionListener(e -> EmojiPicker.showPicker(
				(JFrame) SwingUtilities.getWindowAncestor(this),
				emojiButton,
				emoji -> {
					inputField.setText(inputField.getText() + emoji);
					inputField.requestFocus();
				}
		));
		
		JPanel centerPanel = new JPanel(new BorderLayout(5, 0));
		centerPanel.add(emojiButton, BorderLayout.WEST);
		centerPanel.add(inputField, BorderLayout.CENTER);
		
		sendButton = new JButton("Gửi");
		sendButton.addActionListener(e -> sendMessage());
		
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bottomPanel.add(centerPanel, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		userSelector.addActionListener(e -> {
			String selected = (String) userSelector.getSelectedItem();
			if (selected != null && !"TẤT CẢ".equals(selected)) {
				Integer selectedUserId = userMap.get(selected);
				if (selectedUserId != null) {
					loadChatHistory(selectedUserId);
				}
			}
		});
	}
	
	private void updateUserList(List<String[]> users) {
		userSelector.removeAllItems();
		userSelector.addItem("TẤT CẢ");
		userMap.clear();
		
		for (String[] data : users) {
			int id = Integer.parseInt(data[0]);
			String label = "User " + id + " (" + data[1] + ") [" + data[2] + "]";
			userSelector.addItem(label);
			userMap.put(label, id);
		}
	}
	
	
	private void sendMessage() {
		String content = inputField.getText().trim();
		if (content.isEmpty()) return;
		
		String selected = (String) userSelector.getSelectedItem();
		int receiverId = 0; // mặc định broadcast
		if (!"TẤT CẢ".equals(selected)) {
			receiverId = userMap.getOrDefault(selected, 0);
		}
		
		clientSocket.sendMessage(adminId, receiverId, content);
		addMessage(adminId, "Bạn: " + content);
		inputField.setText("");
	}
	
	public void receiveMessage(String msg) {
		if (msg.startsWith("BOT:")) {
			addMessage(0, "🤖 " + msg.substring(4), new Color(255, 240, 200));
		} else if (msg.startsWith("USER_LIST:")) {
			// USER_LIST:2,Nguyen Van A,online:3,Nguyen Van B,offline
			String[] parts = msg.split(":");
			List<String[]> parsedUsers = new ArrayList<>();
			
			for (int i = 1; i < parts.length; i++) {
				String[] data = parts[i].split(",", 3);
				if (data.length == 3) parsedUsers.add(data); // [id, name, status]
			}
			updateUserList(parsedUsers);
		} else {
			String[] parts = msg.split(":", 2);
			if (parts.length == 2) {
				int senderId = Integer.parseInt(parts[0]);
				String content = parts[1];
				addMessage(senderId, "User " + senderId + ": " + content);
			}
		}
	}
	
	private void addMessage(int senderId, String content) {
		addMessage(senderId, content, Color.WHITE);
	}
	
	private void addMessage(int senderId, String content, Color bgColor) {
		JPanel row = new JPanel(new FlowLayout(senderId == adminId ? FlowLayout.RIGHT : FlowLayout.LEFT));
		JLabel avatarLabel = new JLabel(AvatarUtil.getAvatarIcon(senderId, 32));
		
		JLabel messageLabel = new JLabel("<html><div style='width:280px;'>" + content + "</div></html>");
		messageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		messageLabel.setBackground(bgColor);
		messageLabel.setOpaque(true);
		messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		messageLabel.setPreferredSize(new Dimension(300, messageLabel.getPreferredSize().height));
		
		if (senderId == adminId) {
			row.add(messageLabel);
			row.add(avatarLabel);
		} else {
			row.add(avatarLabel);
			row.add(messageLabel);
		}
		
		messagePanel.add(row);
		messagePanel.revalidate();
		scrollToBottom();
	}
	
	private void loadChatHistory(int userId) {
		messagePanel.removeAll();
		ChatDAO chatDAO = new ChatDAO();
		List<ChatMessage> history = chatDAO.getChatHistory(adminId, userId);
		for (ChatMessage msg : history) {
			String prefix = msg.getSenderId() == adminId ? "Bạn: " : "User " + userId + ": ";
			String content = msg.isFile()
					? prefix + "[Tập tin] " + msg.getFileName()
					: prefix + msg.getContent();
			Color bg = msg.getSenderId() == adminId ? Color.WHITE : new Color(235, 255, 235);
			addMessage(msg.getSenderId(), content, bg);
		}
		revalidate();
		repaint();
	}
	
	private void scrollToBottom() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		});
	}
}