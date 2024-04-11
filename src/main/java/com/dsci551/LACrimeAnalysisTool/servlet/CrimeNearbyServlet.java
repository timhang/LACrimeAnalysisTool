package com.dsci551.LACrimeAnalysisTool.servlet;

import com.dsci551.LACrimeAnalysisTool.controller.JdbcManager;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CrimeNearbyServlet", urlPatterns = "/crimes-nearby")
public class CrimeNearbyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        double longitude = Double.parseDouble(req.getParameter("longitude"));
        double latitude = Double.parseDouble(req.getParameter("latitude"));
        double radius = Double.parseDouble(req.getParameter("radius"));

        List<Map<String, String>> crimes = new ArrayList<>();
        JdbcManager jdbcManager = new JdbcManager(
            1,
            "root");
        crimes.addAll(jdbcManager.fetchCrimesNearby(longitude, latitude, radius));
        jdbcManager = new JdbcManager(
            2,
            "root");
        crimes.addAll(jdbcManager.fetchCrimesNearby(longitude, latitude, radius));
        jdbcManager = new JdbcManager(
            3,
            "root");
        crimes.addAll(jdbcManager.fetchCrimesNearby(longitude, latitude, radius));

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Map<String, String> crime : crimes) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            crime.forEach(objectBuilder::add);
            arrayBuilder.add(objectBuilder);
        }

        resp.getWriter().write(arrayBuilder.build().toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        // Read JSON from the request body
        double longitude, latitude, radius;
        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject jsonInput = reader.readObject();
            longitude = jsonInput.getJsonNumber("longitude").doubleValue();
            latitude = jsonInput.getJsonNumber("latitude").doubleValue();
            radius = jsonInput.getJsonNumber("radius").doubleValue();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON input");
            return;
        }

        List<Map<String, String>> crimes = new ArrayList<>();
        JdbcManager jdbcManager = new JdbcManager(
            1,
            "root");
        crimes.addAll(jdbcManager.fetchCrimesNearby(longitude, latitude, radius));
        jdbcManager = new JdbcManager(
            2,
            "root");
        crimes.addAll(jdbcManager.fetchCrimesNearby(longitude, latitude, radius));
        jdbcManager = new JdbcManager(
            3,
            "root");
        crimes.addAll(jdbcManager.fetchCrimesNearby(longitude, latitude, radius));

        // Build JSON response
        try {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Map<String, String> crime : crimes) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                crime.forEach(objectBuilder::add);
                arrayBuilder.add(objectBuilder);
            }
            resp.getWriter().write(arrayBuilder.build().toString());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}