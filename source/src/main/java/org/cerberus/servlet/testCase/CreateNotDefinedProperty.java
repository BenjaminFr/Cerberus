/*
 * Cerberus  Copyright (C) 2013  vertigo17
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.servlet.testCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cerberus.entity.TestCaseCountryProperties;
import org.cerberus.factory.IFactoryTestCaseCountryProperties;
import org.cerberus.factory.impl.FactoryTestCaseCountryProperties;
import org.cerberus.service.ITestCaseCountryPropertiesService;
import org.cerberus.service.ITestCaseCountryService;
import org.cerberus.service.impl.TestCaseCountryPropertiesService;
import org.cerberus.service.impl.TestCaseCountryService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author memiks
 */
@WebServlet(name = "CreateNotDefinedProperty", urlPatterns = {"/CreateNotDefinedProperty"})
public class CreateNotDefinedProperty extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());

        ITestCaseCountryPropertiesService testCaseCountryPropertiesService = appContext.getBean(TestCaseCountryPropertiesService.class);
        ITestCaseCountryService testCaseCountryService = appContext.getBean(TestCaseCountryService.class);
        IFactoryTestCaseCountryProperties factoryTestCaseCountryProperties = appContext.getBean(FactoryTestCaseCountryProperties.class);

        try {

            String[] properties = request.getParameterValues("property");
            String toTest = request.getParameter("totest");
            String toTestCase = request.getParameter("totestcase");

            // We retrieve all country of the destination TestCase
            List<String> toCountriesAll = testCaseCountryService.findListOfCountryByTestTestCase(toTest, toTestCase);

            if (toCountriesAll != null && toCountriesAll.size() > 0) {
                // Variable for the properties list of the destination TestCase
                List<TestCaseCountryProperties> listOfPropertiesToInsert = new ArrayList<TestCaseCountryProperties>();

                // Variable for the countries of a property of the destination TestCase
                List<String> toCountriesProp;

                for (String property : properties) {

                    // List of all country of the destination test for the current property
                    List<String> toCountries = new ArrayList<String>();
                    toCountries.addAll(toCountriesAll);

                    // Retrieve the country of the destination TestCase for the property,
                    // if not empty remove it (property aleady exists for these countries)
                    toCountriesProp = testCaseCountryPropertiesService.findCountryByPropertyNameAndTestCase(toTest, toTestCase, property);
                    if (toCountriesProp != null && toCountriesProp.size() > 0) {
                        toCountries.removeAll(toCountriesProp);
                    }

                    for (String country : toCountries) {
                        listOfPropertiesToInsert.add(factoryTestCaseCountryProperties.create(
                                toTest,
                                toTestCase,
                                country,
                                property,
                                "text",
                                "VC",
                                "** Property not defined **",
                                "",
                                0,
                                0,
                                "STATIC"
                        ));
                    }
                }

                testCaseCountryPropertiesService.insertListTestCaseCountryProperties(listOfPropertiesToInsert);
            }

        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
