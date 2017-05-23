

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Servlet implementation class MainController
 */
@WebServlet("/MainController")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String type=request.getParameter("type");
		System.out.println("@@@@@@@@@@@@@type@@@@@@@@@@@@@"+type);
		String word;
		HttpSession httpSession=request.getSession();
		
		if(type.equalsIgnoreCase("askPin")){
			
			word=request.getParameter("input");
			
			try {
				
				 	String twitterConsumerKey = "Q6gfVjPvV4JS5KweATHYDTMfv";
			        String twitterConsumerSecret = "kOJglRwvLKT9gPGd6Ob4yBdgtZ6sai8WFmHDkK7auMUeBLXPlQ";
			        ConfigurationBuilder cb = new ConfigurationBuilder();
			        cb.setJSONStoreEnabled(true);
			       

			        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			        httpSession.setAttribute("twitter", twitter);
			        
			        twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
			        RequestToken requestToken = twitter.getOAuthRequestToken();
			        httpSession.setAttribute("requestToken", requestToken);
			        
			        System.out.println("Generating Url");
			        
		            System.out.println(requestToken.getAuthorizationURL());
		            
			          
					response.setContentType("text/plain");
		            response.setCharacterEncoding("UTF-8");
		            response.getWriter().write(requestToken.getAuthenticationURL().toString());
			
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}else if(type.equalsIgnoreCase("getMatchedTweets")){
			
			String result="";
			int index=Integer.parseInt(request.getParameter("index"));
			JSONArray array=new JSONArray();
			
			
			System.out.println("@@@@ Inside Get Mateched Tweets@@@"+index);
			ArrayList<String> input=(ArrayList<String>) httpSession.getAttribute("tweets");
			System.out.println("@@@tweets list@@@@"+httpSession.getAttribute("tweets"));
			TreeMap<String,Integer> map = new TreeMap<String,Integer> ();
			
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
				System.out.println("@@@@@@@@Inside Clustering@@@@@@@@@@@");
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
				
			}
			
		 HashMap<Integer,TreeMap<String,Integer>> map1 = new HashMap<Integer,TreeMap<String,Integer>> ();
			
			 HashMap<String, Integer> freqMap = new HashMap<String,Integer>();
		
			 int negtiveCount = 0;
			 int positiveCount = 0;
			 int neutralCount = 0;
			SentimentAnalysis senti = new SentimentAnalysis();
			int clustercount=0;
			int temp = 0;
			for(Points.Point tweet:p.getPoints()){
			//	System.out.print(tweet.clusterId);
				if(tweet.clusterId == (index-1)){
					
					String s = p.getTweetsList().get(tweet.id);
					temp = senti.getSentiment(s, process);
					if(temp>0){
						positiveCount++;
					}else if(temp<0){
						negtiveCount++;
					}
					else neutralCount++;
					
					String[] split = s.toLowerCase().replaceAll("(https(\\S)+)|(http(\\S)+ )|(( @|\\A@)(\\S)+)", "").split("[,!.;?(){}\\[\\]]");
					for(String line : split){
						if(line.length()>2){
							String[] split1 = line.split("[^A-Za-z0-9']");
							for(int k =0;k<split1.length;k++){
								if(!map1.containsKey(k)){
									map1.put(k, new TreeMap<String,Integer>());
								}
								if(!process.getSet().contains(split1[k]) && split1[k].length() >2 ){
									//word=word.replaceAll("@", "");
									if(map1.get(k).containsKey(split1[k])){
										map1.get(k).replace(split1[k],map1.get(k).get(split1[k])+1);
									}
									else{
										map1.get(k).put(split1[k], 1);
									}
								}
							}
						}
					}
					clustercount++;
				}
			}
			
			if(clustercount>0){
				System.out.println("Size of cluster : " +clustercount);
				String[] temp1 = {"","",""};
				ArrayList<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
				l.add(0,new ArrayList<String>());
				l.add(1,new ArrayList<String>());
				l.add(2,new ArrayList<String>());
				
				for(Entry<Integer,TreeMap<String, Integer>> e:map1.entrySet()){
					PriorityQueue<Entry<String, Integer>> que = new PriorityQueue<Entry<String, Integer>>(new MyComp1());
					for(Entry<String, Integer> e1 : e.getValue().entrySet()){
						que.offer(e1);
					}
					int i  = 0;
					while(!que.isEmpty() && i<3){
						if(!l.get(i).isEmpty()){
							while(!que.isEmpty() && l.get(i).contains(que.peek().getKey())){
								que.poll();
							}
							if(!que.isEmpty()){
								l.get(i).add(que.poll().getKey());
							}
						}
						else {
							l.get(i).add(que.poll().getKey());
						}
						i++;	
					}
					
				}
				for(int i =0;i<l.size();i++){
					for(String s: l.get(i)){
						temp1[i] = temp1[i]+s+" ";
					}
				} 
				
				System.out.println(temp1[0]);
				System.out.println(temp1[1]);
				System.out.println(temp1[2]);
				
				
				
				PriorityQueue<Entry<String,Integer>> freqMap1 = new PriorityQueue<Entry<String,Integer>>(new MyComp1()); 
				for(Entry<String,Integer> e:senti.getFreq().entrySet()){
					freqMap1.offer(e);
				}
				try {
				for(int j=0; j<10 && j<freqMap1.size() ; j++){
					JSONObject jsonObject=new JSONObject();
					System.out.println(freqMap1.peek().getKey()+" "+freqMap1.poll().getValue());
					
						jsonObject.put("word", freqMap1.peek().getKey());
						jsonObject.put("count", freqMap1.poll().getValue());
						array.put(jsonObject);
				}
				
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Total Positive tweets : " +positiveCount);
				System.out.println("Total Negitive tweets : " +negtiveCount);
				System.out.println("Total Neutral tweets : " +neutralCount);
				
				result=temp1[0]+"@#"+temp1[1]+"@#"+temp1[2]+"@#"+positiveCount+"@#"+negtiveCount+"@#"+neutralCount;
				
				positiveCount = 0;
				negtiveCount = 0;
				neutralCount= 0;
				
			}
			
			result=result+"@#"+array;
			response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(result.toString());
              
           // response.getWriter().write(temp.toString());
			
			
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession httpSession=request.getSession();
		String word;
		
		String type=request.getParameter("type");
		System.out.println("@#@#@#type@#@#@#"+type);
		if(type.equalsIgnoreCase("getTweets")){
			
			
			 String pin=request.getParameter("pin");
			 word=request.getParameter("input");
			 String url=request.getParameter("url");
			 url="https:"+url;
			 //word="\""+word+"\""+"+exclude:retweets";
			 word="\""+word+"\"" + "\"christmas\"" + "+exclude:retweets";
			try {
				
				Properties prop = new Properties();
		        InputStream input = null;
		        ArrayList<String> tweets = new ArrayList<String>();
		        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
		        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
		       
		     
		          twitter.getOAuthAccessToken(requestToken, pin);
		          httpSession.removeAttribute("requestToken");
		     
		        //persist to the accessToken for future reference.
		        twitter.verifyCredentials().getId();
		        Query query = new Query(word);
		        query.setCount(99);
		        query.setLang("en");
		        query.setResultType(Query.RECENT);
		        	
		        int tweetcount =0;
		        long maxID = 0;
		        
		        for(int i =0; i<50 ;i++){
		        	if(i!=0){
		        		query.setMaxId(maxID-1);
		        	}
		        	
		        	QueryResult result = twitter.search(query);
		             
		        	for (Status status : result.getTweets()) {
		        		
		        	//	String[] str = process.extract(status.getText(), status.getLang());
		        		tweets.add(status.getText());
		        		//System.out.println(status.getText());
		        		maxID = status.getId();
			        	tweetcount++; 
			        }
		        		
		        }
			//	return tweets;
				
				Kmeans k = new Kmeans();
				 Map<String,String> data=k.Kmeans(tweets);
				
				 httpSession.setAttribute("tweets", tweets);
				 httpSession.setAttribute("sandtMap", data);
				 
				 
				 RequestDispatcher rd = getServletConfig().getServletContext().getRequestDispatcher("/showTopics.jsp");
				 rd.forward(request, response);
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
	}
	
	
	class MyComp1 implements Comparator<Entry<String, Integer>>{

		@Override
		public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
			if(arg0.getValue()>arg1.getValue()){
				return -1;
			}
			else if(arg0.getValue()<arg1.getValue()){
				return 1;
			}
			else return 0;
		}
		
		
	}
	
	

}
