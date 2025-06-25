package main;

import login.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main class để khởi động ứng dụng Internet Billing System
 */
public class Mainn {
	public static void main(String[] args) {
		// Thiết lập Look and Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Không thể thiết lập Look and Feel: " + e.getMessage());
		}
		
		// Chạy ứng dụng trên Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			try {
				// Hiển thị splash screen
				showSplashScreen();
				
				// Kiểm tra kết nối database
				if (!DatabaseUtils.testConnection()) {
					showDatabaseErrorDialog();
					return;
				}
				
			
				
				// Khởi tạo và hiển thị LoginFrame
				LoginFrame loginFrame = new LoginFrame();
				loginFrame.setVisible(true);
				
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Lỗi khởi tạo ứng dụng: " + e.getMessage(),
						"Lỗi",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		});
	}
	
	/**
	 * Hiển thị splash screen khi khởi động ứng dụng
	 */
	private static void showSplashScreen() {
		JWindow splash = new JWindow();
		splash.setSize(450, 350);
		splash.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(52, 152, 219));
		panel.setLayout(new BorderLayout());
		
		// Main content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(new Color(52, 152, 219));
		contentPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		
		// Logo/Icon
		JLabel iconLabel = new JLabel("🌐", SwingConstants.CENTER);
		iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 64));
		iconLabel.setForeground(Color.WHITE);
		gbc.gridy = 0;
		contentPanel.add(iconLabel, gbc);
		
		// Title
		JLabel titleLabel = new JLabel("Internet Billing System", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
		titleLabel.setForeground(Color.WHITE);
		gbc.gridy = 1;
		contentPanel.add(titleLabel, gbc);
		
		// Subtitle
		JLabel subtitleLabel = new JLabel("Hệ Thống Quản Lý Hóa Đơn Internet", SwingConstants.CENTER);
		subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		subtitleLabel.setForeground(Color.WHITE);
		gbc.gridy = 2;
		contentPanel.add(subtitleLabel, gbc);
		
		// Version
		JLabel versionLabel = new JLabel("Version 1.0", SwingConstants.CENTER);
		versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		versionLabel.setForeground(Color.WHITE);
		gbc.gridy = 3;
		contentPanel.add(versionLabel, gbc);
		
		// Loading
		JLabel loadingLabel = new JLabel("Đang khởi tạo...", SwingConstants.CENTER);
		loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
		loadingLabel.setForeground(Color.WHITE);
		gbc.gridy = 4;
		gbc.insets = new Insets(20, 10, 10, 10);
		contentPanel.add(loadingLabel, gbc);
		
		panel.add(contentPanel, BorderLayout.CENTER);
		
		// Footer
		JLabel footerLabel = new JLabel("© 2024 Internet Billing System", SwingConstants.CENTER);
		footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		footerLabel.setForeground(Color.WHITE);
		footerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		panel.add(footerLabel, BorderLayout.SOUTH);
		
		splash.add(panel);
		splash.setVisible(true);
		
		// Hiển thị trong 3 giây
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		splash.dispose();
	}
	
	/**
	 * Hiển thị dialog lỗi kết nối database
	 */
	private static void showDatabaseErrorDialog() {
		String message = """
            Không thể kết nối đến cơ sở dữ liệu MySQL!
            
            Vui lòng kiểm tra:
            1. MySQL Server đã được khởi động
            2. Database 'internet_billing' đã được tạo
            3. Thông tin kết nối trong DatabaseConnection.java
            
            Hướng dẫn:
            - Chạy MySQL Server
            - Thực hiện script sql/init_database.sql
            - Cập nhật username/password trong DatabaseConnection.java
            """;
		
		JOptionPane.showMessageDialog(null,
				message,
				"Lỗi Kết Nối Database",
				JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}
}