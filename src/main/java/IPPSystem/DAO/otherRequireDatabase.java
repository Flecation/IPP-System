package IPPSystem.DAO;

import java.sql.*;
import java.util.HashMap;

public class otherRequireDatabase {
    private static Connection con;
    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getAllProjectType(){
        HashMap<Integer,String> type = new HashMap<>();
        String sql = "SELECT * FROM projectTypes";
        try(PreparedStatement ps = con.prepareCall(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                type.put(rs.getInt("projectTypeId"),rs.getString("typeName"));
            }
            return type;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getAllBuilding(){
        HashMap<Integer,String> building = new HashMap<>();
        String sql = "SELECT * FROM buildings";
        try (PreparedStatement ps = con.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                building.put(rs.getInt("projectBuildingId"),rs.getString("projectBuildingName"));
            }
            return building;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getAllLevel(){
        HashMap<Integer,String> level = new HashMap<>();
        String sql = "SELECT * FROM projectLevels";
        try (PreparedStatement ps = con.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                level.put(rs.getInt("projectLevelId"),rs.getString("projectLevelName"));
            }
            return level;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getBuildingNameByProjectId(int projectId){
        HashMap<Integer,String> building = new HashMap<>();
        try (CallableStatement cs = con.prepareCall("{CALL getBuildingNameByProjectId(?)}")){
            cs.setInt(1,projectId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
                building.put(rs.getInt("projectBuildingId"),rs.getString("projectBuildingName"));
            }
            return building;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getLevelByProjectId(int projectId){
        HashMap<Integer,String> level = new HashMap<>();
        try (CallableStatement cs = con.prepareCall("{CALL getLevelByProjectId(?)}")){
            cs.setInt(1,projectId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
                level.put(rs.getInt("projectLevelId"),rs.getString("projectLevelName"));
            }
            return level;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getAssignStatus(){
        HashMap<Integer,String> status = new HashMap<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM assignStatus")){

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                status.put(rs.getInt("assignStatusId"),rs.getString("assignStatusName"));
            }
            return status;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Integer,String> getProjectStatus(){
        HashMap<Integer,String> status = new HashMap<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM projectStaus")){

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                status.put(rs.getInt("projectStatusId"),rs.getString("projectStatusName"));
            }
            return status;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
