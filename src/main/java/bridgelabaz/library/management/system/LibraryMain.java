package bridgelabaz.library.management.system;

import java.sql.SQLException;
import java.util.List;

public class LibraryMain {

	public static void main(String[] args) throws SQLException {
		Library lib=new Library();

		//		1.add book to db
//				lib.addBook(new Book(1,"java" , "james gosling", true));


		//		2.remove book from db
		//		lib.removeBook(1);

		//	     3. Search Books by Title, Author, or ISBN	
		//		 lib.searchBooks("java").forEach(System.out::println);

		//		   4.add members
		//         lib.registerMember(new Member(2, "shahbaz"));

		//		  5.remove member 
		//          lib.removeMember(1);

		//        6.  borrow book
		//          lib.borrowBook(1, 1);

		//		7.return book
		//		lib.returnBook(1,1);

		//		8.log a borrow record
		//		lib.logBorrowRecord(1,1 );

		//		9.update return record
		//		lib.updateReturnRecord(1, 1);

		//	  10.view browwing history of a member
//		   lib.getBorrowRecordsForMember(1).forEach(System.out::println);

		//	11.	viewCurrentlyBorrowedBooksByMember
//            lib.viewCurrentlyBorrowedBooksByMember(1).forEach(System.out::println);


	}

}
