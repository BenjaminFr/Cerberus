<%--
  ~ Cerberus  Copyright (C) 2013  vertigo17
  ~ DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
  ~
  ~ This file is part of Cerberus.
  ~
  ~ Cerberus is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ Cerberus is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%
    String campaignName = request.getParameter("CampaignName");
    String tag = request.getParameter("Tag");
    String[] environments = request.getParameterValues("Environment");
    String[] countries = request.getParameterValues("Country");
    String[] browsers = request.getParameterValues("Browser");
    
    boolean onlyFunction = ("true".equalsIgnoreCase(request.getParameter("OnlyFunction")));

    StringBuffer query = new StringBuffer("CampaignName=").append(campaignName);
    query.append("&Tag=").append(tag);
    
    if(environments != null && environments.length > 0) {
        for(String environment : environments) {
            query.append("&Environment=").append(environment);
        }
    }
    
    if(countries != null && countries.length > 0) {
        for(String country : countries) {
            query.append("&Country=").append(country);
        }
    }
    
    if(browsers != null && browsers.length > 0) {
        for(String browser : browsers) {
            query.append("&Browser=").append(browser);
        }
    }
%>
<% Date DatePageStart = new Date();%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Campaign Reporting</title>
        <link rel="stylesheet" 
              type="text/css" href="css/crb_style.css"
              />
        <link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
        <link rel="stylesheet" type="text/css" href="css/crb_style.css">
        <link rel="stylesheet" type="text/css" href="css/jquery-ui.css">
        <link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
        <script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
        <script type="text/javascript" src="js/jquery-ui-1.10.2.js"></script>
        <script type="text/javascript" src="js/sorttable.js"></script>
        <script type="text/javascript" src="js/Chartjs/Chart.js"></script>
        <script type="text/javascript" src="js/Chartjs/extensions/Chart.ColoredBar.js"></script>
        <script type="text/javascript" src="js/Chartjs/extensions/Chart.StackedBar.js"></script>
        <script type="text/javascript" src="js/campaignReport.js"></script>

        <script>
            Chart.defaults.global.responsive = true;
            // Number - Scale label font size in pixels
            Chart.defaults.global.scaleFontSize= '14';

            var executionStatus, pieExecutionStatus;

            $(document).ready(function () {
                
                createGraphFromAjaxToElement("./CampaignExecutionReportByFunction?<%=query.toString() %>","#functionTest", null);
                createGraphFromAjaxToElement("./CampaignExecutionStatusBarGraphByFunction?<%=query.toString() %>","#functionBar", null);
                
                jQuery.ajax("./CampaignExecutionGraphByStatus?<%=query.toString() %>").done(function(data) {
                    // function used to generate the Pie graph about status number
                    var pie = createGraphFromDataToElement(data,"#myDonut", null);
                    
                    $("#myDonut").on('click', function (evt) {
                        var activePoints = pie.getSegmentsAtEvent(evt);

                        var anchor = $('a[name="Status' + activePoints[0].label + '"]');
                        $('html').animate({
                            scrollTop: anchor.offset().top
                        }, 'slow');

                        return false;
                    });

                    
                    // code used to create the execution status table.
                    $("div.executionStatus").empty().append("<table  class='arrondTable fullSize'><thead><tr><th>Execution status</th><th>TestCase Number</th></tr></thead><tbody></tbody></table>");
                    var total = 0;
                    // create each line of the table
                    for (var index = 0; index < data.labels.length; index++) {
                        $("div.executionStatus table tbody").append(
                                $("<tr></tr>").append(
                                $("<td></td>").text(data.axis[index].label))
                                .append($("<td></td>").text(data.axis[index].value))
                            );
                        // increase the total execution
                        total = total + data.axis[index].value;
                    }
                    // add a line for the total
                    $("div.executionStatus table tbody").append(
                            $("<tr></tr>").append(
                                $("<th>Total</th>"))
                            .append($("<th></th>").text(total))
                    );
                });

                $.get("./CampaignExecutionReport", "<%=query.toString() %>", function (report) {
                    // Get context with jQuery - using jQuery's .get() method.

                    for (var index = 0; index < report.length; index++) {
<%
                        if(!onlyFunction) {
%>
                            report[index].Function = (report[index].Function ? report[index].Function : report[index].Test);
<%
                        }
%>
                        addTestCaseToStatusTabs(report[index]);
                    }

                    $("table.needToBeSort").each(function(){
                        sorttable.makeSortable(this);
                    });
                    
                });
            });
        </script>
        <style>

            html, body { 
                height: 100%;
                padding:0; margin:0;
                background: white;
            }
            

        .ID {
                width: 5%;
            }
        .Test {
                width: 10%;
            }
        .TestCase {
                width: 5%;
            }
        .ShortDescription {
                width: 32%;
            }
        .Function {
                width: 10%;
            }
        .Control {
                width: 3%;
            }
        .Status {
                width: 5%;
            }
        .Application {
                width: 5%;
            }
        .BugID {
                width: 5%;
            }
        .Comment {
                width: 10%;
            }
        .Start {
                width: 10%;
            }
        .End {
                width: 10%;
            }

            table.noBorder td {
                border: none;
            }

            table.fullSize {
                width: 100%;
            }

            a.StatusOK {
                color: #00EE00;
            }

            a.StatusKO {
                color: #F7464A;
            }

            a.StatusFA {
                color: #FDB45C;
            }

            a.StatusNA {
                color: #EEEE00;
            }

            a.StatusNE {
                color: #000;
            }

            a.StatusPE {
                color: #2222FF;
            }

            table.needToBeSort th:not(.sorttable_sorted):not(.sorttable_sorted_reverse):not(.sorttable_nosort):after { 
                content: " \25B4\25BE" 
            }            
        </style>
    </head>
    <body>
        <%@ include file="include/function.jsp" %>
        <%@ include file="include/header.jsp" %>

        <div id="main">
            <table class="fullSize noBorder">
                <tr>
                    <td style="width: 20%">
                        <div class="executionStatus"></div>
                        <br>
                        <canvas id="myDonut"></canvas>
                    </td>
                    <td style="width: 78%">
                        <canvas id="functionBar"></canvas>
                    </td>
                </tr>
                <tr style="width: 98%">
                    <td colspan="2">
                        <canvas id="functionTest"></canvas>
                    </td>
                </tr>
            </table>
            <h1><a name="StatusNE" class="StatusNE">Not Executed</a></h1>
            <table id="StatusNE" class="arrondTable fullSize needToBeSort">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Function</th>
                        <th>Test</th>
                        <th>TestCase</th>
                        <th>Description</th>
                        <th>Control</th>
                        <th>Status</th>
                        <th>Application</th>
                        <th>BugID</th>
                        <th class="wrapAll">Comment</th>
                        <th>Start</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>            
            <h1><a name="StatusKO" class="StatusKO">Status KO</a></h1>
            <table id="StatusKO" class="arrondTable fullSize needToBeSort">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Function</th>
                        <th>Test</th>
                        <th>TestCase</th>
                        <th>Description</th>
                        <th>Control</th>
                        <th>Status</th>
                        <th>Application</th>
                        <th>BugID</th>
                        <th class="wrapAll">Comment</th>
                        <th>Start</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <h1><a name="StatusFA" class="StatusFA">Status FA</a></h1>
            <table id="StatusFA" class="arrondTable fullSize needToBeSort">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Function</th>
                        <th>Test</th>
                        <th>TestCase</th>
                        <th>Description</th>
                        <th>Control</th>
                        <th>Status</th>
                        <th>Application</th>
                        <th>BugID</th>
                        <th class="wrapAll">Comment</th>
                        <th>Start</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <h1><a name="StatusNA" class="StatusNA">Status NA</a></h1>
            <table id="StatusNA" class="arrondTable fullSize needToBeSort">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Function</th>
                        <th>Test</th>
                        <th>TestCase</th>
                        <th>Description</th>
                        <th>Control</th>
                        <th>Status</th>
                        <th>Application</th>
                        <th>BugID</th>
                        <th class="wrapAll">Comment</th>
                        <th>Start</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <h1><a name="StatusPE" class="StatusPE">Status PE</a></h1>
            <table id="StatusPE" class="arrondTable fullSize needToBeSort">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Function</th>
                        <th>Test</th>
                        <th>TestCase</th>
                        <th>Description</th>
                        <th>Control</th>
                        <th>Status</th>
                        <th>Application</th>
                        <th>BugID</th>
                        <th class="wrapAll">Comment</th>
                        <th>Start</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <h1><a name="StatusOK" class="StatusOK">Status OK</a></h1>
            <table id="StatusOK" class="arrondTable fullSize needToBeSort">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Function</th>
                        <th>Test</th>
                        <th>TestCase</th>
                        <th>Description</th>
                        <th>Control</th>
                        <th>Status</th>
                        <th>Application</th>
                        <th>BugID</th>
                        <th class="wrapAll">Comment</th>
                        <th>Start</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </body>
</html>