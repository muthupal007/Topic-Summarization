//package com.dataonfocus.clustering;

import java.util.ArrayList;
import java.util.Random;

public class Clusters {
	
	private ArrayList<ArrayList<Double>> centroids = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<Double>> prevCentroids = new ArrayList<ArrayList<Double>>();
	//private ArrayList<ArrayList<Double>> defaultCentroids = new ArrayList<ArrayList<Double>>();
	//private ArrayList<Double> centroids = new ArrayList<Double>();
	private Points p ;
	private boolean flag = true;
	
	public Clusters(Points p,int num){
		this.p=p;
		Random r = new Random();
		if(num<=p.getPoints().size()){
			for(int i =0;i<num;i++){
			centroids.add(p.getPoints().get((int) (p.getPoints().size()*r.nextDouble())).point);
			prevCentroids.add(p.getDefault());
			}
			
		}
		else{
			System.out.println("Cannot form "+num+" clusters with the provided tweets");
		}
	}
	
	public void formClusters(){
		for(int i=0;i<p.getPoints().size();i++){
			//System.out.println("please wait");
		//for(Points.Point tweet : p.getPoints()){
			double temp=Double.MAX_VALUE;
			double dist =0.0;
			for(int j = 0;j<centroids.size();j++){
				dist = distance(p.getPoints().get(i).point,centroids.get(j));
				if(dist<temp){
					p.getPoints().get(i).clusterId = j;
				//System.out.println(	"form"+p.getPoints().get(i).clusterId);	
					temp = dist;
				}
			}
		}
		if(flag){
			check();
			flag =false;
		}
		System.out.println("Change in centroids : "+centroidDisplacement());	
		while(centroidDisplacement()>0.5){
			reassignCentroids();
			formClusters();
		}
	}
	
	public double distance(ArrayList<Double> p1, ArrayList<Double> p2){
		double ss =0.0;
		for(int i=0;i<p1.size();i++){
			double temp = (p1.get(i)-p2.get(i));
			ss = ss+(temp*temp);
		}
		return Math.sqrt(ss); 
	}
	
	public void check(){
		
		int[] count = new int[centroids.size()];
		for(Points.Point tweet : p.getPoints()){
			count[tweet.clusterId]++;	
		}
		for(int i :count){
			if(i<10){
				System.out.println("ReClustering");
				Random r = new Random();
				for(int j =0;j<centroids.size();j++){
					centroids.set(j,p.getPoints().get((int) (p.getPoints().size()*r.nextDouble())).point);
				}
				formClusters();
				break;
			}
		}
		flag=false;
	}
	
	public double centroidDisplacement(){
		double displacement =0.0;
		for(int i=0;i<centroids.size();i++){
			displacement = displacement+distance(centroids.get(i),prevCentroids.get(i));
		}
		return displacement;
	}
	
	public void reassignCentroids(){
		prevCentroids.clear();
		prevCentroids.addAll(centroids);
		resetCentroids();
		int[] count = new int[centroids.size()];
		for(Points.Point tweet : p.getPoints()){
			centroids.set(tweet.clusterId,addPoints(count[tweet.clusterId],centroids.get(tweet.clusterId),tweet.point));
			count[tweet.clusterId]++;
		}
	}
	
	public void resetCentroids(){
		for(int i =0;i<centroids.size();i++){
			centroids.set(i, p.getDefault());
		}
	}
	
	public ArrayList<Double> addPoints(int count,ArrayList<Double> p1,ArrayList<Double> p2){
		ArrayList<Double> p0 = new ArrayList<Double>();
		for(int i =0;i<p1.size();i++){
			p0.add((count*p1.get(i)+p2.get(i))/(count+1));
		}
		return p0;
	}

	public ArrayList<ArrayList<Double>> getCentroids() {
		return centroids;
	}
	
	public ArrayList<Points.Point> getNearestTweet() {
		
		double[] buffer = new double[centroids.size()];
		ArrayList<Points.Point> nearPoints = new ArrayList<Points.Point>();
		
		
		 for(int i =0;i<buffer.length;i++){
			nearPoints.add(null);
			buffer[i]=Double.MAX_VALUE;
		}
		
		for(Points.Point tweet: p.getPoints()){
			double tempDist = distance(tweet.point, centroids.get(tweet.clusterId));
			if(buffer[tweet.clusterId]>tempDist){
				nearPoints.set(tweet.clusterId, tweet);
				buffer[tweet.clusterId]=tempDist;
			}
		}
		return nearPoints;
	}

}
	
