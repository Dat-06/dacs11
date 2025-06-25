package PASS;

import users.User;
import users.UserDAO;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {
	private JPasswordField currentPasswordField;
	private JPasswordField newPasswordField;
	private JPasswordField confirmPasswordField;
	private JButton changeButton;
	
	public ChangePasswordPanel(User user) {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel title = new JLabel("Đổi Mật Khẩu", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 18));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(title, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridy++;
		add(new JLabel("Mật khẩu hiện tại:"), gbc);
		currentPasswordField = new JPasswordField(20);
		gbc.gridx = 1;
		add(currentPasswordField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Mật khẩu mới:"), gbc);
		newPasswordField = new JPasswordField(20);
		gbc.gridx = 1;
		add(newPasswordField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Xác nhận mật khẩu:"), gbc);
		confirmPasswordField = new JPasswordField(20);
		gbc.gridx = 1;
		add(confirmPasswordField, gbc);
		
		changeButton = new JButton("Đổi mật khẩu");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(changeButton, gbc);
		
		changeButton.addActionListener(e -> {
			String current = new String(currentPasswordField.getPassword());
			String newPass = new String(newPasswordField.getPassword());
			String confirm = new String(confirmPasswordField.getPassword());
			
			if (!current.equals(user.getPassword())) {
				JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!");
			} else if (newPass.length() < 6) {
				JOptionPane.showMessageDialog(this, "Mật khẩu mới phải từ 6 ký tự trở lên!");
			} else if (!newPass.equals(confirm)) {
				JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp!");
			} else {
				user.setPassword(newPass);
				if (new UserDAO().updateUser(user)) {
					JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
					currentPasswordField.setText("");
					newPasswordField.setText("");
					confirmPasswordField.setText("");
				} else {
					JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật mật khẩu.");
				}
			}
		});
	}
}
