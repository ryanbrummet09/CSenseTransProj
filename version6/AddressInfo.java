package version3;

public class AddressInfo {

	/**
	 * Defines a type that is used to keep track of the next available index of an array of user-defined types with name of the value of this.type
	 * @param type
	 * @param address
	 */
	public AddressInfo(String type, int address){
		this.type = Constants.zeroString(type) + "ADDRESS";
		this.address = address;
	}
	
	/**
	 * 
	 * @return returns the name of array holding a single type of user-defined objects
	 */
	public String getTypeArray(){
		return type;
	}
	
	/**
	 * 
	 * @return returns the next available index in an array holding a single type of user-defined objects
	 */
	public int getNextOpenAddress(){
		return address;
	}
	
	/**
	 * increments the next available index in an array holding a single type of user-defined objects
	 */
	public void incrementAddress(){
		address++;
	}
	private int address;
	private String type;
}
