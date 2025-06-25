//package login;
//
//import users.User;
//import users.UserDAO;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
///**
// * Dialog quên mật khẩu
// */
//public class ForgotPasswordDialog extends JDialog {
//	private JTextField emailField;
//	private JButton sendButton;
//	private JButton cancelButton;
//	private UserDAO userDAO;
//	private EmailService emailService;
//
//	public ForgotPasswordDialog(Frame parent) {
//		super(parent, "Quên mật khẩu", true);
//		userDAO = new UserDAO();
//		emailService = new EmailService();
//		initializeComponents();
//		setupLayout();
//		setupEventHandlers();
//	}
//
//	private void initializeComponents() {
//		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		setResizable(false);
//
//		emailField = new JTextField(20);
//		sendButton = new JButton("Gửi mật khẩu mới");
//		cancelButton = new JButton("Hủy");
//
//		// Thiết lập font
//		Font labelFont = new Font("SansSerif", Font.BOLD, 12);
//		Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);
//
//		emailField.setFont(fieldFont);
//		sendButton.setFont(labelFont);
//		cancelButton.setFont(labelFont);
//
//		// Thiết lập màu sắc
//		sendButton.setBackground(new Color(52, 152, 219));
//		sendButton.setForeground(Color.WHITE);
//		cancelButton.setBackground(new Color(149, 165, 166));
//		cancelButton.setForeground(Color.WHITE);
//
//		// Thiết lập kích thước button
//		Dimension buttonSize = new Dimension(150, 35);
//		sendButton.setPreferredSize(buttonSize);
//		cancelButton.setPreferredSize(buttonSize);
//	}
//
//	private void setupLayout() {
//		setLayout(new BorderLayout());
//
//		JPanel mainPanel = new JPanel(new GridBagLayout());
//		mainPanel.setBackground(Color.WHITE);
//		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.insets = new Insets(10, 10, 10, 10);
//
//		// Tiêu đề
//		JLabel titleLabel = new JLabel("Quên mật khẩu");
//		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
//		titleLabel.setForeground(new Color(52, 73, 94));
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.gridwidth = 2;
//		gbc.anchor = GridBagConstraints.CENTER;
//		mainPanel.add(titleLabel, gbc);
//
//		// Hướng dẫn
//		JLabel instructionLabel = new JLabel("<html><center>Nhập email của bạn để nhận mật khẩu mới</center></html>");
//		instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
//		instructionLabel.setForeground(Color.GRAY);
//		gbc.gridy = 1;
//		gbc.insets = new Insets(5, 10, 15, 10);
//		mainPanel.add(instructionLabel, gbc);
//
//		// Email
//		gbc.insets = new Insets(10, 10, 10, 10);
//		gbc.gridwidth = 1;
//		gbc.anchor = GridBagConstraints.EAST;
//
//		JLabel emailLabel = new JLabel("Email:");
//		emailLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
//		gbc.gridx = 0;
//		gbc.gridy = 2;
//		mainPanel.add(emailLabel, gbc);
//
//		gbc.gridx = 1;
//		gbc.anchor = GridBagConstraints.WEST;
//		mainPanel.add(emailField, gbc);
//
//		// Buttons
//		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
//		buttonPanel.setBackground(Color.WHITE);
//		buttonPanel.add(sendButton);
//		buttonPanel.add(cancelButton);
//
//		gbc.gridx = 0;
//		gbc.gridy = 3;
//		gbc.gridwidth = 2;
//		gbc.anchor = GridBagConstraints.CENTER;
//		gbc.insets = new Insets(20, 10, 10, 10);
//		mainPanel.add(buttonPanel, gbc);
//
//		add(mainPanel, BorderLayout.CENTER);
//
//		pack();
//		setLocationRelativeTo(getParent());
//	}
//
//	private void setupEventHandlers() {
//		sendButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				sendNewPassword();
//			}
//		});
//
//		cancelButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				dispose();
//			}
//		});
//
//		emailField.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				sendNewPassword();
//			}
//		});
//	}
//
//	private void sendNewPassword() {
//		String email = emailField.getText().trim();
//
//		if (email.isEmpty()) {
//			JOptionPane.showMessageDialog(this,
//					"Vui lòng nhập email!",
//					"Lỗi",
//					JOptionPane.ERROR_MESSAGE);
//			return;
//		}
//
//		// Kiểm tra email hợp lệ
//		if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$"))
//			// vẫn giữ đoạn JOptionPane như cũ
//
// {
//			JOptionPane.showMessageDialog(this,
//					"Email không hợp lệ!",
//					"Lỗi",
//					JOptionPane.ERROR_MESSAGE);
//			return;
//		}
//
//		sendButton.setEnabled(false);
//		sendButton.setText("Đang gửi...");
//
//		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
//			@Override
//			protected Boolean doInBackground() throws Exception {
//				// Tìm user theo email
//				User user = userDAO.findByEmail(email);
//				if (user == null) {
//					return false;
//				}
//
//				// Tạo mật khẩu mới
//				String newPassword = EmailService.generateRandomPassword();
//
//				// Cập nhật mật khẩu trong database
//				boolean passwordUpdated = userDAO.updatePassword(email, newPassword);
//				if (!passwordUpdated) {
//					return false;
//				}
//
//				// Gửi email
//				return emailService.sendNewPassword(email, newPassword, user.getFullName());
//			}
//
//			@Override
//			protected void done() {
//				try {
//					Boolean success = get();
//					if (success) {
//						JOptionPane.showMessageDialog(ForgotPasswordDialog.this,
//								"Mật khẩu mới đã được gửi về email của bạn!\nVui lòng kiểm tra hộp thư.",
//								"Thành công",
//								JOptionPane.INFORMATION_MESSAGE);
//						dispose();
//					} else {
//						JOptionPane.showMessageDialog(ForgotPasswordDialog.this,
//								"Email không tồn tại trong hệ thống hoặc có lỗi xảy ra!",
//								"Lỗi",
//								JOptionPane.ERROR_MESSAGE);
//					}
//				} catch (Exception ex) {
//					JOptionPane.showMessageDialog(ForgotPasswordDialog.this,
//							"Có lỗi xảy ra: " + ex.getMessage(),
//							"Lỗi",
//							JOptionPane.ERROR_MESSAGE);
//				} finally {
//					sendButton.setEnabled(true);
//					sendButton.setText("Gửi mật khẩu mới");
//				}
//			}
//		};
//
//		worker.execute();
//	}
//}