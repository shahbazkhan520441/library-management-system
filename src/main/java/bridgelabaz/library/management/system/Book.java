package bridgelabaz.library.management.system;

public class Book {
	
	private int isbn;
	private String title;
	private String author;
	private boolean isAvailable;
	
	public int getIsbn() {
		return isbn;
	}
	public void setIsbn(int isbn) {
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public boolean isAvailable() {
		return isAvailable;
	}
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	public Book(int isbn, String title, String author, boolean isAvailable) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.isAvailable = isAvailable;
	}
	@Override
	public String toString() {
		return "Book [isbn=" + isbn + ", title=" + title + ", author=" + author + ", isAvailable=" + isAvailable + "]";
	}
	
	

	
	

}
