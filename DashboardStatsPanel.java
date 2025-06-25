package internetbilling;


import goicuoc.Package;
import goicuoc.PackageDAO;
import goicuoc.Subscription;
import goicuoc.SubscriptionDAO;
import lichsu.Transaction;
import lichsu.TransactionDAO;
import users.User;
import users.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel hiển thị thống kê tổng quan cho Admin
 */
public class DashboardStatsPanel extends JPanel {
	private UserDAO userDAO;
	private SubscriptionDAO subscriptionDAO;
	private TransactionDAO transactionDAO;
	private PackageDAO packageDAO;
	
	private JLabel totalUsersLabel;
	private JLabel activeSubscriptionsLabel;
	private JLabel totalRevenueLabel;
	private JLabel pendingPaymentsLabel;
	
	public DashboardStatsPanel() {
		userDAO = new UserDAO();
		subscriptionDAO = new SubscriptionDAO();
		transactionDAO = new TransactionDAO();
		packageDAO = new PackageDAO();
		
		initializeComponents();
		setupLayout();
		refreshData();
	}
	
	private void initializeComponents() {
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		// Khởi tạo labels
		totalUsersLabel = new JLabel("0");
		activeSubscriptionsLabel = new JLabel("0");
		totalRevenueLabel = new JLabel("0 VND");
		pendingPaymentsLabel = new JLabel("0");
		
		// Thiết lập font cho số liệu
		Font numberFont = new Font("SansSerif", Font.BOLD, 24);
		totalUsersLabel.setFont(numberFont);
		activeSubscriptionsLabel.setFont(numberFont);
		totalRevenueLabel.setFont(numberFont);
		pendingPaymentsLabel.setFont(numberFont);
	}
	
	private void setupLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Tiêu đề
		JLabel titleLabel = new JLabel("THỐNG KÊ TỔNG QUAN HỆ THỐNG");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		titleLabel.setForeground(new Color(52, 73, 94));
		
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
		gbc.insets = new Insets(0, 0, 30, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		add(titleLabel, gbc);
		
		// Reset gridwidth
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		
		// Tổng số người dùng
		JPanel usersPanel = createStatsCard("👥", "Tổng Người Dùng", totalUsersLabel, new Color(52, 152, 219));
		gbc.gridx = 0; gbc.gridy = 1;
		add(usersPanel, gbc);
		
		// Đăng ký đang hoạt động
		JPanel subscriptionsPanel = createStatsCard("📶", "Đăng Ký Hoạt Động", activeSubscriptionsLabel, new Color(46, 204, 113));
		gbc.gridx = 1; gbc.gridy = 1;
		add(subscriptionsPanel, gbc);
		
		// Tổng doanh thu
		JPanel revenuePanel = createStatsCard("💰", "Tổng Doanh Thu", totalRevenueLabel, new Color(241, 196, 15));
		gbc.gridx = 2; gbc.gridy = 1;
		add(revenuePanel, gbc);
		
		// Thanh toán chờ xử lý
		JPanel pendingPanel = createStatsCard("⏳", "Chờ Thanh Toán", pendingPaymentsLabel, new Color(231, 76, 60));
		gbc.gridx = 3; gbc.gridy = 1;
		add(pendingPanel, gbc);
		
		// Panel thống kê chi tiết
		JPanel detailPanel = createDetailPanel();
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
		gbc.weighty = 2.0;
		gbc.insets = new Insets(20, 10, 10, 10);
		add(detailPanel, gbc);
	}
	
	private JPanel createStatsCard(String icon, String title, JLabel valueLabel, Color accentColor) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
		));
		
		// Icon và title
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBackground(Color.WHITE);
		
		JLabel iconLabel = new JLabel(icon);
		iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 30));
		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		titleLabel.setForeground(new Color(127, 140, 141));
		
		topPanel.add(iconLabel);
		topPanel.add(Box.createHorizontalStrut(10));
		topPanel.add(titleLabel);
		
		// Value
		valueLabel.setForeground(accentColor);
		valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(valueLabel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createDetailPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
		));
		
		JLabel titleLabel = new JLabel("CHI TIẾT THỐNG KÊ");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		titleLabel.setForeground(new Color(52, 73, 94));
		panel.add(titleLabel, BorderLayout.NORTH);
		
		// Tạo bảng thống kê chi tiết
		String[] columnNames = {"Gói Cước", "Số Đăng Ký", "Doanh Thu", "Trạng Thái"};
		Object[][] data = getPackageStats();
		
		JTable table = new JTable(data, columnNames);
		table.setRowHeight(25);
		table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
		table.getTableHeader().setBackground(new Color(240, 240, 240));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(0, 200));
		panel.add(scrollPane, BorderLayout.CENTER);
		
		return panel;
	}
	
	private Object[][] getPackageStats() {
		try {
			List<goicuoc.Package> packages = packageDAO.getAllPackages();
			List<Subscription> subscriptions = subscriptionDAO.getAllSubscriptions();
			List<Transaction> transactions = transactionDAO.getAllTransactions();
			
			Object[][] data = new Object[packages.size()][4];
			
			for (int i = 0; i < packages.size(); i++) {
				Package pkg = packages.get(i);
				
				// Đếm số đăng ký
				long subscriptionCount = subscriptions.stream()
						.filter(s -> s.getPackageId() == pkg.getId() && "active".equals(s.getStatus()))
						.count();
				
				// Tính doanh thu
				BigDecimal revenue = transactions.stream()
						.filter(t -> t.getSubscription() != null &&
								t.getSubscription().getPackageInfo() != null &&
								t.getSubscription().getPackageInfo().getName().equals(pkg.getName()) &&
								"completed".equals(t.getStatus()))
						.map(Transaction::getAmount)
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				
				data[i][0] = pkg.getName();
				data[i][1] = subscriptionCount;
				data[i][2] = String.format("%,.0f VND", revenue);
				data[i][3] = pkg.isActive() ? "Hoạt động" : "Tạm dừng";
			}
			
			return data;
		} catch (Exception e) {
			System.err.println("Lỗi lấy thống kê packages: " + e.getMessage());
			return new Object[0][4];
		}
	}
	
	public void refreshData() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					// Lấy tổng số users
					List<User> users = userDAO.getAllUsers();
					SwingUtilities.invokeLater(() -> {
						totalUsersLabel.setText(String.valueOf(users.size()));
					});
					
					// Lấy số đăng ký hoạt động
					List<Subscription> activeSubscriptions = subscriptionDAO.getActiveSubscriptions();
					SwingUtilities.invokeLater(() -> {
						activeSubscriptionsLabel.setText(String.valueOf(activeSubscriptions.size()));
					});
					
					// Lấy tổng doanh thu
					List<Transaction> transactions = transactionDAO.getAllTransactions();
					BigDecimal totalRevenue = transactions.stream()
							.filter(t -> "completed".equals(t.getStatus()))
							.map(Transaction::getAmount)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					
					SwingUtilities.invokeLater(() -> {
						totalRevenueLabel.setText(String.format("%,.0f VND", totalRevenue));
					});
					
					// Lấy số thanh toán chờ xử lý
					long pendingCount = transactions.stream()
							.filter(t -> "pending".equals(t.getStatus()))
							.count();
					
					SwingUtilities.invokeLater(() -> {
						pendingPaymentsLabel.setText(String.valueOf(pendingCount));
					});
					
				} catch (Exception e) {
					System.err.println("Lỗi refresh data: " + e.getMessage());
				}
				return null;
			}
			
			@Override
			protected void done() {
				// Refresh detail panel
				removeAll();
				setupLayout();
				revalidate();
				repaint();
			}
		};
		
		worker.execute();
	}
}