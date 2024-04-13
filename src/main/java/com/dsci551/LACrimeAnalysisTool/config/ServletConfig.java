package com.dsci551.LACrimeAnalysisTool.config;

import com.dsci551.LACrimeAnalysisTool.servlet.CrimeCodeServlet;
import com.dsci551.LACrimeAnalysisTool.servlet.CrimeNearbyServlet;
import com.dsci551.LACrimeAnalysisTool.servlet.CrimeSearchServlet;
import com.dsci551.LACrimeAnalysisTool.servlet.CrimeServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<CrimeServlet> crimeServletRegistrationBean() {
        ServletRegistrationBean<CrimeServlet> registrationBean = new ServletRegistrationBean<>(new CrimeServlet(), "/crimeById");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean<CrimeNearbyServlet> crimeNearbyServletRegistrationBean() {
        ServletRegistrationBean<CrimeNearbyServlet> registrationBean = new ServletRegistrationBean<>(new CrimeNearbyServlet(), "/crimes-nearby");
        return registrationBean;
    }
    @Bean
    public ServletRegistrationBean<CrimeSearchServlet> crimeSearchServletRegistrationBean() {
        ServletRegistrationBean<CrimeSearchServlet> registrationBean = new ServletRegistrationBean<>(new CrimeSearchServlet(), "/search-crimes");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean<CrimeCodeServlet> crimeCodeServletRegistrationBean() {
        ServletRegistrationBean<CrimeCodeServlet> registrationBean = new ServletRegistrationBean<>(new CrimeCodeServlet(), "/crime-codes");
        return registrationBean;
    }

}
