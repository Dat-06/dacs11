package lichsu;

import goicuoc.Subscription;
import goicuoc.SubscriptionDAO;
import users.User;
import users.UserDAO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TransactionHistoryPanel extends JPanel {
	private TransactionDAO transactionDAO;
	private UserDAO userDAO;
	private SubscriptionDAO subscriptionDAO;
	private JTable transactionTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JButton addButton, editButton, deleteButton, refreshButton, exportInvoiceButton;
	private User currentUser;
	
	public TransactionHistoryPanel() {
		this(null);
	}
	
	public TransactionHistoryPanel(User user) {
		this.currentUser = user;
		transactionDAO = new TransactionDAO();
		userDAO = new UserDAO();
		subscriptionDAO = new SubscriptionDAO();
		
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
		
		addButton = new JButton("Thêm Giao Dịch");
		editButton = new JButton("Sửa");
		deleteButton = new JButton("Xóa");
		refreshButton = new JButton("Làm mới");
		exportInvoiceButton = new JButton("Xuất Hóa Đơn");
		
		if (currentUser != null && "user".equals(currentUser.getRole())) {
			addButton.setVisible(false);
			editButton.setText("Xem Chi Tiết");
			deleteButton.setVisible(false);
			exportInvoiceButton.setVisible(false);
		}
		
		addButton.setBackground(new Color(46, 204, 113));
		addButton.setForeground(Color.black);
		editButton.setBackground(new Color(52, 152, 219));
		editButton.setForeground(Color.black);
		deleteButton.setBackground(new Color(231, 76, 60));
		deleteButton.setForeground(Color.black);
		refreshButton.setBackground(new Color(149, 165, 166));
		refreshButton.setForeground(Color.black);
		exportInvoiceButton.setBackground(new Color(241, 196, 15));
		exportInvoiceButton.setForeground(Color.BLACK);
		
		String[] columnNames = {"ID", "Người Dùng", "Gói Cước", "Số Tiền", "Loại", "Trạng Thái", "Mô Tả", "Ngày Giao Dịch"};
		if (currentUser != null && "user".equals(currentUser.getRole())) {
			columnNames = new String[]{"ID", "Gói Cước", "Số Tiền", "Loại", "Trạng Thái", "Mô Tả", "Ngày Giao Dịch"};
		}
		
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		transactionTable = new JTable(tableModel);
		transactionTable.setRowHeight(25);
		transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transactionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
		transactionTable.getTableHeader().setBackground(new Color(240, 240, 240));
	}
	
	private void setupLayout() {
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		
		String title = (currentUser != null && "user".equals(currentUser.getRole()))
				? "LỊCH SỬ GIAO DỊCH CỦA TÔI"
				: "QUẢN LÝ GIAO DỊCH";
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
		if (addButton.isVisible()) buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		if (deleteButton.isVisible()) buttonPanel.add(deleteButton);
		buttonPanel.add(refreshButton);
		if (exportInvoiceButton.isVisible()) buttonPanel.add(exportInvoiceButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(transactionTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		
		add(mainPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void setupEventHandlers() {
		addButton.addActionListener(e -> showAddTransactionDialog());
		editButton.addActionListener(e -> showTransactionDetails());
		deleteButton.addActionListener(e -> deleteSelectedTransaction());
		refreshButton.addActionListener(e -> refreshData());
		exportInvoiceButton.addActionListener(e -> exportInvoice());
		
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { searchTransactions(); }
			public void removeUpdate(DocumentEvent e) { searchTransactions(); }
			public void changedUpdate(DocumentEvent e) { searchTransactions(); }
		});
		
		transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					showTransactionDetails();
				}
			}
		});
	}
	
	private void showAddTransactionDialog() {
		JOptionPane.showMessageDialog(this, "Chức năng Thêm Giao Dịch đang được phát triển.");
	}
	
	private void showTransactionDetails() {
		int selectedRow = transactionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch để xem chi tiết.");
			return;
		}
		Object transactionId = tableModel.getValueAt(selectedRow, 0);
		JOptionPane.showMessageDialog(this, "Chi tiết giao dịch: ID = " + transactionId);
	}
	
	private void deleteSelectedTransaction() {
		int selectedRow = transactionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch để xóa.");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa giao dịch này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
			if (transactionDAO.deleteTransaction(transactionId)) {
				tableModel.removeRow(selectedRow);
				JOptionPane.showMessageDialog(this, "Giao dịch đã được xóa.");
			} else {
				JOptionPane.showMessageDialog(this, "Xóa thất bại.");
			}
		}
	}
	
	private void searchTransactions() {
		String keyword = searchField.getText().trim();
		List<Transaction> transactions = keyword.isEmpty()
				? (currentUser != null && "user".equals(currentUser.getRole())
				? transactionDAO.getTransactionsByUserId(currentUser.getId())
				: transactionDAO.getAllTransactions())
				: transactionDAO.searchTransactions(keyword);
		
		if (currentUser != null && "user".equals(currentUser.getRole())) {
			transactions = transactions.stream()
					.filter(t -> t.getUserId() == currentUser.getId())
					.toList();
		}
		loadTransactionsToTable(transactions);
	}
	
	public void refreshData() {
		SwingWorker<List<Transaction>, Void> worker = new SwingWorker<>() {
			@Override
			protected List<Transaction> doInBackground() {
				return (currentUser != null && "user".equals(currentUser.getRole()))
						? transactionDAO.getTransactionsByUserId(currentUser.getId())
						: transactionDAO.getAllTransactions();
			}
			
			@Override
			protected void done() {
				try {
					List<Transaction> transactions = get();
					loadTransactionsToTable(transactions);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(TransactionHistoryPanel.this,
							"Lỗi khi tải dữ liệu: " + e.getMessage(),
							"Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute();
	}
	
	private void loadTransactionsToTable(List<Transaction> transactions) {
		tableModel.setRowCount(0);
		for (Transaction transaction : transactions) {
			Object[] rowData;
			if (currentUser != null && "user".equals(currentUser.getRole())) {
				rowData = new Object[]{
						transaction.getId(),
						transaction.getSubscription() != null && transaction.getSubscription().getPackageInfo() != null ? transaction.getSubscription().getPackageInfo().getName() : "N/A",
						String.format("%,.0f VND", transaction.getAmount()),
						transaction.getType(),
						transaction.getStatus(),
						transaction.getDescription(),
						transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString().substring(0, 16) : ""
				};
			} else {
				rowData = new Object[]{
						transaction.getId(),
						transaction.getUser() != null ? transaction.getUser().getFullName() : "",
						transaction.getSubscription() != null && transaction.getSubscription().getPackageInfo() != null ? transaction.getSubscription().getPackageInfo().getName() : "N/A",
						String.format("%,.0f VND", transaction.getAmount()),
						transaction.getType(),
						transaction.getStatus(),
						transaction.getDescription(),
						transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString().substring(0, 16) : ""
				};
			}
			tableModel.addRow(rowData);
		}
	}
	
	private void exportInvoice() {
		int selectedRow = transactionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch để xuất hóa đơn.");
			return;
		}
		int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
		Transaction transaction = transactionDAO.getTransactionById(transactionId);
		if (transaction == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy giao dịch.");
			return;
		}
		StringBuilder invoice = new StringBuilder();
		invoice.append("=== HÓA ĐƠN GIAO DỊCH ===\n");
		invoice.append("Mã Giao Dịch: ").append(transaction.getId()).append("\n");
		invoice.append("Khách Hàng: ").append(transaction.getUser().getFullName()).append("\n");
		invoice.append("Gói Cước: ").append(
				transaction.getSubscription() != null && transaction.getSubscription().getPackageInfo() != null
						? transaction.getSubscription().getPackageInfo().getName()
						: "N/A").append("\n");
		invoice.append("Số Tiền: ").append(String.format("%,.0f VND", transaction.getAmount())).append("\n");
		invoice.append("Loại: ").append(transaction.getType()).append("\n");
		invoice.append("Trạng Thái: ").append(transaction.getStatus()).append("\n");
		invoice.append("Mô Tả: ").append(transaction.getDescription()).append("\n");
		invoice.append("Ngày: ").append(transaction.getTransactionDate()).append("\n");
		
		JTextArea textArea = new JTextArea(invoice.toString());
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 250));
		JOptionPane.showMessageDialog(this, scrollPane, "Hóa Đơn", JOptionPane.INFORMATION_MESSAGE);
		
		// Lưu hóa đơn ra file
		JFileChooser fileChooser = new JFileChooser();
		String defaultFileName = "invoice_" + transaction.getId() + "_" + transaction.getUser().getFullName().replaceAll("\\s+", "_") + ".txt";
		fileChooser.setSelectedFile(new File(defaultFileName));
		fileChooser.setDialogTitle("Chọn nơi lưu hóa đơn");
		
		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			try (FileWriter writer = new FileWriter(fileToSave)) {
				writer.write(invoice.toString());
				JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn tại: " + fileToSave.getAbsolutePath());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Lỗi khi ghi file: " + ex.getMessage());
			}
		}
	}}

