package cs5530;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FakeDataGen {
	BookStoreLogic bsl;
	public FakeDataGen(){
		bsl = new BookStoreLogic();
	}
	
	public void generateRandomFeedback(int customerid, int opinionid, int count) throws Exception{
		for (int i = 0; i < count; i++){
			int rand_rating = randInt(0,3);
			System.out.println(rand_rating);
			bsl.insertFeedback(opinionid, customerid, rand_rating);
		}
	}
	
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public void generateBooks(int count){
		for (int i = 0; i < count; i++){
			
		}
	}
	
	public void generateCustomers(int count) throws Exception{
		RandomString rs = new RandomString(7);
		for (int i = 0; i < count; i++){
			bsl.registerCustomer(rs.nextString(), rs.nextString(), "00000", "99999", rs.nextString() + " Street", rs.nextString());
		}	
	}
	
	public static void main(String[] args) throws Exception {
		FakeDataGen f = new FakeDataGen();
		for (int i = 4; i < 7; i++){
			f.generateRandomFeedback(i, 9, 5);
		}
	}
}
