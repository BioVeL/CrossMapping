<%--
  #%L
  XMap GUI CrossMapping Portlet
  %%
  Copyright (C) 2012 - 2013 Cardiff University
  %%
  Use of this software is governed by the attached licence file. If no licence 
  file is present the software must not be used.
  
  The use of this software, including reverse engineering, for any other purpose 
  is prohibited without the express written permission of the software owners, 
  Cardiff University and Italy National Research Council.
  #L%
  --%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<html>
  <head>
	<meta contentType="text/html; charset=UTF-8">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/css/CrossMappingPortlet.css" />

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>CrossMapping</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                                               -->       
	<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/crossmappingportlet/crossmappingportlet.nocache.js"></script>

  </head>


  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <div id="mainPanel"></div>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
   
  </body>
</html>


