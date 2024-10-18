package bridgelabaz.library.management.system;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bridgelabaz.library.management.system.databaseconnection.DatabaseConnection;

public class Library {

	//	--------------------------------------------BOOK--------------------------------------------------------------------------------------------------------

	// 1. Add a new book to the database -------------------------------------------------------------------------------------------------------------------

	public void addBook(Book book) {
		String sql = "INSERT INTO book (isbn, tittle, author, isavailable) VALUES (?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, book.getIsbn());
			pstmt.setString(2, book.getTitle());  // Updated from title to tittle
			pstmt.setString(3, book.getAuthor());
			pstmt.setBoolean(4, book.isAvailable());

			pstmt.executeUpdate();
			System.out.println("Book added to the database.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 2.Remove a book from the database-----------------------------------------------------------------------------------------------------------------------

	public void removeBook(int isbn) {
		String sql = "DELETE FROM book WHERE isbn = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, isbn);
			pstmt.executeUpdate();
			System.out.println("Book removed from the database.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 3. Search for books by title, author, or ISBN---------------------------------------------------------------------------------------------------------

	public List<Book> searchBooks(String searchQuery) {
		List<Book> books = new ArrayList<>();
		String sql = "SELECT * FROM book WHERE tittle LIKE ? OR author LIKE ? OR isbn = ?"; // Updated from title to tittle

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, "%" + searchQuery + "%");
			pstmt.setString(2, "%" + searchQuery + "%");
			pstmt.setInt(3, Integer.parseInt(searchQuery)); // This might throw NumberFormatException if not a valid ISBN

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Book book = new Book(
						rs.getInt("isbn"),
						rs.getString("tittle"),  // Updated from title to tittle
						rs.getString("author"),
						rs.getBoolean("isavailable")
						);
				books.add(book);
			}

		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}

		return books;
	}

//----------------------------------Member Management------------------------------------------------------------------------------------------------------------------
	
	// 4. Register a new member-------------------------------------------------------------------------------------------------------------------------------------------

	public void registerMember(Member member) {
		String sql = "INSERT INTO member (member_id, name) VALUES (?, ?)"; // Updated from members to member

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, member.getMemberId());
			pstmt.setString(2, member.getName());

			pstmt.executeUpdate();
			System.out.println("Member registered successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 5. Remove a member from the database-------------------------------------------------------------------------------------------------------------------------

	public void removeMember(int memberId) {
		String sql = "DELETE FROM member WHERE member_id = ?"; // Updated from members to member

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberId);
			pstmt.executeUpdate();
			System.out.println("Member removed from the database.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	 // 6. View member details-------------------------------------------------------------------------------------------------------
	
    public void viewMemberDetails(int memberId) {
        Member member = findMemberById(memberId);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.println("Member Details:");
        System.out.println("ID: " + member.getMemberId());
        System.out.println("Name: " + member.getName());

        System.out.println("Borrow Records:");
        List<BorrowRecord> borrowRecords = member.getBorrowRecords();
        if (borrowRecords.isEmpty()) {
            System.out.println("No borrow records.");
        } else {
            for (BorrowRecord record : borrowRecords) {
                System.out.println(record);
            }
        }
    }
    
//    ----------------------------------Borrowing Books--------------------------------------------------------------------------------------------------------

	// 7. Borrow a book--------------------------------------------------------------------------------------------------------------------------------------------

	public void borrowBook(int memberId, int isbn) {
		Member member = findMemberById(memberId);
		if (member == null) {
			System.out.println("Member not found.");
			return;
		}

		Book book = findBookByIsbn(isbn);
		if (book == null) {
			System.out.println("Book not found.");
			return;
		}

		if (!book.isAvailable()) {
			System.out.println("Book is not available for borrowing.");
			return;
		}

		if (!member.canBorrow()) {
			System.out.println("Member cannot borrow more books.");
			return;
		}

		String sql = "INSERT INTO borrowrecords (memberId, isbn, borrowDate) VALUES (?, ?, ?)"; // Updated table name from borrowrecords to borrowrecord

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberId);
			pstmt.setInt(2, isbn);
			pstmt.setDate(3, Date.valueOf(LocalDate.now()));

			pstmt.executeUpdate();

			// Update book availability
			updateBookAvailability(isbn, false);

			System.out.println("Book borrowed successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//8. Return a book-------------------------------------------------------------------------------------------------------------------------------------------

	public void returnBook(int memberId, int isbn) {
		Member member = findMemberById(memberId);
		if (member == null) {
			System.out.println("Member not found.");
			return;
		}

		BorrowRecord record = member.getBorrowRecords().stream()
				.filter(r -> r.getBook().getIsbn() == isbn && r.getReturnDate() == null)
				.findFirst()
				.orElse(null);

		if (record != null) {
			record.setReturnDate(LocalDate.now());

			// Update book availability in the database
			updateBookAvailability(isbn, true);

			// Update borrow record with return date
			String sql = "UPDATE borrowrecords SET returnDate = ? WHERE memberId = ? AND isbn = ?"; // Updated table name from borrowrecords to borrowrecord

			try (Connection conn = DatabaseConnection.getConnection();
					PreparedStatement pstmt = conn.prepareStatement(sql)) {

				pstmt.setDate(1, Date.valueOf(LocalDate.now()));
				pstmt.setInt(2, memberId);
				pstmt.setInt(3, isbn);

				pstmt.executeUpdate();
				System.out.println("Book returned successfully.");

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("This book was not borrowed.");
		}
	}

	// Helper method to update book availability--------------------------------------------------------------------------------------------------------------------\

	private void updateBookAvailability(int isbn, boolean isAvailable) {
		String sql = "UPDATE book SET isavailable = ? WHERE isbn = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setBoolean(1, isAvailable);
			pstmt.setInt(2, isbn);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Helper method to find a book by ISBN------------------------------------------------------------------------------------------------------------------------------

	private Book findBookByIsbn(int isbn) {
		String sql = "SELECT * FROM book WHERE isbn = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, isbn);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return new Book(
						rs.getInt("isbn"),
						rs.getString("tittle"),  // Updated from title to tittle
						rs.getString("author"),
						rs.getBoolean("isavailable")
						);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	// Helper method to find a member by ID------------------------------------------------------------------------------------------------------------------

	public Member findMemberById(int memberId) {
	    String sql = "SELECT * FROM member WHERE memberId = ?";
	    Member member = null;

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, memberId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                member = new Member(
	                    rs.getInt("memberId"),
	                    rs.getString("name")
	                );
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return member;
	}
	
	



	// Helper method to fetch borrow records for a member------------------------------------------------------------------------------------------------------

	public List<BorrowRecord> getBorrowRecordsForMember(int memberId) {
	    List<BorrowRecord> records = new ArrayList<>();

	    // SQL to get borrow records and associated book details
	    String sql = "SELECT br.*, b.isbn, b.tittle, b.author, b.isavailable " +
	                 "FROM borrowrecords br " +
	                 "JOIN book b ON br.isbn = b.isbn " +
	                 "WHERE br.memberId = ?";

	    // Fetch member details once
	    Member member = null;
	    try (Connection conn = DatabaseConnection.getConnection()) {
	    	System.out.println(memberId);
	        member = findMemberById(memberId);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, memberId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Book book = new Book(
	                    rs.getInt("isbn"),
	                    rs.getString("tittle"),
	                    rs.getString("author"),
	                    rs.getBoolean("isavailable")
	                );

	                // Create BorrowRecord with the fetched member details
	                BorrowRecord record = new BorrowRecord(
	                    book,
	                    member
	                );
	                record.setBorrowDate(rs.getDate("borrowDate").toLocalDate());
	                record.setReturnDate(rs.getDate("returnDate") != null ? rs.getDate("returnDate").toLocalDate() : null);

	                records.add(record);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return records;
	}


	
	

	// Log a borrow record------------------------------------------------------------------------------------------------------------------------------------

	public void logBorrowRecord(int memberId, int isbn) {
		String sql = "INSERT INTO borrowrecords (memberId, isbn, borrowDate) VALUES (?, ?, ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberId);
			pstmt.setInt(2, isbn);
			pstmt.setDate(3, Date.valueOf(LocalDate.now()));

			pstmt.executeUpdate();
			System.out.println("Borrow record logged.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Update return record-----------------------------------------------------------------------------------------------------------------------------------------

	public void updateReturnRecord(int memberId, int isbn) {
		String sql = "UPDATE borrowrecords SET returnDate = ? WHERE memberId = ? AND isbn = ? AND returnDate IS NULL";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDate(1, Date.valueOf(LocalDate.now()));
			pstmt.setInt(2, memberId);
			pstmt.setInt(3, isbn);

			pstmt.executeUpdate();
			System.out.println("Return record updated.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	
	
	 public List<Book> viewCurrentlyBorrowedBooksByMember(int memberId) throws SQLException {
	        List<Book> borrowedBooks = new ArrayList<>();
	        String query = "SELECT b.isbn, b.tittle, bb.borrowdate " +
	                       "FROM book b " +
	                       "JOIN borrowrecords bb ON b.isbn = bb.isbn " +
	                       "WHERE bb.memberid = ? AND bb.returndate IS NULL";

	        try (Connection conn = DatabaseConnection.getConnection();
	        		PreparedStatement statement = conn.prepareStatement(query)) {
	            statement.setInt(1, memberId);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                while (resultSet.next()) {
	                    int isbn = resultSet.getInt("isbn");
	                    String title = resultSet.getString("tittle");
	                    LocalDate borrowDate = resultSet.getDate("borrowdate").toLocalDate();
	                   
	                    borrowedBooks.add(new Book(isbn, title,"",false )); // assuming Book class has a constructor with title and borrowDate
	                }
	            }
	        }
	        return borrowedBooks;
	    }
	
	
	
}