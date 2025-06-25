package admin;

import chat.AdminChatPanel;
import chat.ChatClientSocket;
import goicuoc.PackageManagementPanel;
import goicuoc.SubscriptionPanel;
import internetbilling.*;
import lichsu.TransactionHistoryPanel;
import login.LoginFrame;
import users.User;
import users.UserManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AdminDashboard extends JFrame {
	private User currentUser;
	private JPanel contentPanel;
	private CardLayout cardLayout;
	private JLabel welcomeLabel;
	private bieudo.ReportsChartPanel reportsPanel;
	private ChatClientSocket clientSocket;
	
	// Panels
	private DashboardStatsPanel statsPanel;
	private UserManagementPanel userPanel;
	private PackageManagementPanel packagePanel;
	private SubscriptionPanel subscriptionPanel;
	private TransactionHistoryPanel transactionPanel;
	private AdminChatPanel chatPanel;
	
	public AdminDashboard(User user) {
		this.currentUser = user;
		setTitle("Admin Dashboard - Internet Billing");
		setSize(1000, 650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		initializeComponents();
		setupMenu();
		setVisible(true);
	}
	
	private void initializeComponents() {
		welcomeLabel = new JLabel("Xin chào, Quản trị viên: " + currentUser.getFullName());
		welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(welcomeLabel, BorderLayout.NORTH);
		
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		
		statsPanel = new DashboardStatsPanel();
		userPanel = new UserManagementPanel();
		packagePanel = new PackageManagementPanel();
		subscriptionPanel = new SubscriptionPanel();
		transactionPanel = new TransactionHistoryPanel();
		
		// 🔧 Kết nối tới Chat Server
		JPanel chatFallbackPanel = new JPanel();
		chatFallbackPanel.add(new JLabel("Không thể kết nối đến server chat."));
		
		try {
			Socket socket = new Socket("localhost", 5000);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			clientSocket = new ChatClientSocket(out);
			
			AdminChatPanel adminChatPanel = new AdminChatPanel(currentUser.getId(), clientSocket);
			clientSocket.setAdminPanel(adminChatPanel);
			chatPanel = adminChatPanel;
			
			contentPanel.add(chatPanel, "Chat");
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến server chat!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			contentPanel.add(chatFallbackPanel, "Chat");
		}
		
		reportsPanel = new bieudo.ReportsChartPanel();
		
		contentPanel.add(statsPanel, "Stats");
		contentPanel.add(userPanel, "Users");
		contentPanel.add(packagePanel, "Packages");
		contentPanel.add(subscriptionPanel, "Subscriptions");
		contentPanel.add(transactionPanel, "Transactions");
		contentPanel.add(reportsPanel, "Reports");
		
		add(contentPanel, BorderLayout.CENTER);
	}
	
	private void setupMenu() {
		JPanel menuPanel = new JPanel(new GridLayout(0, 1));
		menuPanel.setPreferredSize(new Dimension(200, 0));
		
		String[] labels = {
				"Thống kê hệ thống", "Người dùng", "Gói cước", "Đăng ký", "Giao dịch", "Báo cáo", "Chat hỗ trợ", "Đăng xuất"
		};
		String[] actions = {
				"Stats", "Users", "Packages", "Subscriptions", "Transactions", "Reports", "Chat", "Logout"
		};
		
		for (int i = 0; i < labels.length; i++) {
			JButton btn = new JButton(labels[i]);
			String action = actions[i];
			
			btn.addActionListener((ActionEvent e) -> {
				if ("Logout".equals(action)) {
					dispose();
					new LoginFrame();
				} else {
					cardLayout.show(contentPanel, action);
				}
			});
			
			menuPanel.add(btn);
		}
		
		add(menuPanel, BorderLayout.WEST);
	}
}
