package goicuoc;

import users.User;
import users.UserDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class SubscriptionPanel extends JPanel {
	private SubscriptionDAO subscriptionDAO;
	private UserDAO userDAO;
	private PackageDAO packageDAO;
	private JTable subscriptionTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JButton addButton, editButton, deleteButton, refreshButton;
	private User currentUser;
	
	public SubscriptionPanel() {
		this(null);
	}
	
	public SubscriptionPanel(User user) {
		this.currentUser = user;
		subscriptionDAO = new SubscriptionDAO();
		userDAO = new UserDAO();
		packageDAO = new PackageDAO();
		
		initializeComponents();
		setupLayout();
		setupEventHandlers();
		refreshData();
	}
	
	private void initializeComponents() {
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		searchField = new JTextField(20);
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		
		addButton = new JButton("Thêm Đăng Ký");
		editButton = new JButton("Sửa");
		deleteButton = new JButton("Xóa");
		refreshButton = new JButton("Làm mới");
		
		if (currentUser != null && "user".equals(currentUser.getRole())) {
			addButton.setText("Đăng Ký Gói Mới");
			deleteButton.setVisible(false);
		}
		
		addButton.setBackground(new Color(46, 204, 113));
		addButton.setForeground(Color.black);
		editButton.setBackground(new Color(52, 152, 219));
		editButton.setForeground(Color.black);
		deleteButton.setBackground(new Color(231, 76, 60));
		deleteButton.setForeground(Color.black);
		refreshButton.setBackground(new Color(149, 165, 166));
		refreshButton.setForeground(Color.black);
		
		String[] columnNames = {"ID", "Người Dùng", "Gói Cước", "Tốc Độ", "Giá", "Trạng Thái", "Ngày Bắt Đầu", "Ngày Kết Thúc"};
		if (currentUser != null && "user".equals(currentUser.getRole())) {
			columnNames = new String[]{"ID", "Gói Cước", "Tốc Độ", "Giá", "Trạng Thái", "Ngày Bắt Đầu", "Ngày Kết Thúc"};
		}
		
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		subscriptionTable = new JTable(tableModel);
		subscriptionTable.setRowHeight(25);
		subscriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subscriptionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
		subscriptionTable.getTableHeader().setBackground(new Color(240, 240, 240));
	}
	
	private void setupLayout() {
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		
		String title = (currentUser != null && "user".equals(currentUser.getRole())) ? "ĐĂNG KÝ CỦA TÔI" : "QUẢN LÝ ĐĂNG KÝ";
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		titleLabel.setForeground(new Color(52, 73, 94));
		topPanel.add(titleLabel, BorderLayout.WEST);
		
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		searchPanel.setBackground(Color.WHITE);
		searchPanel.add(new JLabel("Tìm kiếm:"));
		searchPanel.add(searchField);
		topPanel.add(searchPanel, BorderLayout.EAST);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		if (deleteButton.isVisible()) {
			buttonPanel.add(deleteButton);
		}
		buttonPanel.add(refreshButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(subscriptionTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		
		add(mainPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void setupEventHandlers() {
		addButton.addActionListener(e -> showAddSubscriptionDialog());
		editButton.addActionListener(e -> showEditSubscriptionDialog());
		deleteButton.addActionListener(e -> deleteSelectedSubscription());
		refreshButton.addActionListener(e -> refreshData());
		
		searchField.addActionListener(e -> searchSubscriptions());
		
		subscriptionTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					showEditSubscriptionDialog();
				}
			}
		});
	}
	
	public void refreshData() {
		SwingWorker<List<Subscription>, Void> worker = new SwingWorker<>() {
			@Override
			protected List<Subscription> doInBackground() throws Exception {
				if (currentUser != null && "user".equals(currentUser.getRole())) {
					return subscriptionDAO.getSubscriptionsByUserId(currentUser.getId());
				} else {
					return subscriptionDAO.getAllSubscriptions();
				}
			}
			
			@Override
			protected void done() {
				try {
					List<Subscription> subscriptions = get();
					loadSubscriptionsToTable(subscriptions);
					if (currentUser == null || "admin".equals(currentUser.getRole())) {
						int renewed = subscriptionDAO.autoRenewSubscriptions();
						if (renewed > 0) {
							JOptionPane.showMessageDialog(SubscriptionPanel.this, "\u2714 Đã tự động gia hạn " + renewed + " gói cước!");
							refreshData();
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(SubscriptionPanel.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute();
	}
	
	private void showAddSubscriptionDialog() {
		SubscriptionDialog dialog = new SubscriptionDialog(SwingUtilities.getWindowAncestor(this), "Thêm Đăng Ký", null, currentUser);
		Subscription subscription = dialog.showDialog();
		if (subscription != null) {
			boolean success = subscriptionDAO.addSubscription(subscription);
			if (success) {
				JOptionPane.showMessageDialog(this, "Thêm đăng ký thành công!");
				refreshData();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể thêm đăng ký.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void showEditSubscriptionDialog() {
		int selectedRow = subscriptionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa.");
			return;
		}
		int subscriptionId = (int) tableModel.getValueAt(selectedRow, 0);
		Subscription subscription = subscriptionDAO.getSubscriptionById(subscriptionId);
		if (subscription == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy đăng ký tương ứng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		SubscriptionDialog dialog = new SubscriptionDialog(SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Đăng Ký", subscription, currentUser);
		Subscription updated = dialog.showDialog();
		if (updated != null) {
			boolean success = subscriptionDAO.updateSubscription(updated);
			if (success) {
				JOptionPane.showMessageDialog(this, "Cập nhật đăng ký thành công!");
				refreshData();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể cập nhật đăng ký.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void deleteSelectedSubscription() {
		int selectedRow = subscriptionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa.");
			return;
		}
		int subscriptionId = (int) tableModel.getValueAt(selectedRow, 0);
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đăng ký này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			boolean success = subscriptionDAO.deleteSubscription(subscriptionId);
			if (success) {
				JOptionPane.showMessageDialog(this, "Xóa thành công!");
				refreshData();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể xóa đăng ký.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void searchSubscriptions() {
		String keyword = searchField.getText().trim();
		List<Subscription> results;
		if (keyword.isEmpty()) {
			results = (currentUser != null && "user".equals(currentUser.getRole()))
					? subscriptionDAO.getSubscriptionsByUserId(currentUser.getId())
					: subscriptionDAO.getAllSubscriptions();
		} else {
			results = subscriptionDAO.searchSubscriptions(keyword);
			if (currentUser != null && "user".equals(currentUser.getRole())) {
				results.removeIf(s -> s.getUserId() != currentUser.getId());
			}
		}
		loadSubscriptionsToTable(results);
	}
	
	private void loadSubscriptionsToTable(List<Subscription> subscriptions) {
		tableModel.setRowCount(0);
		for (Subscription s : subscriptions) {
			Object[] row;
			if (currentUser != null && "user".equals(currentUser.getRole())) {
				row = new Object[]{
						s.getId(),
						s.getPackageInfo().getName(),
						s.getPackageInfo().getSpeed(),
						String.format("%,.0f VND", s.getPackageInfo().getPrice()),
						s.getStatus(),
						s.getStartDate(),
						s.getEndDate()
				};
			} else {
				row = new Object[]{
						s.getId(),
						s.getUser().getFullName(),
						s.getPackageInfo().getName(),
						s.getPackageInfo().getSpeed(),
						String.format("%,.0f VND", s.getPackageInfo().getPrice()),
						s.getStatus(),
						s.getStartDate(),
						s.getEndDate()
				};
			}
			tableModel.addRow(row);
		}
	}
}
