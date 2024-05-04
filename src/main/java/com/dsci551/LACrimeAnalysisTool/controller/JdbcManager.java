package com.dsci551.LACrimeAnalysisTool.controller;

import com.dsci551.LACrimeAnalysisTool.model.CrimeData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcManager {

    private String url;

    private String url1 = "jdbc:mysql://crimedata1.czk6okwo4kzv.us-west-1.rds.amazonaws.com:3306/CrimeData?useSSL=false&serverTimezone=UTC";

    private String url2 = "jdbc:mysql://crimedata2.czk6okwo4kzv.us-west-1.rds.amazonaws.com:3306/CrimeData?useSSL=false&serverTimezone=UTC";

    private String url3 = "jdbc:mysql://crimedata3.czk6okwo4kzv.us-west-1.rds.amazonaws.com:3306/CrimeData?useSSL=false&serverTimezone=UTC";

    private String user = "root";
    private String password;
    private String password1 = "crimedata1";

    private String password2 = "crimedata2";

    private String password3 = "crimedata3";

    public JdbcManager(int dbNumber, String user) {
        if (dbNumber == 3) {
            this.url = url3;
            this.password = password3;
        } else if (dbNumber == 2) {
            this.url = url2;
            this.password = password2;
        } else {
            this.url = url1;
            this.password = password1;
        }
    }

    public List<String> fetchCrimeData() {
        List<String> crimeDataList = new ArrayList<>();
        String query = "SELECT * FROM CrimeData LIMIT 10";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String drNo = rs.getString("DR_NO");
                crimeDataList.add(drNo); // Add other columns as needed
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Consider a better error handling strategy for your context
        }

        return crimeDataList;
    }

    public CrimeData fetchCrimeDataByDrNo(String drNo) {
        CrimeData crimeData = null;
        String query = "SELECT * FROM CrimeData WHERE DR_NO = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, drNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    crimeData = new CrimeData();
                    crimeData.setDrNo(rs.getString("DR_NO"));
                    // Set other fields of crimeData from ResultSet
                    crimeData.setDrNo(rs.getString("DR_NO"));
                    crimeData.setDateRptd(rs.getTimestamp("Date_Rptd")); // Assuming Date_Rptd and DATE_OCC are Timestamps
                    crimeData.setDateOcc(rs.getTimestamp("DATE_OCC"));
                    crimeData.setTimeOcc(rs.getInt("TIME_OCC"));
                    crimeData.setArea(rs.getInt("AREA"));
                    crimeData.setAreaName(rs.getString("AREA_NAME"));
                    crimeData.setRptDistNo(rs.getString("Rpt_Dist_No"));
                    crimeData.setCrmCd(rs.getString("Crm_Cd"));
                    crimeData.setCrmCdDesc(rs.getString("Crm_Cd_Desc"));
                    crimeData.setVictAge(rs.getInt("Vict_Age"));
                    crimeData.setVictSex(rs.getString("Vict_Sex"));
                    crimeData.setVictDescent(rs.getString("Vict_Descent"));
                    crimeData.setPremisCd(rs.getInt("Premis_Cd"));
                    crimeData.setPremisDesc(rs.getString("Premis_Desc"));
                    crimeData.setWeaponUsedCd(rs.getString("Weapon_Used_Cd"));
                    crimeData.setWeaponDesc(rs.getString("Weapon_Desc"));
                    crimeData.setStatus(rs.getString("Status"));
                    crimeData.setStatusDesc(rs.getString("Status_Desc"));
                    crimeData.setLocation(rs.getString("LOCATION"));
                    crimeData.setCrossStreet(rs.getString("Cross_Street"));
                    crimeData.setLat(rs.getFloat("LAT"));
                    crimeData.setLon(rs.getFloat("LON"));
                    crimeData.setViolenceLevel(determineViolenceLevel(rs.getString("Weapon_Desc"), rs.getString("Crm_Cd_Desc")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Consider a better error handling strategy for your context
        }

        return crimeData;
    }

    public List<Map<String, String>> fetchCrimesNearby(double longitude, double latitude, double radius) {
        List<Map<String, String>> crimes = new ArrayList<>();
        String query = "SELECT CD.DR_NO, CD.Crm_Cd_Desc, ST_AsText(CL.GeoLocation) AS Location " +
            "FROM CrimeLocations as CL JOIN CrimeData AS CD ON CD.DR_NO = CL.DR_NO " +
            "WHERE ST_Distance_Sphere(GeoLocation, ST_GeomFromText(?)) <= ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String point = String.format("POINT(%f %f)", longitude, latitude);
            pstmt.setString(1, point);
            pstmt.setDouble(2, radius);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> crime = new HashMap<>();
                    crime.put("DR_NO", rs.getString("DR_NO"));
                    crime.put("Description", rs.getString("Crm_Cd_Desc"));
                    crime.put("Location", rs.getString("Location"));
                    crimes.add(crime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Consider a better error handling strategy
        }
        return crimes;
    }

    public int determineViolenceLevel(String weaponDesc, String crimeDesc) {
        weaponDesc = weaponDesc.toUpperCase();
        crimeDesc = crimeDesc.toUpperCase();

        if (weaponDesc.contains("GUN") || weaponDesc.contains("RIFLE") || weaponDesc.contains("PISTOL") ||
            weaponDesc.contains("SHOTGUN") || weaponDesc.contains("REVOLVER") || weaponDesc.contains("FIREARM") ||
            crimeDesc.contains("HOMICIDE") || crimeDesc.contains("RAPE") || crimeDesc.contains("ROBBERY") ||
            crimeDesc.contains("ASSAULT WITH DEADLY")) {
            return 3;
        } else if (crimeDesc.contains("ASSAULT") || crimeDesc.contains("KIDNAPPING") ||
            weaponDesc.contains("KNIFE") || weaponDesc.contains("BLADE")) {
            return 3;
        } else if (weaponDesc.contains("KNIFE") || weaponDesc.contains("BLADE") || weaponDesc.contains("AXE") ||
            weaponDesc.contains("MACHETE") || weaponDesc.contains("HAMMER") || weaponDesc.contains("SCREWDRIVER") ||
            weaponDesc.contains("CLUB") || weaponDesc.contains("BAT") || weaponDesc.contains("CHAIN") ||
            weaponDesc.contains("BLACKJACK") || weaponDesc.contains("BOTTLE") || crimeDesc.contains("BURGLARY") ||
            crimeDesc.contains("THEFT") || crimeDesc.contains("VANDALISM") || crimeDesc.contains("FRAUD")) {
            return 2;
        } else {
            return 1;
        }
    }

    public List<CrimeData> fetchCrimes(String startDate, String endDate, String areaName, String crimeCode, Double latitude, Double longitude, Double radius) {
        List<CrimeData> crimes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM CrimeData WHERE ");

        List<Object> params = new ArrayList<>();

        // Date filter
        if (startDate != null && endDate != null) {
            sql.append("DATE_OCC BETWEEN ? AND ? AND ");
            params.add(startDate);
            params.add(endDate);
        }

        // Area name filter
        if (areaName != null && !areaName.isEmpty()) {
            sql.append("AREA_NAME = ? AND ");
            params.add(areaName);
        }

        // Crime code filter
        if (crimeCode != null && !crimeCode.isEmpty()) {
            sql.append("Crm_Cd = ? AND ");
            params.add(crimeCode);
        }

        // Geographic filter using latitude and longitude
        if (latitude != null && longitude != null && radius != null) {
            sql.append("ST_Distance_Sphere(point(LON, LAT), point(?, ?)) <= ? ");
            params.add(longitude);
            params.add(latitude);
            params.add(radius * 1609.34); // Convert miles to meters
        }

        // Remove the last "AND " if necessary
        if (sql.toString().endsWith("AND ")) {
            sql = new StringBuilder(sql.substring(0, sql.length() - 4));
        }

        // Execute the query
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Assuming you have a Crime class to store these
                CrimeData crime = new CrimeData();
                crime.setDrNo(rs.getString("DR_NO"));
                crime.setDateOcc(rs.getDate("DATE_OCC"));
                crime.setAreaName(rs.getString("AREA_NAME"));
                crime.setCrmCd(rs.getString("Crm_Cd"));
                crime.setCrmCdDesc(rs.getString("Crm_Cd_Desc"));
                crime.setLat(rs.getFloat("LAT"));
                crime.setLon(rs.getFloat("LON"));
                crime.setViolenceLevel(determineViolenceLevel(rs.getString("Weapon_Desc"), rs.getString("Crm_Cd_Desc")));
                crimes.add(crime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crimes;
    }

    public List<CrimeData> fetchCrimesByArea(String startDate, String endDate, String areaName, String crimeCode) {
        List<CrimeData> crimes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM CrimeData WHERE ");

        List<Object> params = new ArrayList<>();

        // Date filter
        if (startDate != null && endDate != null) {
            sql.append("DATE_OCC BETWEEN ? AND ? AND ");
            params.add(startDate);
            params.add(endDate);
        }

        // Crime code filter
        if (crimeCode != null && !crimeCode.isEmpty()) {
            sql.append("Crm_Cd = ? AND ");
            params.add(crimeCode);
        }

        // Area name filter
        if (areaName != null && !areaName.isEmpty()) {
            sql.append("AREA_NAME = ? ");
            params.add(areaName);
        }

        // Remove the last "AND " if necessary
        if (sql.toString().endsWith("AND ")) {
            sql = new StringBuilder(sql.substring(0, sql.length() - 4));
        }

        // Execute the query
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Assuming you have a Crime class to store these
                CrimeData crime = new CrimeData();
                crime.setDrNo(rs.getString("DR_NO"));
                crime.setDateOcc(rs.getDate("DATE_OCC"));
                crime.setAreaName(rs.getString("AREA_NAME"));
                crime.setCrmCd(rs.getString("Crm_Cd"));
                crime.setCrmCdDesc(rs.getString("Crm_Cd_Desc"));
                crime.setLat(rs.getFloat("LAT"));
                crime.setLon(rs.getFloat("LON"));
                crime.setViolenceLevel(determineViolenceLevel(rs.getString("Weapon_Desc"), rs.getString("Crm_Cd_Desc")));
                crimes.add(crime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crimes;
    }

    // Method to get a list of all crime codes
    public Map<String, String> getAllCrimeCodes() {
        Map<String, String> codes = new HashMap<>();
        String sql = "SELECT DISTINCT Crm_Cd, Crm_Cd_Desc FROM CrimeData WHERE Crm_Cd IS NOT NULL AND Crm_Cd_Desc IS NOT NULL ORDER BY Crm_cd";

        // Using try-with-resources to ensure that all resources are closed properly
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("Crm_Cd");
                String description = rs.getString("Crm_Cd_Desc");
                // CrimeData newCrimeCode = new CrimeData();
                // newCrimeCode.setCrmCd(code);
                // newCrimeCode.setCrmCdDesc(description);
                codes.put(code, description);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions or throw them as needed
        }
        return codes;
    }
}
