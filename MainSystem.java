public class MainSystem {
	public static void main (String [] args) {
	
		
	}
}

class User {
	private int systemNumber;
	private String firstname;
	private String lastname;
	private int pesel;
	private String adress;
	Account account;
	//private double resources;
	
	
	User(int sNo, String fname, String lname, int p, String adr, double money){
			systemNumber = sNo;
			firstname = new String (fname);
			lastname = new String (lname);
			pesel = p;
			adress = new String (adr);
			account = new Account (systemNumber, money);
			//resources = account.getResources();
	}
	
	void display () {
		System.out.println("No. " + systemNumber + " " + firstname + " " + lastname + " " + pesel + " " + adress + " " + account.getResources());
	}
}

class Account{
	private double resources;
	private int userSystemNumber;
	
	Account(int userSysNo, double money){
		resources = money;
		userSystemNumber = userSysNo;
	}
	
	double getResources () {
		return resources;
	}
	
	void payment (double money) {
		resources += money;
	}
	
	void payout (double money) {
		if (money <= resources) {
			resources -= money;
		} else {
			System.out.println("Brak wystarczajacych srodkow!");
		}
	}
	
	
	 
}

class BankSystem{
	private User [] users; 
	private int size;
	
	BankSystem(){
		size = 0;
	}
	
	void addUser (int sNo, String fname, String lname, int p, String adr, double money){
		users[size++] = new User(sNo, fname, lname, p, adr, money);
	}
	
	void displayAll () {
		for(int i = 0; i < size ; i++) {
				users[i].display();
		}
	}
	
	int find () {
		
		return 0; // do zmiany
	}
	
}
