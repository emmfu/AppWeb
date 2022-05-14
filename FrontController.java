package com.revature.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class FrontController extends HttpServlet{
	private static final long serialVersionUID = 8339100247721381693L;
	
	private static Logger log = Logger.getLogger(FrontController.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// save the URI and rewrite it to determine what functionality the user is requesting based on the endpoint
		final String URI = req.getRequestURI().replace("/HelloFrontController/", "");		
		log.info("URI: " + URI);
		switch(URI) {
		case "employees":
			// URL: http://localhost:8080/HelloFrontController/employees
			log.info("Employee wants a list of users from API...");
			RequestHelper.processAllEmployees(req, resp);
			break;
		case "employees/employee":
			log.info("Employee search form API based on params. URI: " + URI + ".");
			RequestHelper.processUserBySearchParam(req, resp);
		case "managers":
			log.info("Manager search for all Managers...");
			RequestHelper.processAllManagers(req, resp);
			break;
		case "managers/manager":
			log.info("Manager search by perams. URI: " + URI +".");
			RequestHelper.processManagerBySearchParam(req, resp);
			break;
		case "requests":
			log.info("Retreiving all requests....");
			RequestHelper.processAllRequests(req, resp);
			break;
		case "request":
			log.info("Retrieving requests by perams...");
			RequestHelper.processRequestsBySearchParam(req, resp);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		final String URI = req.getRequestURI().replace("/HelloFrontController/", "");		
		log.info("URI: " + URI);
		switch(URI) {
		case "/register/employee":
			log.info("Register Employee ...");
			RequestHelper.processRegistration(req, resp);
			break;
		case "/register/manager":
			log.info("Register Manager ...");
			RequestHelper.processRegistration(req, resp);
		default:
		case "/employee/submitrequest":
			log.info("Submiting request...");
			RequestHelper.submitRequest(req, resp);
			break;
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse res) {
		final String URI = req.getRequestURI().replace("/HelloFrontController/", "");
		log.info("URI" + URI);
		switch(URI) {
		case "employee/update":
			log.info(")
		}
	}
}
