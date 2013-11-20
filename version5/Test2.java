package version3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Test2 {
	public static int here = 2;
	public int there;
	private static boolean here3 = false;

	public Test2(int attr, int attr2){
		attr = attr*2;
		there = attr2;
		here = 5;
	}
	
	public Test2(int attr, int attr2, int attr3){
		attr2 = attr3*3;
		here = 3;
	}
	
	public static void main(String[] args) {
		Test2 b = new Test2(1,2);
		Test2 c = new Test2(6,3,5);
		boolean bv = true;
		b.there = 3;
		here3 = true;
		int g = c.there + b.there;
		here+=8;
		/*
		for(int i = 0; i <9; i++){
			for(int j = 8; j>4; j--){
				int y = 9;
			}
		}*/
		
		
	}
	
	public void bob(int i, int d2){
		i = 0;
		int a = 0;
		i = a + i;
		here = 8;
	}
	
	public void gh(Test1 j){
		bob(1,2);
	}
	
	public static void ghj(Test1 j){
		int v = 9;
	}
}
