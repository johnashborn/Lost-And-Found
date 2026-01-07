
import com.mysql.cj.protocol.Resultset;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;


public class SignUp extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SignUp.class.getName());

    private String requiredSignUpEmail;
    private String requiredSignUpPassword;
    private LogIn parent;

    /**
     * Creates new form SignUp
     *
     * @param parent
     */
    public SignUp(LogIn parent) {
        this.parent = parent;
        initComponents();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

       

        addPlaceholder(fullNameField, "Last name,First name");
    }

    public void registerAction() throws SQLException {
        String email = signUpEmail1.getText().trim();
        String password = new String(signUpPassword1.getPassword());
        String contactNumber = phone.getText().trim();
        String fullName = fullNameField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || contactNumber.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all the fields", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("na Triggered sa sign up");
            return;
        }
        
                if (password.length() < 8) {
             JOptionPane.showMessageDialog(this,
                 "Password must be at least 8 characters long",
                 "Error",
                 JOptionPane.ERROR_MESSAGE);
             return;
         }

        if (!contactNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "The contact number must be numbers only", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
            if (!contactNumber.matches("\\d{11}")) {
        JOptionPane.showMessageDialog(this,
            "The contact number must be exactly 11 digits",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

        if (!(email.endsWith("@gmail.com") || email.endsWith("@bisu.edu.ph") || email.endsWith("@yahoo.com"))) {
    
                JOptionPane.showMessageDialog(this, 
                    "Must be a valid email address",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
        } else {

            try (Connection connection = DataBaseConnection.getConnection()) {
                if (connection != null) {
                    // Check email
                        String emailQuery = "SELECT COUNT(*) FROM student WHERE email = ?";
                        PreparedStatement emailStmt = connection.prepareStatement(emailQuery);
                        emailStmt.setString(1, email);
                        ResultSet emailRs = emailStmt.executeQuery();
                        
                        if (emailRs.next() && emailRs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            emailRs.close();
                            emailStmt.close();
                            return;

                            }

                      
                        // check if phone numebr kay unique
                    String phoneQuery = "SELECT COUNT(*) FROM student WHERE contact_number = ?";
                        PreparedStatement phoneStmt = connection.prepareStatement(phoneQuery);
                        phoneStmt.setString(1, contactNumber);
                        ResultSet phoneRs = phoneStmt.executeQuery();
                        
                        if (phoneRs.next() && phoneRs.getInt(1) > 0) {
                            phoneRs.close();
                            phoneStmt.close();
                            JOptionPane.showMessageDialog(this, "Contact number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }


                    // Insert new account sa database if unique ang number and email
                    System.out.println("Attempting to insert new student: " + email);
                    String query = "INSERT INTO student(email, password, full_name, contact_number) VALUES(?,?,?,?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, password);
                    preparedStatement.setString(3, fullName);
                    preparedStatement.setString(4, contactNumber);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                            signUpEmail1.setText("");
                            signUpPassword1.setText("");
                            phone.setText("");
                            fullNameField.setText("");
                        // balik sa log in
                        if (parent != null) {
                             this.dispose();              // Close signup form first
                                parent.clearLoginFields();    // Clear the fields
                                parent.setVisible(true);      // Then show login
                                parent.toFront();            // Bring to front
                                parent.requestFocus();       // Request focus
                        }
                        JOptionPane.showMessageDialog(this, "Account successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                      

                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create account!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    preparedStatement.close();
                    emailStmt.close();
                    phoneStmt.close();
                }
            } catch (SQLException exp) {
                if (exp.getErrorCode() == 1062) { // MySQL duplicate entry error
                    JOptionPane.showMessageDialog(this, "Email or contact number must be unique!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    exp.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // pang placeholder sa fullname text field
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fullNameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        createAcc = new javax.swing.JButton();
        signUpPassword1 = new javax.swing.JPasswordField();
        signUpPassword2 = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        signUpEmail1 = new javax.swing.JTextField();
        phone = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Bohol Island State University - Copy.png"))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Sign-Up Your Account");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, 68));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Email");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 56, -1));

        fullNameField.setBackground(new java.awt.Color(255, 255, 255));
        fullNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        fullNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullNameFieldActionPerformed(evt);
            }
        });
        jPanel2.add(fullNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 460, 490, 49));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Full Name");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 430, -1, -1));

        createAcc.setBackground(new java.awt.Color(255, 255, 255));
        createAcc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        createAcc.setForeground(new java.awt.Color(0, 0, 0));
        createAcc.setText("Create Account");
        createAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAccActionPerformed(evt);
            }
        });
        jPanel2.add(createAcc, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 540, -1, 51));

        signUpPassword1.setBackground(new java.awt.Color(255, 255, 255));
        signUpPassword1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel2.add(signUpPassword1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, 490, 51));

        signUpPassword2.setBackground(new java.awt.Color(255, 255, 255));
        signUpPassword2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel2.add(signUpPassword2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, 490, 51));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Your Password");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Phone number");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 330, -1, -1));

        signUpEmail1.setBackground(new java.awt.Color(255, 255, 255));
        signUpEmail1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        signUpEmail1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signUpEmail1ActionPerformed(evt);
            }
        });
        jPanel2.add(signUpEmail1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 490, 49));

        phone.setBackground(new java.awt.Color(255, 255, 255));
        phone.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        phone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneActionPerformed(evt);
            }
        });
        jPanel2.add(phone, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, 490, 49));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/background.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 710));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fullNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fullNameFieldActionPerformed

    private void createAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAccActionPerformed
        try {
            // TODO add your handling code here:
            registerAction();
        } catch (SQLException ex) {
            System.getLogger(SignUp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_createAccActionPerformed

    private void signUpEmail1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signUpEmail1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_signUpEmail1ActionPerformed

    private void phoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phoneActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createAcc;
    private javax.swing.JTextField fullNameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField phone;
    private javax.swing.JTextField signUpEmail1;
    private javax.swing.JPasswordField signUpPassword1;
    private javax.swing.JPasswordField signUpPassword2;
    // End of variables declaration//GEN-END:variables
}
