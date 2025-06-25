package users;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import PASS.ChangePasswordPanel;
import PASS.SupportPanel;
import chat.ChatClientSocket;
import chat.ChatPanel;

import goicuoc.Subscription;
import goicuoc.SubscriptionDAO;
import goicuoc.SubscriptionPanel;
import lichsu.Transaction;
import lichsu.TransactionDAO;
import lichsu.TransactionHistoryPanel;

public class UserDashboard extends JFrame {
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JLabel subscriptionCountLabel;
	private JLabel pendingTransactionLabel;
	private User currentUser;
	private ChatClientSocket clientSocket;
	
	public UserDashboard(User user) {
		this.currentUser = user; // Gán user NGAY từ đầu
		setTitle("Internet Billing - User Dashboard");
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// Sidebar
		JPanel sidebar = createSidebar(user);
		add(sidebar, BorderLayout.WEST);
		
		// Main content
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		mainPanel.add(new UserInfoPanel(user), "Info");
		mainPanel.add(new SubscriptionPanel(user), "Subscription");
		mainPanel.add(new TransactionHistoryPanel(user), "Transactions");
		
		// Kết nối Chat Server
		try {
			Socket socket = new Socket("localhost", 5000);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			this.clientSocket = new ChatClientSocket(out); // sử dụng this.clientSocket
			
			// ChatPanel
			ChatPanel chatPanel = new ChatPanel(currentUser.getId(), clientSocket);
			clientSocket.setUserPanel(chatPanel);
			mainPanel.add(chatPanel, "Chat");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Không thể kết nối server chat", "Lỗi", JOptionPane.ERROR_MESSAGE);
			mainPanel.add(new JLabel("Không thể khởi tạo chat"), "Chat");
		}
		
		mainPanel.add(new ChangePasswordPanel(user), "ChangePassword");
		mainPanel.add(new SupportPanel(), "Support");
		
		add(mainPanel, BorderLayout.CENTER);
		setVisible(true);
	}
	
	
	private JPanel createSidebar(User user) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(220, 0));
		panel.setBackground(new Color(236, 240, 241));
		
		// Header user info
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(new Color(52, 152, 219));
		header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		
		JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
		avatar.setFont(new Font("SansSerif", Font.PLAIN, 48));
		avatar.setForeground(Color.WHITE);
		
		JLabel nameLabel = new JLabel("<html><center>" + user.getFullName() + "</center></html>", SwingConstants.CENTER);
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		header.add(avatar, BorderLayout.NORTH);
		header.add(nameLabel, BorderLayout.SOUTH);
		
		// Sidebar buttons
		JPanel menuPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		menuPanel.setBackground(new Color(236, 240, 241));
		
		String[] labels = {"Thông tin cá nhân", "Gói cước", "Lịch sử giao dịch", "Chat", "Đổi mật khẩu", "Hỗ trợ"};
		String[] actions = {"Info", "Subscription", "Transactions", "Chat", "ChangePassword", "Support"};
		
		for (int i = 0; i < labels.length; i++) {
			JButton btn = new JButton(labels[i]);
			btn.setFocusPainted(false);
			btn.setBackground(new Color(255, 255, 255));
			btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
			final String action = actions[i];
			btn.addActionListener(e -> cardLayout.show(mainPanel, action));
			menuPanel.add(btn);
		}
		
		// Extra Info
		JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		infoPanel.setBackground(new Color(236, 240, 241));
		
		subscriptionCountLabel = new JLabel("Gói cước đang dùng: 0");
		subscriptionCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		subscriptionCountLabel.setForeground(new Color(44, 62, 80));
		
		pendingTransactionLabel = new JLabel("Giao dịch đang chờ: 0");
		pendingTransactionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		pendingTransactionLabel.setForeground(new Color(192, 57, 43));
		
		infoPanel.add(subscriptionCountLabel);
		infoPanel.add(pendingTransactionLabel);
		
		// Combine all
		panel.add(header, BorderLayout.NORTH);
		panel.add(menuPanel, BorderLayout.CENTER);
		panel.add(infoPanel, BorderLayout.SOUTH);
		
		// Load dynamic stats
		updateSidebarInfo(user);
		
		return panel;
	}
	
	private void updateSidebarInfo(User user) {
		SwingUtilities.invokeLater(() -> {
			// Load active subscriptions
			SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
			List<Subscription> subscriptions = subscriptionDAO.getSubscriptionsByUserId(user.getId());
			long activeCount = subscriptions.stream()
					.filter(s -> "active".equalsIgnoreCase(s.getStatus()))
					.count();
			subscriptionCountLabel.setText("Gói cước đang dùng: " + activeCount);
			
			// Load pending transactions
			TransactionDAO transactionDAO = new TransactionDAO();
			List<Transaction> transactions = transactionDAO.getTransactionsByUserId(user.getId());
			long pendingCount = transactions.stream()
					.filter(t -> "pending".equalsIgnoreCase(t.getStatus()))
					.count();
			pendingTransactionLabel.setText("Giao dịch đang chờ: " + pendingCount);
		});
	}

}
