package cs5530;

public class Book {
	
	public String Isbn;
	public String Title;
	public double Price;
	public String Genre;
	public int Format;
	public String Publisher;
	public int PublisherYear;
	public int Count;
	public String Author;
	public String Date;
	 
	
	public Book(String isbn, String title, double price, String genre, int format, 
			String publisher, int publisheryear, int count, String author){
		Title = title;
		Isbn = isbn;
		Price = price;
		Genre = genre;
		Format = format;
		Publisher = publisher;
		PublisherYear = publisheryear;
		Count = count;
		Author = author;
	}
	
	public Book(){
	
	}

}
