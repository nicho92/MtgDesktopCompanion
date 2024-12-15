package org.beta;

import static spark.Spark.get;

public class TestJetty {

	public static void main(String[] args) {
		
	//	port(8080);
	
		get("/hello/:id", (request, response) -> {
				System.out.println(request.pathInfo());
				System.out.println(request.matchedPath());
				System.out.println(request.queryString());
				System.out.println(request.servletPath());
				
				return "hello";
		});
		
		
		
	}
	
	
}
