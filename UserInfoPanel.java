package users;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class UserInfoPanel extends JPanel {
	private JLabel avatarLabel;
	private User user;
	
	private JTextField fullNameField, emailField, phoneField, addressField;
	private JLabel usernameLabel, roleLabel;
	
	public UserInfoPanel(User user) {
		this.user = user;
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		avatarLabel = new JLabel();
		avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loadAvatar(user.getAvatarPath());
		
		JButton changeAvatarButton = new JButton("Thay đổi ảnh");
		changeAvatarButton.addActionListener(e -> changeAvatar());
		
		leftPanel.add(avatarLabel, BorderLayout.CENTER);
		leftPanel.add(changeAvatarButton, BorderLayout.SOUTH);
		leftPanel.setPreferredSize(new Dimension(180, 240));
		
		JPanel infoPanel = createInfoPanel();
		add(leftPanel, BorderLayout.WEST);
		add(infoPanel, BorderLayout.CENTER);
	}
	
	private JPanel createInfoPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		
		Font labelFont = new Font("SansSerif", Font.BOLD, 13);
		Font fieldFont = new Font("SansSerif", Font.PLAIN, 13);
		
		int row = 0;
		
		fullNameField = new JTextField(user.getFullName(), 20);
		addEditableField(panel, gbc, row++, "👤 Họ tên:", fullNameField, labelFont, fieldFont);
		
		usernameLabel = new JLabel(user.getUsername());
		addLabelField(panel, gbc, row++, "🧾 Tên đăng nhập:", usernameLabel, labelFont, fieldFont);
		
		emailField = new JTextField(user.getEmail(), 20);
		addEditableField(panel, gbc, row++, "📧 Email:", emailField, labelFont, fieldFont);
		
		phoneField = new JTextField(user.getPhone(), 20);
		addEditableField(panel, gbc, row++, "📱 Số điện thoại:", phoneField, labelFont, fieldFont);
		
		addressField = new JTextField(user.getAddress(), 20);
		addEditableField(panel, gbc, row++, "🏠 Địa chỉ:", addressField, labelFont, fieldFont);
		
		roleLabel = new JLabel(user.getRole());
		addLabelField(panel, gbc, row++, "🔐 Vai trò:", roleLabel, labelFont, fieldFont);
		
		gbc.gridx = 1;
		gbc.gridy = row++;
		JButton saveBtn = new JButton("💾 Lưu thay đổi");
		saveBtn.addActionListener(e -> saveChanges());
		panel.add(saveBtn, gbc);
		
		return panel;
	}
	
	private void addEditableField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField textField, Font labelFont, Font fieldFont) {
		gbc.gridx = 0;
		gbc.gridy = row;
		JLabel lbl = new JLabel(label);
		lbl.setFont(labelFont);
		panel.add(lbl, gbc);
		
		gbc.gridx = 1;
		textField.setFont(fieldFont);
		panel.add(textField, gbc);
	}
	
	private void addLabelField(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valueLabel, Font labelFont, Font fieldFont) {
		gbc.gridx = 0;
		gbc.gridy = row;
		JLabel lbl = new JLabel(label);
		lbl.setFont(labelFont);
		panel.add(lbl, gbc);
		
		gbc.gridx = 1;
		valueLabel.setFont(fieldFont);
		panel.add(valueLabel, gbc);
	}
	
	private void saveChanges() {
		String newFullName = fullNameField.getText().trim();
		String newEmail = emailField.getText().trim();
		String newPhone = phoneField.getText().trim();
		String newAddress = addressField.getText().trim();
		
		if (newFullName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
			return;
		}
		
		user.setFullName(newFullName);
		user.setEmail(newEmail);
		user.setPhone(newPhone);
		user.setAddress(newAddress);
		
		boolean success = new UserDAO().updateUserInfo(user);
		if (success) {
			JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin.");
		}
	}
	
	private void loadAvatar(String avatarPath) {
		try {
			Image avatarImage;
			if (avatarPath != null && !avatarPath.trim().isEmpty()) {
				File file = new File(avatarPath);
				if (file.exists()) {
					avatarImage = ImageIO.read(file);
				} else {
					avatarImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("users.jpg"));
				}
			} else {
				avatarImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("users.jpg"));
			}
			Image scaled = avatarImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			avatarLabel.setIcon(new ImageIcon(scaled));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changeAvatar() {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			try {
				File avatarsDir = new File("avatars");
				if (!avatarsDir.exists()) avatarsDir.mkdir();
				String newPath = "avatars/" + user.getId() + "_" + selectedFile.getName();
				File dest = new File(newPath);
				try (InputStream in = new FileInputStream(selectedFile);
				     OutputStream out = new FileOutputStream(dest)) {
					in.transferTo(out);
				}
				user.setAvatarPath(newPath);
				loadAvatar(newPath);
				new UserDAO().updateAvatar(user.getId(), newPath);
				JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thành công!");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật ảnh: " + e.getMessage());
			}
		}
	}
}
