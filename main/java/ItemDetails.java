
import com.mysql.cj.protocol.Resultset;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */

/**
 *
 * @author arant
 */
public class ItemDetails extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ItemDetails.class.getName());
    
    private String itemName;
    private String category;
    private String owner;
    private String dateLost;
    private String status;
    private byte[] imageBytes; // optional, for the picture if naa
    private int itemId;
    private String email;
    private String contactNumber;
    
    private DashBoard dashBoard;
    
    
    
    /**
     * Creates new form ItemDetails
     */
    public ItemDetails(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // center on parent frame
        setLocationRelativeTo(parent);
        

     
        
    }
    
    // custom constructor para makabutang tas details gikan sa jtable with picture
    public ItemDetails(Frame parent,DashBoard dashboard, int itemId, String itemName, String category,
                   String dateLost, String status, byte[] imageBytes) {
    super(parent, true);
    initComponents();
    setLocationRelativeTo(parent);
    
    this.dashBoard = dashboard;
    this.itemId = itemId;
    this.itemName = itemName;
    this.category = category;
    this.dateLost = dateLost;
    this.status = status;
    this.imageBytes = imageBytes;

    ItemText.setText(itemName);
    categoryText.setText(category);
    locText.setText(dateLost);
    statusLabel.setText(status);
    ownerText.setText(getItemOwnerName(itemId));
    
    
     if (dashBoard.getStudentID() != getItemOwnerStudentID(itemId)) {
    delete.setVisible(false);
     }
    
    
    //checks if ang status sa item is found or lost, if found ang item, i check if ang Id sa student nag match sa
    // Id sa student nga ga report sa item para pwde ba niya ma claim
    
    int ownerId = getItemOwnerStudentID(itemId); // fetch reported_by from DB

    if ("Found".equals(status) && dashBoard.getStudentID() == ownerId) {
        foundBtn.setText("Claim");
        foundBtn.setBackground(new Color(0, 204, 102)); // green
    } else if ("Lost".equals(status)) {
        foundBtn.setText("Found it");
        foundBtn.setBackground(new Color(255, 102, 102)); // red
    } else {
        foundBtn.setVisible(false);
        lastSeen.setText("Item Found");
    }
    
    // checks if naa bay picture or wala ang item
    if (imageBytes != null && imageBytes.length > 0) {
        ImageIcon icon = new ImageIcon(imageBytes);
        Image scaledImage = icon.getImage().getScaledInstance(itemImage.getWidth()+10, itemImage.getHeight(), Image.SCALE_SMOOTH);
        itemImage.setIcon(new ImageIcon(scaledImage));
    } else {
        itemImage.setText("No image available");
    }

    getDescription(itemId);
    changeColor();
    
 
}
    
    
    // another constructor that acceppts itam nga wala image
    public ItemDetails(Frame parent, int itemId, String itemName, String category,
                   String dateLost, String status) {
    super(parent, true);
    initComponents();
    setLocationRelativeTo(parent);

    this.itemId = itemId;
    this.itemName = itemName;
    this.category = category;
    this.dateLost = dateLost;
    this.status = status;

    ItemText.setText(itemName);
    categoryText.setText(category);
    locText.setText(dateLost);
    statusLabel.setText(status);
    ownerText.setText(getItemOwnerName(itemId));
    

    changeColor();
    if (dashBoard.getStudentID() != getItemOwnerStudentID(itemId)) {
    delete.setVisible(false);
}

}
    
    public void setDashboard(DashBoard dashBoard){
        this.dashBoard = dashBoard;
    }
    
    public boolean checkOwner(){
        int ownerId = getItemOwnerStudentID(itemId);
        if(dashBoard.getStudentID() == (ownerId)){
            JOptionPane.showMessageDialog(this, "Congratulations for finding your own item","Congratulations",JOptionPane.DEFAULT_OPTION);
            return true;
        }
        return false;
    }

    public void deleteItem(int itemId, int requestingStudentId) {
    Connection connection = null;

    try {
        connection = DataBaseConnection.getConnection();
        connection.setAutoCommit(false); // START TRANSACTION

        // 1️⃣ Delete child records FIRSt
        String deleteHistory = "DELETE FROM itemhistory WHERE ITEM_ID = ?";
        PreparedStatement psHistory = connection.prepareStatement(deleteHistory);
        psHistory.setInt(1, itemId);
        psHistory.executeUpdate();
        psHistory.close();

        String deleteItem = "DELETE FROM items WHERE ITEM_ID = ? AND reported_by = ?";
        PreparedStatement psItem = connection.prepareStatement(deleteItem);
        psItem.setInt(1, itemId);
        psItem.setInt(2, requestingStudentId);

        int affected = psItem.executeUpdate();
        psItem.close();

        if (affected == 0) {
            connection.rollback();
            JOptionPane.showMessageDialog(
                this,
                "You are not allowed to delete this item.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        connection.commit(); // ✅ SUCCESS

        JOptionPane.showMessageDialog(
            this,
            "Item successfully deleted.",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
        dashBoard.refreshTable();
        this.dispose();


        this.dispose();
      

    } catch (SQLException e) {
        try {
            if (connection != null) connection.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        e.printStackTrace();
        JOptionPane.showMessageDialog(
            this,
            "Error deleting item.",
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    } finally {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



    public void getDescription(int itemID){
        String description ="";
        
        try(Connection connection = DataBaseConnection.getConnection()){
            String query = "SELECT description FROM items where ITEM_ID =?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            
            // i set ang parameter
            preparedStatement.setInt(1, itemID);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            
            if(resultSet.next()){
                description = resultSet.getString("description");
                descriptionText.setText(description);
                descriptionText.setCaretPosition(0);
                descriptionText.setEditable(false);
            }
            
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        
    }
    
    public void changeColor(){
       
        if(status.equalsIgnoreCase("lost")){
            colorPanel.setBackground(Color.red);
        }else if(status.equalsIgnoreCase("Found")){
            colorPanel.setBackground(Color.blue);
        }else if(status.equalsIgnoreCase("Claimed")){
            colorPanel.setBackground(Color.green);
        }
    }
    
    public void openSubmit(String email, String fullName, String contactNumber,
                       String itemName, String category, byte[] photo) {
        
        ImageIcon icon = (photo != null && photo.length > 0) 
        ? new ImageIcon(photo) 
        : null;

    Submit sub = new Submit(
            dashBoard,
            dashBoard.getCurrentUserEmail(),
            itemId,
            dashBoard.getStudentID(),
            dashBoard.getCurrentUserEmail(),
            dashBoard.getCurrentUserFullName(),
            dashBoard.getCurrentUserContact(),
            itemName,
            category,
            icon
        );
    sub.setEmail(email);
    sub.setFullName(fullName);
    sub.setContactNumber(contactNumber);
    sub.setItemName(itemName);
    sub.setCategory(category);

    
    System.out.println("Email: " + email);
    System.out.println("Full Name: " + fullName);
    System.out.println("Contact: " + contactNumber);
    
    sub.setLocationRelativeTo(null);
    sub.setVisible(true);
    this.dispose();
    dashBoard.setVisible(false);
    
    
}
    
    
    // kuhaon ang Id sa nag report para i compare sa studentId sa item, that way, maka balo ta if iyaha bajud
    private int getItemOwnerStudentID(int itemId) {
    int ownerId = -1;
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "SELECT reported_by FROM items WHERE Item_ID = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, itemId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ownerId = rs.getInt("reported_by");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return ownerId;
}
    
    private String getItemOwnerName(int itemId) {
    String ownerName = "";
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "SELECT s.full_name " +
                       "FROM items i " +
                       "JOIN student s ON i.reported_by = s.id " +
                       "WHERE i.item_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, itemId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ownerName = rs.getString("full_name");
        }
        rs.close();
        ps.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return ownerName;
}
    
    // para ma claim ang item
    private void claimItem(int itemId, int studentId) {
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "UPDATE items SET status = 'Claimed',found_by =?,claimed_at = CURRENT_TIMESTAMP WHERE Item_ID = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        
        ps.setInt(1, studentId); // whoever claimed/found it
        ps.setInt(2, itemId);

        int updated = ps.executeUpdate();
        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "Item successfully claimed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            logClaimEvent(itemId, studentId); // I butang sa ItemHistory table sad
            this.dispose();
            DashBoard d = new DashBoard(dashBoard.getCurrentUserEmail(),
                                        dashBoard.getStudentID(),
                                        dashBoard.getCurrentUserFullName(),
                                        dashBoard.getCurrentUserContact());
            d.setVisible(true);

        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    //I insert dayun sa itemHIstory table if na claim
    private void logClaimEvent(int itemID, int studentID) {
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "INSERT INTO itemhistory (ITEM_ID, EVENT_TYPE, EVENT_DATE, DETAILS, STUDENT_ID) " +
                       "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setInt(1, itemID);
        ps.setString(2, "Claimed"); // event type
        ps.setString(3, "Item claimed by student " + studentID); // details
        ps.setInt(4, studentID);

        ps.executeUpdate();
        ps.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
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
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lastSeen = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionText = new javax.swing.JTextArea();
        header = new javax.swing.JPanel();
        itemImage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        ownerText = new javax.swing.JTextField();
        locText = new javax.swing.JTextField();
        categoryText = new javax.swing.JTextField();
        ItemText = new javax.swing.JTextField();
        foundBtn = new javax.swing.JButton();
        colorPanel = new RoundedPanel(20);
        statusLabel = new javax.swing.JLabel();
        delete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Item name:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/found.png"))); // NOI18N
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 187, 31, 25));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Item Details");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 187, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Category: ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Owner: ");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 300, -1, -1));

        lastSeen.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lastSeen.setForeground(new java.awt.Color(0, 0, 0));
        lastSeen.setText("Location last seen:");
        jPanel1.add(lastSeen, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Item description:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, -1, -1));

        descriptionText.setEditable(false);
        descriptionText.setBackground(new java.awt.Color(255, 255, 255));
        descriptionText.setColumns(20);
        descriptionText.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        descriptionText.setForeground(new java.awt.Color(0, 0, 0));
        descriptionText.setRows(5);
        descriptionText.setText("Unta I balik ninyo ako selpon kay wala nako selpon\n");
        descriptionText.setWrapStyleWord(true);
        jScrollPane2.setViewportView(descriptionText);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 380, 100));

        header.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        itemImage.setBackground(new java.awt.Color(204, 204, 204));
        itemImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itemImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/placeholder.png"))); // NOI18N
        itemImage.setText("placeholder");
        itemImage.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        itemImage.setPreferredSize(new java.awt.Dimension(200, 200));
        header.add(itemImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 210, 136));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/header.png"))); // NOI18N
        header.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 169));

        jPanel1.add(header, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 170));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 220, 375, 2));

        ownerText.setEditable(false);
        ownerText.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ownerText.setText("Aranton");
        ownerText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerTextActionPerformed(evt);
            }
        });
        jPanel1.add(ownerText, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 320, 170, 40));

        locText.setEditable(false);
        locText.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        locText.setText("Sa room");
        locText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locTextActionPerformed(evt);
            }
        });
        jPanel1.add(locText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 380, 40));

        categoryText.setEditable(false);
        categoryText.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        categoryText.setText("Selpon");
        categoryText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryTextActionPerformed(evt);
            }
        });
        jPanel1.add(categoryText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 170, 40));

        ItemText.setEditable(false);
        ItemText.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ItemText.setText("Item name");
        ItemText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemTextActionPerformed(evt);
            }
        });
        jPanel1.add(ItemText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 380, 40));

        foundBtn.setBackground(new java.awt.Color(153, 255, 153));
        foundBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        foundBtn.setForeground(new java.awt.Color(0, 0, 0));
        foundBtn.setText("Found it");
        foundBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foundBtnActionPerformed(evt);
            }
        });
        jPanel1.add(foundBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 590, 90, 40));

        statusLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(255, 255, 255));
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusLabel.setText("Status");

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel1.add(colorPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 180, 100, 30));

        delete.setBackground(new java.awt.Color(255, 0, 0));
        delete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        delete.setForeground(new java.awt.Color(255, 255, 255));
        delete.setText("Delete");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        jPanel1.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 590, 90, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ownerTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ownerTextActionPerformed

    private void locTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_locTextActionPerformed

    private void categoryTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryTextActionPerformed

    private void ItemTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ItemTextActionPerformed

    private void foundBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foundBtnActionPerformed
        // TODO add your handling code here:
        String label = foundBtn.getText();

    if ("Found it".equals(label)) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you've found the item?\n\n Warning: If you're trolling, your identity can be traced.",
            "Confirm Found",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if(checkOwner()){
                claimItem(itemId, dashBoard.getStudentID() );
            }else{
                openSubmit(email, owner, contactNumber, itemName, category, imageBytes);
            }

        }
    } else if ("Claim".equals(label)) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Do you want to claim this item?",
            "Confirm Claim",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            claimItem(itemId, dashBoard.getStudentID()); // update DB status to Claimed
        }
    }


        
    }//GEN-LAST:event_foundBtnActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        // TODO add your handling code here:
         int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete this item?\nThis action cannot be undone.",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
        deleteItem(itemId, dashBoard.getStudentID());
    }
        
    }//GEN-LAST:event_deleteActionPerformed

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
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ItemDetails dialog = new ItemDetails(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ItemText;
    private javax.swing.JTextField categoryText;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JButton delete;
    private javax.swing.JTextArea descriptionText;
    private javax.swing.JButton foundBtn;
    private javax.swing.JPanel header;
    private javax.swing.JLabel itemImage;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lastSeen;
    private javax.swing.JTextField locText;
    private javax.swing.JTextField ownerText;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
}
