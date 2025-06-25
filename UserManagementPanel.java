package users;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
	private JTable userTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JButton addButton, editButton, deleteButton, refreshButton;
	
	public UserManagementPanel() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createTitledBorder("Quản lý người dùng"));
		
		// Bảng
		tableModel = new DefaultTableModel(new Object[]{
				"ID", "Tên đăng nhập", "Họ tên", "Email", "SĐT", "Địa chỉ", "Vai trò"
		}, 0);
		userTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(userTable);
		
		// Toolbar
		JPanel topPanel = new JPanel(new BorderLayout(10, 10));
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		searchField = new JTextField(20);
		JButton searchButton = new JButton("Tìm");
		
		addButton = new JButton("Thêm");
		editButton = new JButton("Sửa");
		deleteButton = new JButton("Xóa");
		refreshButton = new JButton("Làm mới");
		
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(refreshButton);
		
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		searchPanel.add(new JLabel("Tìm:"));
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		
		topPanel.add(buttonPanel, BorderLayout.WEST);
		topPanel.add(searchPanel, BorderLayout.EAST);
		
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		
		// Load dữ liệu ban đầu
		loadData();
		
		// Sự kiện
		refreshButton.addActionListener(e -> loadData());
		searchButton.addActionListener(e -> searchUsers());
		deleteButton.addActionListener(e -> deleteSelectedUser());
		
		// Gợi ý mở rộng:
		// - Mở dialog để thêm/sửa
		// - Validate khi xóa
	}
	
	private void loadData() {
		UserDAO dao = new UserDAO();
		List<User> users = dao.getAllUsers();
		tableModel.setRowCount(0);
		for (User u : users) {
			tableModel.addRow(new Object[]{
					u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getPhone(), u.getAddress(), u.getRole()
			});
		}
	}
	
	private void searchUsers() {
		String keyword = searchField.getText().trim().toLowerCase();
		if (keyword.isEmpty()) {
			loadData();
			return;
		}
		
		UserDAO dao = new UserDAO();
		List<User> users = dao.getAllUsers();
		tableModel.setRowCount(0);
		
		for (User u : users) {
			if (u.getUsername().toLowerCase().contains(keyword) ||
					u.getFullName().toLowerCase().contains(keyword) ||
					u.getEmail().toLowerCase().contains(keyword)) {
				tableModel.addRow(new Object[]{
						u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getPhone(), u.getAddress(), u.getRole()
				});
			}
		}
	}
	
	private void deleteSelectedUser() {
		int selectedRow = userTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa.");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa người dùng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			int userId = (int) tableModel.getValueAt(selectedRow, 0);
			UserDAO dao = new UserDAO();
			if (dao.deleteUser(userId)) {
				JOptionPane.showMessageDialog(this, "Xóa thành công!");
				loadData();
			} else {
				JOptionPane.showMessageDialog(this, "Xóa thất bại!");
			}
		}
	}
}
