package version3;

import java.io.*;
import java.util.ArrayList;

public class firstGoTrans {

	public static void main(String args[]) throws Exception{
		SourceStructure3 ss = new SourceStructure3(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/version3/Test2.class"));
		ArrayList<ClassStructure3> cs = ss.getClassStructures();
		ArrayList<MethodStructure3> ms;
		for(int c = 0; c < cs.size(); c++){
			ms = cs.get(c).getMethodStructures();
			for(int m = 0; m < ms.size(); m++){
				sequenceStarts = new ArrayList<Integer>();
			}
		}
		
	}

	
	private static ArrayList<Integer> sequenceStarts;
}
