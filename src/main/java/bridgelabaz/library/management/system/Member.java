package bridgelabaz.library.management.system;

import java.util.ArrayList;
import java.util.List;

public class Member {
	
	private int memberId;
	private String name;
	private List<BorrowRecord> borrowRecords;
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BorrowRecord> getBorrowRecords() {
		return borrowRecords;
	}
	public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
		this.borrowRecords = borrowRecords;
	}
	
	 public Member(int memberId, String name) {
	        this.memberId = memberId;
	        this.name = name;
	        this.borrowRecords = new ArrayList<>();
	    }
	

	    public boolean canBorrow() {
	        return borrowRecords.stream().filter(record -> record.getReturnDate() == null).count() < 5;
	    }

	
	
	

}
