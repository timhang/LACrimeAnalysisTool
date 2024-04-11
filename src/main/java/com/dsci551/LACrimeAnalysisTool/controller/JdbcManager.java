package com.dsci551.LACrimeAnalysisTool.controller;

import com.dsci551.LACrimeAnalysisTool.model.CrimeData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcManager {

    private String url;

    private String url1 = "jdbc:mysql://localhost:3306/CrimeData?useSSL=false&serverTimezone=UTC";

    private String url2 = "jdbc:mysql://localhost:3307/CrimeData?useSSL=false&serverTimezone=UTC";

    private String url3 = "jdbc:mysql://localhost:3308/CrimeData?useSSL=false&serverTimezone=UTC";

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
}
