package org.beta;

import java.io.IOException;

import io.magicthegathering.javasdk.api.SetAPI;

public class QueryTester {


	

	public static void main(String[] args) throws IOException {
		SetAPI.getAllSets().forEach(s->{
			
			System.out.println(s.getReleaseDate() + " "+ s.getName());
			
		});
	}
}
