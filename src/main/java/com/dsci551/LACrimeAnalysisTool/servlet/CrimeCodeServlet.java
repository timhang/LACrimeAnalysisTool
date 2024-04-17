package com.dsci551.LACrimeAnalysisTool.servlet;

import com.dsci551.LACrimeAnalysisTool.controller.JdbcManager;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CrimeCodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Map<String, String> codes = new HashMap<>();
        JdbcManager jdbcManager = new JdbcManager(1, "root");
        codes.putAll(jdbcManager.getAllCrimeCodes());
        jdbcManager = new JdbcManager(2, "root");
        codes.putAll(jdbcManager.getAllCrimeCodes());
        jdbcManager = new JdbcManager(3, "root");
        codes.putAll(jdbcManager.getAllCrimeCodes());
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        Iterator<String> iterator = codes.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add("Crm_Cd", key);
            jsonObjectBuilder.add("Crm_Cd_Desc", codes.get(key));
            jsonArrayBuilder.add(jsonObjectBuilder);
        }
//        for (Iterable<String, String> code : codes) {
//            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
//            jsonObjectBuilder.add("Crm_Cd", code.getCrmCd());
//            jsonObjectBuilder.add("Crm_Cd_Desc", code.getCrmCdDesc());
//            jsonArrayBuilder.add(jsonObjectBuilder);
//        }

        Json.createWriter(response.getOutputStream()).writeArray(jsonArrayBuilder.build());
    }
}
