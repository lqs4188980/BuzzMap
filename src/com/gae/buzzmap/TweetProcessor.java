package com.gae.buzzmap;

import static com.gae.buzzmap.OfyService.ofy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter4j.TwitterException;

public class TweetProcessor {
	
	private Map<String, Integer> keywordsCounter(List<Tweet> tweets){
		Map<String, Integer> keywordCountMap = new HashMap<String, Integer>();
		for(Tweet tweet : tweets){
			Set<String> keySet = keywordCountMap.keySet();
			List<String> keywordsList = tweet.getKeywords();
			for(String keyword : keywordsList){
				if (keySet.contains(keyword)) {
					Integer count = keywordCountMap.get(keyword);
					count++;
					keywordCountMap.remove(keyword);
					keywordCountMap.put(keyword, count);
				}
				else {
					keywordCountMap.put(keyword, 1);
				}
			}
		}
		return keywordCountMap;
	}
	
	private Map<String, Integer> getKeywords(List<Tweet> tweets, String category){
		System.out.println("#TweetProcessor: Reaching getKeywords()");
		Map<String, Integer> keywordsMap = new HashMap<String, Integer>();
		for(Tweet tweet : tweets){
			String text = tweet.getText();
			List<String> keywordList = keywordParser(text,category);
			tweet.setKeywords(keywordList);
			updateKeywordsMap(keywordsMap, keywordList);
			ofy().save().entity(tweet);
		}
		return keywordsMap;
	}
	private void updateKeywordsMap(Map<String, Integer> keywordsMap, List<String> keywordsList){
		System.out.println("#TweetProcessor: updateKeywordsMap()");
		Set<String> keySet = keywordsMap.keySet();
		for(String keyword : keywordsList){
			
			if (keySet.contains(keyword)) {
				Integer count = keywordsMap.get(keyword);
				keywordsMap.remove(keyword);
				count++;
				keywordsMap.put(keyword, count);
			}
			else {
				keywordsMap.put(keyword, 1);
			}
		}
	}
	private List<String> keywordParser(String text, String category){
		System.out.println("#TweetProcessor: Reaching keywordParser()");
		String regexString = regexConstructor(category);
		String[] wordsBase = {"fan","you","can","NOT","You","Can","team","game","don","check","Check","move","great","Yahoo","being","though","even","NBC","TV","report","should","their","first","look","last","interview","pro","Center","Have","thing","name","talk","By","photo","No","no","On","on","others","music","only","says","will","From","This","ago","key","get","often","options","dates","finally","some","center","info","So","know","name","also","tonight","Today","class","anyway","Much","old","article","radio","blog","games","today","0","9","8","7","6","5","4","3","2","1","sportsnews","season","via","new","Is","one","back","piece","if","If","About","about","With","want","Ever","take","never","First","they","think","Get","tell","before","need","say","Former","former","ruins","night","play","them","can","okay","City","if","every","NEWS","news","News","called","story","ever","read","best","OH","FUCKING","How","T","It","z","y","x","w","v","t","s","r","q","p","o","n","m","l","k","j","h","g","f","e","d","c","b","soon","than","i","meet","u","just","Go","go","For","now","our","We","we","There","going","Our","Into","into","U","Thank","else","until","fuck","shit","us","He","When","when","most","all","much","would","what","could","not","from","my","has","Yor","And","goes","nothing","but","who","Your","your","she","him","he","her","his","free","after","A","Shop","shop","shopping","Shopping","you","will","be","sport","Sport","sports","Sports","Down","Up","down","up","The","My","she","he","why","it","how","am","are","is","were","was","day","Ok","do","me","try","again","or","OK","off","ok","on","its","like","of","have","this","these","there","that","What","In","isn","s","next","Out","year","out","New","post",""," ","the","I","is","to","for","as","so","RT","by","in","at","and","an","a","here","there","with","therefore","yes","no"};
		List<String> keywordList = new LinkedList<String>();
		List<String> nonKeywords = new LinkedList<String>();
		for(String temp : wordsBase){
			nonKeywords.add(temp);
		}
		String result = text.replaceAll("@|:|,|!||\\.|`|~|#|$|%|&|(|)|\"|/|<|>|\\{|\\}|[|]|_", "");
		result = result.replaceAll(";", "");
		result = result.replaceAll("\\.", "");
		result = result.replaceAll("/", "");
		String[] cache = result.split("\\W");
		for(String temp : cache){
			if ((!nonKeywords.contains(temp))&&(!temp.contains("http"))&&(!temp.startsWith("1"))&&(!temp.startsWith("2"))&&(!temp.matches(regexString))) {
				keywordList.add(temp);
			}
		}
		
		return keywordList;
	}
	
	private boolean dataChecker(String category, String statename) {
		System.out.println("#TweetProcessor: dataChecker()");
		boolean isDataExist = false;
		List<Tweet> result = ofy().load().type(Tweet.class).filter("category ==", category).filter("state_name ==", statename).list();
		if (result.size() != 0) {
			isDataExist = true;
		}
		return isDataExist;
	}
	
	private Map<String, List<Tweet>> prepareData(List<String> sortedKeywordsList, List<Tweet> loadedTweets) {
		System.out.println("#TweetProcessor: prepareData()");
		Map<String, List<Tweet>> formattedData = new HashMap<String, List<Tweet>>();
		List<String> addedTweets = new LinkedList<String>();
		int keywordNum = 10;
		int keywordListSize = sortedKeywordsList.size();
		for (int i = 0; i < keywordNum; i++) {
			String keyword = sortedKeywordsList.get(i);
			List<Tweet> tweets = new LinkedList<Tweet>();
			int j = 0;
			for(Tweet tweet : loadedTweets){
				List<String> keywordList = tweet.getKeywords();
				if (keywordList.contains(keyword)&&(!addedTweets.contains(tweet.getText()))) {
					tweets.add(tweet);
					addedTweets.add(tweet.getText());
					j++;
					if (j >= 5) {
						break;
					}
				}
			}
			if(tweets.size() > 1){
				formattedData.put(keyword, tweets);
			}
			else {
				if (keywordNum < keywordListSize) {
					keywordNum++;
				}
				else {
					break;
				}
			}
		}
		return formattedData;
	}
	
	private List<Tweet> loadData(String category, String statename) {
		System.out.println("#TweetProcessor: loadData()");
		List<Tweet> loadedTweets = ofy().load().type(Tweet.class).filter("category ==", category).filter("state_name ==", statename).list();
		return loadedTweets;
	}
	
	
	
	private List<String> sortKeywords(Map<String, Integer> keywordsMap){
		System.out.println("#TweetProcessor: sortKeywordsMap()");
		List<String> keywords = new LinkedList<String>();
		while(!keywordsMap.isEmpty()){
			Set<String> keySet = keywordsMap.keySet();
			String significantWord = null;
			Integer max = 0;
			for(String key : keySet){
				Integer currentCount = keywordsMap.get(key);
				if (currentCount > max) {
					max = currentCount;
					significantWord = key;
				}
			}
			keywords.add(significantWord);
			keywordsMap.remove(significantWord);
		}
		// For test use
		
		System.out.println("*****************************Test for keywords*****************************");
		for(String key : keywords){
			System.out.println(key + ": " + keywordsMap.get(key));
			
		}
		return keywords;
	}
	
	private String regexConstructor(String keyword){
		int length = keyword.length();
		String matchRegex = "";
		for(int i = 0; i < length; i++){
			String alphaChar = keyword.substring(i, i + 1);
			matchRegex = matchRegex + "[" + alphaChar.toUpperCase() + alphaChar.toLowerCase() + "]";
		}
		return matchRegex;
	}
	
	public Map<String, List<Tweet>> getTweets(String category, String statename, double lat, double lng) throws TwitterException {
		System.out.println("#TweetProcessor: Reaching getTweets()");
		Map<String, List<Tweet>> formattedData = new HashMap<String, List<Tweet>>();
		// Check if data exist
		if (dataChecker(category, statename)) {
			// Load data
			List<Tweet> loadedTweets = loadData(category, statename);
			// Process data
			Map<String, Integer> keywordsMap = keywordsCounter(loadedTweets);
			List<String> sortedKeywords = sortKeywords(keywordsMap);
			formattedData = prepareData(sortedKeywords, loadedTweets);
			
			// Return data
			return formattedData;
		}
		else {
			// Call dataminer
			DataMiner dataMiner = new DataMiner();
			// getTweets
			SearchKeywords searchKeywords = new SearchKeywords(category, statename);
			List<Tweet> tweets = dataMiner.packageTweets(dataMiner.getTweets(searchKeywords, lat, lng));
			// Call keyword processor
			Map<String, Integer> keywordsMap = getKeywords(tweets,category);
			List<String> sortedKeywords = sortKeywords(keywordsMap);
			formattedData = prepareData(sortedKeywords, tweets);
			// return data
			return formattedData;
		}
	}
}
