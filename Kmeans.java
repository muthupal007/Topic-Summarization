
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;


import org.json.JSONArray;

public class Kmeans {
	
	
	
		public Map<String,String> Kmeans(ArrayList<String> input){
			
			Map<String,Map<Integer ,String>> subtopicandTweet = new LinkedHashMap();
			 
			 Map<String, String> sandtMap =  new LinkedHashMap();
			
			 
			 ArrayList<String> subtopics=new ArrayList<>();
			 ArrayList<String> tweet=new ArrayList<>();
			 
			 System.out.println("Start Preprocessing");
			PreProcess process = new PreProcess();
	        for(String s: input){
	        	process.termMatrix(s);
	        }
			System.out.println("Finished Preprocessing");
			
			Points p = new Points(process.getIndex());
			for(String s: input){
	        	p.generatePoints(s);
	        }
			System.out.println("Finished Generating Point");
			Clusters c = new Clusters(p, 10);
			System.out.println("Started forming clusters");
			c.formClusters();
			for(int i =0;i<c.getCentroids().size();i++){
				int j =0;
				PriorityQueue<double[]> que = new PriorityQueue<double[]>(new MyComp(process.getFreqMap()));
				for(double d:c.getCentroids().get(i)){
					double[] temp = {j,d};
					que.offer(temp);
					j++;
				}
			
				String subtopic="";
				for(int k=0;k<3;k++){
					//System.out.println(que.peek()[1]);
					String temp = process.getInvIndex().get((int)que.poll()[0]);
					if(!subtopic.contains(temp.substring(0,temp.length()-3))){
						subtopic = subtopic+temp+" ";
					}
				}
				//subtopic = subtopic +" "+ process.getInvIndex().get((int)status[0][0]) +" "+ process.getInvIndex().get((int)status[1][0]) +" "+ process.getInvIndex().get((int)status[2][0]);
				String matchedTweet = p.getTweetsList().get(c.getNearestTweet().get(i).id);
				subtopics.add(subtopic);
				tweet.add(matchedTweet);
				System.out.println("Subtopic-"+(i+1)+": "+subtopic);
				System.out.println("MatchedTweet : "+matchedTweet);
				sandtMap.put(subtopic, matchedTweet);
			}
			
			return sandtMap;
			
			
			
			//System.out.println(p.getPoints().size());
			
		}
}

class MyComp implements Comparator<double[]>{
	
	private HashMap<Integer,Integer> map;

	MyComp(HashMap<Integer,Integer> freqMap){
		map = freqMap;
		double temp = 1.2;
	}

	@Override
	public int compare(double[] arg0, double[] arg1) {
		if(arg0[1]>arg1[1]){
			return -1;
		}
		else if(arg0[1]<arg1[1]){
			return 1;
		}
		else{
			if(map.get((int)arg0[0])>map.get((int)arg1[0])){
				return -1;
			}
			else if(map.get((int)arg0[0])<map.get((int)arg1[0])){
				return 1;
			}
			else return 0;
		}
	}
	
}