package goicuoc;

import users.User;
import users.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class SubscriptionDialog extends JDialog {
	private final boolean isEdit;
	private final User contextUser;
	private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
	private final UserDAO userDAO = new UserDAO();
	private final PackageDAO packageDAO = new PackageDAO();
	
	private JComboBox<User> userComboBox;
	private JComboBox<Package> packageComboBox;
	private JComboBox<String> statusComboBox;
	private JTextField startDateField, endDateField;
	
	private boolean confirmed = false;
	private Subscription subscription;
	
	public SubscriptionDialog(Window parent, String title, Subscription subscription, User contextUser) {
		super(parent, title, ModalityType.APPLICATION_MODAL);
		this.isEdit = subscription != null;
		this.subscription = (subscription != null) ? subscription : new Subscription();
		this.contextUser = contextUser;
		initializeUI();
	}
	
	private void initializeUI() {
		setSize(500, 300);
		setLocationRelativeTo(getParent());
		
		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		int row = 0;
		
		// User selection (admin only)
		if (contextUser == null || "admin".equals(contextUser.getRole())) {
			gbc.gridx = 0; gbc.gridy = row;
			form.add(new JLabel("Người dùng:"), gbc);
			
			userComboBox = new JComboBox<>();
			List<User> users = userDAO.getAllUsers();
			for (User u : users) userComboBox.addItem(u);
			userComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
				JLabel label = new JLabel();
				if (value instanceof User) {
					User user = (User) value;
					label.setText(user.getFullName() + " (" + user.getUsername() + ")");
				}
				return label;
			});
			
			gbc.gridx = 1;
			form.add(userComboBox, gbc);
			row++;
		}
		
		// Package
		gbc.gridx = 0; gbc.gridy = row;
		form.add(new JLabel("Gói cước:"), gbc);
		packageComboBox = new JComboBox<>();
		for (Package p : packageDAO.getActivePackages()) packageComboBox.addItem(p);
		packageComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
			JLabel label = new JLabel();
			if (value instanceof Package) {
				Package pkg = (Package) value;
				label.setText(pkg.getName() + " - " + pkg.getSpeed() + " - " + String.format("%,.0f VND", pkg.getPrice()));
			}
			return label;
		});
		gbc.gridx = 1;
		form.add(packageComboBox, gbc);
		row++;
		
		// Status
		gbc.gridx = 0; gbc.gridy = row;
		form.add(new JLabel("Trạng thái:"), gbc);
		statusComboBox = new JComboBox<>(new String[]{"active", "inactive", "suspended"});
		gbc.gridx = 1;
		form.add(statusComboBox, gbc);
		row++;
		
		// Start date
		gbc.gridx = 0; gbc.gridy = row;
		form.add(new JLabel("Ngày bắt đầu (yyyy-mm-dd):"), gbc);
		startDateField = new JTextField(20);
		startDateField.setText(LocalDate.now().toString());
		gbc.gridx = 1;
		form.add(startDateField, gbc);
		row++;
		
		// End date
		gbc.gridx = 0; gbc.gridy = row;
		form.add(new JLabel("Ngày kết thúc (yyyy-mm-dd):"), gbc);
		endDateField = new JTextField(20);
		endDateField.setText(LocalDate.now().plusMonths(1).toString());
		gbc.gridx = 1;
		form.add(endDateField, gbc);
		row++;
		
		// Buttons
		JPanel buttonPanel = new JPanel();
		JButton saveBtn = new JButton("Lưu");
		JButton cancelBtn = new JButton("Hủy");
		saveBtn.addActionListener(this::onSave);
		cancelBtn.addActionListener(e -> dispose());
		buttonPanel.add(saveBtn);
		buttonPanel.add(cancelBtn);
		
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		form.add(buttonPanel, gbc);
		
		add(form);
		
		if (isEdit) loadExistingData();
	}
	
	private void loadExistingData() {
		if (userComboBox != null) {
			for (int i = 0; i < userComboBox.getItemCount(); i++) {
				if (userComboBox.getItemAt(i).getId() == subscription.getUserId()) {
					userComboBox.setSelectedIndex(i);
					break;
				}
			}
		}
		
		for (int i = 0; i < packageComboBox.getItemCount(); i++) {
			if (packageComboBox.getItemAt(i).getId() == subscription.getPackageId()) {
				packageComboBox.setSelectedIndex(i);
				break;
			}
		}
		
		statusComboBox.setSelectedItem(subscription.getStatus());
		if (subscription.getStartDate() != null) startDateField.setText(subscription.getStartDate().toString());
		if (subscription.getEndDate() != null) endDateField.setText(subscription.getEndDate().toString());
	}
	
	private void onSave(ActionEvent e) {
		try {
			Date startDate = Date.valueOf(startDateField.getText().trim());
			Date endDate = Date.valueOf(endDateField.getText().trim());
			
			if (contextUser != null && "user".equals(contextUser.getRole())) {
				subscription.setUserId(contextUser.getId());
			} else {
				User selectedUser = (User) userComboBox.getSelectedItem();
				subscription.setUserId(selectedUser.getId());
			}
			
			Package selectedPackage = (Package) packageComboBox.getSelectedItem();
			subscription.setPackageId(selectedPackage.getId());
			subscription.setStatus((String) statusComboBox.getSelectedItem());
			subscription.setStartDate(startDate);
			subscription.setEndDate(endDate);
			
			confirmed = true;
			dispose();
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Định dạng: yyyy-mm-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public Subscription showDialog() {
		setVisible(true);
		return confirmed ? subscription : null;
	}
}
