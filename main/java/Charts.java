/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


public class Charts {
    
       // 🟦 1. Bar Chart (for Weekly Reports)
    public static ChartPanel createWeeklyReportChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(12, "Items Found", "Monday");
        dataset.addValue(18, "Items Found", "Tuesday");
        dataset.addValue(8, "Items Found", "Wednesday");
        dataset.addValue(20, "Items Found", "Thursday");
        dataset.addValue(10, "Items Found", "Friday");

        JFreeChart chart = ChartFactory.createBarChart(
                "Weekly Reports",
                "Day",
                "Count",
                dataset
        );

        return new ChartPanel(chart);
    }

    // 🟣 2. Pie Chart (for Statistics)
    public static ChartPanel createStatisticsPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("To be Claimed", 53);
        dataset.setValue("Items Found", 17);
        dataset.setValue("Claimed Items", 30);

        JFreeChart chart = ChartFactory.createPieChart(
                "Statistics",
                dataset,
                true,   // include legend
                true,   // tooltips
                false   // URLs
        );

        return new ChartPanel(chart);
    }
    
}
