package fanweizhu.fastSim.core;
import fanweizhu.fastSim.core.GenerateSim.*;
import fanweizhu.fastSim.data.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;


public class Genertate_Candidate {
	private class Dentry{
		public int dim;
		public double val;
		public double ub_i;
		public Dentry(int dimension, double value, double ub_at_i){
			dim = dimension;
			val = value;
			ub_i = ub_at_i;
		}
	}
	static class NPComparator implements Comparator<NodesPair> {

		public int compare(NodesPair one, NodesPair two) {
			return Double.compare(two.val, one.val);
		}
	}
	public class NodesPair{
		public int n1;
		public int n2;
		public double val;
		public NodesPair(int node1, int node2, double v){
			n1 = node1;
			n2 = node2;
			val = v;
		}
	}
	private class FindResult{
		public boolean exist;
		public int Sx;
		public double val;

		public FindResult(boolean exist, int Sx, double val){
			this.exist = exist;
			this.Sx = Sx;
			this.val = val;
		}
	}
	private GenerateSim Gs;
	public Genertate_Candidate(GenerateSim Gs){
		this.Gs = Gs;
	}
	public PriorityQueue<NodesPair> generate_candidate(int k){
		double theta;
		Set<Integer> S = new HashSet<Integer>();
		NPComparator  npc = new NPComparator();
		PriorityQueue<NodesPair> H = new PriorityQueue<NodesPair>(k, npc);
		for(int x_idx = 0; x_idx < Gs.Sim.size(); x_idx++){
			theta = S.size() < k ? 0 : H.peek().val;
			FindResult fr = find(x_idx, theta);
			if (fr.exist){
			int n1 = x_idx;
			int n2 = fr.Sx;
			int larger_n = n1 > n2 ? n1 : n2;
			int smaller_n = n1 < n2 ? n1 : n2;
			int new_idx = larger_n*(Gs.Sim.size()-1)+smaller_n;
			if (S.contains(new_idx)) // continue if such pair is already encountered
				continue;
				if (S.size() == k){
					H.poll();
				}
				H.add(new NodesPair(smaller_n, larger_n, fr.val));
				S.add(new_idx);
			}
		}
		
		return H;
	}

	private FindResult find(int x_idx, double theta){
		Map<Integer,Double> x_vector = Gs.Sim.get(x_idx);
		if (x_vector == null)
			return new FindResult(false, -1, -1); 
		Dentry[] D = new Dentry[x_vector.size()];
		double[] ub = new double[x_vector.size()];
		int Sx = -1;
		//Map<Integer, InvList> I = new HashMap<Integer, InvList>();
		Iterator<Entry<Integer, Double>> it = x_vector.entrySet().iterator();
		int idx = 0;
		while (it.hasNext()) {// line 3
			Entry<Integer, Double> x_Di = it.next();
			int dim = x_Di.getKey(); // get current dimension of the x_vector component
			InvList Ii = Gs.iLists.get(dim);
			double ub_i = 0;
			for(int i = 0; i < Ii.nodeValList.size(); i++){
				NodeVal y_Di = Ii.nodeValList.get(i);
				if (y_Di.idx != x_idx){ // if the current node-value pair in the inverted list is not the node x
					ub_i = x_Di.getValue()*y_Di.val;
					break; // the node closest to the head has the largest value in the list 
				}
			}
			D[idx] = new Dentry(dim, ub_i/Ii.nodeValList.size(), ub_i); // set the score used for ranking for Dimension idx
			idx++;
		}

		Arrays.sort(D, new Comparator<Dentry>() {
			@Override
			public int compare(Dentry o1, Dentry o2) {
				return Double.compare(o2.val, o1.val);
			}
		});

		double currentMax = 0;

		Map<Integer, Double> aggr = new HashMap<Integer, Double>();
		int stopDimension = 0;
		for (int i = 0; i < D.length; i++){ // line 10
			int dim = D[i].dim; // get current dimension in D
			InvList Ii = Gs.iLists.get(dim);
			for(NodeVal y_val : Ii.nodeValList){
				int y_idx = y_val.idx;
				if (y_idx != x_idx){ // if the current node-value pair in the inverted list is not the node x
					Double aggr_y = aggr.get(y_idx);
					if (aggr_y != null){
						aggr_y += y_val.val*x_vector.get(dim); // line 10
					}
					else{
						aggr_y = y_val.val*x_vector.get(dim);
					}
					aggr.put(y_idx, aggr_y);
					if (aggr_y >currentMax){
						currentMax = aggr_y;
						Sx = y_idx;
					}
				}
			}
			double sum_ub = 0;
			for (int j = i + 1; j < D.length; j++){
				sum_ub += D[j].ub_i;
			}
			double max = currentMax > theta ? currentMax:theta;
			if (sum_ub < max){
				stopDimension = i;
				break;
			}
		}

		NodeVal[] F = new NodeVal[aggr.size()];
		Iterator<Entry<Integer, Double>> it_aggr = aggr.entrySet().iterator();
		idx = 0;
		while (it_aggr.hasNext()) {
			Entry<Integer, Double> y_aggr = it_aggr.next();
			NodeVal node_val = new NodeVal(y_aggr.getKey(), y_aggr.getValue());
			F[idx] = node_val;
			idx++;
		}

		Arrays.sort(F, new Comparator<NodeVal>() {
			@Override
			public int compare(NodeVal o1, NodeVal o2) {
				return Double.compare(o2.val, o1.val);
			}
		});

		for(NodeVal y_aggr : F){
			int y_idx = y_aggr.idx;
			Double aggr_y = y_aggr.val;
			for (int i = stopDimension + 1; i < D.length; i++){
				int dim = D[i].dim; // get current dimension in D
				Map<Integer,Double> y_vector = Gs.Sim.get(y_idx);
				if (y_vector.get(dim) == null)
					continue;
				aggr_y += y_vector.get(dim)*x_vector.get(dim); // line 19
				aggr.put(y_idx, aggr_y);
				double sum_ub = 0;
				for (int j = i + 1; j < D.length; j++){
					sum_ub += D[j].ub_i;
				}
				double max = currentMax > theta ? currentMax:theta;
				if (sum_ub + aggr_y < max) // line 20
					break;
			}

			if (aggr_y > currentMax){
				currentMax = aggr_y;
				Sx = y_idx;
			}
		}
		if (currentMax >= theta)
			return new FindResult(true, Sx, currentMax);
		else return new FindResult(false, -1, -1);	
	}

}
