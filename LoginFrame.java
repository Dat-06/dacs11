package login;


import admin.AdminDashboard;
import users.User;
import users.UserDAO;
import users.UserDashboard;
import login.ForgotPasswordFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện đăng nhập
 */
public class LoginFrame extends JFrame {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton exitButton;
	private JButton forgotPasswordButton;
	private UserDAO userDAO;
	
	public LoginFrame() {
		userDAO = new UserDAO();
		initializeComponents();
		setupLayout();
		setupEventHandlers();
		
		
	}
	
	private void initializeComponents() {
		setTitle("Internet Billing System - Đăng Nhập");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		// Tạo các components
		usernameField = new JTextField(20);
		passwordField = new JPasswordField(20);
		loginButton = new JButton("Đăng Nhập");
	
		
		exitButton = new JButton("Thoát");
		
		forgotPasswordButton = new JButton("Quên mật khẩu?");
		
		// Thiết lập font
		Font labelFont = new Font("SansSerif", Font.BOLD, 12);
		Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);
		
		usernameField.setFont(fieldFont);
		passwordField.setFont(fieldFont);
		loginButton.setFont(labelFont);
		exitButton.setFont(labelFont);
		forgotPasswordButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
		
		// Thiết lập màu sắc
		loginButton.setBackground(new Color(52, 152, 219));
		loginButton.setForeground(Color.blue);
		exitButton.setBackground(new Color(231, 76, 60));
		exitButton.setForeground(Color.blue);
		forgotPasswordButton.setBackground(Color.WHITE);
		forgotPasswordButton.setForeground(new Color(52, 152, 219));
		forgotPasswordButton.setBorder(BorderFactory.createEmptyBorder());
		forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// Thiết lập kích thước button
		Dimension buttonSize = new Dimension(120, 35);
		loginButton.setPreferredSize(buttonSize);
		exitButton.setPreferredSize(buttonSize);
		
		// Focus mặc định
		loginButton.setDefaultCapable(true);
		getRootPane().setDefaultButton(loginButton);
	}
	
	private void setupLayout() {
		// Panel trái
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(60, 0));
		leftPanel.setBackground(new Color(224, 242, 241)); // màu xanh nhạt
		
		// Panel phải
		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(60, 0));
		rightPanel.setBackground(new Color(224, 242, 241)); // cùng màu trái
		
		// Panel chính (giữa)
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		
		// Tiêu đề
		JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ GÓI CƯỚC INTERNET");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		titleLabel.setForeground(new Color(52, 73, 94));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(titleLabel, gbc);
		
		// Icon
		JLabel iconLabel = new JLabel("🌐");
		iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 1;
		gbc.insets = new Insets(20, 10, 20, 10);
		mainPanel.add(iconLabel, gbc);
		
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;
		
		// Username
		JLabel usernameLabel = new JLabel("Tên đăng nhập:");
		usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		gbc.gridx = 0;
		gbc.gridy = 2;
		mainPanel.add(usernameLabel, gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		mainPanel.add(usernameField, gbc);
		
		// Password
		JLabel passwordLabel = new JLabel("Mật khẩu:");
		passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.EAST;
		mainPanel.add(passwordLabel, gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		mainPanel.add(passwordField, gbc);
		
		// Forgot password link
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 10, 10, 10);
		mainPanel.add(forgotPasswordButton, gbc);
		
		// Buttons panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(loginButton);
		buttonPanel.add(exitButton);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(15, 10, 10, 10);
		mainPanel.add(buttonPanel, gbc);
		
		// Thông tin mặc định
		JLabel infoLabel = new JLabel("<html><center>Tài khoản mặc định:<br/>admin / admin123</center></html>");
		infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
		infoLabel.setForeground(Color.GRAY);
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 6;
		gbc.insets = new Insets(20, 10, 10, 10);
		mainPanel.add(infoLabel, gbc);
		
		// Panel tổng bao gồm trái, giữa, phải
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.add(leftPanel, BorderLayout.WEST);
		wrapperPanel.add(mainPanel, BorderLayout.CENTER);
		wrapperPanel.add(rightPanel, BorderLayout.EAST);
		
		add(wrapperPanel, BorderLayout.CENTER);
		
		// Thiết lập kích thước và vị trí
		pack();
		setLocationRelativeTo(null);
	}
	
	
	private void setupEventHandlers() {
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performLogin();
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		// Enter key trong password field
		passwordField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performLogin();
			}
		});
		
		// Forgot password button
		forgotPasswordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			
				ForgotPasswordFrame dialog = new ForgotPasswordFrame();
				dialog.setVisible(true);
			}
		});
	}
	
	private void performLogin() {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword());
		
		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!",
					"Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Hiển thị loading
		loginButton.setEnabled(false);
		loginButton.setText("Đang xử lý...");
		
		// Thực hiện đăng nhập trong SwingWorker để không block UI
		SwingWorker<User, Void> loginWorker = new SwingWorker<User, Void>() {
			@Override
			protected User doInBackground() throws Exception {
				return userDAO.login(username, password).orElse(null);
			}
			
			
			@Override
			protected void done() {
				try {
					User user = get();
					if (user != null) {
						JOptionPane.showMessageDialog(LoginFrame.this,
								"Đăng nhập thành công! Chào mừng " + user.getFullName(),
								"Thành công",
								JOptionPane.INFORMATION_MESSAGE);
						
						openDashboard(user);
						dispose();
					
					
				} else {
						// Đăng nhập thất bại → gợi ý đk
						int choice = JOptionPane.showConfirmDialog(LoginFrame.this,
								"Tên đăng nhập không tồn tại hoặc mật khẩu không đúng.\nBạn có muốn đăng ký không?",
								"Lỗi đăng nhập",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						
						if (choice == JOptionPane.YES_OPTION) {
							// Mở form quên mật khẩu
							RegisterFrame registerFrame = new RegisterFrame();
							registerFrame.setVisible(true);
						}
						
						passwordField.setText("");
						passwordField.requestFocus();
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(LoginFrame.this,
							"Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(),
							"Lỗi",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					// Reset button
					loginButton.setEnabled(true);
					loginButton.setText("Đăng Nhập");
				}
			}
		};
		
		loginWorker.execute();
	}
	
	private void openDashboard(User user) {
		SwingUtilities.invokeLater(() -> {
			if ("admin".equals(user.getRole())) {
				new AdminDashboard(user).setVisible(true);
			} else {
				new UserDashboard(user).setVisible(true);
			}
		});
	}
}