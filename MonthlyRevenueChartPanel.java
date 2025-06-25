package bieudo;

import internetbilling.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MonthlyRevenueChartPanel extends JPanel {
	private ChartPanel chartPanel;
	private JComboBox<Integer> yearSelector;
	
	public MonthlyRevenueChartPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Doanh thu theo tháng"));
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		yearSelector = new JComboBox<>();
		int currentYear = java.time.Year.now().getValue();
		for (int y = currentYear - 5; y <= currentYear; y++) {
			yearSelector.addItem(y);
		}
		yearSelector.setSelectedItem(currentYear);
		yearSelector.addActionListener(e -> refreshChart((Integer) yearSelector.getSelectedItem()));
		
		topPanel.add(new JLabel("Chọn năm:"));
		topPanel.add(yearSelector);
		add(topPanel, BorderLayout.NORTH);
		
		refreshChart(currentYear);
	}
	
	public void refreshChart(int year) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String sql = "SELECT MONTH(created_at) as month, SUM(amount) as total FROM transactions " +
				"WHERE YEAR(created_at) = ? AND status = 'completed' GROUP BY MONTH(created_at)";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, year);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int month = rs.getInt("month");
					double total = rs.getDouble("total");
					dataset.addValue(total, "Doanh thu", "Tháng " + month);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFreeChart chart = ChartFactory.createBarChart(
				"Doanh thu theo tháng - " + year,
				"Tháng",
				"Doanh thu (VND)",
				dataset
		);
		
		if (chartPanel != null) remove(chartPanel);
		chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}