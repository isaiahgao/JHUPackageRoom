package work.packageroom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * this file is a trainwreck. it's meant to work, not look good.
 * dont question how it works, it just does.
 * @author Isaiah
 * 
 * Use Instructions:
 * 1. Copy/paste entire package room page(s) you want into [raw.config]
 * 2. Run the program
 * 3. Find the output in [out.txt]
 * 
 * Update Format:
 * 1. Edit the following files:
 *    • General format: [format.txt]
 *    • Format for when orgs only have old packages: [format2.txt]
 *    • Format for the old packages: [format_prev.txt]
 *
 */
public class Main {
    
	private static final String NL = System.lineSeparator();
    public static Map<String, String> orgEmails = new HashMap<>();
    public static Map<String, String> orgNames = new HashMap<>();
    public static Map<String, List<String>> altEmails = new HashMap<>();
    public static String emailMsg = "", oldMsg = "", emailOldOnlyMsg = "";
    
    private static final ObjectMapper JSON = new ObjectMapper();
    
    public static void main(String[] args) {
    	String[] files = {
    		"alts.txt",
    		"format.txt",
    		"format2.txt",
    		"format_prev.txt",
    		"names.txt",
    		"orgs.txt",
    		"out.txt",
    		"raw.config"
    	};
    	for (String str : files)
    		createIfNonexistent(str);
    	
        loadEmailMsg();
        loadOrgEmails();
        loadOrgNames();
        loadAltEmails();
        process();
    }
    
    private static void createIfNonexistent(String file) {
    	File _file = new File(file);
    	if (!_file.exists())
			try {
				_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    
    private static void loadEmailMsg() {
    	try {
	    	a("emailMsg", "format.txt");
	    	a("oldMsg", "format_prev.txt");
	    	a("emailOldOnlyMsg", "format2.txt");
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
    
    private static void a(String field, String file) throws Exception {
    	String s = "";

        File msg = new File(file);
        Scanner sc = new Scanner(msg);
        while (sc.hasNextLine()) {
        	if (!s.isEmpty())
        		s += System.lineSeparator();
            s += sc.nextLine();
        }
        sc.close();
        
        java.lang.reflect.Field f = Main.class.getField(field);
        f.set(null, s);
    }
    
    private static void loadOrgNames() {
        File msg = new File("names.txt");
        if (!msg.exists()) {
            try {
                msg.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        try {
            Scanner sc = new Scanner(msg);
            while (sc.hasNextLine()) {
                String[] arr = sc.nextLine().split("\t");
                orgNames.put(arr[0].toLowerCase().trim(), arr[1].trim().toLowerCase());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void loadOrgEmails() {
        File msg = new File("orgs.txt");
        if (!msg.exists()) {
            return;
        }
        
        try {
            Scanner sc = new Scanner(msg);
            while (sc.hasNextLine()) {
                String[] arr = sc.nextLine().split("\t");
                orgEmails.put(arr[0].toLowerCase().trim(), arr[1].toLowerCase().trim());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void loadAltEmails() {
        File msg = new File("alts.txt");
        if (!msg.exists()) {
            return;
        }
        
        try {
            Scanner sc = new Scanner(msg);
            while (sc.hasNextLine()) {
                String[] arr = sc.nextLine().split("\t");
                List<String> list = altEmails.get(arr[0].trim().toLowerCase());
                if (list == null)
                    altEmails.put(arr[0].trim().toLowerCase(), list = new LinkedList<>());
                for (int j = 1; j < arr.length; j++)
                    list.add(arr[j].trim().toLowerCase());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void process() {
        List<String> text = new LinkedList<>();
        File msg = new File("raw.config");
        try {
            Scanner sc = new Scanner(msg);
            while (sc.hasNextLine()) {
                text.add(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // get day 1 week from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        String pickupDate = DayOfWeek.values()[(cal.get(Calendar.DAY_OF_WEEK) + 5) % 7].toString() + " "
                    + capitalizeFirst(Month.of(cal.get(Calendar.MONTH) + 1).toString()) + " "
                    + cal.get(Calendar.DATE) + ", "
                    + cal.get(Calendar.YEAR);
        
        Map<String, List<PackageData>> etp = new HashMap<>();
        boolean saveMap = false, saveNames = false;
        Scanner sc = new Scanner(System.in);
        for (String s : text) {
            String[] arr = s.split("\t");
            if (arr.length == 0)
            	continue;
            
            if (!arr[0].matches("(.+?(?=(?![0-9])))-([0-9]*)"))
            	// isn't in the format ##-##
            	continue;
            
            if (arr.length > 11) {
            	if (arr[11] != null && !arr[11].isEmpty()) {
            		continue;
            	}
            }
            
            String org = arr[1].toLowerCase().trim();
            String date = arr[2];
            String from = arr.length < 4 ? "Unknown" : arr[3];
            
            if (org.toLowerCase().contains("jackie")) {
            	continue;
            }
            
            String email = orgEmails.get(org);
            if (email == null) {
            	// query https://johnshopkins.campuslabs.com/engage/api/discovery/search/organizations?top=10&filter=&query=" + SEARCH + "&skip=0
            	// to try to get email
            	// TODO
            	String groupName = null;
            	
            	try {
	            	String query = "https://johnshopkins.campuslabs.com/engage/api/discovery/search/organizations?top=10&filter=&query=" + org + "&skip=0";
	            	StringBuffer response = IO.getHTTPResponse(query);
	            	JsonNode bigjson = JSON.readTree(response.toString());
	            	JsonNode json = bigjson.get("value").get(0);
	                groupName = json.get("Name").asText();
	                String websiteKey = json.get("WebsiteKey").asText();
	                
	                String query2 = "https://johnshopkins.campuslabs.com/engage/organization/" + websiteKey;
	                StringBuffer response2 = IO.getHTTPResponse(query2);
	                
	                int index = response2.indexOf("\"email\":");
	                if (index < 0)
	                	throw new ArrayIndexOutOfBoundsException();
	                
	                int emailStart = index + 9;
	                int emailEnd = response2.indexOf("\",", emailStart);
	                email = response2.subSequence(emailStart, emailEnd).toString();
	                orgEmails.put(org.toLowerCase().trim(), email);
	                orgNames.put(email, groupName);
	                saveMap = true;
	                saveNames = true;
            	} catch (Exception e) {
            		//e.printStackTrace(System.err);
	                System.out.println("Failed to look up email for: " + arr[1] + " (it's not listed in their main page). :(");
	                System.out.println("What is the email for: " + arr[1] + "?");
	                String nemail = sc.nextLine();
	                String[] split = nemail.split(",");
	                
	                orgEmails.put(org.toLowerCase().trim(), split[0].toLowerCase().trim());
	                List<String> list = new LinkedList<>();
	                for (int j = 1; j < split.length; j++) {
	                    list.add(split[j]);
	                }
	                if (!list.isEmpty())
	                    altEmails.put(split[0], list);
	                email = split[0];
	                saveMap = true;
            	}
            }
            
            String name = orgNames.get(email);
            if (name == null) {
                System.out.println("What is the name of organization with email: " + email + "?");
                name = sc.nextLine().toLowerCase().trim();
                
                orgNames.put(email, name);
                saveNames = true;
            }
            
            PackageData data = new PackageData(arr[0], date, from, arr.length > 7 && arr[7] != null && !arr[7].trim().isEmpty());
            List<PackageData> list = etp.get(email);
            if (list == null)
                etp.put(email, list = new LinkedList<>());
            list.add(data);
        }
        sc.close();
        
        if (saveMap) {
            try {
                FileWriter writer = new FileWriter(new File("orgs.txt"));
                for (Entry<String, String> entry : orgEmails.entrySet()) {
                    writer.write(entry.getKey() + "\t" + entry.getValue() + NL);
                }
                writer.close();
                
                FileWriter writer2 = new FileWriter(new File("alts.txt"));
                for (Entry<String, List<String>> entry : altEmails.entrySet()) {
                    String s = entry.getKey();
                    for (String ss : entry.getValue()) {
                        s = s + "\t" +  ss;
                    }
                    writer2.write(s + NL);
                }
                writer2.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if (saveNames) {
            try {
                FileWriter writer = new FileWriter(new File("names.txt"));
                for (Entry<String, String> entry : orgNames.entrySet()) {
                    writer.write(entry.getKey() + "\t" + entry.getValue() + NL);
                }
                writer.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
            FileWriter writer = new FileWriter(new File("out.txt"));
            
            for (Entry<String, List<PackageData>> entry : etp.entrySet()) {
                String locations = "";
                Set<String> used = new HashSet<>();
                for (PackageData data : entry.getValue()) {
                	String from = data.from.toUpperCase();
                    if (!used.contains(from)) {
                        used.add(from);
                        locations += from + ", ";
                    }
                }
                
                if (locations.endsWith(", ")) {
                    locations = locations.substring(0, locations.length() - 2);
                }
                
                String oldpackageIds = "";
                String packageIds = "";
                int newcount = 0;
                for (PackageData pd : entry.getValue()) {
                	if (pd.isOld) {
                		if (!oldpackageIds.isEmpty()) 
                			oldpackageIds += System.lineSeparator();
                    	oldpackageIds += "  • " + pd.pid;
                	} else {
                    	packageIds += "  • " + pd.pid + System.lineSeparator();
                    	++newcount;
                	}
                }
                
                String body = packageIds.isEmpty() ? emailOldOnlyMsg : emailMsg;
                
                body = body.replace("%ORG%", orgNames.get(entry.getKey()).toUpperCase())
                		.replace("%NUM%", entry.getValue().size() + "")
                		.replace("%LOCATIONS%", locations)
                		.replace("%DELIVERY_DATE%", entry.getValue().get(0).date)
                		.replace("%IDS%", packageIds)
                		.replace("%PICKUP_BY%", pickupDate);
                
                body = body.replace("%PREVIOUS%",
                		!oldpackageIds.isEmpty() ?
                		oldMsg.replace("%IDS%", oldpackageIds + (newcount > 0 ? System.lineSeparator() : ""))
                		: "");
                
                writer.write(entry.getKey() + NL);
                if (altEmails.containsKey(entry.getKey()))
                    for (String s : altEmails.get(entry.getKey()))
                        writer.write(s + NL);
                writer.write("Package(s) - Student Organization Supply Order\r\n");
                writer.write(body);
                writer.write(NL + NL + NL);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static String capitalizeFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

}
