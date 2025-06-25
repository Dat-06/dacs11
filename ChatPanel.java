package chat;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
	private static final int ADMIN_ID = 1;
	
	private int userId;
	private ChatClientSocket clientSocket;
	
	private JPanel messagePanel;
	private JScrollPane scrollPane;
	private JTextField inputField;
	private JButton sendButton;
	private JButton emojiButton;
	
	public ChatPanel(int userId, ChatClientSocket socket) {
		this.userId = userId;
		this.clientSocket = socket;
		initUI();
		loadChatHistory();
	}
	
	private void initUI() {
		setLayout(new BorderLayout());
		
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(messagePanel);
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
		inputField = new JTextField();
		sendButton = new JButton("Gửi");
		emojiButton = new JButton("😊");
		
		emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
		emojiButton.setFocusable(false);
		
		emojiButton.addActionListener(e -> {
			EmojiPicker.showPicker((JFrame) SwingUtilities.getWindowAncestor(this), emojiButton, emoji -> {
				inputField.setText(inputField.getText() + emoji);
				inputField.requestFocus();
			});
		});
		
		sendButton.addActionListener(e -> sendMessage());
		
		JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
		leftPanel.add(emojiButton, BorderLayout.WEST);
		leftPanel.add(inputField, BorderLayout.CENTER);
		
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bottomPanel.add(leftPanel, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);
		
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void sendMessage() {
		String content = inputField.getText().trim();
		if (content.isEmpty()) return;
		
		clientSocket.sendMessage(userId, ADMIN_ID, content);
		addMessage(userId, "Bạn: " + content);
		inputField.setText("");
	}
	
	public void receiveMessage(String msg) {
		if (msg.startsWith("BOT:")) {
			addMessage(ADMIN_ID, "🤖 " + msg.substring(4), new Color(255, 240, 200));
		} else {
			String[] parts = msg.split(":", 2);
			if (parts.length == 2) {
				int senderId = Integer.parseInt(parts[0]);
				String content = parts[1];
				addMessage(senderId, content);
			}
		}
	}
	
	private void addMessage(int senderId, String content) {
		addMessage(senderId, content, Color.WHITE);
	}
	
	private void addMessage(int senderId, String content, Color bgColor) {
		JPanel row = new JPanel(new FlowLayout(senderId == userId ? FlowLayout.RIGHT : FlowLayout.LEFT));
		JLabel avatarLabel = new JLabel(AvatarUtil.getAvatarIcon(senderId, 32));
		
		JLabel messageLabel = new JLabel("<html><div style='width:280px;'>" + content + "</div></html>");
		messageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		messageLabel.setBackground(bgColor);
		messageLabel.setOpaque(true);
		messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		messageLabel.setPreferredSize(new Dimension(300, messageLabel.getPreferredSize().height));
		
		if (senderId == userId) {
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
	
	private void scrollToBottom() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		});
	}
	private void loadChatHistory() {
		SwingUtilities.invokeLater(() -> {
			ChatDAO chatDAO = new ChatDAO();
			var history = chatDAO.getChatHistory(userId, ADMIN_ID);
			
			for (ChatMessage msg : history) {
				String prefix = (msg.getSenderId() == userId) ? "Bạn: " : "";
				Color bg = (msg.getSenderId() == userId) ? Color.WHITE : new Color(235, 255, 235);
				
				String content;
				if (msg.isFile()) {
					content = prefix + "[Tập tin] " + msg.getFileName();
				} else {
					content = prefix + msg.getContent();
				}
				addMessage(msg.getSenderId(), content, bg);
			}
		});
	}
	
}
