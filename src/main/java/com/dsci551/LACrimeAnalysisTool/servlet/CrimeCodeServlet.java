package com.dsci551.LACrimeAnalysisTool.servlet;

import com.dsci551.LACrimeAnalysisTool.controller.JdbcManager;
import com.dsci551.LACrimeAnalysisTool.model.CrimeData;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class CrimeCodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        JdbcManager jdbcManager = new JdbcManager(1, "root");
        List<CrimeData> codes = jdbcManager.getAllCrimeCodes();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (CrimeData code : codes) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add("Crm_Cd", code.getCrmCd());
            jsonObjectBuilder.add("Crm_Cd_Desc", code.getCrmCdDesc());
            jsonArrayBuilder.add(jsonObjectBuilder);
        }

        Json.createWriter(response.getOutputStream()).writeArray(jsonArrayBuilder.build());
    }
}
