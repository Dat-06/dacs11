//package login;
//
//import users.User;
//import users.UserDAO;
//
//import javax.swing.JOptionPane;
//
///**
// * Ví dụ sử dụng chức năng quên mật khẩu
// */
//public class ForgotPasswordExample {
//
//	public static void main(String[] args) {
//		// Test chức năng quên mật khẩu
//		forgotPassword();
//	}
//
//	public static void forgotPassword() {
//		String email = JOptionPane.showInputDialog("Nhập email của bạn:");
//
//		if (email == null || email.trim().isEmpty()) {
//			JOptionPane.showMessageDialog(null, "Email không được để trống!");
//			return;
//		}
//
//		UserDAO userDAO = new UserDAO();
//
//		// Kiểm tra email có tồn tại không
//		if (!userDAO.isEmailExists(email)) {
//			JOptionPane.showMessageDialog(null, "Email không tồn tại trong hệ thống!");
//			return;
//		}
//
//		try {
//			// Lấy thông tin user
//			User user = userDAO.findByEmail(email);
//			if (user == null) {
//				JOptionPane.showMessageDialog(null, "Email không tồn tại trong hệ thống!");
//				return;
//			}
//
//			// Tạo mật khẩu mới
//			String newPassword = EmailService.generateRandomPassword();
//
//			// Cập nhật mật khẩu trong database
//			boolean passwordUpdated = userDAO.updatePassword(email, newPassword);
//
//			if (passwordUpdated) {
//				// Gửi email mật khẩu mới
//				boolean emailSent = EmailService.sendNewPassword(email, newPassword, user.getFullName());
//
//				if (emailSent) {
//					JOptionPane.showMessageDialog(null,
//							"Mật khẩu mới đã được gửi về email của bạn!");
//				} else {
//					JOptionPane.showMessageDialog(null,
//							"Cập nhật mật khẩu thành công nhưng không thể gửi email. " +
//									"Mật khẩu mới của bạn là: " + newPassword);
//				}
//			} else {
//				JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi cập nhật mật khẩu!");
//			}
//
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
//		}
//	}
//}