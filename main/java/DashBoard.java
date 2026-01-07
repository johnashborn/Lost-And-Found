
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author arant
 */
public class DashBoard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashBoard.class.getName());
    
   
    private int studentId;
    private String currentUserFullName;
    private String currentUserContact;
    private final String currentUserEmail;

    
  
                

    /**
     * Creates new form DashBoard
     */
    
    
    public DashBoard(String email,int studentId,String fullname, String contact) {
        
        
        
        //mga designs
        initComponents();
       setLocationRelativeTo(null);
        showWeeklyChart();
        showSummaryChart();
        showTotalReports();
        showGaugeCharts();
        updateItemSummary();
        displayFullName(email);
        itemsIntoTable(recentTable);
        statusColumn(recentTable);
        updateNotificationPanel();
        
        //timer para ma referesh kanunay ang notif panel
        Timer timer = new Timer(10000, e -> updateNotificationPanel());
        timer.start();

        
        //i pass on ang email sa db into the report lost item
        this.studentId = studentId;
        this.currentUserEmail = email;
        this.currentUserFullName = fullname;
        this.currentUserContact = contact;
      
        
        
        // himoun ang lay out mo display pa vertical
        notifPanel.setLayout(new BoxLayout(notifPanel, BoxLayout.Y_AXIS));
        
        userProfile.setToolTipText("Under development");
        
    }
    
        public void referesh(){
            DashBoard d = new DashBoard(currentUserEmail, studentId, currentUserFullName, currentUserContact);
            d.setVisible(true);
            this.dispose();
            
        }
    
        public int getStudentID() {
            return studentId;
        }

        public String getCurrentUserEmail() {
            return currentUserEmail;
        }

        public String getCurrentUserFullName() {
            return currentUserFullName;
        }

        public String getCurrentUserContact() {
            return currentUserContact;
        }
    
    
    // paras mga headers
  public void updateItemSummary() {
    int total = 0;
    int found = 0;
    int claimed = 0;
    int toBeClaimed = 0;

    try (Connection connection = DataBaseConnection.getConnection()) {
        //  Get total items from items table
        String totalQuery = "SELECT COUNT(*) AS total FROM items";
        try (PreparedStatement totalPs = connection.prepareStatement(totalQuery);
             ResultSet totalRs = totalPs.executeQuery()) {
            if (totalRs.next()) {
                total = totalRs.getInt("total");
            }
        }

        //  Get counts from itemHistory table para dili mag change@ ang values
        String historyQuery = "SELECT event_type, COUNT(*) AS count FROM itemHistory GROUP BY event_type";
        try (PreparedStatement historyPs = connection.prepareStatement(historyQuery);
             ResultSet historyRs = historyPs.executeQuery()) {
            while (historyRs.next()) {
                String status = historyRs.getString("event_type");
                int count = historyRs.getInt("count");

                switch (status) {
                    case "Found": 
                        found = count; 
                        break;
                    case "Claimed": 
                        claimed = count; 
                        break;
                }
            }
        }

        // Update labels
        allItemsLabel.setText(String.valueOf(total));
        itemsFoundlabel.setText(String.valueOf(found));
        claimedItemsLabel.setText(String.valueOf(claimed));
       
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
   
    
    
    // paras fullname I display
public void displayFullName(String email) {
    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "SELECT full_name FROM Student WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            userName.setText(rs.getString("full_name"));
        } else {
            userName.setText("Name not found");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        userName.setText("Error fetching name");
    }
}
private void showWeeklyChart() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    List<String> days = List.of("Mon","Tue","Wed","Thurs","Fri","Sat","Sun");
    List<String> statuses = List.of("Lost","Found","Claimed");
    
    for(String day: days){
        for(String status: statuses){
            dataset.addValue(0, status, day);
        }
    }
    
    try (Connection connection = DataBaseConnection.getConnection()) {
        // Use the appropriate date based on event type
        String query = "SELECT ih.EVENT_TYPE, " +
                       "DAYNAME(CASE " +
                       "  WHEN ih.EVENT_TYPE = 'Lost' THEN i.date_lost " +
                       "  WHEN ih.EVENT_TYPE = 'Found' THEN i.date_found " +
                       "  WHEN ih.EVENT_TYPE = 'Claimed' THEN i.claimed_at " +
                       "  ELSE i.date_lost " +
                       "END) AS day, " +
                       "COUNT(*) AS count " +
                       "FROM itemhistory ih " +
                       "JOIN items i ON ih.ITEM_ID = i.ITEM_ID " +
                       "WHERE (CASE " +
                       "  WHEN ih.EVENT_TYPE = 'Lost' THEN i.date_lost " +
                       "  WHEN ih.EVENT_TYPE = 'Found' THEN i.date_found " +
                       "  WHEN ih.EVENT_TYPE = 'Claimed' THEN i.claimed_at " +
                       "  ELSE i.date_lost " +
                       "END) BETWEEN ? AND ? " +
                       "GROUP BY ih.EVENT_TYPE, DAYNAME(CASE " +
                       "  WHEN ih.EVENT_TYPE = 'Lost' THEN i.date_lost " +
                       "  WHEN ih.EVENT_TYPE = 'Found' THEN i.date_found " +
                       "  WHEN ih.EVENT_TYPE = 'Claimed' THEN i.claimed_at " +
                       "  ELSE i.date_lost " +
                       "END)";
        
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setDate(1, java.sql.Date.valueOf(startOfWeek));
        ps.setDate(2, java.sql.Date.valueOf(endOfWeek));
      
        ResultSet rs = ps.executeQuery();
        
        Map<String, String> dayMap = Map.of(
            "Monday", "Mon",
            "Tuesday", "Tue",
            "Wednesday", "Wed",
            "Thursday", "Thurs",
            "Friday", "Fri",
            "Saturday", "Sat",
            "Sunday", "Sun"
        );
        
        while (rs.next()) {
            String fullDay = rs.getString("day");
            if (fullDay != null) {  // Add null check
                fullDay = fullDay.trim();
                String shortDay = dayMap.getOrDefault(fullDay, fullDay);
                String status = rs.getString("Event_type");
                int count = rs.getInt("count");
                
                status = status.trim();
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                dataset.addValue(count, status, shortDay);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading chart data: " + e.getMessage(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    JFreeChart chart = ChartFactory.createStackedBarChart(
        "Weekly Reports",
        "Day",
        null,
        dataset,
        PlotOrientation.VERTICAL,
        false,   
        true,
        false
    );
    
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setSize(weeklyPanel.getWidth(), weeklyPanel.getHeight());
    chartPanel.setPreferredSize(new Dimension(weeklyPanel.getWidth(), weeklyPanel.getHeight()));
    weeklyPanel.removeAll();
    weeklyPanel.setLayout(new BorderLayout());
    weeklyPanel.add(chartPanel, BorderLayout.CENTER);
    weeklyPanel.validate();
    weeklyPanel.repaint();
}
       
    
    
//private void showWeeklyChart() {
//    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//    
//    // gama ug list para sa days, I prefill nato tanang days and statuses with zero 
//    List<String> days = List.of("Mon","Tue","Wed","Thurs","Fri","Sat","Sun");
//    List<String> statuses = List.of("Lost","Found","Claimed");
//    
//    for(String day: days){
//        for(String status: statuses){
//            dataset.addValue(0, status, day);
//        }
//    }
//    
//    // Query ang actual nga data
//    try (Connection connection = DataBaseConnection.getConnection()) {
//        // kani nga query kay mo pull sa mga lost,found,claimed each day sa item history table
//       String query = "SELECT EVENT_TYPE, DAYNAME(EVENT_DATE) AS day, COUNT(*) AS count " +
//               "FROM itemhistory " +
//               "WHERE EVENT_DATE BETWEEN ? AND ? " +
//               "GROUP BY EVENT_TYPE, DAYNAME(EVENT_DATE)";
//        
//        // get the curent date
//        LocalDate today = LocalDate.now();
//        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
//        LocalDate endOfWeek = startOfWeek.plusDays(6);
//
//
//                        
//        //“Put all rows that share the same day and the same status into the same bucket, then count them.” Mao na ang bucketing.
//        
//        /* ngani ang "bucketing"
//        So the SQL engine makes little piles:
//        - Bucket 1: (Monday, Lost) → count how many rows = 2
//        - Bucket 2: (Monday, Found) → count = 1
//        - Bucket 3: (Tuesday, Claimed) → count = 1
//        - Bucket 4: (Tuesday, Lost) → count = 1
//        Each bucket becomes one row in sa result set
//
//        */
//        
//        
//        PreparedStatement ps = connection.prepareStatement(query);
//        ps.setDate(1, java.sql.Date.valueOf(startOfWeek));
//        ps.setDate(2, java.sql.Date.valueOf(endOfWeek));
//
//      
//        ResultSet rs = ps.executeQuery();
//
//        // Map full day names to short labels
//        Map<String, String> dayMap = Map.of(
//            "Monday", "Mon",
//            "Tuesday", "Tue",
//            "Wednesday", "Wed",
//            "Thursday", "Thurs",
//            "Friday", "Fri",
//            "Saturday", "Sat",
//            "Sunday", "Sun"
//        );
//
//        while (rs.next()) {
//            String fullDay = rs.getString("day").trim();         // e.g., "Wednesday"
//            String shortDay = dayMap.getOrDefault(fullDay, fullDay); // e.g., "Wed"
//            String status = rs.getString("Event_type");       // syarog di kasabot para asa na
//            int count = rs.getInt("count");
//            
//            status = status.trim(); // remove leading/trailing spaces
//            status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase(); // "lost" → "Lost"
//            dataset.addValue(count, status, shortDay); // I butang dayun sa data set tanan
//            
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    JFreeChart chart = ChartFactory.createStackedBarChart(
//        "Weekly Reports",
//        "Day",
//        null,
//        dataset,
//        PlotOrientation.VERTICAL,
//        false,   // show legend since statuses are dynamic man
//        true,
//        false
//    );
//
//    ChartPanel chartPanel = new ChartPanel(chart);
//    chartPanel.setSize(weeklyPanel.getWidth(), weeklyPanel.getHeight());
//    chartPanel.setPreferredSize(new Dimension(weeklyPanel.getWidth(), weeklyPanel.getHeight()));
//
//    weeklyPanel.removeAll();
//    weeklyPanel.setLayout(new BorderLayout());
//    weeklyPanel.add(chartPanel, BorderLayout.CENTER);
//    weeklyPanel.validate();
//    weeklyPanel.repaint();
//}
  
  
public void showTotalReports() {
   DefaultPieDataset dataset = new DefaultPieDataset();
   
   try(Connection connection = DataBaseConnection.getConnection()){
      String query = "SELECT EVENT_TYPE, COUNT(*) AS count FROM itemhistory GROUP BY EVENT_TYPE";
       PreparedStatement preparedStatement = connection.prepareStatement(query);
       ResultSet resultSet = preparedStatement.executeQuery();
       
       while(resultSet.next()){
           String eventType = resultSet.getString("Event_type");
           int count = resultSet.getInt("count");
           
           // build label text like "total items lost"
           
           String label = "Total Items " + eventType;
           dataset.setValue(label, count);
       }
   }catch(SQLException ex){
       ex.printStackTrace();
   }
    
    
    // gamag chart
    JFreeChart chart = ChartFactory.createPieChart(
    "Total Reports",
    dataset,
    false,  // hide legend
    true,   // paras tooltips para nice
    false   // no URLs kay lain naman tan awn
);
    
            PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Total Items Lost", Color.RED);
        plot.setSectionPaint("Total Items Found", Color.BLUE);
        plot.setSectionPaint("Total Items Claimed", Color.GREEN);

    
    // chart panel 
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setSize(totalReports.getWidth(),totalReports.getHeight());
    chartPanel.setPreferredSize(new Dimension(totalReports.getWidth(),totalReports.getHeight()));
    
    // clear na dayun ang wekkly panel tas add na chart panel pang display
    totalReports.removeAll();
    totalReports.setLayout(new BorderLayout());
    totalReports.add(chartPanel,BorderLayout.CENTER);
    totalReports.validate();
    totalReports.repaint();
    
}



private void showSummaryChart() {
    // Prepare datasets for each ring
     DefaultPieDataset lostDataset = new DefaultPieDataset();
    DefaultPieDataset foundDataset = new DefaultPieDataset();
    DefaultPieDataset claimedDataset = new DefaultPieDataset();

    
    try(Connection connection = DataBaseConnection.getConnection()){
        String query = "SELECT EVENT_TYPE, COUNT(*) AS count FROM itemhistory GROUP BY EVENT_TYPE"; // i total tanang status
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultset = preparedStatement.executeQuery();
        
        while(resultset.next()){
            String status = resultset.getString("Event_Type");
            int count = resultset.getInt("count");
            
            switch (status) {
            case "Lost":
                lostDataset.setValue("Lost", count);
                int lostMax = Math.max(count, 50);
                lostDataset.setValue("", lostMax - count);
                break;

            case "Found":
                foundDataset.setValue("Found", count);
                int foundMax = Math.max(count, 50);
                foundDataset.setValue("", foundMax - count);
                break;

            case "Claimed":
                claimedDataset.setValue("Claimed", count);
                int claimedMax = Math.max(count, 50);
                claimedDataset.setValue("", claimedMax - count);
                break;

                default:
                    System.out.println("Unknown event type: " + status);
            }
        }
    }catch(SQLException ex){
        ex.printStackTrace();
    }
   
    //  create ring charts paras gauge
    JFreeChart lostChart = ChartFactory.createRingChart("Lost", lostDataset, false, false, false);
    JFreeChart foundChart = ChartFactory.createRingChart("Found", foundDataset, false, false, false);
    JFreeChart claimedChart = ChartFactory.createRingChart("Claimed", claimedDataset, false, false, false);
    
    
    // I manipulate ang ka bagaon sa ring charts
    RingPlot lostPlot = (RingPlot) lostChart.getPlot(); // Ring plot is a class nga maka control sa appearance sa ring chart
    lostPlot.setSectionDepth(0.35); // sets sa ka baga sa ring
    lostPlot.setSectionPaint("Lost", Color.RED); // pang set ug color
    lostPlot.setSectionPaint("", new Color(230, 230, 230)); // filler slice (light gray)
   lostPlot.setLabelGenerator(null); 


    RingPlot foundPlot = (RingPlot) foundChart.getPlot();
    foundPlot.setSectionDepth(0.35);
    foundPlot.setSectionPaint("Found", Color.BLUE);
    foundPlot.setSectionPaint("", new Color(230, 230, 230));
    foundPlot.setLabelGenerator(null); 



    RingPlot claimedPlot = (RingPlot) claimedChart.getPlot();
    claimedPlot.setSectionDepth(0.35);
    claimedPlot.setSectionPaint("Claimed", new Color(0, 153, 0)); // green
    claimedPlot.setSectionPaint("", new Color(230, 230, 230));
    claimedPlot.setLabelGenerator(null); 



    // i tapad ang charts side by side
    summaryPanel.removeAll();
    summaryPanel.setLayout(new GridLayout(1, 3)); // 1 row, 3 columns
    

    summaryPanel.add(new ChartPanel(lostChart));
    summaryPanel.add(new ChartPanel(foundChart));
    summaryPanel.add(new ChartPanel(claimedChart));

    summaryPanel.validate();
    summaryPanel.repaint();
}


public void showGaugeCharts() {
    int lostCount = 0;
    int foundCount = 0;
    int claimedCount = 0;

    try (Connection connection = DataBaseConnection.getConnection()) {
        String query = "SELECT EVENT_TYPE, COUNT(*) AS count FROM itemhistory GROUP BY EVENT_TYPE";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet result = preparedStatement.executeQuery();

        while (result.next()) {
            String eventType = result.getString("EVENT_TYPE");
            int count = result.getInt("count");

            switch (eventType) {
                case "Lost":
                    lostCount = count;
                    break;
                case "Found":
                    foundCount = count;
                    break;
                case "Claimed":
                    claimedCount = count;
                    break;
                default:
                    System.out.println("Unknown event type: " + eventType);
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    gaugePanelz.removeAll();
    gaugePanelz.setLayout(new FlowLayout(FlowLayout.CENTER, 0, -20));

    // Match colors sa pie chart: Red, Blue, Yellow
    SimpleGauge lostGauge = new SimpleGauge(0, "Total Items Lost", new Color(255, 102, 102));
    gaugePanelz.add(lostGauge);
    animateGauge(lostGauge, 100);

    SimpleGauge foundGauge = new SimpleGauge(0, "Total Items Found", new Color(51, 102, 255));
    gaugePanelz.add(foundGauge);
    int foundPercent = (lostCount > 0) ? (int) ((foundCount * 100.0) / lostCount) : 0;
    animateGauge(foundGauge, foundPercent);

    SimpleGauge claimedGauge = new SimpleGauge(0, "Total Items Claimed", new Color(0, 255, 0));
    gaugePanelz.add(claimedGauge);
    int claimedPercent = (foundCount > 0) ? (int) ((claimedCount * 100.0) / foundCount) : 0;
    animateGauge(claimedGauge, claimedPercent);


    gaugePanelz.revalidate();
    gaugePanelz.repaint();
}

// pang animate sa gauges
public void animateGauge(SimpleGauge gauge, int targetValue) {
    Timer timer = new Timer(20, null); // 20ms delay between frames
    final int[] currentValue = {0};

    timer.addActionListener(e -> {
        if (currentValue[0] < targetValue) {
            currentValue[0]++;
            gauge.setValue(currentValue[0]); // update gauge fill
        } else {
            ((Timer) e.getSource()).stop(); // stop when done
        }
    });

    timer.start();
}

// function para fetch ug real data tas i butang sa ato jtable
public void itemsIntoTable(JTable table){
    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
    tableModel.setRowCount(0);
    
    try(Connection connection = DataBaseConnection.getConnection()){
        //I join ang student table and items table since ang items table kay nag 
        // refer man sa studentId gikan sa student table, so inorder to display
        //the student name, along with the items, we have to join the tables
        String query = """
            SELECT i.item_id, i.item_name, i.category, s.full_name AS owner,s.email,s.contact_number, i.date_lost, i.status, i.photo
            FROM items i
            JOIN student s ON i.reported_by = s.id
            ORDER BY i.date_lost DESC
        """; //Join each item with the student whose student_id matches the student_id stored in the item
        // kang sir piquit nga topic ni boi 
        // DESC means descending order

        
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()){
            int itemId = resultSet.getInt("item_id");
            String name = resultSet.getString("item_name");
            String email = resultSet.getString("email");
            String contact_number = resultSet.getString("contact_number");
            String category = resultSet.getString("category");
            String owner = resultSet.getString("owner");
            String dateLost = resultSet.getString("date_lost");
            String status = resultSet.getString("status");
            byte[] imageByte = resultSet.getBytes("photo");
            
          
            tableModel.addRow(new Object[]{itemId,name, category, owner,email,contact_number, dateLost, status, imageByte});
        }
        
           // hide ItemID column naa nas index 0
        TableColumn idColumn = table.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);
        
        // these columns are hidden since I am just using them to pass on the values from the database paras laing forms
        //I hide ang email column
        TableColumn emailColumn = table.getColumnModel().getColumn(4);
        emailColumn.setMinWidth(0);
        emailColumn.setMaxWidth(0);
        emailColumn.setPreferredWidth(0);
        
        // I hide sad ang Contact number column
        TableColumn numberColumn = table.getColumnModel().getColumn(5);
        numberColumn.setMinWidth(0);
        numberColumn.setMaxWidth(0);
        numberColumn.setPreferredWidth(0);



        
        // I hide ang 5th column nga nag contain ug image, ana rana ma view sa jDialog
        TableColumn imageColumn = table.getColumnModel().getColumn(8);
        imageColumn.setMinWidth(0);
        imageColumn.setMaxWidth(0);
        imageColumn.setPreferredWidth(0);
        
        styleTable(table);


    }catch(SQLException ex){
        ex.printStackTrace();
    }
}

public void refreshTable() {
    itemsIntoTable(recentTable); // use your actual JTable variable
    showWeeklyChart();
}


//function para lahi lahi ang color sa each status sa table, and if pinduton kay mo open siya sa jDialog
public void statusColumn(JTable table){
    // custom renderer paras column status
    table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer(){
        
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = value!=null ? value.toString() : "";
        
         switch (status) {
                case "Lost":
                    component.setForeground(Color.red);
                    break;
                case "Found":
                    component.setForeground(Color.blue);
                    break;
                case "Claimed":
                    component.setForeground(Color.green);
                    break;
                default:
                    component.setForeground(Color.BLACK);
            }
         
         component.setFont(component.getFont().deriveFont(Font.BOLD));
         return component; 
    }  
});
    
    table.repaint();
    
    // mouse listener para if mo click ang user
    table.addMouseListener(new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent event){
            int row = table.rowAtPoint(event.getPoint());
            int col = table.columnAtPoint(event.getPoint());
            
            //Mo trigger ra if ang status column kay ma clicked
            if(col == 7 && row >= 0){
                int itemId = (int) table.getValueAt(row, 0);
                String itemName = table.getValueAt(row, 1).toString();
                String category = table.getValueAt(row, 2).toString();
                String owner = table.getValueAt(row, 3).toString();
                String email = table.getValueAt(row, 4).toString();
                String contactNumber = table.getValueAt(row, 5).toString();
                String dateLost = table.getValueAt(row, 6).toString();
                String status = table.getValueAt(row, 7).toString();
                byte[] imageBytes = (byte[]) table.getValueAt(row, 8);

                openDetails(itemId, itemName, category, dateLost, status, imageBytes);
                            }
        }
    });
    
    // if mo hover ang cursor sa status, mahimo hand
    table.addMouseMotionListener(new MouseMotionAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        int column = table.columnAtPoint(e.getPoint());
        int statusColumnIndex = 7; // mao ni ang column sa status

        if (column == statusColumnIndex) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            table.setCursor(Cursor.getDefaultCursor());
        }
    }
});
    
}


// pang style sa table (kapuya naman ni)
public static void styleTable(JTable table) {
    // Set row height
    table.setRowHeight(30); // or 40 for chunkier rows

    // Set column widths
    TableColumnModel columnModel = table.getColumnModel();
    if (columnModel.getColumnCount() >= 9) {
        columnModel.getColumn(1).setPreferredWidth(150); // Item Name
        columnModel.getColumn(2).setPreferredWidth(100); // Category
        columnModel.getColumn(3).setPreferredWidth(150); // Owner
        columnModel.getColumn(6).setPreferredWidth(100); // Date Lost
        columnModel.getColumn(7).setPreferredWidth(80);  // Status
    }

    // Add padding inside cells
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // top, left, bottom, right
    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }
}


public void updateNotificationPanel() {
    ActivityLogDAO dao = new ActivityLogDAO();
    List<ActivityLog> logs = dao.getRecentActivity();

    notifPanel.removeAll(); // clear old entries

    for (ActivityLog log : logs) {
    JPanel entry = new JPanel();
    entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
    entry.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    entry.setAlignmentX(Component.LEFT_ALIGNMENT);
    entry.setOpaque(true);
    entry.setBackground(new Color(230, 230, 230)); // soft gray

    //  Stretch full width
    entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, entry.getPreferredSize().height));
    // set max heigt sa kada entry nga mahimo
    entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

    // Icon
    JLabel icon = new JLabel();
    icon.setIcon(getIconForType(log.getEventType()));
    icon.setAlignmentY(Component.TOP_ALIGNMENT); // force top alignment
    icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // spacing

    // Text block
    JPanel textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textPanel.setOpaque(false);
    textPanel.setAlignmentY(Component.TOP_ALIGNMENT);

    JLabel title = new JLabel(log.getItemName());
    title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
    
    // para if walay location, iingon ra location unkown
    String details = log.getDetails();
    if (details == null || details.trim().isEmpty() || details.equalsIgnoreCase(log.getItemName())) {
        details = "Location unknown";
    }
    JLabel location = new JLabel("📍 " + details);
    location.setFont(location.getFont().deriveFont(12f));
    location.setForeground(new Color(80, 80, 80)); // optional: darker gray

    JLabel time = new JLabel("⏰ " + timeAgo(log.getEventDate()));
    time.setFont(time.getFont().deriveFont(10f));
    time.setForeground(Color.GRAY);

    textPanel.add(title);
    textPanel.add(location);
    textPanel.add(time);

    entry.add(icon);
    entry.add(textPanel);
    notifPanel.add(entry);
}

    notifPanel.revalidate();
    notifPanel.repaint();
    
    //ensures nga ang scroll mahitabo AFTER ma reinvalidate ang panel and ma paint
    SwingUtilities.invokeLater(() -> notifScroll.getVerticalScrollBar().setValue(0));

}




// helper class sa update notification panel
private Icon getIconForType(String eventType) {
    String path;
    switch (eventType) {
        case "Lost":    path = "/Images/lost.png"; break;
        case "Found":   path = "/Images/found.png"; break;
        case "Claimed": path = "/Images/claimed.png"; break;
        default:        path = "/Images/default.png"; break;
    }

    URL url = getClass().getResource(path);
    if (url != null) {
        return new ImageIcon(url);
    } else {
        System.err.println("Resource not found: " + path);
        return new ImageIcon(); // empty icon fallback
    }
}

// helper class sa update notification panel
//mang convert sa date into a readable string like "5 minutes ago"
private String timeAgo(Timestamp eventDate) {
    long diffMillis = System.currentTimeMillis() - eventDate.getTime();
    long diffSeconds = diffMillis / 1000;
    long diffMinutes = diffSeconds / 60;
    long diffHours = diffMinutes / 60;
    long diffDays = diffHours / 24;

    if (diffSeconds < 60) {
        return diffSeconds + " seconds ago";
    } else if (diffMinutes < 60) {
        return diffMinutes + " minutes ago";
    } else if (diffHours < 24) {
        return diffHours + " hours ago";
    } else {
        return diffDays + " days ago";
    }
}





    
    // codes for opening different forms
     // this method kay focus ras item context
    public void openDetails(int itemId, String itemName, String category,
                        String dateLost, String status, byte[] imageBytes) {
    ItemDetails details = new ItemDetails(this,this, itemId, itemName, category,
                                          dateLost, status, imageBytes);
     // pass Dashboard reference paras reporter info
    details.setVisible(true);
}
    
    //for search item form
    public void openSearch(){
        SearchItem searchItem = new SearchItem(this,userName.getText(),studentId);
        searchItem.setVisible(true);
        this.setVisible(false);
    }
    
    //for reports form
    public void openReports(){
        Reports reports = new Reports(this,userName.getText(), studentId);
        reports.setVisible(true);
        this.setVisible(false);
    }
    
    //for submitting reports butoon
    public void openReportLost(){
        ReportLostItem rep = new ReportLostItem(currentUserEmail);
        rep.setVisible(true);
        this.setVisible(false);
    }
    
   
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    
    // how do I even do this
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        userName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        userProfile = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        allItems = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        allItemsLabel = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        allItemsFound = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        itemsFoundlabel = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        allClaimedItems = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        claimedItemsLabel = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        weeklyPanel = new RoundedPanel(20);
        reportItem_btn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        recentTable = new javax.swing.JTable();
        jPanel16 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        summaryPanel = new RoundedPanel(20);
        totalReports = new RoundedPanel(20);
        gaugePanelz = new RoundedPanel(20);
        notifScroll = new javax.swing.JScrollPane();
        notifPanel = new javax.swing.JPanel();
        jPanel6 = new RoundedPanel(20);
        jLabel5 = new javax.swing.JLabel();
        reportItem_btn1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton2 = new javax.swing.JToggleButton();
        search_btn = new javax.swing.JToggleButton();
        reports_btn = new javax.swing.JToggleButton();
        logOut = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(153, 51, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-box-64 (2).png"))); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Lost and Found Mini System");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Bohol Island State University");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(2, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 70));

        userName.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        userName.setForeground(new java.awt.Color(255, 255, 255));
        userName.setText("John Sekiro");
        jPanel2.add(userName, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 20, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Student");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 50, -1, -1));

        userProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-user-icon-32.png"))); // NOI18N
        userProfile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        userProfile.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                userProfileMouseMoved(evt);
            }
        });
        jPanel2.add(userProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 20, -1, 50));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1030, 90));

        jScrollPane2.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 51, 255)));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(153, 51, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        allItems.setBackground(new java.awt.Color(255, 255, 255));
        allItems.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("All Items");
        allItems.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 6, -1, -1));

        allItemsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        allItemsLabel.setForeground(new java.awt.Color(0, 0, 0));
        allItemsLabel.setText("100");
        allItems.add(allItemsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 32, 55, -1));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/found.png"))); // NOI18N
        allItems.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 30, 30));

        jPanel5.add(allItems, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 240, 70));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 250, 70));

        jPanel7.setBackground(new java.awt.Color(51, 51, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        allItemsFound.setBackground(new java.awt.Color(255, 255, 255));
        allItemsFound.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Items Found");
        allItemsFound.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 6, -1, -1));

        itemsFoundlabel.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        itemsFoundlabel.setForeground(new java.awt.Color(0, 0, 0));
        itemsFoundlabel.setText("43");
        allItemsFound.add(itemsFoundlabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 32, 80, -1));

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-search-30.png"))); // NOI18N
        allItemsFound.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 30, 30));

        jPanel7.add(allItemsFound, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 230, 70));

        jPanel4.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 240, 70));

        jPanel11.setBackground(new java.awt.Color(0, 255, 0));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        allClaimedItems.setBackground(new java.awt.Color(255, 255, 255));
        allClaimedItems.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Claimed Items");
        allClaimedItems.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 6, -1, -1));

        claimedItemsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        claimedItemsLabel.setForeground(new java.awt.Color(0, 0, 0));
        claimedItemsLabel.setText("20");
        allClaimedItems.add(claimedItemsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 32, 90, -1));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/claimed.png"))); // NOI18N
        allClaimedItems.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 30, 30));

        jPanel11.add(allClaimedItems, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 230, 70));

        jPanel4.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 240, 70));

        weeklyPanel.setBackground(new java.awt.Color(255, 255, 255));
        weeklyPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel4.add(weeklyPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 480, 220));

        reportItem_btn.setBackground(new java.awt.Color(153, 51, 255));
        reportItem_btn.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        reportItem_btn.setForeground(new java.awt.Color(255, 255, 255));
        reportItem_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-add-20.png"))); // NOI18N
        reportItem_btn.setText("Report Lost Item");
        reportItem_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        reportItem_btn.setOpaque(true);
        reportItem_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportItem_btnActionPerformed(evt);
            }
        });
        jPanel4.add(reportItem_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 350, 190, 50));

        recentTable.setAutoCreateRowSorter(true);
        recentTable.setBackground(new java.awt.Color(255, 255, 255));
        recentTable.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 204, 204)));
        recentTable.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        recentTable.setForeground(new java.awt.Color(0, 0, 0));
        recentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "Yamaha Key", "Keys", "Black", null, null, "12-12-2021", null, null},
                {null, "School Bag", "Bag", "Blue", null, null, "11-21-14", null, null},
                {null, "Tecno Camon ", "Phone", "Black", null, null, "10-5-2025", null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ItemID", "Item  Name", "Category", "Owner", "Email", "Contact Number", "Date Reported", "Status", "Photo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        recentTable.setGridColor(new java.awt.Color(255, 255, 255));
        recentTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        recentTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        recentTable.setShowGrid(true);
        jScrollPane1.setViewportView(recentTable);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 530, 380));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setText("Recent Items");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 230, 50));

        summaryPanel.setBackground(new java.awt.Color(255, 255, 255));
        summaryPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));

        javax.swing.GroupLayout summaryPanelLayout = new javax.swing.GroupLayout(summaryPanel);
        summaryPanel.setLayout(summaryPanelLayout);
        summaryPanelLayout.setHorizontalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 472, Short.MAX_VALUE)
        );
        summaryPanelLayout.setVerticalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.add(summaryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 480, 90));

        totalReports.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout totalReportsLayout = new javax.swing.GroupLayout(totalReports);
        totalReports.setLayout(totalReportsLayout);
        totalReportsLayout.setHorizontalGroup(
            totalReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        totalReportsLayout.setVerticalGroup(
            totalReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.add(totalReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 90, 300, 170));

        gaugePanelz.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout gaugePanelzLayout = new javax.swing.GroupLayout(gaugePanelz);
        gaugePanelz.setLayout(gaugePanelzLayout);
        gaugePanelzLayout.setHorizontalGroup(
            gaugePanelzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        gaugePanelzLayout.setVerticalGroup(
            gaugePanelzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.add(gaugePanelz, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 270, 300, 70));

        notifScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notifScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        notifPanel.setBackground(new java.awt.Color(255, 255, 255));
        notifPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        notifPanel.setLayout(new javax.swing.BoxLayout(notifPanel, javax.swing.BoxLayout.LINE_AXIS));
        notifScroll.setViewportView(notifPanel);

        jPanel4.add(notifScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 490, 270, 380));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Recent Activities");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 430, 270, 50));

        reportItem_btn1.setBackground(new java.awt.Color(153, 51, 255));
        reportItem_btn1.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        reportItem_btn1.setForeground(new java.awt.Color(255, 255, 255));
        reportItem_btn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-add-20.png"))); // NOI18N
        reportItem_btn1.setText("Report Lost Item");
        reportItem_btn1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        reportItem_btn1.setOpaque(true);
        reportItem_btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportItem_btn1ActionPerformed(evt);
            }
        });
        jPanel4.add(reportItem_btn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 350, 190, 50));

        jScrollPane2.setViewportView(jPanel4);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 85, 840, 530));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jToggleButton2.setBackground(new java.awt.Color(204, 204, 204));
        jToggleButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jToggleButton2.setForeground(new java.awt.Color(0, 0, 0));
        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-home-50.png"))); // NOI18N
        jToggleButton2.setText("Menu");
        jToggleButton2.setMargin(new java.awt.Insets(2, 20, 3, 14));
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jToggleButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 170, 50));

        search_btn.setBackground(new java.awt.Color(204, 204, 204));
        search_btn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        search_btn.setForeground(new java.awt.Color(0, 0, 0));
        search_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-search-20.png"))); // NOI18N
        search_btn.setText("Search Item");
        search_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_btnActionPerformed(evt);
            }
        });
        jPanel3.add(search_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 170, 51));

        reports_btn.setBackground(new java.awt.Color(204, 204, 204));
        reports_btn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        reports_btn.setForeground(new java.awt.Color(0, 0, 0));
        reports_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-reports-20.png"))); // NOI18N
        reports_btn.setText("Reports");
        reports_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reports_btnActionPerformed(evt);
            }
        });
        jPanel3.add(reports_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 170, 51));

        logOut.setBackground(new java.awt.Color(255, 255, 255));
        logOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        logOut.setForeground(new java.awt.Color(255, 51, 51));
        logOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-log-out-30.png"))); // NOI18N
        logOut.setText("Log Out");
        logOut.setIconTextGap(10);
        logOut.setOpaque(true);
        logOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logOutMouseClicked(evt);
            }
        });
        logOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutActionPerformed(evt);
            }
        });
        jPanel3.add(logOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, 150, 50));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 190, 530));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reports_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reports_btnActionPerformed
        // TODO add your handling code here:
        openReports();
    }//GEN-LAST:event_reports_btnActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void search_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_btnActionPerformed
        // TODO add your handling code here:
        openSearch();
    }//GEN-LAST:event_search_btnActionPerformed

    private void reportItem_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportItem_btnActionPerformed
        // TODO add your handling code here:
        openReportLost();

    }//GEN-LAST:event_reportItem_btnActionPerformed

    private void logOutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logOutMouseClicked
        // TODO add your handling code here:
        LogIn login = new LogIn();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logOutMouseClicked

    private void userProfileMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userProfileMouseMoved
        // TODO add your handling code here:
        
    }//GEN-LAST:event_userProfileMouseMoved

    private void reportItem_btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportItem_btn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reportItem_btn1ActionPerformed

    private void logOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_logOutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
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
       
        java.awt.EventQueue.invokeLater(() -> new DashBoard("test",1,"test","test").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel allClaimedItems;
    private javax.swing.JPanel allItems;
    private javax.swing.JPanel allItemsFound;
    private javax.swing.JLabel allItemsLabel;
    private javax.swing.JLabel claimedItemsLabel;
    private javax.swing.JPanel gaugePanelz;
    private javax.swing.JLabel itemsFoundlabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JButton logOut;
    private javax.swing.JPanel notifPanel;
    private javax.swing.JScrollPane notifScroll;
    private javax.swing.JTable recentTable;
    private javax.swing.JButton reportItem_btn;
    private javax.swing.JButton reportItem_btn1;
    private javax.swing.JToggleButton reports_btn;
    private javax.swing.JToggleButton search_btn;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JPanel totalReports;
    private javax.swing.JLabel userName;
    private javax.swing.JLabel userProfile;
    private javax.swing.JPanel weeklyPanel;
    // End of variables declaration//GEN-END:variables
}
