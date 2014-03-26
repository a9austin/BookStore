// Austin Truong
// CS5530 - PHASE 2

package cs5530;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.Iterator;

public class CommandInterface {
	
	public static void main(String[] args) throws Exception {
		CommandInterface c = new CommandInterface();
		c.start();
	}
	
	BookStoreLogic BookStore;
	ArrayList<Book> Cart;
	String customerid;
	boolean quit;
	
	public CommandInterface(){
		BookStore = new BookStoreLogic();
		Cart = new ArrayList<Book>();
	}
	
	public void start() throws Exception{
		Scanner s = new Scanner(System.in);
		BookStoreLogic BookStore = new BookStoreLogic();
		quit = false;
		customerid = "2";
		loggedInOptions(15, s); // For testing
//		while (quit == false){
//			System.out.println("Welcome to the Book Store");
//			System.out.println("0. Quit");
//			System.out.println("1. Login");
//			System.out.println("2. Register");
//			System.out.println("Enter Input:");
//			String input = s.nextLine();
//			if (input.equals("0")){
//				quit = true;
//			}
//			else if (input.equals("1")){
//				// Handle Login
//				System.out.println("Please provide login parameters");
//				System.out.println("Please input your username");
//				String username = s.nextLine();
//				System.out.println("Please input your password");
//				String password = s.nextLine();
//				customerid = BookStore.loginCustomer(username, password);
//				if (customerid == "ERROR"){
//					System.out.println("Please try again, login credentials were in correct");
//				} else{					
//					int customerid_int = Integer.parseInt(customerid);
//					loggedInOptions(customerid_int, s);
//				}
//				
//			}
//			else if (input.equals("2")){
//				System.out.println("Please provide registration parameters");
//				System.out.println("Please input your username");
//				String username = s.nextLine();
//				System.out.println("Please input your password");
//				String password = s.nextLine();
//				System.out.println("Please input your credit number");
//				String credit_card = s.nextLine();
//				System.out.println("Please input your phone number");
//				String phone = s.nextLine();
//				System.out.println("Please input your address");
//				String address = s.nextLine();
//				System.out.println("Please input your full name");
//				String name = s.nextLine();
//				
//				try{
//					int customerid = Integer.parseInt(BookStore.registerCustomer(username, password, credit_card, phone, address, name));
//					loggedInOptions(customerid, s);
//				
//				}
//				catch (Exception e){
//					System.out.println(e.toString());
//					System.out.println("Error occured while registering");
//				}
//			}
//			
//		}
		System.out.println("Thanks for shopping!");
	}
	
	public void loggedInOptions(int customerid, Scanner s) throws NumberFormatException, Exception{
		
		boolean logged_in = true;
		System.out.println("Login successful!");
		while(logged_in == true){
			System.out.println("0. Log Out");
			System.out.println("1. Order Book");
			System.out.println("2. Display all my information");
			System.out.println("3. Add book to store");
			System.out.println("4. Increase inventory for book");
			System.out.println("5. Give feedback");
			System.out.println("6. Give usefulness feedback");
			System.out.println("7. Search Book");
			System.out.println("8. Top useful feedback");
			System.out.println("9. Suggestions based off book");
			System.out.println("10. Given two authors degree of seperation");
			System.out.println("11. Top trusted users");
			System.out.println("12. Top useful users");
			
			String choice = s.nextLine(); 
			if (choice.equals("1")){
				bookPurchase(s);
			}
			else if (choice.equals("2")){
				displayCustomerInfo(customerid);
			}
			else if (choice.equals("3")){
				bookInsertion(s);
			} 
			else if (choice.equals("4")){
				increaseInventory(s);
			}
			else if (choice.equals("5")){
				giveOpinion(s);
			}
			else if (choice.equals("6")){
				giveFeedback(s);
			}
			else if (choice.equals("7")){
				searchBook(s);
			}
			else if (choice.equals("8")){
				topUsefulFeedback(s);
			}
			else if (choice.equals("9")){
				displayRecommendedBooks(s);
			}
			else if (choice.equals("10")){
				displayAuthorDegree(s);
			}
			else if (choice.equals("11")){
				displayTopTrusted(s);
			}
			else if (choice.equals("12")){
				displayTopUseful(s);
			}
			
			System.out.println("0. Quit or 1. Continue using Shop");
			String choice_2 = s.nextLine();
			if (choice_2.equals("0")){
				logged_in = false;
				quit = true;
			}
			
		}
	}
	
	private void displayAllAuthors(){
		System.out.println("** All Authors **");
		HashMap<String, String> authors_map = BookStore.selectAllAuthors();
	    @SuppressWarnings("rawtypes")
		java.util.Iterator it = authors_map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + ": " + pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	private void displayAuthorDegree(Scanner s){
		displayAllAuthors();
		try{
			System.out.println("Please provide parameters for determining author degree");
			System.out.println("Please input customer id 1 (Int)");
			int cid1 = Integer.parseInt(s.nextLine());
			System.out.println("Please input customer id 2 (Int)");
			int cid2 = Integer.parseInt(s.nextLine());
			try {
				String degree = BookStore.findAuthorDegree(cid1, cid2);
				System.out.println("The two authors are " + degree +" degree separation");
			}
			catch (Exception e){
				
			}
		}
		catch (Exception e){
			System.out.println("customer id's must be an Integer");
			return;
		}
		
		

	}
	
	private void displayTopUseful(Scanner s){
		System.out.println("Please provide parameters for for top useful users");
		System.out.println("Please input how many customers to display");
		int n = Integer.parseInt(s.nextLine());
		ArrayList<Customer> customers = BookStore.mostUseful(n);
		for (int i = 0; i < customers.size(); i++){
			System.out.println("Customer Name: " + customers.get(i).Name);
		}
	}
	
	private void displayTopTrusted(Scanner s){
		System.out.println("Please provide parameters for top trusted users");
		System.out.println("Please input how many customers to display");
		int n = Integer.parseInt(s.nextLine());
		ArrayList<Customer> customers = BookStore.mostTrusted(n);
		for (int i = 0; i < customers.size(); i++){
			System.out.println("Customer Name: " + customers.get(i).Name);
		}
	}
	
	private void displayRecommendedBooks(Scanner s){
		displayAllBooks();
		System.out.println("Please provide parameters for to find recommended books");
		System.out.println("Please input ISBN");
		String isbn = s.nextLine();
		ArrayList<Book> books = BookStore.recommendedBooks(isbn);
		System.out.println("** Recommended Books **");
		for (int i = 0; i < books.size(); i++){
			System.out.println("ISBN: " + books.get(i).Isbn + ", Title: " + books.get(i).Title);
			System.out.println("----");
		}
	}
	
	private void topUsefulFeedback(Scanner s)
	{
		System.out.println("Please provide parameters for top feedbacks");
		System.out.println("Please input ISBN");
		String isbn = s.nextLine();
		System.out.println("Please input how many feedbacks to retrieve");
		int n = s.nextInt();
		String[] opinions = BookStore.topOpinion(n, isbn);
		for (int i = 0; i < opinions.length; i++){
			System.out.println("Feedback Description: " + opinions[i]);
		}
	}
	
	private void giveFeedback(Scanner s) throws NumberFormatException, Exception{
		System.out.println("** All Opinions **");
		ArrayList<Opinion> opinions = BookStore.selectOpinions();
		for (int i = 0; i < opinions.size(); i++){
			System.out.println("Opinion id: " + opinions.get(i).Id + ", Description: " + opinions.get(i).Description);
		}
		System.out.println("Please provide parameters for giving usefulness feedback");
		System.out.println("Please input opinion id");
		int opinionid = s.nextInt();
		System.out.println("Please input a rating (0-2)");
		int rating = s.nextInt();
		int result = BookStore.insertFeedback(opinionid, Integer.parseInt(customerid), rating);
		if (result == 1){
			System.out.println("Successful insertion of feedback rating");
		} else{
			System.out.println("Insertion of usefulness feedback has an issue");
		}
	}
	
	private void searchBook(Scanner s){
		boolean filtering = true;
		String by_title = "";
		String by_author = "";
		String by_publisher = "";
		String by_genre = "";
		boolean sort_year = false;
		boolean sort_score = false;
		boolean sort_score_trust = false;
		while (filtering){
			System.out.println("0. Filter search");
			System.out.println("1. Search by title");
			System.out.println("2. Search by author");
			System.out.println("3. Search by publisher");
			System.out.println("4. Apply ordering by year");
			System.out.println("5. Apply ordering by score");
			System.out.println("6. Apply ordering by score (trusted only)");
			String choice = s.nextLine();
			if (choice.equals("0")){
				ArrayList<Book> books = BookStore.searchBook(by_title, by_author, by_publisher, by_genre, sort_year, sort_score, sort_score_trust);
				for (int i = 0; i < books.size(); i++)
				{
					System.out.println("ISBN: " + books.get(i).Isbn + ", Title: " + books.get(i).Title + 
							", Author: " + books.get(i).Author + ", Publisher: " + books.get(i).Publisher +
							", Publisher Year: " + books.get(i).PublisherYear + ", Genre: " + books.get(i).Genre +
							", Price: " + books.get(i).Price);
					
					System.out.println("----");
				}
				filtering = false;
			}
			else if (choice.equals("1")){
				System.out.println("Please input title keywords");
				by_title += s.nextLine();
			}
			else if (choice.equals("2")){
				System.out.println("Please input author keywords");
				by_author += s.nextLine();
			}
			else if (choice.equals("3")){
				System.out.println("Please input publisher keywords");
				by_publisher += s.nextLine();
			}
			else if (choice.equals("4")){
				sort_year = true;
			} 
			else if (choice.equals("5")){
				sort_score = true;
			}
			else if (choice.equals("6")){
				sort_score_trust = true;
			}
			else{
				System.out.println("Please input a choice from one of the options displayed");
			}
			
		}
	}
	
	private void displayAllBooks()
	{
		ArrayList<Book> books = BookStore.selectBooks();
		System.out.println("** All Books **");
		for (int i = 0; i < books.size(); i++){
			System.out.println("ISBN: " + books.get(i).Isbn + ", Title: " + books.get(i).Title);
		}
	}
	
	private void giveOpinion(Scanner s) throws NumberFormatException, Exception
	{
		displayAllBooks();
		
		int score = 0;
		System.out.println("Please provide parameters for giving feedback");
		System.out.println("Please input the ISBN");
		String isbn = s.nextLine();
		System.out.println("Please input the score (0-10)");
		try{
		score = Integer.parseInt(s.nextLine());
		}
		catch (Exception e){
			System.out.println("Score must be an integer");
			return;
		}
		System.out.println("Please input the description");
		String desc = s.nextLine();
		
		if (desc.isEmpty()){
			System.out.println("You must give a description");
			return;
		}
		
		if (isbn.isEmpty()){
			System.out.println("You must give a ISBN");
			return;
		}
		
		int result = BookStore.insertOpinion(isbn, Integer.parseInt(customerid), score, desc);
		if (result == 1){
			System.out.println("Feedback successful!");
		} else{
			System.out.println("Feedback has had an issue");
		}
	}
	
	private void increaseInventory(Scanner s) throws Exception{
		displayAllBooks();
		System.out.println("Please provide parameters for increasing inventory count");
		System.out.println("Please input the ISBN");
		String isbn = s.nextLine();
		System.out.println("Please input how many books to add (Integer)");
		try{
			int inv = s.nextInt();
			int result = BookStore.increaseInventory(isbn, inv);
			if (result == 1){
				System.out.println("Successful increased inventory!");
			}
			else{
				System.out.println("Inventory increase has had an issue");
			}
		}
		catch (Exception e){
			System.out.println("Must be an integer");
			return;
		}
		
		
	}
	
	private void bookInsertion(Scanner s) throws Exception
	{
		try
		{
			ArrayList<String> author_names = new ArrayList<String>();
			System.out.println("Please provide parameters for inserting a book");
			System.out.println("Please input the ISBN");
			String isbn = s.nextLine();
			System.out.println("Please input the title");
			String title = s.nextLine();
			System.out.println("Please input the price (double)");
			double price = Double.parseDouble(s.nextLine());
			System.out.println("Please input the genre");
			String genre = s.nextLine();
			System.out.println("Please input the format (0 or 1)");
			int format = Integer.parseInt(s.nextLine());
			System.out.println("Please input the publisher");
			String publisher = s.nextLine();
			System.out.println("Please input the publisher year");
			int publisher_year = Integer.parseInt(s.nextLine());
			System.out.println("Please input the inventory count");
			int inventory_count = Integer.parseInt(s.nextLine());
			
			boolean inserting_authors = true;
			while (inserting_authors == true){
				System.out.println("Please input the author name");
				String author = s.nextLine();
				author_names.add(author);
				System.out.println("0. Finish");
				System.out.println("1. Insert a co-author");
				String choice = s.nextLine();
				if (choice.equals("0")){
					inserting_authors = false;
				}
			}
			String result = BookStore.insertBook(isbn, title, price, genre, format, publisher, publisher_year, inventory_count, author_names);
			if (result.equals("1")){
				System.out.println("Insertion Successful!");
			}
			else{
				System.out.println("Insertion has had an issue");
			}
		}
		catch (Exception e){
			System.out.println("Format must be 0 or 1 OR price must be a double OR inventory count must be an Integer");
			return;
		}
			
	}
	
	private void displayCustomerInfo(int customerid){
		Customer C = BookStore.selectCustomerInfo(customerid);
		System.out.println("** Customer Information **");
		System.out.println("Username: " + C.Login);
		System.out.println("Name: " + C.Name);
		System.out.println("Address: " + C.Address);
		System.out.println("Phone Number: " + C.PhoneNumber);
		System.out.println("Credit Card Information: " + C.CreditCardNumber);
		System.out.println("");
		
		System.out.println("** All Sales **");
		for (int i = 0; i < C.SalesList.size(); i++){
			System.out.println("Title: " + C.SalesList.get(i).Title);
			System.out.println("Count: " + C.SalesList.get(i).Count);
			System.out.println("Date: " + C.SalesList.get(i).Date);
			System.out.println("----");
		}
		
		System.out.println("");
		System.out.println("** Feedback History **");
		for (int i = 0; i < C.OpinionList.size(); i++){
			System.out.println("Title: " + C.OpinionList.get(i).Title);
			System.out.println("Score: " + C.OpinionList.get(i).Score);
			System.out.println("Description: " + C.OpinionList.get(i).Description);
			System.out.println("----");
		}
		System.out.println("");
		System.out.println("** Usefulness Rating **");
		for (int i = 0; i < C.FeedbackList.size(); i++){
			System.out.println("Opinion ID: " + C.FeedbackList.get(i).OpinionId);
			System.out.println("Opinion Description: " + C.FeedbackList.get(i).OpinionDescription);
			System.out.println("Opinion Rating: " + C.FeedbackList.get(i).Rating);
			System.out.println("Opinion Date: " + C.FeedbackList.get(i).Date);
			System.out.println("----");
		}
		System.out.println("");
		System.out.println("** Trusted Users **");
		for (int i = 0; i < C.TrustList.size(); i++){
			System.out.println("Username given Trust Review: " + C.TrustList.get(i).Username);
			if (C.TrustList.get(i).Trust == 0){
				System.out.println("Not Trusted");
			}
			else{
				System.out.println("Trusted");
			}
			System.out.println("----");
		}
	}
	
	private void bookPurchase(Scanner s) throws NumberFormatException, Exception{
		System.out.println("Order Book");
		displayBooks(BookStore.selectBooks());
		
		boolean flag = true;
		while (flag == true){
			System.out.println("Please provide parameters for ordering a book");
			Book book = new Book();
			System.out.println("Input the ISBN of the book to buy");
			String isbn = s.nextLine();
			System.out.println("Input how many books to buy");
			String count = s.nextLine();
			book.Isbn = isbn;
			book.Count = Integer.parseInt(count);
			Cart = BookStore.addBookToCart(Cart, book);
			System.out.println("1. Continue Shopping");
			System.out.println("2. End Shopping and Checkout");
			String choice = s.nextLine();
			if (choice.equals("2")){
				if (BookStore.insertTransaction(Cart, Integer.parseInt(customerid)) == 1)
				{
					System.out.println("Successfull Transaction");
					// Clear the cart 
					Cart.clear();
					System.out.println("Shopping cart has been cleared");
					flag = false;
				}
				else{
					System.out.println("Error has occured during transaction");
					flag = false;
				}
			}
		}
	}
	
	private static void displayBooks(ArrayList<Book> books){
		for (int i = 0; i < books.size(); i++){
			System.out.println("ISBN: " + books.get(i).Isbn + ", Title: " + books.get(i).Title);
		}
	}
	
	
}
