package bieudo;

import internetbilling.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRegistrationChartPanel extends JPanel {
	public UserRegistrationChartPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Số người dùng đăng ký theo tháng"));
		refreshChart();
	}
	
	public void refreshChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String sql = "SELECT MONTH(created_at) as month, COUNT(*) as total FROM users GROUP BY MONTH(created_at)";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				int month = rs.getInt("month");
				int total = rs.getInt("total");
				dataset.addValue(total, "Người dùng", "Tháng " + month);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFreeChart chart = ChartFactory.createBarChart(
				"Số người dùng đăng ký theo tháng",
				"Tháng",
				"Số lượng",
				dataset
		);
		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}