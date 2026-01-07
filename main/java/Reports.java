
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;




/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author arant
 */
public class Reports extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reports.class.getName());

    /**
     * Creates new form Reports
     */
    private String currentUserName;
    private int studentId;
    private DashBoard dashBoard;
    
    public Reports(DashBoard dashBoard,String username,int studentId) {
        initComponents();
        setLocationRelativeTo(null);
        this.currentUserName = username;
        this.studentId =studentId;
        this.dashBoard = dashBoard;
        setLocationRelativeTo(null);
        chartPanel.setLayout(new BorderLayout());
        loadData(); 
    }
    
    public Reports(String username,int studentId) {
        initComponents();
        this.currentUserName = username;
        this.studentId =studentId;
        this.dashBoard = dashBoard;
        setLocationRelativeTo(null);
        chartPanel.setLayout(new BorderLayout());
        loadData(); 
    }
    public void backToDashboard(){
        if(dashBoard!=null){
            dashBoard.setVisible(true);
            this.dispose();
        }
    }
    
    // kuhaon ang total items reported
    private int getTotalReported() {
    try (Connection conn = DataBaseConnection.getConnection()) {
        String query = "SELECT COUNT(*) FROM items";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}
    
    
    //kuhaon ang total Items Claimed
    private int getTotalClaimed() {
    try (Connection conn = DataBaseConnection.getConnection()) {
        String query = "SELECT COUNT(*) FROM items WHERE status = 'Claimed'";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

    // kuhaon ang average resolution time
    private double getAverageResolutionDays() {
    try (Connection conn = DataBaseConnection.getConnection()) {
        String query = "SELECT AVG(TIMESTAMPDIFF(HOUR, date_found, claimed_at) / 24.0) FROM items WHERE status = 'Claimed' AND claimed_at IS NOT NULL";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble(1);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0.1;
}
    
    
    // paras item distribution
    private Map<String, Integer> getCategoryCounts() {
    Map<String, Integer> categoryMap = new LinkedHashMap<>();
    try (Connection conn = DataBaseConnection.getConnection()) {
        String query = "SELECT category, COUNT(*) AS count FROM items GROUP BY category ORDER BY count DESC";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            categoryMap.put(rs.getString("category"), rs.getInt("count"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return categoryMap;
}
    
    //pang calculate sa percentage
    private Map<String, Double> getCategoryPercentages(Map<String, Integer> counts) {
    Map<String, Double> percentages = new LinkedHashMap<>();
    int total = counts.values().stream().mapToInt(Integer::intValue).sum();

    for (Map.Entry<String, Integer> entry : counts.entrySet()) {
        double percent = (entry.getValue() * 100.0) / total;
        percentages.put(entry.getKey(), percent);
    }
    return percentages;
}
    
    
    
    //I load ang mga data sa mga text field
        public void loadData() {
    totalItems.setText(String.valueOf(getTotalReported()));
    itemsClaimed.setText(String.valueOf(getTotalClaimed()));
   

    Map<String, Integer> counts = getCategoryCounts();
    Map<String, Double> percentages = getCategoryPercentages(counts);

    CategoryChartPanel chart = new CategoryChartPanel(counts, percentages);
    chart.setPreferredSize(new Dimension(500, counts.size() * 50));

    chartPanel.removeAll(); // clear old chart
    chartPanel.setLayout(new BorderLayout()); // make sure layout stretches
    chartPanel.add(chart, BorderLayout.CENTER); // add chart to container
    chartPanel.revalidate();
    chartPanel.repaint();
}
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        toDashboard = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        main = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        totalItems = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        itemsClaimed = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        chartPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(153, 51, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Reports and Analytics");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(" View detailed reports of lost and found items  ");

        toDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-arrow-left-40.png"))); // NOI18N
        toDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toDashboardMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(263, 263, 263)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel2)))
                .addContainerGap(397, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel1))
                    .addComponent(toDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        main.setBackground(new java.awt.Color(204, 204, 204));
        main.setForeground(new java.awt.Color(0, 0, 0));
        main.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(153, 51, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Total Items Reported");

        totalItems.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        totalItems.setForeground(new java.awt.Color(102, 0, 204));
        totalItems.setText("100");

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/found.png"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalItems, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 695, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalItems))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel10)))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 940, 120));

        main.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, 119));

        jPanel7.setBackground(new java.awt.Color(0, 153, 51));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Total Items Claimed");

        itemsClaimed.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        itemsClaimed.setForeground(new java.awt.Color(102, 0, 204));
        itemsClaimed.setText("97");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/claimed.png"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemsClaimed, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 705, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(itemsClaimed))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 940, 120));

        main.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 950, -1));

        chartPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 940, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        main.add(chartPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 940, 280));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 51, 255));
        jLabel3.setText("Categories");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        main.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 190, 40));

        jScrollPane1.setViewportView(main);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1059, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void toDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toDashboardMouseClicked
        // TODO add your handling code here:
        backToDashboard();
    }//GEN-LAST:event_toDashboardMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Reports("test",1).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JLabel itemsClaimed;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel main;
    private javax.swing.JLabel toDashboard;
    private javax.swing.JLabel totalItems;
    // End of variables declaration//GEN-END:variables
}
