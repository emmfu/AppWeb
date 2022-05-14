package com.revature.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dao.EmployeeDAOImpl;
import com.revature.dao.ManagerDAOImpl;
import com.revature.dao.RequestDAOImpl;
import com.revature.models.Employee;
import com.revature.models.Manager;
import com.revature.models.Request;
import com.revature.services.EmployeeService;
import com.revature.services.EmployeeServiceImpl;
import com.revature.services.ManagerService;
import com.revature.services.ManagerServiceImpl;
import com.revature.services.RequestService;
import com.revature.services.RequestServiceImpl;

public class RequestHelper {

	private static EmployeeService eserv= new EmployeeServiceImpl(new EmployeeDAOImpl());
	private static ManagerService mserv = new ManagerServiceImpl(new ManagerDAOImpl());
	private static RequestService rserv = new RequestServiceImpl(new RequestDAOImpl());
	private static Logger log = Logger.getLogger(RequestHelper.class);
	private static ObjectMapper om = new ObjectMapper();
	

	public static void processError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if something goes wrong, redirect the user to a custom 404 error page
		request.getRequestDispatcher("error.html").forward(request, response);
	        /*
		 * Remember that the forward() method does NOT produce a new request,
		 * it just forwards it to a new resource, and we also maintain the URL
		*/
	}
	
	// This method will process a post request, so we can't capture parameters from the request like we would in a GET request
		public static void processLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
			// we need to capture the user input from the request BODY 
			BufferedReader reader = request.getReader();
			StringBuilder s = new StringBuilder(); // username=bob&password=pass -> we need to extract bob and pass, but first we transform to string
			
			// transfer everything over to the string builder FROM te buffered reader
			String line = reader.readLine();
			
			while(line!= null) {
				s.append(line);
				line = reader.readLine();  //  req body looks like this: username=bob&password=secret
			}
			
			String body = s.toString(); 
			String [] sepByAmp = body.split("&"); // separate username=bob&password=pass -> [username=bob, password=pass]
			
			List<String> values = new ArrayList<String>();
			
			for (String pair : sepByAmp) { // each element in array looks like this
				values.add(pair.substring(pair.indexOf("=") + 1)); // trim each String element in the array to just value -> [bob, pass]
			}
			
			// capture the actual username and password values
			String username = values.get(0); // bob
			String password = values.get(1); // pass
			
			System.out.println("User attempted to login with username" + username);
			
			// call the confirmLogin() method and fetch the actual User object from the DB
			User u = userv.login(username, password);
			
			// return the user found and show the object in the browser
			if (u != null) {
				// Utilize JwtService to create a JSON web token with user information inside to send with response
				String jwt = jwtService.createJwt(u);
				response.addHeader("X-Auth-Token", "Bearer " + jwt); 
				
				// grab the session & add the user to the session
				HttpSession session = request.getSession();
				session.setAttribute("user", u);
				
				// print the logged in user to the screen
				PrintWriter out = response.getWriter();
				response.setContentType("application/json");
				
				// convert the object with the object mapper
				out.println(om.writeValueAsString(u));
				
				// log it!
				System.out.println("The user " + username + " has logged in.");
			} else {
				// if the returned object is null, return No Content status (successfull request, but no user found in DB).
				response.setStatus(204); 
			}
			
		}
	
	public static void processAllEmployees(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("In Request Helper getting all Employees....");
		// 1. set the content type to return JSON to the browser
		resp.setContentType("application/json");
		
		// 2. get a list of all users in the database
		List<Employee> allEmployee = eserv.findAllEmployees();
		
		// 3. turn that list of java objects into a JSON string (using Jackson)
		String json = om.writeValueAsString(allEmployee);
		
		// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
		PrintWriter pw = resp.getWriter();
		pw.println(json);
		log.info("Leaving RequestHelper");
	}

	public static void processUserBySearchParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("In RequestHelper. Searching by param");
		BufferedReader br = req.getReader();
		StringBuilder sb = new StringBuilder();
		
		//We are just transferring our reader data into our stringBuilder object, line by line.
		String line = br.readLine();
		while(line != null) {
			sb.append(line);
			line = br.readLine();
		}
		
		String body = sb.toString();
		// here I will extract all of the unneeded param symbols from the body
		// in request body:
		// username=bob&password=123&firstname=bob&....
		// so just removing & marks!
		log.info("Employee search with this info: " + body);
		String[] sepByAmp = body.split("&");
		List<String> values = new ArrayList<String>();
		for(String pair : sepByAmp) { // each element in array look like key-value pairs
			values.add(pair.substring(pair.indexOf("=")+1)); // trim each String element in the array to just value -> bob, 123, bob, etc...
		}
		// determine what type of search we are doing.
		if(body.startsWith("id")) {
			log.info("In RequestHelper. Getting user by id....");
			// 1. set the content type to return JSON to the browser
			resp.setContentType("application/json");
			
			// 2. get employee from database
			int id = Integer.parseInt(values.get(0));
			Employee employee = eserv.findEmployeeById(id);
			// 3. turn that list of java objects into a JSON string (using Jackson)
			String json = om.writeValueAsString(employee);
			
			// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
			PrintWriter pw = resp.getWriter();
			pw.println(json);
			log.info("Leaving RequestHelper");
		} else if (body.startsWith("firstname")) {
			log.info("In RequestHelper getting Employee by first name...");
			// 1. set the content type to return JSON to the browser
			resp.setContentType("application/json");
			
			// 2. get a list of all users in the database
			String firstName = values.get(0);
			Employee employee = eserv.findEmployeeByUserName(firstName);
			// 3. turn that list of java objects into a JSON string (using Jackson)
			String json = om.writeValueAsString(employee);
			
			// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
			PrintWriter pw = resp.getWriter();
			pw.println(json);
			log.info("Leaving RequestHelper");
		}
		
	}

	public static void processRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("inside of RequestHelper...processRegistration...");
		BufferedReader reader = request.getReader();
		StringBuilder s = new StringBuilder();

		// we are just transferring our Reader data to our StringBuilder, line by line
		String line = reader.readLine();
		while (line != null) {
			s.append(line);
			line = reader.readLine();
		}

		String body = s.toString(); 
		String [] sepByAmp = body.split("&"); // separate username=bob&password=pass -> [username=bob, password=pass]
		
		List<String> values = new ArrayList<String>();
		
		for (String pair : sepByAmp) { // each element in array looks like this
			values.add(pair.substring(pair.indexOf("=") + 1)); // trim each String element in the array to just value -> [bob, pass]
		}
		log.info("User attempted to register with information:\n " + body);
		// capture the actual username and password values
		String username = values.get(0); // bob
		String password = values.get(1); // pass
		String firstname = values.get(2);
		String lastname = values.get(3);
		String email = values.get(4);
		
		Employee e = new Employee(username, password, firstname, lastname, email);
		int targetId = eserv.register(e);

		if (targetId != 0) {
			PrintWriter pw = response.getWriter();
			e.setEmployeeId(targetId);
			log.info("New user: " + e);
			String json = om.writeValueAsString(e);
			pw.println(json);
			System.out.println("JSON:\n" + json);
			
			response.setContentType("application/json");
			response.setStatus(200); // SUCCESSFUL!
			log.info("User has successfully been created.");
		} else {
			response.setContentType("application/json");
			response.setStatus(204); // this means that the connection was successful but no user created!
		}
		log.info("leaving request helper now...");
	}
	
	
	//                     ******   Managers
	
	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */

	public static void processAllManagers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("In Request Helper getting all Mangers....");
		// 1. set the content type to return JSON to the browser
		resp.setContentType("application/json");
		
		// 2. get a list of all users in the database
		List<Manager> allManagers = mserv.findAllManagers();
		
		// 3. turn that list of java objects into a JSON string (using Jackson)
		String json = om.writeValueAsString(allManagers);
		
		// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
		PrintWriter pw = resp.getWriter();
		pw.println(json);
		log.info("Leaving RequestHelper");
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */
	public static void processManagerBySearchParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		log.info("In RequestHelper. Searching Managers by param");
		BufferedReader br = req.getReader();
		StringBuilder sb = new StringBuilder();
		
		//We are just transferring our reader data into our stringBuilder object, line by line.
		String line = br.readLine();
		while(line != null) {
			sb.append(line);
			line = br.readLine();
		}
		
		String body = sb.toString();
		// here I will extract all of the unneeded param symbols from the body
		// in request body:
		// username=bob&password=123&firstname=bob&....
		// so just removing & marks!
		log.info("Manager search with this info: " + body);
		String[] sepByAmp = body.split("&");
		List<String> values = new ArrayList<String>();
		for(String pair : sepByAmp) { // each element in array look like key-value pairs
			values.add(pair.substring(pair.indexOf("=")+1)); // trim each String element in the array to just value -> bob, 123, bob, etc...
		}
		// determine what type of search we are doing.
		if(body.startsWith("id")) {
			log.info("In RequestHelper. Getting Manager by id....");
			// 1. set the content type to return JSON to the browser
			resp.setContentType("application/json");
			
			// 2. get employee from database
			int id = Integer.parseInt(values.get(0));
			Manager manager = mserv.findManagerById(id);
			// 3. turn that list of java objects into a JSON string (using Jackson)
			String json = om.writeValueAsString(manager);
			
			// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
			PrintWriter pw = resp.getWriter();
			pw.println(json);
			log.info("Leaving RequestHelper");
		} else if (body.startsWith("firstname")) {
			log.info("In RequestHelper getting Manager by first name...");
			// 1. set the content type to return JSON to the browser
			resp.setContentType("application/json");
			
			// 2. get a list of all users in the database
			String firstName = values.get(0);
			Manager manager = mserv.findManagerByUserName(firstName);
			// 3. turn that list of java objects into a JSON string (using Jackson)
			String json = om.writeValueAsString(manager);
			
			// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
			PrintWriter pw = resp.getWriter();
			pw.println(json);
			log.info("Leaving RequestHelper");
		}
		
	}

	public static void processAllRequests(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		log.info("In Request Helper getting all Requests....");
		// 1. set the content type to return JSON to the browser
		resp.setContentType("application/json");
		
		// 2. get a list of all users in the database
		List<Request> allRequests = rserv.finAllRequests();
		
		// 3. turn that list of java objects into a JSON string (using Jackson)
		String json = om.writeValueAsString(allRequests);
		
		// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
		PrintWriter pw = resp.getWriter();
		pw.println(json);
		log.info("Leaving RequestHelper");
		
	}

	public static void processRequestsBySearchParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		log.info("In RequestHelper. Searching Managers by param");
		BufferedReader br = req.getReader();
		StringBuilder sb = new StringBuilder();
		
		//We are just transferring our reader data into our stringBuilder object, line by line.
		String line = br.readLine();
		while(line != null) {
			sb.append(line);
			line = br.readLine();
		}
		
		String body = sb.toString();
		// here I will extract all of the unneeded param symbols from the body
		// in request body:
		// username=bob&password=123&firstname=bob&....
		// so just removing & marks!
		log.info("Manager search with this info: " + body);
		String[] sepByAmp = body.split("&");
		List<String> values = new ArrayList<String>();
		for(String pair : sepByAmp) { // each element in array look like key-value pairs
			values.add(pair.substring(pair.indexOf("=")+1)); // trim each String element in the array to just value -> bob, 123, bob, etc...
		}
		// determine what type of search we are doing.
		if(body.startsWith("id")) {
			log.info("In RequestHelper. Getting Manager by id....");
			// 1. set the content type to return JSON to the browser
			resp.setContentType("application/json");
			
			// 2. get employee from database
			int id = Integer.parseInt(values.get(0));
			Manager manager = mserv.findManagerById(id);
			// 3. turn that list of java objects into a JSON string (using Jackson)
			String json = om.writeValueAsString(manager);
			
			// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
			PrintWriter pw = resp.getWriter();
			pw.println(json);
			log.info("Leaving RequestHelper");
		} else if (body.startsWith("firstname")) {
			log.info("In RequestHelper getting Manager by first name...");
			// 1. set the content type to return JSON to the browser
			resp.setContentType("application/json");
			
			// 2. get a list of all users in the database
			String firstName = values.get(0);
			Manager manager = mserv.findManagerByUserName(firstName);
			// 3. turn that list of java objects into a JSON string (using Jackson)
			String json = om.writeValueAsString(manager);
			
			// 4. use a PrintWriter to write the objects to the response body which will be seen in the browser
			PrintWriter pw = resp.getWriter();
			pw.println(json);
			log.info("Leaving RequestHelper");
		}
	}

	public static void submitRequest(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}
}



















// stuff

