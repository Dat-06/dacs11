package goicuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PackageManagementPanel extends JPanel {
	private final PackageDAO packageDAO = new PackageDAO();
	private final JTable packageTable;
	private final DefaultTableModel tableModel;
	private final JTextField searchField = new JTextField(20);
	private final JButton addButton = new JButton("Thêm Gói");
	private final JButton editButton = new JButton("Sửa");
	private final JButton deleteButton = new JButton("Xóa");
	private final JButton refreshButton = new JButton("Làm mới");
	
	public PackageManagementPanel() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		tableModel = new DefaultTableModel(new String[]{"ID", "Tên Gói", "Tốc Độ", "Giá (VND)", "Mô Tả", "Trạng Thái", "Tự Động Gia Hạn", "Ngày Tạo"}, 0) {
			public boolean isCellEditable(int row, int column) { return false; }
		};
		
		packageTable = new JTable(tableModel);
		packageTable.setRowHeight(25);
		packageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		packageTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
		
		configureColumnWidths();
		initLayout();
		initEvents();
		refreshData();
	}
	
	private void configureColumnWidths() {
		int[] widths = {50, 150, 100, 120, 200, 100, 150, 120};
		for (int i = 0; i < widths.length; i++) {
			packageTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}
	
	private void initLayout() {
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("QUẢN LÝ GÓI CƯỜ INTERNET", JLabel.LEFT);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		titleLabel.setForeground(new Color(52, 73, 94));
		
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		searchPanel.setBackground(Color.WHITE);
		searchPanel.add(new JLabel("Tìm kiếm:"));
		searchPanel.add(searchField);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addStyledButton(addButton, new Color(46, 204, 113));
		addStyledButton(editButton, new Color(52, 152, 219));
		addStyledButton(deleteButton, new Color(231, 76, 60));
		addStyledButton(refreshButton, new Color(149, 165, 166));
		buttonPanel.add(addButton); buttonPanel.add(editButton);
		buttonPanel.add(deleteButton); buttonPanel.add(refreshButton);
		
		topPanel.add(titleLabel, BorderLayout.WEST);
		topPanel.add(searchPanel, BorderLayout.EAST);
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(topPanel, BorderLayout.NORTH);
		headerPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		add(headerPanel, BorderLayout.NORTH);
		add(new JScrollPane(packageTable), BorderLayout.CENTER);
	}
	
	private void addStyledButton(JButton button, Color color) {
		button.setBackground(color);
		button.setForeground(Color.BLACK);
	}
	
	private void initEvents() {
		addButton.addActionListener(e -> showPackageDialog(null));
		editButton.addActionListener(e -> showSelectedPackageDialog());
		deleteButton.addActionListener(e -> deleteSelectedPackage());
		refreshButton.addActionListener(e -> refreshData());
		searchField.addActionListener(e -> searchPackages());
		packageTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) showSelectedPackageDialog();
			}
		});
	}
	
	private void showPackageDialog(Package pkg) {
		PackageDialog dialog = new PackageDialog(SwingUtilities.getWindowAncestor(this), pkg);
		Package result = dialog.showDialog();
		if (result != null) {
			boolean success = (pkg == null) ? packageDAO.addPackage(result) : packageDAO.updatePackage(result);
			if (success) {
				JOptionPane.showMessageDialog(this, (pkg == null ? "Thêm" : "Cập nhật") + " gói cước thành công!");
				refreshData();
			} else {
				JOptionPane.showMessageDialog(this, "Lỗi khi lưu gói cước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void showSelectedPackageDialog() {
		int row = packageTable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn gói cước!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int id = (Integer) tableModel.getValueAt(row, 0);
		Package pkg = packageDAO.getPackageById(id);
		if (pkg != null) showPackageDialog(pkg);
	}
	
	private void deleteSelectedPackage() {
		int row = packageTable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn gói cước cần xóa!");
			return;
		}
		int id = (Integer) tableModel.getValueAt(row, 0);
		String name = (String) tableModel.getValueAt(row, 1);
		
		if (packageDAO.hasActiveSubscriptions(id)) {
			JOptionPane.showMessageDialog(this, "Gói cước đang được sử dụng, không thể xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, "Xóa gói: " + name + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION && packageDAO.deletePackage(id)) {
			JOptionPane.showMessageDialog(this, "Đã xóa gói cước!");
			refreshData();
		}
	}
	
	private void searchPackages() {
		String keyword = searchField.getText().trim();
		List<Package> packages = keyword.isEmpty() ? packageDAO.getAllPackages() : packageDAO.searchPackages(keyword);
		loadPackagesToTable(packages);
	}
	
	public void refreshData() {
		new SwingWorker<List<Package>, Void>() {
			protected List<Package> doInBackground() { return packageDAO.getAllPackages(); }
			protected void done() {
				try { loadPackagesToTable(get()); }
				catch (Exception e) {
					JOptionPane.showMessageDialog(PackageManagementPanel.this, "Lỗi khi tải dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}
	
	private void loadPackagesToTable(List<Package> packages) {
		tableModel.setRowCount(0);
		for (Package p : packages) {
			tableModel.addRow(new Object[]{
					p.getId(), p.getName(), p.getSpeed(),
					String.format("%,.0f", p.getPrice()),
					p.getDescription(), p.isActive() ? "Hoạt động" : "Tạm dừng",
					p.isAutoRenew() ? "Có" : "Không",
					p.getCreatedAt() != null ? p.getCreatedAt().toString().substring(0, 16) : ""
			});
		}
	}
}
