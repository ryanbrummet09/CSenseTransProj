package version3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Test2 {
	
	public static void main(String args[]){
		int i = 3;
		int h = 6;
		int j = 123;
		j = i + h;
		j = -i + -(h+h);
		j = i - h;
		j = i * h;
		j = i / h;
		j = i % h;
		int g = 4;
		j = -j + j + 9 / 4 * 7 - h - h;
		h = j + 2;
		h = 2 + j;
		i = i * 4 + j - h;
		h = (h + j) * -h / j - h % i;
		i = -g * 8 - h + i;
		System.out.println(h);
	}
}
