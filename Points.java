//package com.dataonfocus.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Points {

	public class Point{
		int id;
		int clusterId = -1;
		ArrayList<Double> point;   
		Point(ArrayList<Double> defaultPoint){
			point = new ArrayList<Double>(defaultPoint);
		}
	}
	
    private HashMap<String,Integer>  index = new HashMap<String,Integer>() ;
    private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Double> defaultPoint = new ArrayList<Double>();
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<String> processedList = new ArrayList<String>();
	 
	
	public Points(HashMap<String,Integer> index){
    	this.index =index;
    	for(int i=0;i<index.size();i++){
    		defaultPoint.add(0.0);
    	}
    }
    
    public void generatePoints(String s){
 
    	int indexedWordCount = 0;
    	Point tweet = new Point(defaultPoint);
    	String[] split = s.toLowerCase().replaceAll("(https(\\S)+)|(http(\\S)+ )|(( @|\\A@)(\\S)+)|'", "").split("[^A-Za-z0-9]");
    	for(String word : split){
    		if(index.containsKey(word)){
	    		int temp = index.get(word);
	    		if(temp>=0){
	    			indexedWordCount++;
	    			tweet.point.set(temp, tweet.point.get(temp)+1);
	    		}
    		}
    	}
    	
    	if(indexedWordCount >=5){
    		list.add(s);
    		tweet.id=list.size()-1;
    		points.add(tweet);
    	}
    	
    }
    
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public ArrayList<Double> getDefault() {
		return defaultPoint;
	}
	public ArrayList<String> getTweetsList() {
		return list;
	}
	
}