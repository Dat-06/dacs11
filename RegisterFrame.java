
package login;

import users.User;
import users.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * Form đăng ký tài khoản
 */
public class RegisterFrame extends JFrame {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JTextField fullNameField;
	private JTextField emailField;
	private JTextField phoneField;
	private JTextArea addressArea;
	private JButton registerButton;
	private JButton cancelButton;
	private JButton loginButton;
	private UserDAO userDAO;
	
	public RegisterFrame() {
		userDAO = new UserDAO();
		initializeComponents();
		setupLayout();
		setupEventHandlers();
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initializeComponents() {
		setTitle("Internet Billing System - Đăng Ký");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		// Tạo các components
		usernameField = new JTextField(20);
		passwordField = new JPasswordField(20);
		confirmPasswordField = new JPasswordField(20);
		fullNameField = new JTextField(20);
		emailField = new JTextField(20);
		phoneField = new JTextField(20);
		addressArea = new JTextArea(3, 20);
		registerButton = new JButton("Đăng Ký");
		cancelButton = new JButton("Hủy");
		loginButton = new JButton("Đã có tài khoản? Đăng nhập");
		
		// Thiết lập font
		Font labelFont = new Font("SansSerif", Font.BOLD, 12);
		Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);
		
		usernameField.setFont(fieldFont);
		passwordField.setFont(fieldFont);
		confirmPasswordField.setFont(fieldFont);
		fullNameField.setFont(fieldFont);
		emailField.setFont(fieldFont);
		phoneField.setFont(fieldFont);
		addressArea.setFont(fieldFont);
		registerButton.setFont(labelFont);
		cancelButton.setFont(labelFont);
		loginButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
		
		// Thiết lập màu sắc
		registerButton.setBackground(new Color(39, 174, 96));
		registerButton.setForeground(Color.blue);
		cancelButton.setBackground(new Color(231, 76, 60));
		cancelButton.setForeground(Color.blue);
		loginButton.setBackground(Color.blue);
		loginButton.setForeground(new Color(52, 152, 219));
		loginButton.setBorder(BorderFactory.createEmptyBorder());
		loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// Thiết lập kích thước button
		Dimension buttonSize = new Dimension(120, 35);
		registerButton.setPreferredSize(buttonSize);
		cancelButton.setPreferredSize(buttonSize);
		
		// Thiết lập text area
		addressArea.setLineWrap(true);
		addressArea.setWrapStyleWord(true);
		addressArea.setBorder(BorderFactory.createLoweredBevelBorder());
		
		// Focus mặc định
		registerButton.setDefaultCapable(true);
		getRootPane().setDefaultButton(registerButton);
	}
	
	private void setupLayout() {
		setLayout(new BorderLayout());
		
		// Panel chính
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 10, 8, 10);
		gbc.anchor = GridBagConstraints.WEST;
		
		// Tiêu đề
		JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		titleLabel.setForeground(new Color(52, 73, 94));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 10, 20, 10);
		mainPanel.add(titleLabel, gbc);
		
		// Reset anchor và insets
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(8, 10, 8, 10);
		
		// Username
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);
		gbc.gridx = 1;
		mainPanel.add(usernameField, gbc);
		
		// Password
		gbc.gridx = 0;
		gbc.gridy = 2;
		mainPanel.add(new JLabel("Mật khẩu:"), gbc);
		gbc.gridx = 1;
		mainPanel.add(passwordField, gbc);
		
		// Confirm Password
		gbc.gridx = 0;
		gbc.gridy = 3;
		mainPanel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
		gbc.gridx = 1;
		mainPanel.add(confirmPasswordField, gbc);
		
		// Full Name
		gbc.gridx = 0;
		gbc.gridy = 4;
		mainPanel.add(new JLabel("Họ và tên:"), gbc);
		gbc.gridx = 1;
		mainPanel.add(fullNameField, gbc);
		
		// Email
		gbc.gridx = 0;
		gbc.gridy = 5;
		mainPanel.add(new JLabel("Email:"), gbc);
		gbc.gridx = 1;
		mainPanel.add(emailField, gbc);
		
		// Phone
		gbc.gridx = 0;
		gbc.gridy = 6;
		mainPanel.add(new JLabel("Số điện thoại:"), gbc);
		gbc.gridx = 1;
		mainPanel.add(phoneField, gbc);
		
		// Address
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(new JLabel("Địa chỉ:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		JScrollPane addressScroll = new JScrollPane(addressArea);
		addressScroll.setPreferredSize(new Dimension(200, 60));
		mainPanel.add(addressScroll, gbc);
		
		// Buttons panel
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(registerButton);
		buttonPanel.add(cancelButton);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(15, 10, 10, 10);
		mainPanel.add(buttonPanel, gbc);
		
		// Login button
		gbc.gridy = 9;
		gbc.insets = new Insets(5, 10, 10, 10);
		mainPanel.add(loginButton, gbc);
		
		add(mainPanel, BorderLayout.CENTER);
		pack();
	}
	
	private void setupEventHandlers() {
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performRegister();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearForm();
			}
		});
		
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLoginForm();
			}
		});
	}
	
	private void performRegister() {
		// Validate form
		if (!validateForm()) {
			return;
		}
		
		// Disable button during processing
		registerButton.setEnabled(false);
		registerButton.setText("Đang xử lý...");
		
		SwingWorker<Boolean, Void> registerWorker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				String username = usernameField.getText().trim();
				String password = new String(passwordField.getPassword());
				String fullName = fullNameField.getText().trim();
				String email = emailField.getText().trim();
				String phone = phoneField.getText().trim();
				String address = addressArea.getText().trim();
				
				// Create new user
				User newUser = new User(username, password, fullName, email, phone, address, "user");
				
				// Add to database
				return userDAO.addUser(newUser);
			}
			
			@Override
			protected void done() {
				try {
					boolean success = get();
					if (success) {
						JOptionPane.showMessageDialog(RegisterFrame.this,
								"Đăng ký thành công!\nBạn có thể đăng nhập ngay bây giờ.",
								"Thành công",
								JOptionPane.INFORMATION_MESSAGE);
						openLoginForm();
					} else {
						JOptionPane.showMessageDialog(RegisterFrame.this,
								"Đăng ký thất bại! Vui lòng thử lại.",
								"Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(RegisterFrame.this,
							"Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(),
							"Lỗi",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					registerButton.setEnabled(true);
					registerButton.setText("Đăng Ký");
				}
			}
		};
		
		registerWorker.execute();
	}
	
	private boolean validateForm() {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword());
		String confirmPassword = new String(confirmPasswordField.getPassword());
		String fullName = fullNameField.getText().trim();
		String email = emailField.getText().trim();
		String phone = phoneField.getText().trim();
		
		// Check empty fields
		if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
				fullName.isEmpty() || email.isEmpty()) {
			showError("Vui lòng điền đầy đủ thông tin bắt buộc!");
			return false;
		}
		
		// Check username length
		if (username.length() < 3 || username.length() > 50) {
			showError("Tên đăng nhập phải từ 3-50 ký tự!");
			usernameField.requestFocus();
			return false;
		}
		
		// Check password length
		if (password.length() < 6) {
			showError("Mật khẩu phải có ít nhất 6 ký tự!");
			passwordField.requestFocus();
			return false;
		}
		
		// Check password confirmation
		if (!password.equals(confirmPassword)) {
			showError("Mật khẩu xác nhận không khớp!");
			confirmPasswordField.requestFocus();
			return false;
		}
		
		// Check email format
		if (!isValidEmail(email)) {
			showError("Email không hợp lệ!");
			emailField.requestFocus();
			return false;
		}
		
		// Check phone format
		if (!phone.isEmpty() && !isValidPhone(phone)) {
			showError("Số điện thoại không hợp lệ!");
			phoneField.requestFocus();
			return false;
		}
		
		// Check if username exists
		if (userDAO.isUsernameExists(username)) {
			showError("Tên đăng nhập đã tồn tại!");
			usernameField.requestFocus();
			return false;
		}
		
		// Check if email exists
		if (userDAO.isEmailExists(email)) {
			showError("Email đã được sử dụng!");
			emailField.requestFocus();
			return false;
		}
		
		return true;
	}
	
	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		return Pattern.compile(emailRegex).matcher(email).matches();
	}
	
	private boolean isValidPhone(String phone) {
		// Vietnamese phone number format
		String phoneRegex = "^(\\+84|0)[1-9][0-9]{8,9}$";
		return Pattern.compile(phoneRegex).matcher(phone).matches();
	}
	
	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
	
	private void clearForm() {
		usernameField.setText("");
		passwordField.setText("");
		confirmPasswordField.setText("");
		fullNameField.setText("");
		emailField.setText("");
		phoneField.setText("");
		addressArea.setText("");
		usernameField.requestFocus();
	}
	
	private void openLoginForm() {
		dispose();
		new LoginFrame();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new RegisterFrame();
			}
		});
	}
}
