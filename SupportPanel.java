package PASS;

import javax.swing.*;
import java.awt.*;

public class SupportPanel extends JPanel {
	public SupportPanel() {
		setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Hỗ Trợ & Liên Hệ", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		add(title, BorderLayout.NORTH);
		
		JTextArea supportInfo = new JTextArea();
		supportInfo.setText("""
				📞 Hotline: 1900-xxxxxx
				📧 Email: support@internetbilling.vn
				🌐 Website: https://internetbilling.vn
				🕘 Thời gian làm việc: 8:00 - 17:00 (T2 - T7)

				Nếu bạn có bất kỳ thắc mắc hay sự cố nào liên quan đến gói cước,
				thanh toán hoặc sử dụng dịch vụ, hãy liên hệ với chúng tôi!
				""");
		supportInfo.setFont(new Font("Monospaced", Font.PLAIN, 14));
		supportInfo.setEditable(false);
		supportInfo.setBackground(getBackground());
		supportInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		add(supportInfo, BorderLayout.CENTER);
	}
}
