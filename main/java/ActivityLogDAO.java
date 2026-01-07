
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */
public class ActivityLogDAO {

    // Fetch the latest 10 activity logs para magamit sa notif panel
    public List<ActivityLog> getRecentActivity() {
    List<ActivityLog> logs = new ArrayList<>(); // gamag list of logs

    try (Connection conn = DataBaseConnection.getConnection()) {
        String query = "SELECT ih.EVENT_TYPE, ih.EVENT_DATE, ih.DETAILS, " +
                       "i.ITEM_NAME, s.FULL_NAME " +
                       "FROM itemhistory ih " +
                       "JOIN items i ON ih.ITEM_ID = i.ITEM_ID " +
                       "LEFT JOIN student s ON ih.STUDENT_ID = s.ID " +
                       "ORDER BY ih.EVENT_DATE DESC LIMIT 10";

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String eventType = rs.getString("EVENT_TYPE");
            String rawDetails = rs.getString("DETAILS");
            Timestamp eventDate = rs.getTimestamp("EVENT_DATE");
            String itemName = rs.getString("ITEM_NAME");
            String fullName = rs.getString("FULL_NAME");

            // Override details if claimed and name is available sa database
            String finalDetails = rawDetails;
            if ("Claimed".equalsIgnoreCase(eventType) && fullName != null && !fullName.isBlank()) {
                finalDetails = fullName;
            }
            
            if ("Found".equalsIgnoreCase(eventType) && fullName != null && !fullName.isBlank()) {
                finalDetails = fullName;
            }

            ActivityLog log = new ActivityLog(eventType, finalDetails, eventDate, itemName, fullName);
            logs.add(log);
        }

        rs.close();
        ps.close();

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return logs;
}
}
