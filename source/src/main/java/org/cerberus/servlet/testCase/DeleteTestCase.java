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
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cerberus.entity.MessageGeneral;
import org.cerberus.entity.MessageGeneralEnum;
import org.cerberus.entity.TCase;
import org.cerberus.entity.TestCaseStep;
import org.cerberus.exception.CerberusException;
import org.cerberus.service.ITestCaseService;
import org.cerberus.service.ITestCaseStepService;
import org.cerberus.service.impl.UserService;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author bcivel
 */
@WebServlet(name = "DeleteTestCase", urlPatterns = {"/DeleteTestCase"})
public class DeleteTestCase extends HttpServlet {

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
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String test = policy.sanitize(request.getParameter("test"));
        String testcase = policy.sanitize(request.getParameter("testcase"));
        String fromPage = policy.sanitize(request.getParameter("fromPage"));

        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        ITestCaseService tcService = appContext.getBean(ITestCaseService.class);
        ITestCaseStepService tcsService = appContext.getBean(ITestCaseStepService.class);
        try {
            TCase testCase = tcService.findTestCaseByKey(test, testcase);
            if (testCase != null) {
                List<TestCaseStep> tcsList = tcsService.getTestCaseStepUsingTestCaseInParamter(test, testcase);
                    if (tcsList != null && !tcsList.isEmpty()){
                        response.sendError(403, MessageGeneralEnum.GUI_TESTCASE_DELETE_USED_STEP.getDescription());
                        return;
                    }
                    tcService.deleteTestCase(testCase);
            } else {
                throw new CerberusException(new MessageGeneral(MessageGeneralEnum.NO_DATA_FOUND));
            }
        } catch (CerberusException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.ERROR, null, ex);
        }

        response.sendRedirect(fromPage);

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
