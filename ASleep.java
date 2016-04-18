package main;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetRewindable;
import main.Sparql;

public class ASleep {
	
	private static String stayAwakeAP = ""; // sparql string with APs that should stay awake, used in getRemainingAP()

	private static 	String listUE = ""; // sparql string for the UEs that has already been assigned with AP

	static ArrayList<Node> stayAwakeAPNodes = new ArrayList<Node>();
	
	private static Boolean isAPsleepDone = false;
		
	public static void APsleep(){
		
		// query all the available APs
		String queryAvailableAP = "SELECT  ?UE ?AP\n" +
				"WHERE {\n" +
				"?UE netRes:hasLink ?link.\n" +
				"?link netRes:hasSpeed ?speed;\n" +
				"netRes:hasDestination ?AP\n" +
				"FILTER (?speed >= 3).\n" +
				"} \n" +
				"ORDER BY ?AP"; 
		ResultSetRewindable resultSet = (ResultSetRewindable) Sparql.runQuery(queryAvailableAP);
				
		while(getRemainingAP(resultSet).hasNext() && !isAPsleepDone) {
			resultSet.reset();
		}
		
		System.out.println(stayAwakeAP);
		
	}
	public static ResultSet getRemainingAP(ResultSet resultSet){
		System.out.println("beginning of getRemainingAP");
		findMostFrequentAP(resultSet);

		// be noted that no space is allowed behind "netRes:", otherwise, aparql will die horribly
		if(stayAwakeAP.isEmpty()){
			stayAwakeAP += "?AP = netRes:" + stayAwakeAPNodes.get(stayAwakeAPNodes.size() - 1).getLocalName();
		} else {
			stayAwakeAP += "|| ?AP = netRes:" + stayAwakeAPNodes.get(stayAwakeAPNodes.size() - 1).getLocalName();

		}
		
		System.out.println("stayAwakeAP is :" + stayAwakeAP);

		// query the already assigned UEs
		String queryAlreadyAssignedUE = 
				"SELECT  ?UE ?AP\n" +
				"WHERE {\n" +
				"?UE netRes:hasLink ?link.\n" +
				"?link netRes:hasSpeed ?speed;\n" +
				"netRes:hasDestination ?AP\n" +
				"FILTER (?speed >= 3 && (" +
				stayAwakeAP +
				")).\n" +
				"} \n" +
				"ORDER BY ?AP"; 
		ResultSet resultSetUE = Sparql.runQuery(queryAlreadyAssignedUE);
		
		System.out.println("resultSetUE has next :"+ resultSetUE.hasNext());
		
		listUE = "";
		while(resultSetUE.hasNext()) {
			listUE += " && ?UE != netRes:" + resultSetUE.next().get("UE").asNode().getLocalName();
		}
		System.out.println("list UE is :" + listUE);
		
		String queryRemainingAP = "SELECT  ?UE ?AP\n" +
				"WHERE {\n" +
				"?UE netRes:hasLink ?link.\n" +
				"?link netRes:hasSpeed ?speed;\n" +
				"netRes:hasDestination ?AP\n" +
				"FILTER (?speed >= 3" + 
				listUE + 
				").\n" +
				"} \n" +
				"ORDER BY ?AP"; 
		ResultSet resultSetRemainingAP = Sparql.runQuery(queryRemainingAP);
		
		System.out.println("resultSetRemainingAP's row number is :"+resultSetRemainingAP.getRowNumber());
		
		return resultSetRemainingAP;

	}

	
	public static void findMostFrequentAP(ResultSet tempResultSet){
		System.out.println("beginning of findMostFrequentAP");

		int tempCounter = 1;
		int counter =1;
				
		QuerySolution solution = tempResultSet.next();
		
		Node tempMostFrequentAP = solution.get("AP").asNode();
		Node MostFrequentAP = tempMostFrequentAP;
		
		while(tempResultSet.hasNext()) {
			System.out.println(tempResultSet.getRowNumber() + solution.get("AP").asNode().getLocalName()); 
			
			if(isAlreadyChoosenAP(solution.get("AP").asNode())) {
				System.out.println("if is already choosen AP node");
				solution = tempResultSet.next();
				tempMostFrequentAP = solution.get("AP").asNode();
				continue;
			} else {
				tempMostFrequentAP = solution.get("AP").asNode();
				solution = tempResultSet.next();
				while(solution.get("AP").asNode().sameValueAs(tempMostFrequentAP)) {
					tempCounter ++;
					if(tempResultSet.hasNext()) {
						solution = tempResultSet.next();
					} else {
						break;
					}
				}
				if(tempCounter > counter){
					counter = tempCounter;
					MostFrequentAP = tempMostFrequentAP;
				}
			}
		}
		// find most frequent AP is done
		System.out.println("MostFrequentAP is "+ MostFrequentAP.getLocalName());
		if(stayAwakeAPNodes.contains(MostFrequentAP)) { 	// if all UE has been assigned
			isAPsleepDone = true;
		}
		stayAwakeAPNodes.add(MostFrequentAP);
	}
	
	public static Boolean isAlreadyChoosenAP(Node node) {
		int stayAwakeNodeCounter = 0;
		
		
		while( ! stayAwakeAPNodes.isEmpty() && stayAwakeNodeCounter < stayAwakeAPNodes.size()){ 
			System.out.println("stayAwakeAPNodes is not Empty  " + stayAwakeAPNodes.size()); 

			if(node.sameValueAs(stayAwakeAPNodes.get(stayAwakeNodeCounter))) {
				System.out.println("node in solution is identical with those in alreadyChoosenNode"+stayAwakeAPNodes.get(stayAwakeNodeCounter)); 
				return true;
			}
			stayAwakeNodeCounter ++;
		}
		return false;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		APsleep();
	}

}
