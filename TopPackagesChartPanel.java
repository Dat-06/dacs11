package bieudo;

import internetbilling.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TopPackagesChartPanel extends JPanel {
	public TopPackagesChartPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Top 5 gói cước đăng ký nhiều nhất"));
		refreshChart();
	}
	
	public void refreshChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String sql = "SELECT p.name, COUNT(*) as total FROM subscriptions s " +
				"JOIN packages p ON s.package_id = p.id " +
				"GROUP BY p.name ORDER BY total DESC LIMIT 5";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				dataset.addValue(rs.getInt("total"), "Gói cước", rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFreeChart chart = ChartFactory.createBarChart(
				"Top 5 gói cước được đăng ký nhiều nhất",
				"Gói cước",
				"Lượt đăng ký",
				dataset
		);
		
		add(new ChartPanel(chart), BorderLayout.CENTER);
	}
}
