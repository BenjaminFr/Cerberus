<%--
    Document   : RunTests
    Created on : 22 févr. 2011, 13:48:03
    Author     : acraske
--%>

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.net.InetAddress"%>
<%@page import="java.sql.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<% Date DatePageStart = new Date();%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Run TestCase</title>
        <link rel="stylesheet" type="text/css" href="css/crb_style.css">
        <link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico" />
        <script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
    </head>
    <body>
        <%@ include file="include/function.jsp"%>
        <%@ include file="include/header.jsp"%>
        <div id="body">
            <form method="get" name="InsertPref">

            </form>
            <form method="get" name="RunTest" onsubmit="validateForm()">
                <%

                    String testcaseApplication = null;
                    Connection conn = null;
                    try {

                        conn = db.connect();

                        Statement stmt = conn.createStatement();
                        Statement stmt2 = conn.createStatement();

                        String insertDefIP = "update user set DefaultIP = '"
                                + request.getParameter("ss_ip") + "' where login = '"
                                + request.getUserPrincipal().getName() + "'";

                        if (StringUtils.isNotBlank(request.getParameter("DefaultIP"))) {
                            if (request.getParameter("ss_ip") != null && request.getParameter("ss_ip").compareTo("") != 0) {
                                stmt.execute(insertDefIP);
                            }
                        }





                        if (request.getParameter("statusPage") != null && request.getParameter("statusPage").compareTo("Run") == 0) {

                            StringBuilder params = new StringBuilder();
                            params.append("RunTestCase?redirect=Y");
                            //params.append("http://localhost:8080/newCerberus/RunTestCase?redirect=Y");
                            //params.append("BatchExecution?redirect=Y");
                            Enumeration<String> pList = request.getParameterNames();
                            while (pList.hasMoreElements()) {
                                String sName = pList.nextElement().toString();
                                if (sName.compareTo("Test") == 0 || sName.compareTo("TestCase") == 0
                                        || sName.compareTo("Country") == 0 || sName.compareTo("Environment") == 0
                                        || sName.compareTo("ss_ip") == 0 || sName.compareTo("ss_p") == 0
                                        || sName.compareTo("browser") == 0
                                        || sName.compareTo("manualURL") == 0
                                        || sName.compareTo("myhost") == 0 || sName.compareTo("mycontextroot") == 0 || sName.compareTo("myloginrelativeurl") == 0
                                        || sName.compareTo("myenvdata") == 0
                                        || sName.compareTo("Tag") == 0 || sName.compareTo("outputformat") == 0
                                        || sName.compareTo("verbose") == 0 || sName.compareTo("screenshot") == 0) {
                                    String[] sMultiple = request.getParameterValues(sName);
                                    {
                                        for (int i = 0; i < sMultiple.length; i++) {
                                            params.append("&" + sName + "=" + sMultiple[i] + "");
                                        }
                                    }
                                }
                            }
                            response.sendRedirect(params.toString());
                        }



                        String ssIP;
                        if (request.getParameter("ss_ip") != null && request.getParameter("ss_ip").compareTo("") != 0) {
                            ssIP = request.getParameter("ss_ip");
                        } else {
                            ssIP = request.getHeader("X-FORWARDED-FOR");
                            if (ssIP == null) {

                                String defaultIP = "SELECT DefaultIP from USER where login = '"
                                        + request.getUserPrincipal().getName() + "'";

                                ResultSet rs_Ip = stmt2.executeQuery(defaultIP);

                                if (rs_Ip.first()) {
                                    if (StringUtils.isNotBlank(rs_Ip.getString("DefaultIP"))) {
                                        ssIP = rs_Ip.getString("DefaultIP");
                                    } else {
                                        ssIP = "";
                                    }
                                }

                                //    ssIP = request.getRemoteHost();
                            }
                        }

                        String ssPort;
                        if (request.getParameter(
                                "ss_p") != null && request.getParameter("ss_p").compareTo("") != 0) {
                            ssPort = request.getParameter("ss_p");
                        } else {
                            ssPort = "5555";
                        }

                        String browser;
                        if (request.getParameter(
                                "browser") != null && request.getParameter("browser").compareTo("") != 0) {
                            browser = request.getParameter("browser");;
                        } else {
                            browser = new String("firefox");
                        }

                        StringBuilder sqlOpts = new StringBuilder();


                        String environment;
                        if (request.getParameter(
                                "Environment") != null && request.getParameter("Environment").compareTo("All") != 0) {
                            environment = request.getParameter("Environment");
                        } else {
                            environment = new String("%%");
                        }


                        String test;
                        if (request.getParameter(
                                "Test") != null && request.getParameter("Test").compareTo("All") != 0) {
                            test = request.getParameter("Test");
                        } else {
                            test = new String("%%");
                        }

                        String testcase;
                        if (request.getParameter(
                                "TestCase") != null && request.getParameter("TestCase").compareTo("All") != 0) {
                            testcase = request.getParameter("TestCase");
                        } else {
                            testcase = new String("%%");
                        }

                        String country;
                        if (request.getParameter(
                                "Country") != null && request.getParameter("Country").compareTo("All") != 0) {
                            country = request.getParameter("Country");
                        } else {
                            country = new String("%%");
                        }

                        String tag;
                        if (request.getParameter(
                                "Tag") != null && request.getParameter("Tag").compareTo("All") != 0) {
                            tag = request.getParameter("Tag");
                        } else {
                            tag = new String("None");
                        }

                        String enable = "";

                %>


                <table>
                    <tr><td id="arrond"  ><h3 style="color: blue">Tool Parameters</h3>

                            <table border="0px">
                                <tr>                                         
                                    <td id="wob" style="font-weight: bold; width: 150px"><% out.print(dbDocS(conn, "runnerpage", "SeleniumServerIP", "Selenium Server IP "));%></td>
                                    <td id="wob"><input type="text" name="ss_ip" value="<%= ssIP%>" />
                                        <input id="button" type="submit" <%=enable%> name="DefaultIP" value="Set As My Default IP" >
                                    </td>

                                </tr>
                                <tr>
                                    <td id="wob" style="font-weight: bold; width: 150px"><% out.print(dbDocS(conn, "runnerpage", "SeleniumServerPort", "Selenium Server Port "));%></td>
                                    <td id="wob"><input type="text" name="ss_p" value="<%= ssPort%>" />
                                    </td>
                                </tr>
                                <tr>
                                    <td id="wob" style="font-weight: bold; width: 150px"><% out.print(dbDocS(conn, "runnerpage", "BrowserPath", "Browser Path"));%></td>
                                    <td id="wob">
                                        <%=ComboInvariant(conn, "browser", "width: 90px", "browser", "browser", "37", browser, "", null)%>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

                <br>

                <table>
                    <tr>
                        <td id="arrond">
                            <h3 style="color: blue">Test Parameters</h3>

                            <table id="testParameters" border="0px">
                                <tr>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "test", "Test", "Test"));%></td>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "testcase", "TestCase", "Test Case"));%></td>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "testcasecountryproperties", "Country", "Country"));%></td>
                                </tr>
                                <tr>
                                    <td id="wob"><select size="16" id="test" name="Test"
                                                         style="width: 200px" onchange="document.RunTest.submit()">
                                            <%
                                                ResultSet rsTest = stmt.executeQuery("SELECT DISTINCT t.Test FROM Test t, TestCase tc WHERE tc.test=t.test AND tc.tcactive='Y' AND t.active='Y'");
                                                while (rsTest.next()) {%>
                                            <option style="width: 300px" value="<%= rsTest.getString(1)%>"
                                                    <%=test.compareTo(rsTest.getString(1)) == 0 ? " SELECTED " : ""%>><%= rsTest.getString(1)%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                    <td id="wob"><select size="16" id="testcase"
                                                         name="TestCase" style="width: 600px"
                                                         onchange="document.RunTest.submit()">
                                            <% ResultSet rsTestCase = stmt.executeQuery("SELECT DISTINCT tc.TestCase, tc.Description, tc.application FROM TestCase tc WHERE tc.test = '" + test + "' AND tc.group is not NULL AND tc.group not in ('PRIVATE') AND length(tc.group) > 1 AND TcActive = 'Y'");
                                                while (rsTestCase.next()) {
                                            %>
                                            <option style="width: 600px"
                                                    value="<%= rsTestCase.getString(1)%>"
                                                    <%=testcase.compareTo(rsTestCase.getString(1)) == 0 ? "SELECTED " : ""%>>
                                                <%= rsTestCase.getString(1)%> [<%= rsTestCase.getString(3)%>]
                                                :
                                                <%= rsTestCase.getString(2)%></option>
                                                <%
                                                        if (testcase.compareTo(rsTestCase.getString(1)) == 0) {
                                                            testcaseApplication = rsTestCase.getString(3);
                                                        }
                                                    }%>
                                        </select>
                                    </td>
                                    <td id="wob"><select size="16" id="country"
                                                         name="Country" style="width: 50px"
                                                         onchange="document.RunTest.submit()">
                                            <% ResultSet rsCountry = stmt.executeQuery("SELECT DISTINCT c.Country FROM testcasecountry c "
                                                        + " join invariant i on i.value=c.country and i.id=4 "
                                                        + " WHERE c.test = '" + test + "' AND c.testcase = '" + testcase + "'"
                                                        + " order by sort;");
                                                while (rsCountry.next()) {%>
                                            <option style="width: 300px" value="<%= rsCountry.getString(1)%>"
                                                    <%=country.compareTo(rsCountry.getString(1)) == 0 ? " SELECTED " : ""%>><%= rsCountry.getString(1)%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td id="wob" style="font-weight: bold; width: 150px"><% out.print(dbDocS(conn, "countryenvparam", "Environment", "Environment"));%></td>
                                </tr>
                                <tr>
                                    <td colspan="3">
                                        <table>
                                            <tr id="trmanualconfig">
                                                <td id="wob">
                                                    <input type="radio" name="manualURL" value="N" onclick="setEnvAutomatic()" checked>Automatic
                                                    <input type="radio" name="manualURL" value="Y" onclick="setEnvManual()">Manual<br>
                                                </td>
                                                <td>
                                                    <table>
                                                        <tr>
                                                            <td id="wob" colspan="2"><span style="font-weight: normal;">Environment</span></td>
                                                            <td id="wob" colspan="6">
                                                                <select id="environment" name="Environment" style="width: 400px">
                                                                    <%
                                                                        StringBuilder sql = new StringBuilder();
                                                                        sql.append("SELECT DISTINCT ce.Environment Environment, ce.Build Build, ");
                                                                        sql.append("    ce.Revision Revisionv ");
                                                                        sql.append("FROM countryenvironmentparameters cea, countryenvparam ce, invariant i ");
                                                                        sql.append("WHERE ce.country = cea.country AND ce.environment = cea.environment ");
                                                                        sql.append("    AND cea.Application = '");
                                                                        sql.append(testcaseApplication);
                                                                        sql.append("' AND cea.country='");
                                                                        sql.append(country);
                                                                        sql.append("'");
                                                                        sql.append("    AND ce.active='Y' ");
                                                                        sql.append("    AND i.id = 5 AND i.Value = ce.Environment ");
                                                                        sql.append("ORDER BY i.sort ");

                                                                        if (!(testcaseApplication == null) && !(country.isEmpty()) && !(country.equalsIgnoreCase("%%"))) {
                                                                            ResultSet rsEnv = stmt.executeQuery(sql.toString());
                                                                            while (rsEnv.next()) {
                                                                    %>
                                                                    <option style="width: 400px" value="<%= rsEnv.getString(1)%>"
                                                                            <%=environment.compareTo("%%") == 0 && rsEnv.getString(1).contains("UAT") ? " SELECTED " : ""%>
                                                                            <%=environment.compareTo(rsEnv.getString(1)) == 0 ? " SELECTED " : ""%>><%= rsEnv.getString(1)%>
                                                                        With Build : <%= rsEnv.getString(2)%> And Revision : <%= rsEnv.getString(3)%></option>
                                                                        <%
                                                                                }
                                                                            }
                                                                        %>
                                                                </select>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td id="wob"><span style="font-weight: normal;font-size: smaller">My Host</span></td>
                                                            <td id="wob"><input type="text" style="width: 100px" name="myhost" id="myhost" disabled value="localhost:8080"></td>
                                                            <td id="wob"><span style="font-weight: normal;font-size: smaller">My Context Root</span></td>
                                                            <td id="wob"><input type="text" style="width: 200px" name="mycontextroot" id="mycontextroot" disabled value="/myapp/"></td>
                                                            <td id="wob"><span style="font-weight: normal;font-size: smaller">My Login Relative URL</span></td>
                                                            <td id="wob"><input type="text" style="width: 200px" name="myloginrelativeurl" id="myloginrelativeurl" disabled value="login.jsp?comcode=200"></td>
                                                            <td id="wob"><span style="font-weight: normal;font-size: smaller">Data Environment</span></td>
                                                            <td id="wob">
                                                                <select id="myenvdata" name="myenvdata" style="width: 200px" disabled>
                                                                    <%
                                                                        if (!(testcaseApplication == null) && !(country.isEmpty()) && !(country.equalsIgnoreCase("%%"))) {
                                                                            ResultSet rsEnv = stmt.executeQuery(sql.toString());
                                                                            while (rsEnv.next()) {
                                                                    %>
                                                                    <option style="width: 400px" value="<%= rsEnv.getString(1)%>"
                                                                            <%=environment.compareTo("%%") == 0 && rsEnv.getString(1).contains("UAT") ? " SELECTED " : ""%>
                                                                            <%=environment.compareTo(rsEnv.getString(1)) == 0 ? " SELECTED " : ""%>><%= rsEnv.getString(1)%>
                                                                        With Build : <%= rsEnv.getString(2)%> And Revision : <%= rsEnv.getString(3)%></option>
                                                                        <%
                                                                                }
                                                                            }%>
                                                                </select>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

                <br>                                        

                <table>
                    <tr>
                        <td id="arrond">
                            <h3 style="color: blue">Execution Parameters</h3>
                            <table>
                                <tr>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "runnerpage", "Tag", "Tag"));%></td>
                                    <td class="wob" colspan="4">
                                        <input id="tag" name="Tag" style="width: 200px">
                                    </td>
                                </tr>
                                <tr>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "runnerpage", "outputformat", ""));%></td>
                                    <td class="wob">
                                        <%=ComboInvariant(conn, "outputformat", "width: 90px", "Format", "Format", "24", "gui", "", null)%>
                                    </td>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "runnerpage", "verbose", ""));%></td>
                                    <td class="wob">
                                        <%=ComboInvariant(conn, "verbose", "width: 90px", "Verbose", "Verbose", "25", "0", "", null)%>
                                    </td>
                                    <td id="wob" style="font-weight: bold; width: 100px"><% out.print(dbDocS(conn, "runnerpage", "screenshot", ""));%></td>
                                    <td class="wob">
                                        <%=ComboInvariant(conn, "screenshot", "width: 90px", "Screenshot", "Screenshot", "39", "1", "", null)%>
                                    </td>
                                    <td class="wob" id="button"><input type="submit" id="buttonRun" style="font-size: large" name="statusPage" value="Run"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

                <br>


                <% if (test.compareTo("%%") != 0 && testcase.compareTo("%%") != 0 && country.compareTo("%%") != 0) {
                %>

                <table border="0px">
                </table>
            </form>

            <%                    }

                } catch (Exception e) {
                    out.println(e);
                } finally {
                    try {
                        conn.close();
                        db.disconnect();
                    } catch (Exception ex) {
                        out.println(ex);
                    }
                }
            %>

        </div>

        <br><% out.print(display_footer(DatePageStart));%>

    <script type="text/javascript">
        function validateForm(){
            if($("#myloginrelativeurl").val()){
                var val = $("#myloginrelativeurl").val().replace("&", "%26");
                $("#myloginrelativeurl").val(val);
            }
            return true;
        }
    </script>
    </body>
</html>