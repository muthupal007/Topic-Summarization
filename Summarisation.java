import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;


public class Summarisation {

	private TreeMap<String,Integer> map = new TreeMap<String,Integer> ();
	
	public void getSummary(Points p,PreProcess process) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Select a subtopic number for summary: ");
		int i = scan.nextInt();
		System.out.println();
		int clustercount=0;
		for(Points.Point tweet:p.getPoints()){
			System.out.print(tweet.clusterId);
			if(tweet.clusterId == (i-1)){
				String s = p.getTweetsList().get(tweet.id);
				String[] split = s.replaceAll("(https(\\S)+)|(http(\\S)+ )|(( @|\\A@)(\\S)+)", "").split("[^A-Za-z0-9']");
				for(String word : split){
					if(!process.getSet().contains(word.toLowerCase().replaceAll("'", "")) && word.length() >2 ){
						//word=word.replaceAll("@", "");
						if(map.containsKey(word)){
							map.replace(word, map.get(word)+1);
						}
						else{
							map.put(word, 1);
						}
					}
				}
				clustercount++;
			}
		}
		
		generateSummary(clustercount);
		
		System.out.print("Press 1 if you wish to continue else 0: ");
		if(scan.nextInt()==1){
			getSummary(p, process);
		}
		
	}

	private void generateSummary(int num) {
	//	System.out.println("Size of cluster : " +num);
		String temp ="";
		for(Entry<String,Integer> e:map.entrySet()){
			if(e.getValue()>=(num/2)){
				temp = temp + e.getKey()+" ";
			}
		}
		System.out.println("mateched words"+temp);
		
	}
	
	
}
