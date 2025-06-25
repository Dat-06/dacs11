package login;

import users.User;
import users.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Form quên mật khẩu
 */
public class ForgotPasswordFrame extends JFrame {
	private JTextField emailField;
	private JButton sendButton;
	private JButton cancelButton;
	
	public ForgotPasswordFrame() {
		initializeComponents();
		setupLayout();
		setupEventHandlers();
	}
	
	private void initializeComponents() {
		setTitle("Quên Mật Khẩu - Internet Billing System");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		emailField = new JTextField(25);
		sendButton = new JButton("Gửi Mật Khẩu Mới");
		cancelButton = new JButton("Hủy");
		
		// Thiết lập font và màu
		Font buttonFont = new Font("SansSerif", Font.BOLD, 12);
		sendButton.setFont(buttonFont);
		cancelButton.setFont(buttonFont);
		
		sendButton.setBackground(new Color(52, 152, 219));
		sendButton.setForeground(Color.WHITE);
		cancelButton.setBackground(new Color(149, 165, 166));
		cancelButton.setForeground(Color.WHITE);
	}
	
	private void setupLayout() {
		setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		
		// Tiêu đề
		JLabel titleLabel = new JLabel("QUÊN MẬT KHẨU");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		titleLabel.setForeground(new Color(52, 73, 94));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(titleLabel, gbc);
		
		// Hướng dẫn
		JLabel instructionLabel = new JLabel("<html><center>Nhập email của bạn để nhận mật khẩu mới</center></html>");
		instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		instructionLabel.setForeground(Color.GRAY);
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 10, 15, 10);
		mainPanel.add(instructionLabel, gbc);
		
		// Email label
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(10, 10, 10, 5);
		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		gbc.gridx = 0;
		gbc.gridy = 2;
		mainPanel.add(emailLabel, gbc);
		
		// Email field
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(10, 5, 10, 10);
		mainPanel.add(emailField, gbc);
		
		// Button panel
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(sendButton);
		buttonPanel.add(cancelButton);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(20, 10, 10, 10);
		mainPanel.add(buttonPanel, gbc);
		
		add(mainPanel, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	private void setupEventHandlers() {
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				processForgotPassword();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		// Enter key
		emailField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				processForgotPassword();
			}
		});
	}
	
	private void processForgotPassword() {
		String email = emailField.getText().trim();
		
		if (email.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Vui lòng nhập email!",
					"Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Validate email format
		if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
			JOptionPane.showMessageDialog(this,
					"Email không đúng định dạng!",
					"Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Disable button while processing
		sendButton.setEnabled(false);
		sendButton.setText("Đang xử lý...");
		
		// Process in background thread
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				UserDAO userDAO = new UserDAO();
				
				// Tìm user theo email
				User user = userDAO.findByEmail(email);
				if (user == null) {
					return false;
				}
				
				// Tạo mật khẩu mới
				String newPassword = EmailService.generateRandomPassword();
				
				// Cập nhật mật khẩu trong database
				boolean passwordUpdated = userDAO.updatePassword(email, newPassword);
				if (!passwordUpdated) {
					return false;
				}
				
				// Gửi email
				return EmailService.sendNewPassword(email, newPassword, user.getFullName());
			}
			
			@Override
			protected void done() {
				try {
					boolean success = get();
					if (success) {
						JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
								"Mật khẩu mới đã được gửi về email của bạn!",
								"Thành công",
								JOptionPane.INFORMATION_MESSAGE);
						dispose();
					} else {
						JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
								"Không thể gửi email. Vui lòng kiểm tra lại email hoặc liên hệ quản trị viên.",
								"Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(ForgotPasswordFrame.this,
							"Đã xảy ra lỗi: " + e.getMessage(),
							"Lỗi",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					sendButton.setEnabled(true);
					sendButton.setText("Gửi Mật Khẩu Mới");
				}
			}
		};
		
		worker.execute();
	}
}