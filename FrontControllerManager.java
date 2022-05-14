package com.revature.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class FrontControllerManager extends HttpServlet {

	private static final long serialVersionUID = 6225552933986248152L;
	private static Logger log = Logger.getLogger(FrontControllerManager.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// save the URI and rewrite it to determine what functionality the user is requesting based on the endpoint
		final String URI = req.getRequestURI().replace("/HelloFrontController/", "");		
		log.info("URI: " + URI);
		switch(URI) {
		case "managers":
			// URL: http://localhost:8080/HelloFrontController/managers
			log.info("Manager wants a list of managers from API...");
			RequestHelper.processAllEmployees(req, resp);
			break;
		case "manager":
			log.info("Manager search form API based on params. URI: " + URI + ".");
			RequestHelper.processUserBySearchParam(req, resp);
		default:
			break;
		}	
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		final String URI = req.getRequestURI().replace("/HelloFrontController/managers", "");		
		log.info("URI: " + URI);
		switch(URI) {
		case "register":
			log.info("Register Employee ...");
			RequestHelper.processRegistration(req, resp);
			break;
		default:
			break;
		}
	}
}
