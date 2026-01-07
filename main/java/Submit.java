
import com.mysql.cj.PreparedQuery;
import java.awt.Image;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author arant
 */
public class Submit extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReportLostItem.class.getName());

    /**
     * Creates new form ReportLostItem
     */
    private String currentUsername;
    private int itemId;
    private int studentID;
    private Date dateItemFound;
    private DashBoard dashboard;
    private UserSession session;

    public Submit(String username,int id,int studentId) {
        initComponents();
        setLocationRelativeTo(null);
        this.currentUsername = username;
        this.itemId = id;
        
        // auto fill ang date inag report
        dateItemFound = Date.valueOf(LocalDate.now());
        dateFound.setText(dateItemFound.toString());

       
    }
    

    // another constructor pang set sa values gikan sa itemDetails
    public Submit(DashBoard dashBoard,String username, int id,int studentId, String email, String fullName,
                  String contactNumber, String itemName, String category, ImageIcon photo) {
        this(username, id,studentId);
        this.dashboard = dashBoard;
        setEmail(email);
        setFullName(fullName);
        setContactNumber(contactNumber);
        setItemName(itemName);
        setCategory(category);
        setPhoto(photo);
        
        // auto filled ang date sa found para deretso na (ug ma bara awh patay)
       dateItemFound = Date.valueOf(LocalDate.now());
       dateFound.setText(dateItemFound.toString());
        
    }
    
    //navigation to dashboard
    public void backToDashboard() {
    if (dashboard != null) {
        dashboard.setVisible(true);
        this.dispose();
    } else {
        DashBoard d = new DashBoard(dashboard.getCurrentUserEmail(), dashboard.getStudentID(), dashboard.getCurrentUserFullName(), dashboard.getCurrentUserContact());
        d.setVisible(true);
        this.dispose();
    }
}
    
// setters para ma pass on ang values gikan sa item details dialog
    public void setEmail(String email) {
       bisuEmail.setText(dashboard.getCurrentUserEmail());
         
    }

    public void setFullName(String fullName) {
        fullNameField.setText(dashboard.getCurrentUserFullName());
          
    }

    public void setContactNumber(String contactNumber) {
        contactNumberField.setText(dashboard.getCurrentUserContact());
    }

    public void setItemName(String itemName) {
        ItemNameFileld.setText(itemName);
         
    }

    public void setCategory(String category) {
        categoryField.setSelectedItem(category);
    }
    
    

    public void setPhoto(ImageIcon icon) {
    if (icon != null) {
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(
            pictureIcon.getWidth(),
            pictureIcon.getHeight(),
            Image.SCALE_SMOOTH
        );
        pictureIcon.setIcon(new ImageIcon(scaledImage));
    } else {
        pictureIcon.setIcon(null); // or set a default "No photo available" icon
    }
}
    
    // i insert ang location sa item table
   public void insertFoundToDb(String location, Date dateFound) {
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "UPDATE items SET status = ?, Found_At = ?,found_by =?, date_found = ? WHERE Item_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setString(1, "Found");   // mahimo found ang lost
        preparedStatement.setString(2, location); // Found_At
        preparedStatement.setInt(3, dashboard.getStudentID());
        preparedStatement.setDate(4, dateFound);  // date_Found
        preparedStatement.setInt(5, itemId);      // Item_ID pang refer sa item nakita

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Item " + itemId + " marked as found at " + location);
            itemEvents(itemId, "Found", location, dashboard.getStudentID());
            JOptionPane.showMessageDialog(this, "Item Reported Successfully", "Success!",JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            
            // gama bag o nga dashboard para bag o ang data after submission
             DashBoard d = new DashBoard(
                dashboard.getCurrentUserEmail(),        // or fullEmail
                dashboard.getStudentID(),    // studentId
                dashboard.getCurrentUserFullName(),     // fullName
                dashboard.getCurrentUserContact()// contact
    );

            d.setVisible(true);
        } else {
            System.out.println("No item updated. Check Item_ID.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}
   
   public void getDetails(){
       
       
       String location = locationFound.getText();
       
       if(location.isEmpty()){
           JOptionPane.showMessageDialog(this, "Please specify where did you find the item", "Location required", JOptionPane.ERROR_MESSAGE);
           return;
       }
       
       insertFoundToDb(location, dateItemFound);
       
       
       
   }
   
       //method para ma populate and Item history table, which is gamiton nato para sa mini notif panel
    public void itemEvents(int itemID,String eventType, String details,Integer studentId){
        try(Connection connection = DataBaseConnection.getConnection()){
              String query = "INSERT INTO itemhistory (ITEM_ID, EVENT_TYPE, EVENT_DATE, DETAILS, STUDENT_ID) " +
                       "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";
               PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, itemID);
        ps.setString(2, eventType);
        ps.setString(3, details);
        
            if (studentId != null) {
                ps.setInt(4, studentId);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();

        }catch (SQLException e) {
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        toDashboard = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        bisuEmail = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        contactNumberField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fullNameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ItemNameFileld = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        categoryField = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        locationFound = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        dateFound = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        uploadPanel = new javax.swing.JPanel();
        pictureIcon = new javax.swing.JLabel();
        submit_btn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(153, 51, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Submit Lost Item");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 0, -1, 60));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Let's Return That Item For You");
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, -1, 21));

        toDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-arrow-left-40.png"))); // NOI18N
        toDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toDashboardMouseClicked(evt);
            }
        });
        jPanel2.add(toDashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 30, 30));

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Your Information");

        bisuEmail.setEditable(false);
        bisuEmail.setBackground(new java.awt.Color(255, 255, 255));
        bisuEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bisuEmail.setForeground(new java.awt.Color(0, 0, 0));
        bisuEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bisuEmailActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("BISU email: ");

        contactNumberField.setEditable(false);
        contactNumberField.setBackground(new java.awt.Color(255, 255, 255));
        contactNumberField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        contactNumberField.setForeground(new java.awt.Color(0, 0, 0));
        contactNumberField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactNumberFieldActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Full Name");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Contact Number");

        fullNameField.setEditable(false);
        fullNameField.setBackground(new java.awt.Color(255, 255, 255));
        fullNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        fullNameField.setForeground(new java.awt.Color(0, 0, 0));
        fullNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullNameFieldActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Item Details");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-user-30.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Item Name");

        ItemNameFileld.setEditable(false);
        ItemNameFileld.setBackground(new java.awt.Color(255, 255, 255));
        ItemNameFileld.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ItemNameFileld.setForeground(new java.awt.Color(0, 0, 0));
        ItemNameFileld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemNameFileldActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Category");

        categoryField.setBackground(new java.awt.Color(255, 255, 255));
        categoryField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        categoryField.setForeground(new java.awt.Color(0, 0, 0));
        categoryField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bag", "Cellphone", "Key", "Water Bottle", "School ID" }));
        categoryField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryFieldActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Date Found");

        locationFound.setBackground(new java.awt.Color(255, 255, 255));
        locationFound.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        locationFound.setForeground(new java.awt.Color(0, 0, 0));
        locationFound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationFoundActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Location Found");

        dateFound.setBackground(new java.awt.Color(255, 255, 255));
        dateFound.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        dateFound.setForeground(new java.awt.Color(0, 0, 0));
        dateFound.setText("mm/dd/yy");
        dateFound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateFoundActionPerformed(evt);
            }
        });

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/found.png"))); // NOI18N

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-upload-30.png"))); // NOI18N

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Uploaded Photo ");

        uploadPanel.setBackground(new java.awt.Color(255, 255, 255));
        uploadPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        pictureIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-upload-64.png"))); // NOI18N

        javax.swing.GroupLayout uploadPanelLayout = new javax.swing.GroupLayout(uploadPanel);
        uploadPanel.setLayout(uploadPanelLayout);
        uploadPanelLayout.setHorizontalGroup(
            uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanelLayout.createSequentialGroup()
                .addGap(332, 332, 332)
                .addComponent(pictureIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(376, Short.MAX_VALUE))
        );
        uploadPanelLayout.setVerticalGroup(
            uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(pictureIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        submit_btn.setBackground(new java.awt.Color(153, 51, 255));
        submit_btn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        submit_btn.setForeground(new java.awt.Color(255, 255, 255));
        submit_btn.setText("Submit");
        submit_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submit_btnActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(uploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(categoryField, 0, 886, Short.MAX_VALUE)
                                .addComponent(ItemNameFileld)
                                .addComponent(fullNameField)
                                .addComponent(jLabel3)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4)
                                .addComponent(bisuEmail)
                                .addComponent(contactNumberField)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9)
                                .addComponent(jLabel10)
                                .addComponent(jLabel11)
                                .addComponent(dateFound)
                                .addComponent(locationFound)))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel17)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(609, 609, 609)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(submit_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bisuEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fullNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contactNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ItemNameFileld, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateFound, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(locationFound, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(uploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submit_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void bisuEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bisuEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bisuEmailActionPerformed

    private void contactNumberFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactNumberFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contactNumberFieldActionPerformed

    private void fullNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fullNameFieldActionPerformed

    private void ItemNameFileldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemNameFileldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ItemNameFileldActionPerformed

    private void categoryFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryFieldActionPerformed

    private void locationFoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationFoundActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_locationFoundActionPerformed

    private void dateFoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateFoundActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateFoundActionPerformed

    private void toDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toDashboardMouseClicked
        // TODO add your handling code here:
        backToDashboard();
    }//GEN-LAST:event_toDashboardMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        backToDashboard();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void submit_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submit_btnActionPerformed
        // TODO add your handling code here:
        insertFoundToDb(currentUsername, dateItemFound);
        
        
    }//GEN-LAST:event_submit_btnActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Submit("test",1,2).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ItemNameFileld;
    private javax.swing.JTextField bisuEmail;
    private javax.swing.JComboBox<String> categoryField;
    private javax.swing.JTextField contactNumberField;
    private javax.swing.JTextField dateFound;
    private javax.swing.JTextField fullNameField;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField locationFound;
    private javax.swing.JLabel pictureIcon;
    private javax.swing.JButton submit_btn;
    private javax.swing.JLabel toDashboard;
    private javax.swing.JPanel uploadPanel;
    // End of variables declaration//GEN-END:variables

}

