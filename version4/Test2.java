package version3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Test2 {
	public static int here;
	
	public Test2(int attr, int attr2){
		attr = attr*2;
	}
	
	public static void main(String[] args) {
		Test2 b = new Test2(1,2);
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
