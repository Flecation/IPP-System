package IPPSystem.Utils;

import IPPSystem.DAO.databaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    
    public static void testConnectionAndFetchData() {
        System.out.println("🔍 Testing database connection...");
        
        try (Connection connection = databaseConnection.getConnection()) {
            System.out.println("✅ Successfully connected to the database!");
            
            // Test query to fetch some data
            String query = "SHOW TABLES";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                System.out.println("\n📋 Available tables in the database:");
                while (rs.next()) {
                    System.out.println("- " + rs.getString(1));
                }
                
                // Test query to count projects (assuming you have a projects table)
                try {
                    String countQuery = "SELECT COUNT(*) as project_count FROM projects";
                    try (ResultSet countRs = stmt.executeQuery(countQuery)) {
                        if (countRs.next()) {
                            System.out.println("\n📊 Total projects in database: " + countRs.getInt("project_count"));
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("\nℹ️ Could not fetch project count. The projects table might not exist yet.");
                }
                
            } catch (SQLException e) {
                System.err.println("❌ Error executing query: " + e.getMessage());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to the database!");
            System.err.println("Error details: " + e.getMessage());
            System.err.println("\n🔧 Please check the following:");
            System.err.println("1. Is your MySQL server running?");
            System.err.println("2. Are the database credentials in your .env file correct?");
            System.err.println("3. Is the database URL in the format: jdbc:mysql://hostname:port/database_name");
        }
    }
    
    public static void main(String[] args) {
        testConnectionAndFetchData();
    }
}
