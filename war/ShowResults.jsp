
<%@page import="java.util.HashMap"%>
<%@page import="java.text.BreakIterator"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Set"%>
<%@page import="com.gae.buzzmap.TweetProcessor"%>
<%@page import="com.gae.buzzmap.Tweet"%>
<%@page import="java.util.Map"%>
<%@page import="twitter4j.Status"%>
<%@page import="com.gae.buzzmap.DataMiner"%>
<%@page import="com.gae.buzzmap.SearchKeywords"%>
<%@page import="java.util.List"%>
<%@page import="static com.gae.buzzmap.OfyService.ofy;" %>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Results | BuzzMap</title>
</head>
<body>
	<%
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		String category = request.getParameter("category");
		String stateName = request.getParameter("statename");
		System.out.println("Lat: " + Double.parseDouble(lat) + ", Lng: " + Double.parseDouble(lng) + ", Category: " + category + ", Statename: " + stateName);
		
		Map<String, Integer> sentimentMap = new HashMap<String, Integer>();
		File textFile = new File("./resources/AFINN-111.txt");
		FileReader reader = new FileReader(textFile);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line = null;
		while((line = bufferedReader.readLine())!=null){
			int i = 0;
			int length = line.length();
			String word = null;
			int score = 0;
			for(;i < length; i++){
				System.out.println("i = " + i + "; Char is " + line.charAt(i));
				if (line.charAt(i) == '\t') {
					word = line.substring(0, i-1);
					break;
				}
			}
			for(;i < length;i++){
				System.out.println("i = " + i + "; Char is " + line.charAt(i));
				if (line.charAt(i) != '\t') {
					score = Integer.parseInt(line.substring(i));
					break;
				}
			}
			sentimentMap.put(word, score);
		}
		bufferedReader.close();
		reader.close();
		
		out.println("<h2>State name: " + stateName + "</h1>");
		out.println("<h2>Category: " + category + "</h2><hr />");
		TweetProcessor processor = new TweetProcessor();
		Map<String, List<Tweet>> tweetsMap = processor.getTweets(category.toLowerCase(), stateName, Double.parseDouble(lat), Double.parseDouble(lng));
		Set<String> keySet = tweetsMap.keySet();
		int i = 1;
		for(String key : keySet){
			
			int sentiscore = 0;
			if(sentimentMap.containsKey(key.toLowerCase())){
				sentiscore = sentimentMap.get(key.toLowerCase());
			}
			List<Tweet> tweets = tweetsMap.get(key);
			if(sentiscore == 0){
				out.println("<h3>" + i + ": " + key + "(Neutral)</h3>");
			}
			else if(sentiscore > 0){
				if(sentiscore == 1){
					out.println("<h3>" + i + ": " + key + "(Positive)</h3>");
				}
				if(sentiscore == 2){
					out.println("<h3>" + i + ": " + key + "(Excited)</h3>");
				}
				if(sentiscore == 3){
					out.println("<h3>" + i + ": " + key + "(Very Excited)</h3>");
				}
				if(sentiscore == 4){
					out.println("<h3>" + i + ": " + key + "(Super Excited)</h3>");
				}
				if(sentiscore == 5){
					out.println("<h3>" + i + ": " + key + "(Heven)</h3>");
				}
			}
			else if(sentiscore < 0){
				if(sentiscore == -1){
					out.println("<h3>" + i + ": " + key + "(Negative)</h3>");
				}
				if(sentiscore == -2){
					out.println("<h3>" + i + ": " + key + "(Bad)</h3>");
				}
				if(sentiscore == -3){
					out.println("<h3>" + i + ": " + key + "(Sad)</h3>");
				}
				if(sentiscore == -4){
					out.println("<h3>" + i + ": " + key + "(Very Sad)</h3>");
				}
				if(sentiscore == -5){
					out.println("<h3>" + i + ": " + key + "(Hell)</h3>");
				}
			}
			else{
				out.println("<h3>" + i + ": " + key + "(Neutral)</h3>");
			}
			
			out.println("********************************************************");
			for(Tweet tweet : tweets){
				out.println("<p><img src=\"" + tweet.getProfileImageUrl() + "\" width=\"48\" height=\"48\"></img></p><p>User name: " + tweet.get_from_user_name() + "</p>");
				out.println("<p>User Id: " + tweet.get_from_user() + "</p>");
				out.println("<p>Content: " + tweet.getText() + "</p>");
				out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			}
			
			i++;
		}
		
		
		
	%>
</body>
</html>