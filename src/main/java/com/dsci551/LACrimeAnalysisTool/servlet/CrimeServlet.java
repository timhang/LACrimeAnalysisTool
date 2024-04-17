package com.dsci551.LACrimeAnalysisTool.servlet;

import com.dsci551.LACrimeAnalysisTool.controller.JdbcManager;
import com.dsci551.LACrimeAnalysisTool.model.CrimeData;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CrimeServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html");
//        resp.getWriter().write("<html><body><h2>Crime Data Management</h2></body></html>");
        resp.setContentType("text/html");
        var out = resp.getWriter();

        // Fetch the DR_NO from the request parameter
        String drNo = req.getParameter("drNo");
        if (drNo == null || drNo.isEmpty()) {
            out.println("<html><body><h2>Error: No DR_NO provided.</h2></body></html>");
            return;
        }

        // Initialize JdbcManager with your database connection details
        int dbIndex = Integer.parseInt(drNo) % 3;
        JdbcManager jdbcManager = new JdbcManager(
            dbIndex + 1,
            "root");
        CrimeData crimeData = jdbcManager.fetchCrimeDataByDrNo(drNo);

        // Output the crime data
        if (crimeData != null) {
            out.println("<html><body><h2>Crime Data Details</h2>");
            out.println("<p>DR_NO: " + crimeData.getDrNo() + "</p>");

            // Output other fields as needed
            out.println("<p>Date Reported: " + crimeData.getDateRptd() + "</p>");
            out.println("<p>Date of Occurrence: " + crimeData.getDateOcc() + "</p>");
            out.println("<p>Time of Occurrence: " + crimeData.getTimeOcc() + "</p>");
            out.println("<p>Area: " + crimeData.getArea() + "</p>");
            out.println("<p>Area Name: " + crimeData.getAreaName() + "</p>");
            out.println("<p>Report District No: " + crimeData.getRptDistNo() + "</p>");
            out.println("<p>Crime Code: " + crimeData.getCrmCd() + "</p>");
            out.println("<p>Crime Description: " + crimeData.getCrmCdDesc() + "</p>");
            out.println("<p>Victim Age: " + crimeData.getVictAge() + "</p>");
            out.println("<p>Victim Sex: " + crimeData.getVictSex() + "</p>");
            out.println("<p>Victim Descent: " + crimeData.getVictDescent() + "</p>");
            out.println("<p>Premise Code: " + crimeData.getPremisCd() + "</p>");
            out.println("<p>Premise Description: " + crimeData.getPremisDesc() + "</p>");
            out.println("<p>Weapon Used Code: " + crimeData.getWeaponUsedCd() + "</p>");
            out.println("<p>Weapon Description: " + crimeData.getWeaponDesc() + "</p>");
            out.println("<p>Status: " + crimeData.getStatus() + "</p>");
            out.println("<p>Status Description: " + crimeData.getStatusDesc() + "</p>");
            out.println("<p>Location: " + crimeData.getLocation() + "</p>");
            out.println("<p>Cross Street: " + crimeData.getCrossStreet() + "</p>");
            out.println("<p>Latitude: " + crimeData.getLat() + "</p>");
            out.println("<p>Longitude: " + crimeData.getLon() + "</p>");
            out.println("<p>Violence Level: " + crimeData.getViolenceLevel() + "</p>");
            out.println("</body></html>");
        } else {
            out.println("<html><body><h2>No data found for DR_NO: " + drNo + "</h2></body></html>");
        }
    }

    // Implement doPost, doPut, doDelete as needed based on your requirements


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject jsonInput = reader.readObject();
            String drNo = jsonInput.getString("drNo");
            int dbIndex = Integer.parseInt(drNo) % 3;
            JdbcManager jdbcManager = new JdbcManager(
                dbIndex + 1,
                "root");
            CrimeData crimeData = jdbcManager.fetchCrimeDataByDrNo(drNo);

            if (crimeData != null) {
                JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("drNo", crimeData.getDrNo())
                    // Add other fields as needed
                    .add("dateRptd", crimeData.getDateRptd() != null ? crimeData.getDateRptd().toString() : "")
                    .add("dateOcc", crimeData.getDateOcc() != null ? crimeData.getDateOcc().toString() : "")
                    .add("timeOcc", crimeData.getTimeOcc() == null ? "" : Integer.toString(crimeData.getTimeOcc()))
                    .add("area", crimeData.getArea() == null ? "" : Integer.toString(crimeData.getArea()))
                    .add("areaName", crimeData.getAreaName() != null ? crimeData.getAreaName() : "")
                    .add("rptDistNo", crimeData.getRptDistNo() != null ? crimeData.getRptDistNo() : "")
                    .add("crmCd", crimeData.getCrmCd() != null ? crimeData.getCrmCd() : "")
                    .add("crmCdDesc", crimeData.getCrmCdDesc() != null ? crimeData.getCrmCdDesc() : "")
                    .add("victAge", crimeData.getVictAge() == null ? "" : Integer.toString(crimeData.getVictAge()))
                    .add("victSex", crimeData.getVictSex() != null ? crimeData.getVictSex() : "")
                    .add("victDescent", crimeData.getVictDescent() != null ? crimeData.getVictDescent() : "")
                    .add("premisCd", crimeData.getPremisCd() == null ? "" : Integer.toString(crimeData.getPremisCd()))
                    .add("premisDesc", crimeData.getPremisDesc() != null ? crimeData.getPremisDesc() : "")
                    .add("weaponUsedCd", crimeData.getWeaponUsedCd() != null ? crimeData.getWeaponUsedCd() : "")
                    .add("weaponDesc", crimeData.getWeaponDesc() != null ? crimeData.getWeaponDesc() : "")
                    .add("status", crimeData.getStatus() != null ? crimeData.getStatus() : "")
                    .add("statusDesc", crimeData.getStatusDesc() != null ? crimeData.getStatusDesc() : "")
                    .add("location", crimeData.getLocation() != null ? crimeData.getLocation() : "")
                    .add("crossStreet", crimeData.getCrossStreet() != null ? crimeData.getCrossStreet() : "")
                    .add("lat", crimeData.getLat() == null ? "" : crimeData.getLat().toString())
                    .add("lon", crimeData.getLon() == null ? "" : crimeData.getLon().toString())
                    .add("violenceLevel", crimeData.getViolenceLevel() == null ? "" : Integer.toString(crimeData.getViolenceLevel()))
                    .build();

                resp.getWriter().write(jsonResponse.toString());
            } else {
                resp.getWriter().write(Json.createObjectBuilder()
                    .add("error", "No data found for DR_NO: " + drNo)
                    .build()
                    .toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(Json.createObjectBuilder()
                .add("error", "Error processing request")
                .build()
                .toString());
        }
    }
}