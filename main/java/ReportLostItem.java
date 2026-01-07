
import com.mysql.cj.protocol.Resultset;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author arant
 */
public class ReportLostItem extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReportLostItem.class.getName());

    /**
     * Creates new form ReportLostItem
     */
    private String currentUserName;
    private File imageFile;
    private byte[] imageBytes;
    private int studentID;
    private DashBoard dashboard;
    
   

    public ReportLostItem(String email) {
        initComponents();
        setLocationRelativeTo(null);
        this.currentUserName = email;
        // place holder texts
        addPlaceholder(date, "yyyy-MM-dd");              // Date Lost field
        addPlaceholder(description, "Describe the item..."); // Description field
        addPlaceholder(itemName, "Enter item name");     // Example for item name
        
        //I set ang email,name, and contact number. kuhaon ras DB
        setBisuEmail(email);
        
       
    }
    
   
    
        public void backToDashboard(){
           DashBoard dashBoard = new DashBoard(currentUserName, studentID, currentUserName, currentUserName);
           dashBoard.setVisible(true);
           this.dispose();
        }
    
    
    // para pang text placeholder sa mga text fields
   
public static void addPlaceholder(JTextComponent textComponent, String placeholder) {
    textComponent.setText(placeholder);
    textComponent.setForeground(Color.GRAY);

    textComponent.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (textComponent.getText().equals(placeholder)) {
                textComponent.setText("");
                textComponent.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (textComponent.getText().isEmpty()) {
                textComponent.setText(placeholder);
                textComponent.setForeground(Color.GRAY);
            }
        }
    });
}
    
    
    
    //since naa naman tay email daan sa db, but no additional info, 
    // ang email ray di ma edit sa form
    public void setBisuEmail(String email){
        try{
            //establish connection
            Connection connection = DataBaseConnection.getConnection();
            String query = "SELECT ID,full_name,contact_number  FROM STUDENT WHERE Email =?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            
            //result set
            ResultSet resultset = preparedStatement.executeQuery();
            //result set starts before the 1st column man daw so inag next niya, maon na ang 1st column
            if(resultset.next()){
                //kuhaon ang int nga ID
                studentID = resultset.getInt("ID");
                //i set na daan ang bisu email nga field
                bisuEmail.setText(email);
                
                // kuhaon ang name sa table
                String name = resultset.getString("full_name");
                String contactNum = resultset.getString("contact_number");
                
                //set te contact number and fullname if naa na daan sa db
                contactNumber.setText(contactNum);
                fullName.setText(name);
                
                
                
            }
            
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load info to database",
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // para maka choose ug picture using JfileChooser
    public void chooseImage(){
        // gamag object nga file chooser
        JFileChooser chooser = new JFileChooser();
        
        // I filter nga images ra
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg","jpeg","png");
        
        // gamiton sa filechooser object ang filter
        chooser.setFileFilter(filter);
        
        // Ipakita ang dialog box para mopili og file. Ang result kay mo-indicate kung gi-OK ba sa user.
        int result = chooser.showOpenDialog(this);
        
        // Kung gi-OK sa user, kuhaon nato ang napiling file ug i-store sa imageFile
        if(result == JFileChooser.APPROVE_OPTION){
            imageFile = chooser.getSelectedFile();
            
            //show image sa panel
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(imageIcon.getWidth(), imageIcon.getHeight(), Image.SCALE_SMOOTH);
            
            imageIcon.setIcon(new ImageIcon(scaledImage));
        }
        imageBytes = convertImageToByte(imageFile);
    }
    
    
    // pang transform sa imageFile variable into a byte[] para ma butang siya sa
    // database as BLOB 
    public byte [] convertImageToByte(File imageFile){
        // gamit input and output stream
        try(FileInputStream inputStream = new FileInputStream(imageFile); // reads the image file byte by byte.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){ // collects those bytes into a dynamic array.
            
            // byte variable
            byte[] buffer = new byte[1024];
            int bytesRead; // track how many bytes were read in each loop.
            
            // reads the file chunk by chunk tas writes each chunk into the output stream
            while((bytesRead = inputStream.read(buffer)) != -1){ // -1 means ang kina last sa file or end sa file
                outputStream.write(buffer,0,bytesRead);
            }
            return outputStream.toByteArray(); // after ma read tanan, ma convert ang collected bytes into a byte[] para ma upload sa db
        }catch(IOException exception){
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to read image file","Error",JOptionPane.ERROR_MESSAGE);
                return null;
                }
    }
    
    
      //kuhaon ang mga data sa textfileds and other stuff
    public void getFormData(){
        
        String itemNames = itemName.getText().trim();
        String itemCategory = (String) category.getSelectedItem();
        String dates = date.getText().trim();
        String locations = location.getText().trim();
        String descriptions = description.getText().trim();
        
        if (itemNames.equalsIgnoreCase("Enter item Name")) {
            JOptionPane.showMessageDialog(this, "Please input Item name","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

      
       if(itemNames == null || itemNames.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please input Item name","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
       
       if (category.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }


        
        // tana wn  if naa ba sulod ang mga required jud
                if(itemNames.isEmpty() || dates.isEmpty() || descriptions.isEmpty() || locations.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill all textfields","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

       
        
        java.sql.Date sqlDate;
        
        try {
            sqlDate = java.sql.Date.valueOf(dates);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd", "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        if(imageFile == null){
            imageBytes = null;
        }else{
            imageBytes = convertImageToByte(imageFile);
        }
        
        insertItemToDB(itemNames, itemCategory, locations, sqlDate, descriptions, imageBytes);
        
        

          /* to do (mo kaon sako)
        1. convert the date variable from string to java.sql.date
        2. somehow get image bytes para ma upload sa db
        3. once ma validate tanan, i upload sa db
        
        insertItemToDB(email, name, finalNum, itemName, itemCategory, date, descriptions, photo, null)
        */
        
         
}
    // pang insert sa values into the database
    public void insertItemToDB(String itemName, String category, String location, Date datelost, String description, byte [] photo){
        try{
            Connection connection = DataBaseConnection.getConnection();
            
            if(connection != null){
                String query = "INSERT INTO ITEMS (REPORTED_BY, ITEM_NAME, CATEGORY, DATE_LOST, LOCATION_LAST_SEEN, DESCRIPTION, PHOTO, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
                
                preparedStatement.setInt(1, studentID);
                preparedStatement.setString(2, itemName);
                preparedStatement.setString(3, category);
                preparedStatement.setDate(4, datelost);
                preparedStatement.setString(5, location);
                preparedStatement.setString(6, description);
                
                if(photo == null){
                    preparedStatement.setNull(7, Types.BLOB);
                }else{
                    preparedStatement.setBytes(7, photo);
                }
                
                // I set na nga lost ang item currently
                preparedStatement.setString(8, "Lost");
            
                
               // kuhaon ang generated key sa prepared statement 
             int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                // kuhaon nag generated key
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    int itemId = rs.getInt(1); // this is the new ITEM_ID para magamit nato sa itemEvents method

                    // Log the event into item_history
                    itemEvents(itemId, "Lost",  location, studentID);
                    rs.close();
                }

                JOptionPane.showMessageDialog(this, "Item Successfully Reported","Success",JOptionPane.INFORMATION_MESSAGE);
                backToDashboard();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to report Item",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

                
                
                preparedStatement.close();
                connection.close();
                
            }else{
                JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error Connection",JOptionPane.ERROR_MESSAGE);
            }
            
            
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
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

        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        toDashboard = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        bisuEmail = new javax.swing.JTextField();
        contactNumber = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fullName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        itemName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        category = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        location = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        date = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        uploadPanel = new javax.swing.JPanel();
        imageIcon = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        submitBtn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("BISU email: ");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(153, 51, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Report Lost Item");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 0, -1, 60));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Let's Find That Item For You");
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

        bisuEmail.setBackground(new java.awt.Color(255, 255, 255));
        bisuEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bisuEmail.setForeground(new java.awt.Color(0, 0, 0));
        bisuEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bisuEmailActionPerformed(evt);
            }
        });

        contactNumber.setBackground(new java.awt.Color(255, 255, 255));
        contactNumber.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        contactNumber.setForeground(new java.awt.Color(0, 0, 0));
        contactNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactNumberActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Full Name");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Contact Number");

        fullName.setBackground(new java.awt.Color(255, 255, 255));
        fullName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        fullName.setForeground(new java.awt.Color(0, 0, 0));
        fullName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullNameActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Item Details");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-user-30.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Item Name");

        itemName.setBackground(new java.awt.Color(255, 255, 255));
        itemName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        itemName.setForeground(new java.awt.Color(0, 0, 0));
        itemName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNameActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Category");

        category.setBackground(new java.awt.Color(255, 255, 255));
        category.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        category.setForeground(new java.awt.Color(0, 0, 0));
        category.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bag", "Cellphone", "Key", "Water Bottle", "School ID", "Others" }));
        category.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Date Lost");

        location.setBackground(new java.awt.Color(255, 255, 255));
        location.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        location.setForeground(new java.awt.Color(0, 0, 0));
        location.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Location Last Seen");

        date.setBackground(new java.awt.Color(255, 255, 255));
        date.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        date.setForeground(new java.awt.Color(0, 0, 0));
        date.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateActionPerformed(evt);
            }
        });

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/found.png"))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Additional Information");

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-notes-30.png"))); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Description");

        description.setBackground(new java.awt.Color(255, 255, 255));
        description.setColumns(20);
        description.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        description.setRows(5);
        description.setText("Provide a detailed description about the item\n");
        jScrollPane2.setViewportView(description);

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-upload-30.png"))); // NOI18N

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Upload Photo (Optional)");

        uploadPanel.setBackground(new java.awt.Color(255, 255, 255));
        uploadPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        uploadPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                uploadPanelMouseClicked(evt);
            }
        });

        imageIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-upload-64.png"))); // NOI18N

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Click to Upload an Image Of The Item");

        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("PNG, JPG Up To 5 MB");

        javax.swing.GroupLayout uploadPanelLayout = new javax.swing.GroupLayout(uploadPanel);
        uploadPanel.setLayout(uploadPanelLayout);
        uploadPanelLayout.setHorizontalGroup(
            uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, uploadPanelLayout.createSequentialGroup()
                .addContainerGap(320, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(301, 301, 301))
            .addGroup(uploadPanelLayout.createSequentialGroup()
                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uploadPanelLayout.createSequentialGroup()
                        .addGap(381, 381, 381)
                        .addComponent(jLabel20))
                    .addGroup(uploadPanelLayout.createSequentialGroup()
                        .addGap(408, 408, 408)
                        .addComponent(imageIcon)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        uploadPanelLayout.setVerticalGroup(
            uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(imageIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        submitBtn.setBackground(new java.awt.Color(153, 51, 255));
        submitBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        submitBtn.setForeground(new java.awt.Color(255, 255, 255));
        submitBtn.setText("Submit");
        submitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitBtnActionPerformed(evt);
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
                            .addGap(23, 23, 23)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(7, 7, 7))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(category, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(itemName)
                                .addComponent(fullName)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4)
                                .addComponent(bisuEmail)
                                .addComponent(contactNumber)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9)
                                .addComponent(jLabel10)
                                .addComponent(jLabel11)
                                .addComponent(date)
                                .addComponent(location)
                                .addComponent(jLabel13)
                                .addComponent(jLabel15)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel17)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(609, 609, 609)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(submitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(32, 32, 32)
                .addComponent(bisuEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fullName, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemName, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(uploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(61, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void contactNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contactNumberActionPerformed

    private void fullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fullNameActionPerformed

    private void itemNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemNameActionPerformed

    private void categoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryActionPerformed

    private void locationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_locationActionPerformed

    private void dateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateActionPerformed

    private void toDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toDashboardMouseClicked
        // TODO add your handling code here:
        backToDashboard();  
    }//GEN-LAST:event_toDashboardMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        backToDashboard();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void uploadPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_uploadPanelMouseClicked
        // TODO add your handling code here:
        chooseImage();
    }//GEN-LAST:event_uploadPanelMouseClicked

    private void submitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitBtnActionPerformed
        // TODO add your handling code here:
        getFormData();
    }//GEN-LAST:event_submitBtnActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ReportLostItem("test").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bisuEmail;
    private javax.swing.JComboBox<String> category;
    private javax.swing.JTextField contactNumber;
    private javax.swing.JTextField date;
    private javax.swing.JTextArea description;
    private javax.swing.JTextField fullName;
    private javax.swing.JLabel imageIcon;
    private javax.swing.JTextField itemName;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField location;
    private javax.swing.JButton submitBtn;
    private javax.swing.JLabel toDashboard;
    private javax.swing.JPanel uploadPanel;
    // End of variables declaration//GEN-END:variables
}
