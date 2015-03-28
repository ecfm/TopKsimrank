package fanweizhu.fastSim.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fanweizhu.fastSim.data.Graph;
import fanweizhu.fastSim.data.Node;
import fanweizhu.fastSim.util.Config;

public class VectorMatrix {
	public class DimVal{
		public int dim;
		public double val;
		public DimVal(int dimension, double value){
			dim = dimension;
			val = value;
		}
	}
	public List<List<DimVal>> Matrix;
	public void Generate_Mat(Graph G){
		Matrix = new ArrayList<List<DimVal>>();
		//[G.nodes.size()*(Config.KSI+1)]
		for (Node n : G.nodes.values()){
			
		}
	}
}

