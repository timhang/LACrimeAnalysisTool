package com.dsci551.LACrimeAnalysisTool.servlet;

import com.dsci551.LACrimeAnalysisTool.controller.JdbcManager;
import com.dsci551.LACrimeAnalysisTool.model.CrimeData;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class CrimeSearchServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        // Parse JSON request
        JsonReader reader = Json.createReader(request.getInputStream());
        JsonObject jsonObject = reader.readObject();
        reader.close();

        String startDate = jsonObject.getString("startDate", null);
        String endDate = jsonObject.getString("endDate", null);
        String areaName = jsonObject.getString("areaName", null);
        String crimeCode = jsonObject.getString("crimeCode", null);
        Double latitude = jsonObject.isNull("latitude") ? null : jsonObject.getJsonNumber("latitude").doubleValue();
        Double longitude = jsonObject.isNull("longitude") ? null : jsonObject.getJsonNumber("longitude").doubleValue();
        Double radius = jsonObject.isNull("radius") ? null : jsonObject.getJsonNumber("radius").doubleValue();

        // Query the database
        JdbcManager jdbcManager = new JdbcManager(1, "root");
        List<CrimeData> crimes = jdbcManager.fetchCrimes(startDate, endDate, areaName, crimeCode, latitude, longitude, radius);

        // Build JSON response
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (CrimeData crime : crimes) {
            arrayBuilder.add(Json.createObjectBuilder()
                .add("drNo", crime.getDrNo())
                .add("dateOccurred", crime.getDateOcc().toString())
                .add("areaName", crime.getAreaName())
                .add("crimeCode", crime.getCrmCd())
                .add("crimeDescription", crime.getCrmCdDesc())
                .add("latitude", crime.getLat())
                .add("longitude", crime.getLon())
                .add("violenceLevel", 2));
        }

        // Send response
        Json.createWriter(response.getOutputStream()).writeArray(arrayBuilder.build());
    }
}