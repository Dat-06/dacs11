package main;

import login.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main class để khởi động ứng dụng Internet Billing System
 */
public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				// Thiết lập giao diện Look and Feel
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
				// Khởi động màn hình đăng nhập
				new LoginFrame().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}