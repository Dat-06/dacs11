package bieudo;

import internetbilling.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ActivePackagesChartPanel extends JPanel {
	public ActivePackagesChartPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Gói cước đang hoạt động"));
		refreshChart();
	}
	
	public void refreshChart() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		String sql = "SELECT p.name, COUNT(*) as total FROM subscriptions s " +
				"JOIN packages p ON s.package_id = p.id " +
				"WHERE s.status = 'active' GROUP BY p.name";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				dataset.setValue(rs.getString("name"), rs.getInt("total"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JFreeChart chart = ChartFactory.createPieChart("Gói cước đang hoạt động", dataset, true, true, false);
		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}