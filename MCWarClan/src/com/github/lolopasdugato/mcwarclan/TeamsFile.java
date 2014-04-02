package com.github.lolopasdugato.mcwarclan;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TeamsFile {
	
	public TeamsFile() {
		// TODO Auto-generated constructor stub
	}
	
	private boolean physicalSave(String fileName, Document doc){
		try{
			Source source = new DOMSource(doc);
			File file  = new File(fileName);
			if(file.exists())
				file.delete();
			Result res = new StreamResult(fileName);
			
			TransformerFactory facto = TransformerFactory.newInstance();
			Transformer transformer = facto.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			
			transformer.transform(source, res);
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save(TeamContainer t){
		
		try{
			DocumentBuilderFactory facto = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = facto.newDocumentBuilder();
			Document doc = builder.newDocument();
			doc.setXmlVersion("1.0");
			doc.setXmlStandalone(true);
		
			Element root = doc.createElement("Teams");
			
			for(int i = 0; i < t.get_teamArray().size(); i++){
				Element team = doc.createElement("Team");
				root.appendChild(team);
				
				Element teamName = doc.createElement("Name");
				teamName.setTextContent(t.get_teamArray().get(i).get_name());
				team.appendChild(teamName);
				
				Element teamColor = doc.createElement("Color");
				teamColor.setTextContent(t.get_teamArray().get(i).get_color().get_colorName());
				team.appendChild(teamColor);
				
				Element players = doc.createElement("Players");
				team.appendChild(players);
				
				for(int j = 0; j < t.get_teamArray().get(i).get_team().size(); j++){
					// t.get_teamArray().get(i).get_team().get(j);
					Element playerName = doc.createElement("Player-name");
					playerName.setTextContent(t.get_teamArray().get(i).get_team().get(j).getName());
					players.appendChild(playerName);
				}
				
			}
			doc.appendChild(root);
			return physicalSave("plugins/Teams_DO_NOT_TOUCH.xml", doc);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public TeamContainer load(Server server){
		TeamContainer t = new TeamContainer(TeamContainer.MAXTEAMSIZE);
		Document doc;
		
		String name = null;
		String color = null;
		ArrayList<String> playerNames = new ArrayList<String>();
		
		try{
			DocumentBuilderFactory facto = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = facto.newDocumentBuilder();
			File file = new File("plugins/Teams_DO_NOT_TOUCH.xml");
			if(!file.exists()){
				return null;
			}
			doc = builder.parse(file);
		}
		catch(ParserConfigurationException pce){
			System.out.println("Error during DOM parser configuration");
			System.out.println("while calling facto.newDocumentBuilder();");
			return null;
		}
		catch(SAXException se){
			System.out.println("Error while parsing document");
			System.out.println("while calling builder.parse(file)");
			return null;
		}
		catch(IOException ioe){
			System.out.println("I/O Error");
			System.out.println("while calling builder.parse(file)");
			return null;
		}
		
		Element root = doc.getDocumentElement();
		NodeList list = root.getElementsByTagName("Team");
		
		for(int i = 0; i < list.getLength(); i++){
			Node child = list.item(i).getFirstChild();
			child = child.getNextSibling();
			if(child.getNodeName().equals("Name")){
				name = child.getTextContent();
				child = child.getNextSibling().getNextSibling();
			}
			if(child.getNodeName().equals("Color")){
				System.out.println("Enter color test");
				color = child.getTextContent();
				child = child.getNextSibling().getNextSibling();
			}
			if(child.getNodeName().equals("Players")){
				NodeList playerList = child.getChildNodes();
				for(int j = 0; j < playerList.getLength(); j++){
					Node playerChild = playerList.item(j);
					if(playerChild.getNodeName().equals("Player-name")){
						playerNames.add(playerChild.getTextContent());
						playerChild = playerChild.getNextSibling().getNextSibling();
					}
				}
			}
			if(name == null || color == null){
				System.out.println("Error while reading file");
				return null;
			}
			else{
				System.out.println("Saving current team !");
				Team teamToAdd = new Team(new Color(color), name, Team.DEFAULTTEAMSIZE, t);
				for(int j = 0; j < playerNames.size(); j++){
					teamToAdd.addTeamMate(server.getOfflinePlayer(playerNames.get(j)));
				}
				t.addTeam(teamToAdd);
				System.out.println(" ");
			}
		}
		
		return t;
	}
}
