/*
 * DistanceCalculator.java
 * 
 * A program to calculate flight-times between cities in the United States, extended to include the actual route between cities.
 * 
 * @author alexlacey
 * @version 20171112
 */

import java.util.*;
import java.io.*;

public class DistanceCalculator {
	
	public static void main (String args[]) {
		String fileName = getFileName();
		Map <String,List<Pathextra>> adj_list = readPathsFromFile(fileName);
		displayAdjacencyList(adj_list);
		while (true) {
			String startingCity = getStartingCity();
			Map <String, Pathextra> shortestDistances = findDistances(startingCity, adj_list);
			displayShortest(startingCity, shortestDistances);
		}
	}
	
	private static String getFileName() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter a file name with paths: ");
		return in.nextLine();
	}
	
	public static Map<String, List<Pathextra>> readPathsFromFile (String fileName) {
		Map <String, List<Pathextra>> adj_list = new HashMap<String, List<Pathextra>>();
		List<Pathextra> list = new LinkedList<Pathextra>();
		try {
			File file = new File(fileName);
			Scanner inFile = new Scanner(file);
			while (inFile.hasNext()){
			    String line = inFile.nextLine();   
			    String[] tokens = line.split(",");
			    String cityA = tokens[0];
			    String cityB = tokens[1];
			    double distance = Double.parseDouble(tokens[2]);
			    // first city -> second city
			    if (adj_list.containsKey(cityA)) {
			    	Pathextra path = new Pathextra(cityB, distance, null);
			    	list = adj_list.get(cityA);
			    	list.add(path);
			    	adj_list.put(cityA, list);
			    } else {
			    	Pathextra path = new Pathextra(cityB, distance, null);
			    	list = new LinkedList<Pathextra>();
			    	list.add(path);
			    	adj_list.put(cityA, list);
			    }
			    // second city -> first city
			    if (adj_list.containsKey(cityB)) {
			    	Pathextra path = new Pathextra(cityA, distance, null);
			    	list = adj_list.get(cityB);
			    	list.add(path);
			    	adj_list.put(cityB, list);
			    } else {
			    	Pathextra path = new Pathextra(cityA, distance, null);
			    	list = new LinkedList<Pathextra>();
			    	list.add(path);
			    	adj_list.put(cityB, list);
			    }			    
			}
			inFile.close();
		} catch  (IOException e) {
			System.out.println("Error: " + e);
		}
		return adj_list;
	}
	
	public static void displayAdjacencyList (Map<String, List<Pathextra>> map){
		System.out.println();
		System.out.printf("%-20s%s\n", "Start City", "Paths");
		System.out.printf("%-20s%s\n", "----------", "-----");
		for (String city : map.keySet()){
			System.out.printf("%-20s", city);
			for (Pathextra path : map.get(city)){
				System.out.print("(" + path.getEndpoint() + ":" + path.getCost() + ") ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static Map<String, Pathextra> findDistances(String start, Map<String,List<Pathextra>> adj_list) {
		Map<String, Pathextra> shortestDistances = new HashMap<String, Pathextra>();
		Pathextra initialPath = new Pathextra(start, 0.0, new ArrayList<String>());
		PriorityQueue<Pathextra> queue = new PriorityQueue<Pathextra>();
		queue.add(initialPath);
		while (!queue.isEmpty()) {
			Pathextra current = queue.remove();
			if (!shortestDistances.containsKey((current.getEndpoint()))) {
				Double d = current.getCost();
				String dest = current.getEndpoint();
				shortestDistances.put(dest, current);
				for (Pathextra n : adj_list.get(dest)) {
					ArrayList<String> subRoute = new ArrayList<String>();
					subRoute = current.getRoute();
					if (!subRoute.contains(current.getEndpoint())) {
						subRoute.add(current.getEndpoint());
					}
					Pathextra next = new Pathextra(n.getEndpoint(), d + n.getCost(), subRoute);
					queue.add(next);
				}
			}
		}
		return shortestDistances;
	}
	
	private static String getStartingCity() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter a starting city (empty line to quit): ");
		String startingCity = in.nextLine();
		if (startingCity == "") {
			System.out.println("Goodbye!");
			System.exit(0);
		}
		return startingCity;
	}
		
	public static void displayShortest (String start, Map<String, Pathextra>shortest) {
		System.out.println("Distances from " + start + " to each city:");
		System.out.printf("%-20s%-10s%s\n", "Dest. City", "Distance", "Route");
		System.out.printf("%-20s%-10s%s\n", "----------", "--------", "-----");
		for (String city : shortest.keySet()) {
			System.out.printf("%-20s%-10s%s\n", city, shortest.get(city).getCost(), shortest.get(city).getRoute());
		}
		System.out.println();
	}
	
}