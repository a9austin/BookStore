package cs5530;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

// Handles Database calls regarding customer
public class BookStoreLogic {
	private DB db;
	private String table_name;

	public BookStoreLogic() {
		db = new DB();
	}

	// Queries Regarding Customer
	public String registerCustomer(String login, String password,
			String credit_card_number, String phone_number, String address,
			String name){
		String query = "INSERT INTO customer (login, password, credit_card_number, phone_number, address, name, trust_score)"
				+ " VALUES ('"
				+ login
				+ "','"
				+ password
				+ "','"
				+ credit_card_number
				+ "','"
				+ phone_number
				+ "','"
				+ address
				+ "','" + name + "', 0)";
		String result;
		try {
			result = Integer.toString(db.DB_Update(query, true));
			
			// Initialize a 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = "ERROR";
			e.printStackTrace();
		}
		
		
		return result;
	}

	public String loginCustomer(String login, String password)
			throws Exception {
		String query = "SELECT idcustomer, password FROM customer WHERE login = '" + login
				+ "'";
		String result = db.DB_Select(query);
		if (result.isEmpty()){
			return "ERROR";
		}
		
		String[] customer_info = result.split("\\|");
		
		if (password.equals(customer_info[1])) {
			return customer_info[0]; // Return id for customer
		} 
		else 
		{
			return "ERROR";
		}
	}


	// Queries Regarding Books/Authors
	public int insertAuthor(String name) throws Exception {
		String query = "INSERT INTO author (name) VALUES ('" + name + "')";
		int result = db.DB_Update(query, false);
		return result;
	}
	
	public String selectAuthor(String isbn){
		String query = "SELECT name FROM author A JOIN author_book_relation ABR " +
				"WHERE A.idauthor = ABR.authorid AND ABR.isbn =" + isbn + "GROUP BY A.idauthor";
		
		String result = "";
		try {
			result = db.DB_Select(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String insertBook(String isbn, String title, double price,
		String genre, int format, String publisher, int publisher_year,
		int inventory_count, ArrayList<String> authors){
		String query = "";
		try{
			ArrayList<Integer> authorid_list = new ArrayList<Integer>();
			for (int i = 0; i < authors.size(); i++){
				query = "SELECT idauthor FROM author WHERE name = '" + authors.get(i) + "'";
				String authorid_str = db.DB_Select(query);
				if (authorid_str.isEmpty()){
					String query_ia = "INSERT INTO author (name) VALUES ('"+authors.get(i)+"') ";
					int authorid = db.DB_Update(query_ia, true);
					authorid_list.add(authorid);
				} 
				else
				{
					int authorid = Integer.parseInt(authorid_str);
					authorid_list.add(authorid);
				}
			}
			//query = "SELECT authorid FROM author WHERE name = '" + author + "'";
			//String authorid = db.DB_Select(query);
			
			query = "INSERT INTO book (isbn, title, price, genre, format, publisher, publisher_year, inventory_count) VALUES "
					+ "('"
					+ isbn
					+ "','"
					+ title
					+ "',"
					+ price
					+ ",'"
					+ genre
					+ "',"
					+ format
					+ ",'"
					+ publisher
					+ "',"
					+ publisher_year
					+ "," + inventory_count + ")";
			int result = db.DB_Update(query, false);
			if (result != 1){
				return Integer.toString(result);
			}
			
			// Insert Author book relation
			for (int i = 0; i < authorid_list.size(); i++){
				query = "INSERT INTO author_book_relation (authorid, isbn) VALUES (" + authorid_list.get(i) + ",'" + isbn + "')";
				result = db.DB_Update(query, false);
			}
			return Integer.toString(result);
		}
		catch (Exception e){
			return e.getMessage();
		}
		
		

	}
	
	public int subtractInventory(String isbn, int num) throws Exception {
		String query = "SELECT inventory_count FROM book WHERE isbn = '" + isbn
				+ "'";
		int inv_count = 0;
		try {
			inv_count = Integer.parseInt(db.DB_Select(query));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		inv_count = inv_count - num;
		query = "UPDATE book SET inventory_count = " + inv_count
				+ " WHERE isbn = '" + isbn + "'";
		int result = db.DB_Update(query, false);
		return result;
	}

	public int increaseInventory(String isbn, int num) throws Exception {
		String query = "SELECT inventory_count FROM book WHERE isbn = '" + isbn
				+ "'";
		int inv_count = 0;
		try {
			inv_count = Integer.parseInt(db.DB_Select(query));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		inv_count = inv_count + num;
		query = "UPDATE book SET inventory_count = " + inv_count
				+ " WHERE isbn = '" + isbn + "'";
		int result = db.DB_Update(query, false);
		return result;
	}

	public ArrayList<Book> addBookToCart(ArrayList<Book> cart, Book book) {
		ArrayList<Book> temp_cart = cart;
		boolean flag = false;
		for (int i = 0; i < cart.size(); i++) {
			if (book.Isbn == cart.get(i).Isbn) {
				// Update count
				Book b = cart.get(i);
				b.Count++;
				temp_cart.set(i, b);
				flag = true;
			}
		}

		if (!(flag)) {
			temp_cart.add(book);
		}
		return temp_cart;
	}

	public int insertTransaction(ArrayList<Book> cart, int customerid) throws Exception {
		String query = "";
		// Ensure inventory will not go negative
		for (int i = 0; i < cart.size(); i++) {
			query = "SELECT inventory_count FROM book WHERE isbn = '" + cart.get(i).Isbn + "'";
			int inv = Integer.parseInt(db.DB_Select(query));
			if ((inv-cart.get(i).Count) <= 0){
				System.out.println("Sorry " + cart.get(i).Isbn +" is out of stock");
				return 216;
			}
		}

		
		// Insert new order id
		int result = 0;
		query = "INSERT INTO book_order VALUES (null)";
		int order_id = db.DB_Update(query, true);

		int total_price = 0;

		for (int i = 0; i < cart.size(); i++) {
			query = "INSERT INTO order_book_relation (orderid, isbn, count) VALUES ("
					+ order_id + ",'" + cart.get(i).Isbn + "'," + cart.get(i).Count + ")";
			
			String query_price  = "SELECT price FROM book WHERE isbn = '"+cart.get(i).Isbn + "'";
			String price_str = db.DB_Select(query_price);
			double price = Double.parseDouble(price_str);
			
			db.DB_Update(query, false);
			int count = cart.get(i).Count;
			total_price += (price * cart.get(i).Count); // In this
																	// case
																	// Count
																	// refers to
																	// how many
																	// to books
																	// to buy
			// If query has issue write to log file
			
			// TODO Update Inventory count
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		query = "INSERT INTO transaction (price, orderid, customerid, date) VALUES ("
				+ total_price + "," + order_id + "," + customerid + ",'" + date + "')";
		result = db.DB_Update(query, false);
		
		// Update inventory
		for (int i = 0; i < cart.size(); i++){
			subtractInventory(cart.get(i).Isbn, cart.get(i).Count);
		}

		return result;
	}
	
	public String findAuthorDegree(int authorid1, int authorid2){
		String query = "SELECT OBR.isbn FROM author_book_relation OBR WHERE OBR.authorid = " +authorid1;
		String[] author1_isbn_list;
		String result = "";
		try {
			result = db.DB_Select(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result.isEmpty())
		{
			return "Author ID 1 has not written any books";
		} 
		else{
			author1_isbn_list = result.split("#");
		}
		
		query = "SELECT OBR.isbn FROM author_book_relation OBR WHERE OBR.authorid = " +authorid2;
		String[] author2_isbn_list;
		result = "";
		try {
			result = db.DB_Select(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result.isEmpty())
		{
			return "Author ID 1 has not written any books";
		} 
		else{
			author2_isbn_list = result.split("#");
		}
		
		for (int i = 0; i < author1_isbn_list.length; i++){
			for (int j = 0; j < author2_isbn_list.length; j++){
				if (author1_isbn_list[i].equals(author2_isbn_list[j])){
					return "1";
				}
			}
		}
		
		// Check for 2 degree
		String query_coauthor = "SELECT authorid FROM author_book_relation ABR1 JOIN " + 
				"(SELECT AB.isbn FROM author_book_relation AB WHERE AB.authorid = "+ authorid1 + ") ABR2 " +
				"WHERE ABR2.isbn = ABR1.isbn AND NOT ABR1.authorid = " + authorid1;
		
		String[] author1_coauthors = new String[0];
		try{
			result = db.DB_Select(query_coauthor);
			author1_coauthors = result.split("#");
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		query_coauthor = "SELECT authorid FROM author_book_relation ABR1 JOIN " + 
				"(SELECT AB.isbn FROM author_book_relation AB WHERE AB.authorid = "+ authorid2 + ") ABR2 " +
				"WHERE ABR2.isbn = ABR1.isbn AND NOT ABR1.authorid = " + authorid2;
		
		String[] author2_coauthors = new String[0];
		try{
			result = db.DB_Select(query_coauthor);
			author2_coauthors = result.split("#");
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		for (int i = 0; i < author1_coauthors.length; i++){
			for (int j = 0; j < author2_coauthors.length; j++){
				if (author1_coauthors[i].equals(author2_coauthors[i])){
					return "2";
				}
			}
		}

		return "0";
		
		
	}

	public int insertOpinion(String isbn, int customerid, int score,
			String desc) throws Exception {
		// Ensure isbn exists
		String query = "SELECT isbn FROM book WHERE isbn = '" + isbn + "'";
		String isbn_check = db.DB_Select(query);
		if (isbn_check.isEmpty()) {
			return 101; // Error code for not exists
		}
		
		// Ensure customerid exists
		query = "SELECT idcustomer FROM customer WHERE idcustomer = '" + customerid
				+ "'";
		String customerid_check = db.DB_Select(query);
		if (customerid_check.isEmpty()) {
			return 101;
		}
		if (score < 0 || score > 10) {
			return 102; // Bad rating
		}

		query = "INSERT INTO opinion (score, description, isbn, customerid) VALUES (" + score
				+ ",'" + desc + "','" + isbn + "'," + customerid + ")";
		
		int opinionid = db.DB_Update(query, false);
		// TODO Ensure customerid and isbn super key is unique.

		return opinionid;
	}
	
	// Feedback on how useful the opinion is
	// Ratings 0 (useless), 1 (useful), 2 (very useful)
	public int insertFeedback(int opinionid, int customerid, int rating) throws Exception{
		// Ensure the rating is between 0 ~ 2
		if (0 > rating || rating > 2){
			System.out.println("Please insert a rating from 0-10");
			return 102;
		}
		
		// Ensure feedback not on own opinions
		String query = "SELECT customerid FROM opinion WHERE idopinion = " + opinionid;
		String str_result = db.DB_Select(query);
		if (str_result.isEmpty()){
			System.out.println("Feedback does not exists");
			return 103;
		} else{
			if (str_result.equals(Integer.toString(customerid))){
				System.out.println("Cannot give usefulness feedbacks on own feedbacks");
				return 104; 
			}
		}
		
		// Ensure the customer exsits.
		query = "SELECT idcustomer FROM customer WHERE idcustomer = " + customerid;
		if (db.DB_Select(query).isEmpty()){
			return 103;
		}
		
		query = "INSERT INTO feedback (customerid, opinionid, rating, date) " +
				"VALUES (" + customerid + "," + opinionid + "," + rating + ",'" + getDate() + "')";
		int result = db.DB_Update(query, false);
		return result;
	}
	
	public String[] topOpinion(int n, String isbn){
		String query = "SELECT O.description FROM opinion O JOIN feedback F JOIN customer C WHERE O.isbn = '" +
				isbn+"' AND O.idopinion = F.opinionid AND C.idcustomer = O.customerid GROUP BY O.idopinion ORDER BY AVG(F.rating) DESC LIMIT "+ n;
		
		String result = "";
		try {
			result = db.DB_Select(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String[] opinions = result.split("#");
		return opinions;
	}
	
	public ArrayList<Opinion> selectOpinions(){
		ArrayList<Opinion> opinions = new ArrayList<Opinion>();
		String query = "SELECT idopinion, score, description, isbn FROM opinion";
		String result = "";
		try {
			result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] opinion_list = result.split("#");
				for (int i = 0; i < opinion_list.length; i++){
					String[] opinion_info = opinion_list[i].split("\\|");
					Opinion o = new Opinion();
					o.Id = opinion_info[0];
					o.Score = Integer.parseInt(opinion_info[1]);
					o.Description = opinion_list[2];
					o.Isbn = opinion_info[3];
					opinions.add(o);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return opinions;
		
	}
	
	public Customer selectCustomerInfo(int customerid){
		Customer c = new Customer();
		
		// Select all personal data
		String query = "SELECT * FROM customer WHERE idcustomer = " + customerid;
		String result = "";
		try {
			result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] str = result.split("\\|");
				c.Id = Integer.parseInt(str[0]);
				c.Login = str[1];
				c.CreditCardNumber = str[3];
				c.PhoneNumber = str[4];
				c.Address = str[5];
				c.Name = str[6];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Select all purchases
		query = "SELECT B.title, SUM(OBR.count) as sum, T.date " +  
				"FROM transaction T " + 
				"JOIN order_book_relation OBR JOIN book B " + 
				"WHERE T.customerid = "+customerid+" AND B.isbn = OBR.isbn  AND T.orderid = OBR.orderid " +
				"GROUP BY OBR.isbn";
		
		ArrayList<Book> sales_list = new ArrayList<Book>();
		
		try{
			result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] books = result.split("#");
				for (int i = 0; i < books.length; i++){
					String[] book_info = books[i].split("\\|");
					Book b = new Book();
					b.Title = book_info[0];
					b.Count = Integer.parseInt(book_info[1]);
					b.Date = book_info[2];
					sales_list.add(b);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		// Select all customers opinion on books
		query = "SELECT B.title, O.score, O.description FROM opinion O JOIN book B " +
				"WHERE O.isbn = B.isbn AND customerid = " + customerid;
		
		ArrayList<Opinion> opinion_list = new ArrayList<Opinion>();
		try{
			result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] opinions = result.split("#");
				for (int i = 0; i < opinions.length; i++){
					Opinion o = new Opinion();
					String[] opinion_info = opinions[i].split("\\|");
					o.Title = opinion_info[0];
					o.Score = Integer.parseInt(opinion_info[1]);
					o.Description = opinion_info[2];
					opinion_list.add(o);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		// Select all feedback usefullness
		query = "SELECT B.title, O.description, F.rating, F.date FROM feedback F JOIN opinion O JOIN book B " + 
				"WHERE F.customerid = 2 AND F.opinionid = O.idopinion AND B.isbn = O.isbn;";
		
		ArrayList<Feedback> feedback_list = new ArrayList<Feedback>();
		
		try{
			result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] feedbacks = result.split("#");
				for (int i = 0; i < feedbacks.length; i++){
					Feedback f = new Feedback();
					String[] feedback_info = feedbacks[i].split("\\|");
					f.Title = feedback_info[0];
					f.OpinionDescription = feedback_info[1];
					f.Rating = Integer.parseInt(feedback_info[2]);
					f.Date = feedback_info[3];
					feedback_list.add(f);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		ArrayList<Trust> trust_list = new ArrayList<Trust>();
		
		// Select all trust
		query = "SELECT C.login, T.trust FROM trust T JOIN customer C WHERE T.give_customerid = "+customerid+" AND C.idcustomer = T.recieve_customerid;";
		try{
			result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] trusts = result.split("#");
				for (int i = 0; i < trusts.length; i++){
					Trust t= new Trust();
					String[] trust_info = trusts[i].split("\\|");
					t.Username = trust_info[0];
					t.Trust = Integer.parseInt(trust_info[1]);
					trust_list.add(t);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		c.SalesList = sales_list;
		c.OpinionList = opinion_list;
		c.FeedbackList = feedback_list;
		c.TrustList = trust_list;

		return c;
	}
	
	public String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		return date;
	}
	
	/**
	 * 
	 * @param customerid_give, the customer id of the giver
	 * @param customerid_recieve, the customer id of the reciever
	 * @param trust, the trust rating, either 0 for false or 1 for true.
	 * @return
	 * @throws Exception 
	 */
	public int insertTrust(int customerid_give, int customerid_recieve, int trust) throws Exception{
		// Ensure both customer id's exists
		String query = "SELECT idcustomer FROM customer WHERE idcustomer= " + customerid_give;
		if (db.DB_Select(query).isEmpty()){
			return 103;
		}
		query = "SELECT idcustomer FROM customer WHERE idcustomer = " + customerid_recieve;
		if (db.DB_Select(query).isEmpty()){
			return 103;
		}
		
		// Ensure the trust value is either 1 or 0
		if (trust < 0 || trust > 1){
			return 102;
		}
		
		query = "INSERT INTO trust (give_customerid, recieve_customerid, trust) VALUES" +
				"(" + customerid_give + "," + customerid_recieve + "," + trust + ")";
		
		int result = db.DB_Update(query, false);
		
		// Update current users trust score
		query = "SELECT trust_score FROM customer WHERE idcustomer = " + customerid_recieve;
		int trust_count = Integer.parseInt(db.DB_Select(query));
		if (trust == 1){
			trust_count++;
		}
		else{
			trust_count--;
		}
		String query_update_trust = "UPDATE customer SET trust_count= " + trust_count
				+ " WHERE idcustomer = '" + customerid_recieve + "'";
		int update_result = db.DB_Update(query_update_trust, false);
		
		return result;
	}
	
	
	public ArrayList<Book> searchBook(String by_title, String by_author, String by_publisher, String by_genre, boolean sort_year, boolean sort_score, boolean sort_score_trust){
		ArrayList<Book> book_list = new ArrayList<Book>();
		String query = "SELECT B.isbn, B.title, A.name, B.publisher, B.genre, B.price, B.publisher_year FROM book B JOIN author_book_relation ABR JOIN author A " + 
						"WHERE ABR.isbn = B.isbn AND A.idauthor = ABR.authorid";
		
		// Add the AND keyword for combining LIKE
		if (!by_title.isEmpty() || !by_author.isEmpty() || !by_publisher.isEmpty() || !by_genre.isEmpty()){
			query += " AND";
		}
		
		if (!by_title.isEmpty()){
			query += " B.title LIKE '%" + by_title + "%' OR";
		}
		
		if (!by_author.isEmpty()){
			query += " A.name LIKE '%" + by_author + "%' OR";
		}
		
		if (!by_publisher.isEmpty()){
			query += " B.publisher LIKE '%" + by_publisher + "%' OR";
		}
		
		if (!by_genre.isEmpty()){
			query += " B.genre LIKE '%" + by_genre + "% OR'";
		}
		
		// Remove the extra last OR
		if (!by_title.isEmpty() || !by_author.isEmpty() || !by_publisher.isEmpty() || !by_genre.isEmpty()){
			query = query.substring(0, query.length()-2);
		}
		
		if (sort_year){
			query += " ORDER BY B.publisher_year DESC";
		}
		
		if (sort_score){
			// Order by average score of opinion
			if (!sort_score_trust)
			{
				query += " ORDER BY (SELECT AVG(score) FROM opinion WHERE isbn = B.isbn) desc";
			}
		}
		
		if (sort_score_trust){
			query += " ORDER BY  (SELECT AVG(score) FROM opinion O JOIN trust T " +
					  "JOIN customer C WHERE O.isbn = B.isbn " +
					  "AND C.idcustomer = T.recieve_customerid AND T.trust = 1) desc;";		
		}
		
		try {
			String result = db.DB_Select(query);
			if (!result.isEmpty()){
			String[] books = result.split("#");
				for (int i = 0; i < books.length; i++){
					String[] book_info = books[i].split("\\|");
					Book b = new Book();
					b.Isbn = book_info[0];
					b.Title = book_info[1];
					b.Author = book_info[2];
					b.Publisher = book_info[3];
					b.Genre = book_info[4];
					b.Price = Integer.parseInt(book_info[5]);
					b.PublisherYear = Integer.parseInt(book_info[6]);
					book_list.add(b);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return book_list;
	}
	
	public ArrayList<String> popular_author(int n){
		String query = "SELECT A.name FROM order_book_relation OBR JOIN book B JOIN author A JOIN author_book_relation ABR " +
				"WHERE B.isbn = OBR.isbn AND ABR.isbn = B.isbn AND A.idauthor = ABR.authorid "+
				"GROUP by A.idauthor "+ 
				"ORDER BY OBR.count desc "+
				"LIMIT " + 5;	
		ArrayList<String> author_list = new ArrayList<String>();
		try {
				String result = db.DB_Select(query);
				if (!result.isEmpty()){
				String[] authors = result.split("#");
				for (int i = 0; i < authors.length; i++){
					author_list.add(authors[i]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return author_list;
	}
	
	public ArrayList<String> popular_publisher(int n){
		String query = "SELECT B.publisher FROM order_book_relation OBR JOIN book B  WHERE B.isbn = OBR.isbn "+
					   "GROUP by B.publisher ORDER BY OBR.count desc LIMIT " + n;
		
		ArrayList<String> publisher_list = new ArrayList<String>();
		try {
				String result = db.DB_Select(query);
				if (!result.isEmpty()){
				String[] publishers = result.split("#");
				for (int i = 0; i < publishers.length; i++){
					publisher_list.add(publishers[i]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return publisher_list;
		
	}
	
	public ArrayList<Book> popular_book(int n)
	{
		String query = "SELECT OBR.isbn, B.title FROM order_book_relation OBR JOIN book B " +
					"WHERE B.isbn = OBR.isbn " +
					"GROUP by OBR.isbn " +
					"ORDER BY OBR.count desc " +
					"LIMIT " + n;
		
		
		ArrayList<Book> book_list = queryBook(query); 
		
		return book_list;
	}
	
	public ArrayList<Book> selectBooks(){
		String query = "SELECT B.isbn, B.title FROM book B";
		ArrayList<Book> book_list = queryBook(query);
		return book_list;
	}
	
	public HashMap<String, String> selectAllAuthors(){
		HashMap<String, String> map = new HashMap();
		String query = "SELECT idauthor, name FROM author ORDER BY idauthor ASC";
		String result = "";
		try{
		result = db.DB_Select(query);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		String[] authors = result.split("#");
		for (int i = 0; i < authors.length; i++){
			String[] author_info = authors[i].split("\\|");
			map.put(author_info[0], author_info[1]);
			
		}
		return map;
	}
	
	public ArrayList<Book> queryBook(String query){
		ArrayList<Book> book_list = new ArrayList<Book>();
		try {
			String result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] books = result.split("#");
				for (int i = 0; i < books.length; i++)
				{
					String[] book_info = books[i].split("\\|");
					Book b = new Book();
					b.Isbn = book_info[0];
					b.Title = book_info[1];
					book_list.add(b);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return book_list;
	}
	
	public ArrayList<Customer> mostUseful(int n){
		String query = "SELECT C.idcustomer, C.name, AVG(F.rating) FROM feedback F JOIN customer C " +  
				"WHERE F.customerid = C.idcustomer " +
				"GROUP BY C.idcustomer " +
				"ORDER BY AVG(F.rating) DESC LIMIT " + n;
		ArrayList<Customer> customer_list = new ArrayList<Customer>();
		try {
			String result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] customers = result.split("#");
				for (int i = 0; i < customers.length; i++){
					String[] customer_info = customers[i].split("\\|");
					Customer c = new Customer();
					c.Id = Integer.parseInt(customer_info[0]);
					c.Name = customer_info[1];
					customer_list.add(c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return customer_list;
		
	}
	
	public ArrayList<Customer> mostTrusted(int n)
	{
		String query = "SELECT C.idcustomer, C.name, C.trust_score FROM customer C ORDER BY C.trust_score DESC " +
				"LIMIT " + n;

		ArrayList<Customer> customer_list = new ArrayList<Customer>();
		try {
			String result = db.DB_Select(query);
			if (!result.isEmpty()){
				String[] customers = result.split("#");
				for (int i = 0; i < customers.length; i++){
					String[] customer_info = customers[i].split("\\|");
					Customer c = new Customer();
					c.Id = Integer.parseInt(customer_info[0]);
					c.Name = customer_info[1];
					customer_list.add(c);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return customer_list;
	}
	
	public ArrayList<Book> recommendedBooks(String isbn){
		String query = "SELECT B.isbn, B.title FROM transaction T JOIN order_book_relation OBR JOIN customer C JOIN book B " +
						"JOIN (SELECT T.customerid FROM order_book_relation OBR JOIN transaction T WHERE OBR.isbn = '" +isbn+"' AND T.orderid = OBR.orderid) CB " +
						"WHERE CB.customerid = C.idcustomer AND T.orderid = OBR.orderid AND C.idcustomer = T.customerid AND B.isbn = OBR.isbn " +
						"GROUP BY OBR.isbn ORDER BY SUM(OBR.count) DESC";
		ArrayList<Book> book_list = queryBook(query);
		return book_list;
	}

	public static void main(String[] args) throws Exception {
		BookStoreLogic bsl = new BookStoreLogic();
		int result = 99999;
		String isbn = "0547928211";
		boolean rtn = false;
		ArrayList<Book> cart = new ArrayList<Book>();

//		int check = bsl.registerCustomer("Kevin_Person", "password", "1234567",
//		"801-999-9999", "Tiger Street", "Kevin");
		// boolean rtn = bsl.loginCustomer("austint", "password");
		// Book b1 = new Book("0439708184",
		// "Harry Potter and the Sorcerer's Stone", 7.99, "Adventure", 0,
		// "Scholastic", 1997, 1, "J.K. Rowling");
		// Book b2 = new Book("0439064872",
		// "Harry Potter And The Chamber Of Secrets", 7.99, "Adventure", 0,
		// "Scholastic", 2000, 1, "J.K. Rowling");
		// cart.add(b1);
		// cart.add(b2);

		// public int insertBook(String isbn, String title, double price, String
		// genre, int format, String publisher, int publisher_year, int
		// inventory_count, String author) throws Exception{
		// bsl.insertBook(b2.Isbn, b2.Title, b2.Price, b2.Genre, b2.Format,
		// b2.Publisher, b2.PublisherYear, 5, b2.Author);
		// result = bsl.increaseInventory(isbn, 3);
		
		//bsl.insertTransaction(cart);
		
		//result = bsl.insertOpinion(isbn, 4, 10 ,"Great Book");
		//result = bsl.insertFeedback(7, 11, 2);
		//bsl.topOpinion(2, isbn);
		//bsl.insertAuthor("J.R.R. Tolkien");
		//bsl.insertBook("0547928211", "The Fellowship of the Ring: Being the First Part of The Lord of the Rings", 13.95, "Adventure", 0, "Mariner Books", 2012, 20, "J.R.R. Tolkien");
		//Book lotr = new Book("0547928211", "The Fellowship of the Ring: Being the First Part of The Lord of the Rings", 13.95, "Adventure", 0, "Mariner Books", 2012, 1, "J.R.R. Tolkien");
		//cart.add(lotr);
		//bsl.insertTransaction(cart, 15);
		//bsl.insertTrust(1, 2, 1);
		//bsl.insertTrust(1, 4, 0);
		//bsl.insertTrust(1, 7, 1);
		//bsl.selectCustomerInfo(1);
		//bsl.searchBook("Har", "", "", "");
		//System.out.println(result);
		//bsl.searchBook("har", "", "", "", false, false, false);
		//bsl.searchBook("", "Tol", "", "", false, false, false);
		//		bsl.searchBook("", "", "Sch", "", false, false, false);
		//bsl.searchBook("", "", "", "Adv", false, false, false);
		//bsl.searchBook("", "", "", "", true, false, false);
		//bsl.searchBook("", "", "", "", false, true, false);
		//bsl.searchBook("cha", "harr", "", "", false, false, true);
		//bsl.popular_author(3);
		//bsl.popular_publisher(2);
		//bsl.popular_book(2);
		//bsl.mostTrusted();
		//bsl.mostUseful(3);

//		ArrayList<String> authors = new ArrayList<String>();
//		authors.add("Greh H. Greg");
//		authors.add("Austin Truong");
//		String return_ = bsl.insertBook("11231213", "Checking 2 degree", 4.95, "Horror", 0, "Blie Books", 2014, 5, authors);
		//System.out.println(return_);
		//System.out.println("Complete");

	}

}
