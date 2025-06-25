package bieudo;

import internetbilling.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RevenueByPackageChartPanel extends JPanel {
	private ChartPanel chartPanel;
	
	public RevenueByPackageChartPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Doanh thu theo gói cước"));
		
		JButton refreshBtn = new JButton("Làm mới");
		refreshBtn.addActionListener(e -> refreshChart());
		add(refreshBtn, BorderLayout.SOUTH);
		
		refreshChart();
	}
	
	public void refreshChart() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		String sql = "SELECT p.name, SUM(t.amount) as total FROM transactions t " +
				"JOIN subscriptions s ON t.subscription_id = s.id " +
				"JOIN packages p ON s.package_id = p.id " +
				"WHERE t.status = 'completed' GROUP BY p.name";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				dataset.setValue(rs.getString("name"), rs.getDouble("total"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFreeChart chart = ChartFactory.createPieChart("Doanh thu theo gói", dataset, true, true, false);
		if (chartPanel != null) remove(chartPanel);
		chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}
