package fanweizhu.fastSim.exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.TreeMap;

import fanweizhu.fastSim.core.*;
import fanweizhu.fastSim.core.GenerateSim.NodeVal;
import fanweizhu.fastSim.core.Genertate_Candidate.NodesPair;
import fanweizhu.fastSim.data.*;
import fanweizhu.fastSim.util.*;
import fanweizhu.fastSim.util.io.TextReader;
import fanweizhu.fastSim.util.io.TextWriter;


public class Online {
	public static void main(String args[]) throws Exception {
		//init parameters
//		Config.hubType = args[0];
//    	Config.numHubs = Integer.parseInt(args[1]);

    	Graph graph = new Graph();
//        graph.loadGraphFromFile(Config.nodeFile, Config.edgeFile);
    	graph.loadGraphFromFile("node", "edge");
        //load queries
        GenerateSim gs = new GenerateSim(graph);
        Genertate_Candidate gc = new Genertate_Candidate(gs);
        PriorityQueue<NodesPair> pq = gc.generate_candidate(6);
        for (NodesPair np : pq){
        	System.out.println("Sim("+np.n1+","+np.n2+")="+np.val);
        }
        
        
	}
	
	public void printSimMat(GenerateSim gs){
for (Map<Integer,Double> x_vector : gs.Sim){
        	
    		List<Entry<Integer, Double>> dim_v = new ArrayList<Entry<Integer, Double>>();
    		Iterator<Entry<Integer, Double>> it = x_vector.entrySet().iterator();
    		int idx = 0;
    		while (it.hasNext()) {
    			Entry<Integer, Double> x_Di = it.next();
    			dim_v.add(x_Di);
    		}
    		Collections.sort(dim_v, new Comparator<Entry<Integer, Double>>() {
    		    @Override
    		    public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
    		        return Double.compare(o1.getKey(), o2.getKey());
    		    }
    		});
    		for (Entry<Integer, Double> x_Di : dim_v){
    		System.out.print(x_Di.getKey()+":");
			System.out.format("%.2f",x_Di.getValue());
			System.out.print("\t");
    		
    		}
    		System.out.println();
        }
	}
}
