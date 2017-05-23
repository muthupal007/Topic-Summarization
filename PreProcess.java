import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class PreProcess {
	

	private HashMap<String,Integer> map = new HashMap<String,Integer>(); 
	private HashSet<String> set = new HashSet<String>();
	private int tweetCount;
	private HashMap<String,Integer>  index = new HashMap<String,Integer> ();
	private HashMap<Integer,String>  invIndex = new HashMap<Integer,String> ();
	private HashMap<Integer,Integer> freqMap = new HashMap<Integer,Integer>();
	
	public PreProcess(){
		generateStopwordSet("a about above after again against all am an and any are arent as at be because been before being below between both but by cant cannot could couldnt did didnt do does doesnt doing dont down during each few for from further had hadnt has hasnt have havent having he hed hell hes her here heres hers herself him himself his how hows i id ill im ive if in into is isnt it its its itself lets me more most mustnt my myself no nor not of off on once only or other ought our ours ourselves out over own same shant she shed shell shes should shouldnt so some such than that thats the their theirs them themselves then there theres these they theyd theyll theyre theyve this those through to too under until up very was wasnt we wed well were weve were werent what whats when whens where wheres which while who whos whom why whys with wont would wouldnt you youd youll youre youve your yours yourself yourselves");
		tweetCount=0;
	}
	
	private void generateStopwordSet(String s){
		String[] words = s.split(" ");
		for(String word: words ){
			getSet().add(word);
		}
	}
	
	public void termMatrix(String s){
		
		tweetCount++;	
		String[] split = s.toLowerCase().replaceAll("(https(\\S)+)|(http(\\S)+ )|(( @|\\A@)(\\S)+)|'", "").split("[^A-Za-z0-9]");
		for(String word : split){
			if(!set.contains(word) && word.length() >2 ){
				//word=word.replaceAll("@", "");
				if(map.containsKey(word)){
					map.replace(word, map.get(word)+1);
				}
				else{
					map.put(word, 1);
				}
			}
		}	
	}
	
	public HashMap<String, Integer> getIndex(){
		int count =0;
		int threshold = Integer.max((int) (tweetCount*0.0025), 10);
		for(Entry<String, Integer> e : map.entrySet()){
			if(e.getValue()>=threshold){
				index.put(e.getKey(),count);
				invIndex.put(count,e.getKey());
				freqMap.put(count, e.getValue());
				count++;
			}
		}
		return index;
	}
	

	public HashMap<Integer, String> getInvIndex(){
		return invIndex;
	}
	
	public HashMap<Integer, Integer> getFreqMap(){
		return freqMap;
	}

	public HashSet<String> getSet() {
		return set;
	}

}
	

