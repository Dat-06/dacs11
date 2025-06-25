package goicuoc;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PackageDialog extends JDialog {
	private JTextField nameField;
	private JTextField speedField;
	private JTextField priceField;
	private JTextArea descriptionArea;
	private JCheckBox activeCheckBox;
	private JCheckBox autoRenewCheckBox;
	private boolean confirmed = false;
	private Package pkg;
	
	public PackageDialog(Window parent, Package pkg) {
		super(parent, pkg == null ? "Thêm Gói Cước" : "Sửa Gói Cước", ModalityType.APPLICATION_MODAL);
		this.pkg = pkg;
		initUI();
		setLocationRelativeTo(parent);
	}
	
	private void initUI() {
		setSize(450, 400);
		setLayout(new BorderLayout());
		
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		
		nameField = new JTextField(20);
		speedField = new JTextField(20);
		priceField = new JTextField(20);
		descriptionArea = new JTextArea(4, 20);
		JScrollPane descScroll = new JScrollPane(descriptionArea);
		activeCheckBox = new JCheckBox("Hoạt động");
		autoRenewCheckBox = new JCheckBox("Tự động gia hạn");
		
		int y = 0;
		gbc.gridx = 0; gbc.gridy = y;
		formPanel.add(new JLabel("Tên Gói:"), gbc);
		gbc.gridx = 1;
		formPanel.add(nameField, gbc); y++;
		
		gbc.gridx = 0; gbc.gridy = y;
		formPanel.add(new JLabel("Tốc Độ:"), gbc);
		gbc.gridx = 1;
		formPanel.add(speedField, gbc); y++;
		
		gbc.gridx = 0; gbc.gridy = y;
		formPanel.add(new JLabel("Giá (VND):"), gbc);
		gbc.gridx = 1;
		formPanel.add(priceField, gbc); y++;
		
		gbc.gridx = 0; gbc.gridy = y;
		formPanel.add(new JLabel("Mô Tả:"), gbc);
		gbc.gridx = 1;
		formPanel.add(descScroll, gbc); y++;
		
		gbc.gridx = 1; gbc.gridy = y++;
		formPanel.add(activeCheckBox, gbc);
		
		gbc.gridx = 1; gbc.gridy = y++;
		formPanel.add(autoRenewCheckBox, gbc);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton saveButton = new JButton("Lưu");
		JButton cancelButton = new JButton("Hủy");
		
		saveButton.addActionListener(e -> {
			if (validateFields()) {
				confirmed = true;
				setVisible(false);
			}
		});
		
		cancelButton.addActionListener(e -> setVisible(false));
		
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		
		add(formPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		if (pkg != null) {
			nameField.setText(pkg.getName());
			speedField.setText(pkg.getSpeed());
			priceField.setText(pkg.getPrice().toString());
			descriptionArea.setText(pkg.getDescription());
			activeCheckBox.setSelected(pkg.isActive());
			autoRenewCheckBox.setSelected(pkg.isAutoRenew());
		}
	}
	
	private boolean validateFields() {
		if (nameField.getText().trim().isEmpty() || speedField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
			return false;
		}
		try {
			new BigDecimal(priceField.getText().trim());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Giá không hợp lệ!");
			return false;
		}
		return true;
	}
	
	public Package showDialog() {
		setVisible(true);
		if (confirmed) {
			if (pkg == null) pkg = new Package();
			pkg.setName(nameField.getText().trim());
			pkg.setSpeed(speedField.getText().trim());
			pkg.setPrice(new BigDecimal(priceField.getText().trim()));
			pkg.setDescription(descriptionArea.getText().trim());
			pkg.setActive(activeCheckBox.isSelected());
			pkg.setAutoRenew(autoRenewCheckBox.isSelected());
			return pkg;
		}
		return null;
	}
}
