
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author arant
 */
public class SearchItem extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SearchItem.class.getName());

    /**
     * Creates new form SearchItem
     */
    private String currentUsername;
    private int studentId;
    private DashBoard dashboard;
    
    public SearchItem(String username, int studentId) {
        initComponents();
        setLocationRelativeTo(null);
        JTableHeader header = itemTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        this.currentUsername = username;
        this.studentId = studentId;
        
        //butangan ug actual data ang table
        loadItems();
        
        //pang color sa different statuses
            applyStatusColorRenderer();
        
        
        
        
        // para ma pindot ang column sa status
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          itemTable.setRowHeight(28); // 

            itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = itemTable.rowAtPoint(e.getPoint());
                int col = itemTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == itemTable.getColumn("Status").getModelIndex()) {
                    openDetailsFromTable(row);
                }
            }
        });
    }
    
    public SearchItem(DashBoard dashBoard, String username, int studentId) {
        initComponents();
        setLocationRelativeTo(null);
        this.dashboard = dashBoard;
        JTableHeader header = itemTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        this.currentUsername = username;
        this.studentId = studentId;
        
        //load sad if ever naa ang dashboard
        loadItems();
        //pang color sa diffrernt status
       applyStatusColorRenderer();
        
         // para ma pindot ang column sa status
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setRowHeight(28); // 
            itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = itemTable.rowAtPoint(e.getPoint());
                int col = itemTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == itemTable.getColumn("Status").getModelIndex()) {
                    openDetailsFromTable(row);
                }
            }
        });
    }
    
    public void backToDashboard(){
        if(dashboard != null){
            dashboard.setVisible(true);
            this.dispose();
        }else{
            DashBoard d = new DashBoard(dashboard.getCurrentUserEmail(), dashboard.getStudentID(), dashboard.getCurrentUserFullName(), dashboard.getCurrentUserContact());
            d.setVisible(true);
            this.dispose();
        }
    }
    
  private void openDetailsFromTable(int row) {
    String itemName = itemTable.getValueAt(row, 0).toString();
    String category = itemTable.getValueAt(row, 1).toString();
    String dateFoundStr = itemTable.getValueAt(row, 2).toString(); // keep as String
    String location = itemTable.getValueAt(row, 3).toString();
    String status = itemTable.getValueAt(row, 4).toString();

    int itemId = getItemIdByDetails(itemName, category, dateFoundStr, location); // pass String

    ItemDetails details = new ItemDetails(this, dashboard, itemId, itemName, category, dateFoundStr, status, null);
    details.setVisible(true);
   
}
    
   private int getItemIdByDetails(String name, String category, String dateFoundStr, String location) {
    int id = -1;
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "SELECT Item_ID FROM items WHERE item_name = ? AND category = ? AND date_found = ? AND location_last_seen = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ps.setString(2, category);

        // parse with correct format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dateFoundStr, formatter);
        ps.setDate(3, java.sql.Date.valueOf(parsedDate));

        ps.setString(4, location);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("Item_ID");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return id;
}
   
   private void loadItems() {
    DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
    model.setRowCount(0); // clear old rows
    

    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "SELECT item_name, category, date_found, location_last_seen, status FROM items ORDER BY DATE_FOUND DESC";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getDate("date_found").toString(),
                rs.getString("location_last_seen"),
                rs.getString("status")
            };
            model.addRow(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
   
   //pang color sa status column
   private void applyStatusColorRenderer() {
    int statusCol = itemTable.getColumn("Status").getModelIndex();

    itemTable.getColumnModel().getColumn(statusCol).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = value.toString().trim().toLowerCase();

            switch (status) {
                case "lost":
                    cell.setForeground(Color.RED);
                    break;
                case "found":
                    cell.setForeground(Color.BLUE);
                    break;
                case "claimed":
                    cell.setForeground(new Color(0, 153, 0)); // green
                    break;
                default:
                    cell.setForeground(Color.BLACK);
            }

            // Optional: background highlight
            if (isSelected) {
                cell.setBackground(new Color(230, 230, 250)); // light lavender
            } else {
                cell.setBackground(Color.WHITE);
            }

            return cell;
        }
    });
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
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        toDashboard = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        itemNameField = new javax.swing.JTextField();
        categoryDropdown = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(153, 51, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Search Your Lost Item");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, -1, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-search-100.png"))); // NOI18N
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 100, 90));

        toDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-arrow-left-40.png"))); // NOI18N
        toDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toDashboardMouseClicked(evt);
            }
        });
        jPanel3.add(toDashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 30, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 110));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        itemTable.setBackground(new java.awt.Color(255, 255, 255));
        itemTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        itemTable.setForeground(new java.awt.Color(0, 0, 0));
        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Yamaha Keys", "keys", "12-12-2021", "Room 208", "Claimed"},
                {"School Bag", "Bag", "11-24- 2025", "Library", "To be claimed"},
                {"Tecno Camon 40 Pro", "Phone", "10-5-2025", "Ssg Office", "Found"},
                {null, null, null, null, null}
            },
            new String [] {
                "Item name", "Category", "Date Reported", "Location", "Status"
            }
        ));
        itemTable.setShowGrid(true);
        jScrollPane2.setViewportView(itemTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 987, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 25, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 139, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel2);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 1000, 330));

        jPanel4.setBackground(new java.awt.Color(153, 153, 255));

        itemNameField.setBackground(new java.awt.Color(255, 255, 255));
        itemNameField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemNameField.setForeground(new java.awt.Color(102, 102, 102));
        itemNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNameFieldActionPerformed(evt);
            }
        });

        categoryDropdown.setBackground(new java.awt.Color(255, 255, 255));
        categoryDropdown.setForeground(new java.awt.Color(0, 0, 0));
        categoryDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Bag", "Cellphone", "Key", "Water Bottle", "School ID", "Others" }));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Item name: ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Category: ");

        jButton1.setBackground(new java.awt.Color(153, 51, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(itemNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 1000, 110));

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

    private void itemNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemNameFieldActionPerformed

    private void toDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toDashboardMouseClicked
        // TODO add your handling code here:
        backToDashboard();
    }//GEN-LAST:event_toDashboardMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String itemName = itemNameField.getText().trim();
        String category = categoryDropdown.getSelectedItem().toString();
        
       
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0); // clear previous results inag search
            
         try (Connection connection = DataBaseConnection.getConnection()) {
        // Base query
        String query = "SELECT item_name, category, date_found, location_last_seen, status FROM items WHERE 1=1"; // mas sayun maka append ug condition

        // Add filters dynamically
        if (!itemName.isEmpty()) {
            query += " AND item_name LIKE ?"; // only adds item if the user kay ni type
        }
        if (!category.equalsIgnoreCase("All")) {
            query += " AND category = ?"; // ana ra mo filter sa category if dili naka all ang categroy combo box
        }

        PreparedStatement ps = connection.prepareStatement(query);

        int paramIndex = 1;
        if (!itemName.isEmpty()) {
            ps.setString(paramIndex++, "%" + itemName + "%");
        }
        if (!category.equalsIgnoreCase("All")) {
            ps.setString(paramIndex++, category);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getDate("date_found").toString(),
                rs.getString("location_last_seen"),
                rs.getString("status")
            };
            model.addRow(row);
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching items found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }

    }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new SearchItem("test",1).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> categoryDropdown;
    private javax.swing.JTextField itemNameField;
    private javax.swing.JTable itemTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel toDashboard;
    // End of variables declaration//GEN-END:variables
}
