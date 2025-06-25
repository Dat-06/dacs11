package bieudo;

import javax.swing.*;
import java.awt.*;

public class ReportsChartPanel extends JPanel {
	private JTabbedPane tabbedPane;
	
	public ReportsChartPanel() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		// Add each chart in a pretty container
		tabbedPane.addTab("Người dùng đăng ký", createChartContainer(new UserRegistrationChartPanel()));
		tabbedPane.addTab("Gói cước đang hoạt động", createChartContainer(new ActivePackagesChartPanel()));
		tabbedPane.addTab("Loại giao dịch", createChartContainer(new TransactionTypeChartPanel()));
		tabbedPane.addTab("Doanh thu theo gói", createChartContainer(new RevenueByPackageChartPanel()));
		
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	public void refreshData() {
		tabbedPane.removeAll();
		
		tabbedPane.addTab("Người dùng đăng ký", createChartContainer(new UserRegistrationChartPanel()));
		tabbedPane.addTab("Gói cước đang hoạt động", createChartContainer(new ActivePackagesChartPanel()));
		tabbedPane.addTab("Loại giao dịch", createChartContainer(new TransactionTypeChartPanel()));
		tabbedPane.addTab("Doanh thu theo gói", createChartContainer(new RevenueByPackageChartPanel()));
		
		revalidate();
		repaint();
	}
	
	private JPanel createChartContainer(JPanel chartPanel) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)
		));
		wrapper.add(chartPanel, BorderLayout.CENTER);
		return wrapper;
	}
}
