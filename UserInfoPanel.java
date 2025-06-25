package users;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class UserInfoPanel extends JPanel {
	private JLabel avatarLabel;
	private User user;
	
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
		Font valueFont = new Font("SansSerif", Font.PLAIN, 13);
		
		int row = 0;
		addField(panel, gbc, row++, "👤 Họ tên:", user.getFullName(), labelFont, valueFont);
		addField(panel, gbc, row++, "🧾 Tên đăng nhập:", user.getUsername(), labelFont, valueFont);
		addField(panel, gbc, row++, "📧 Email:", user.getEmail(), labelFont, valueFont);
		addField(panel, gbc, row++, "📱 Số điện thoại:", user.getPhone(), labelFont, valueFont);
		addField(panel, gbc, row++, "🏠 Địa chỉ:", user.getAddress(), labelFont, valueFont);
		addField(panel, gbc, row++, "🔐 Vai trò:", user.getRole(), labelFont, valueFont);
		
		return panel;
	}
	
	private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, String value, Font labelFont, Font valueFont) {
		gbc.gridx = 0;
		gbc.gridy = row;
		JLabel lbl = new JLabel(label);
		lbl.setFont(labelFont);
		panel.add(lbl, gbc);
		
		gbc.gridx = 1;
		JLabel val = new JLabel(value != null ? value : "N/A");
		val.setFont(valueFont);
		panel.add(val, gbc);
	}
	
	private void loadAvatar(String avatarPath) {
		try {
			Image avatarImage;
			if (avatarPath != null && !avatarPath.trim().isEmpty()) {
				File file = new File(avatarPath);
				if (file.exists()) {
					avatarImage = ImageIO.read(file);
				} else {
					// Dùng ảnh mặc định nếu không tồn tại file
					avatarImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("users.jpg"));
				}
			} else {
				// Dùng ảnh mặc định nếu không có đường dẫn
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
				// Copy ảnh vào thư mục avatars/
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
