package version3;

import java.io.File;
import java.util.*;


public class Test1 {
        
        public Test1(int input1, int input2){
                input1 = this.input1;
                input2 = this.input2;
        }
        
        public static void main(String[] args) {
                for(int j = 0; j < 5; j++){
                        System.out.println("Hello World!!!");
                        int b = 3;
                        
                        System.out.println("banb");
                        int h = b + j;
                        int[] po = new int[9];
                        po.getClass();
                        here();
                        switch (b) {
                        case 1:
                        	switch(b-9){
                        	case 1:
                        		break;
                        	case 2:
                        		System.out.println("asdf");
                        		break;
                        	}
                        	System.out.println("ba");
                            break;
                        case 2:
                            break;
                        case 3:
                            b = 5;
                            break;
                        case 4:
                        	break;
                        case 5:
                      		b = 4;
                       		break;
                        }
                }
                if(bool){
                	float lj = 9;
                	System.out.println("sakdl;");
                }else if(bool2){
                	System.out.print(7);
                }else {
                	
                }
                	
                
                do{
                	double ty = 0;
                	System.out.print(ty);
                }while(bool);
                
                for(int s = 0; s < 7; s++){
                        System.out.println("ghj");
                        continue;
                } 
                
                ArrayList<Integer> testArrayList = new ArrayList<Integer>();
                for(Integer i: testArrayList){
                	System.out.println("for each loop");
                }
                
                if(bool || bool2){
                	System.out.println("asdf");
                	while(bool){
                    	System.out.println("while loop");
                    	while(bool){
                    		if(bool){
                    			System.out.println();
                    		}
                    		do{
                    			for(int h =0; h < 9; h++){
                    				System.out.print("asdf");
                    				while(bool){
                    					int asd = 9;
                    				}
                    				switch(h){
                    				case 1:
                    					break;
                    				case 2:
                    					System.out.println("bad");
                    					break;
                    				case 3:
                    					System.out.println("good");
                    					break;
                    				}
                    			}
                    		}while(bool);
                    		//System.out.println("er");
                    	}
                    }
                }
                while(bool){}
                
                int u = 0;
                int y = 0;
                int t = 0;
                t = t + t;
                int g = 9876 << 1;
                System.out.println("THIS IS G: " + g);
                g = g << 2;
                System.out.println("THIS IS G: " + g);
        }
        
        private class innerClass1{
                private void innerClass1Method1(){
                        int j = 0;
                }
                private int innerClass1Method2(){
                        return 2;
                }
        }
        
        private class innerClass2{
                public void innerClass2Method1(){
                        int h = 7+8;
                }
        }
        
        public static int here(){
        	if(bool){
        		System.out.println("hasdf");
        	} else if (bool & bool2){
        		System.out.println("abasd");
        	}
        	int hl = 0;
                return 2;
        }
        
        private final int field1 = 8;
        public final double field2 = 12.3;
        private static String field3 = null;
        private static final String field4 = "field4";
        private static Test2 test = new Test2(1,2);
        private int input1;
        private int input2;
        private static boolean bool = false;
        private static boolean bool2 = true;
}