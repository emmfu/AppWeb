package com.revature.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dao.ManagerDAOImpl;
import com.revature.models.Manager;
import com.revature.services.ManagerService;
import com.revature.services.ManagerServiceImpl;

public class RequestHelperManager {

	private static ManagerService mserv = new ManagerServiceImpl(new ManagerDAOImpl());
	private static Logger log = Logger.getLogger(RequestHelperManager.class);
	private static ObjectMapper om = new ObjectMapper();
	
	public static void processAllEmployees(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("In Request Helper getting all Managers...");
		resp.setContentType("application/json");
		List<Manager> allManagers = mserv.findAllManagers();
		String json = om.writeValueAsString(allManagers);
		PrintWriter pw = resp.getWriter();
		pw.println(json);
		log.info("Leaving RequestHelper");
	}
	
	public static void processManagersBySearchParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("In RequestHelper. Searching by param");
		BufferedReader br = req.getReader();
		
	}
}
