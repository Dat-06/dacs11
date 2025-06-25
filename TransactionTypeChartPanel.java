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

public class TransactionTypeChartPanel extends JPanel {
	public TransactionTypeChartPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Loại giao dịch"));
		refreshChart();
	}
	
	public void refreshChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String sql = "SELECT type, COUNT(*) as total FROM transactions GROUP BY type";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				dataset.addValue(rs.getInt("total"), "Loại", rs.getString("type"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFreeChart chart = ChartFactory.createBarChart(
				"Phân loại giao dịch",
				"Loại giao dịch",
				"Số lượng",
				dataset
		);
		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}