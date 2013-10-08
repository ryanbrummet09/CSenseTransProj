public class Test1 {
	
	public Test1(int input1, int input2){
		input1 = this.input1;
		input2 = this.input2;
		
	}
	
	public static void main(String[] args) {
		
		System.out.println("Hello World!!!");
		int i = 0;
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
	
	public int here(){
		return 2;
	}
	
	private final int field1 = 8;
	public final double field2 = 12.3;
	private static final String field3 = "field3";
	private static final String field4 = "field4";
	private int input1;
	private int input2;
}
