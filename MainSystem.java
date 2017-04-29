import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;


public class MainSystem {
	public static void main (String [] args) {
			if(args.length == 0) {
				BankSystem bank1 = new BankSystem();
				bank1.menu();
			} else {
				BankSystem bank1 = new BankSystem(args[0]);
				bank1.menu();
			} 
		
	}
}

class NoUserFindException extends Exception {
	NoUserFindException() {
	//his.printStackTrace();
	}
};

class BankSystem implements Serializable {
	private Vector<User> users;
	String filename;
	transient Scanner in = new Scanner (System.in);

	BankSystem() {
		System.out.println("Please enter name for database: ");
		filename = in.nextLine();
		users = new Vector<User> ();
	}
	
	BankSystem(String filename) {
		try{
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			BankSystem tmp1 = (BankSystem) ois.readObject();
			ois.close();
			this.users = tmp1.users;
			this.filename = tmp1.filename;
		} catch (Exception e) {
			System.out.println("Exception while load bank state");
			System.exit(1);
		}
	}
	
	void saveState() {
		try{
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			System.out.println("Exception while save bank state");
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	void menu() {
		int choise;
		
		do{		
			choise = chooseMenu();
			switch(choise) {
			case 0:
				System.exit(0);
			case 1:
				addUser();
				break;
			case 2:
				User todelete = enterUserNumber("delete");
				deleteUser(todelete);
				break;
			case 3:
				User topay = enterUserNumber("payment");
				payIn(topay);
				break;
			case 4:
				User totake = enterUserNumber("pay out");
				payOut(totake);
				break;
			case 5:
				transferMoney();
				break;
			case 6:
				displayAll();
				break;
			case 7:
				displaySpecific();
				break;
			case 8:
				saveState();
				break;
			}
			System.out.println();
		} while (true);
	}
	
	void menuDisplayOption() { 
		System.out.println("Welcome in our bank system. What would you like to do?");
		System.out.println("1. Add user");
		System.out.println("2. Delete user");
		System.out.println("3. Pay in account");
		System.out.println("4. Pay out from account");
		System.out.println("5. Transfer money between accounts");
		System.out.println("6. Display information about all acounts");
		System.out.println("7. Display information about specific accounts");
		System.out.println("8. Save state");
		System.out.println("0. Quit");
	}
	
	int chooseMenu () {
		int choise = 0;
		
		do {
			menuDisplayOption();
			choise = in.nextInt();
		} while (choise < 0 || choise > 8);
		
		return choise;
	}
	
	void addUser() {
		System.out.println("Enter system number: ");
		int sNo = in.nextInt();
		in.nextLine();
		System.out.println("Enter first name: ");
		String fname = in.nextLine();
		System.out.println("Enter last name: ");
		String lname = in.nextLine();
		System.out.println("Enter PESEL:");
		int pesel = in.nextInt();
		in.nextLine();
		System.out.println("Enter adress:");
		String adress = in.nextLine();
		System.out.println("Enter amount of money: ");
		double money = in.nextDouble();
		in.nextLine();
		addUser(sNo, fname, lname, pesel, adress, money);
	}

	void addUser (int sNo, String fname, String lname, int p, String adr, double money){
		users.addElement(new User(sNo, fname, lname, p, adr, money));
	}
	
	private User enterUserNumber (String text) {
		System.out.println("Enter system number of user to " + text);
		int number = in.nextInt();
		try {
			User tmp = this.findByNumber(number);
			return tmp;
		} catch (NoUserFindException e) {
			e.printStackTrace();
		}
		return null;
	}
	void payIn(User topay) {
		System.out.println("Enter amount of money to pay in: ");
		double moneytopay = in.nextDouble();
		topay.account.payment(moneytopay);
	}
	
	void payOut(User topayout) {
		System.out.println("Enter amount of money to pay out: ");
		double moneytopayout = in.nextDouble();
		topayout.account.payout(moneytopayout);
	}
	
	void transferMoney() {
		
	}
	
	void deleteUser(User todelete) {
		users.remove(todelete);
}
	
	void displayAll () {
		System.out.println();
		System.out.println("No.\t FName\t\t LName\t\t PESEL\t Adress\t\t Money");
	    Iterator<User> it = users.iterator();
		while(it.hasNext()) {
				it.next().display();
		}
		System.out.println();
	}
	
	void displaySpecific() {
		try {
			Vector<User> tmp = find();
			Iterator<User> it = tmp.iterator();
			while(it.hasNext()) {
				it.next().display();
			}
		} catch (NoUserFindException e) {
			System.out.println("No such user!");
			return;
		}
	}
	
	
	void findDisplay () {
		System.out.println("Do you want search by: ");
		System.out.println("1. Users number in system");
		System.out.println("2. Name");
		System.out.println("3. Last name");
		System.out.println("4. PESEL");
		System.out.println("5. Adress");
		System.out.println("0. Exit");
	}
	
	int chooseFind () {
		int choise;
		do {
			findDisplay();
			choise = in.nextInt();
		} while (choise < 0 || choise > 5);
			
		return choise;
		
	}
	
	Vector<User> find () throws NoUserFindException {
		int choise = chooseFind();
		Vector<User> usersfinded = new Vector<User> ();
		User user = null;
		in.nextLine();
		
		try {
			switch (choise) {
			case 0:
				break;
			case 1:
				user = findByNumber();
				usersfinded.add(user);
				break;
			case 2:
				usersfinded = findByName();
				break;
			case 3:
				usersfinded = findByLastName();
				break;
			case 4:
				user = findByPesel();
				usersfinded.add(user);
				break;
			case 5:
				usersfinded = findByAdress();
				break;
			default:
				System.out.println("Something went wrong!");
				break;
			}
		} catch (NoUserFindException e){
			throw e;
		}

		return usersfinded; 
	}
	
	private User findByNumber() throws NoUserFindException {
		User user;
		
		
		System.out.println("Enter system number of user: ");
		int numbertofind = in.nextInt();
		
		try {
			user = findByNumber(numbertofind);
		} catch (NoUserFindException e) {
			throw e;
		}
		
		return user;
	}
	
	private User findByNumber(int number) throws NoUserFindException {
		
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User searched = it.next();
			if (searched.getNumber() == number) {
				return searched;
			}
		}
		throw new NoUserFindException();
	}
	
	private Vector<User> findByName () throws NoUserFindException {
		Vector<User> usersfinded = new Vector<User> ();

		
		System.out.println("Enter name of user: ");
		String name = new String(in.nextLine());
		
		try {
			usersfinded = findByName(name);
		} catch (NoUserFindException e) {
			throw e;
		}
		
		return usersfinded;
	}
	
	private Vector<User> findByName (String name) throws NoUserFindException {
		Vector<User> usersfinded = new Vector<User> ();
		
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User searched = it.next();
			if (searched.getName().compareTo(name) == 0) {
				usersfinded.add(searched);
			}
		}
		
		if(usersfinded.isEmpty()) {
			throw new NoUserFindException();
		} else {
			return usersfinded;
		}
	}
	
	private Vector<User> findByLastName () throws NoUserFindException {
		Vector<User> usersfinded = new Vector<User> ();
		
		String lastname;
		
		System.out.println("Enter lastname of user: ");
		lastname = in.nextLine();
		
		try {
			usersfinded = findByLastName(lastname);
		} catch (NoUserFindException e) {
			throw e;
		}
		
		return usersfinded;
	}
	
	private Vector<User> findByLastName (String lastname) throws NoUserFindException {
		Vector<User> usersfinded = new Vector<User> ();
		
		
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User searched = it.next();
			if (searched.getLastName().compareTo(lastname) == 0) {
				usersfinded.add(searched);
			}
		}
		
		if(usersfinded.isEmpty()) {
			throw new NoUserFindException();
		} else {
			return usersfinded;
		}
	}
	
	private Vector <User> findByAdress () throws NoUserFindException {
		Vector<User> usersfinded = new Vector<User> ();
		String adress;
		
		System.out.println("Enter adress of user: ");
		adress = in.nextLine();
		
		try {
			usersfinded = findByAdress(adress);
		} catch (NoUserFindException e) {
			throw e;
		}
		
		return usersfinded;
	}
	
	private Vector <User> findByAdress (String adress) throws NoUserFindException {
		Vector<User> usersfinded = new Vector<User> ();
		
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User searched = it.next();
			if (searched.getAdress().compareTo(adress) == 0) {
				usersfinded.add(searched);
			}
		}
		if (usersfinded.isEmpty()) {
			throw new NoUserFindException();
		} else {
			return usersfinded;
		}
	}
	
	private User findByPesel() throws NoUserFindException {
		User user;		
		
		System.out.println("Enter PESEL number: ");
		int numbertofind = in.nextInt();
		
		try {
			user = findByPesel(numbertofind);
		} catch (NoUserFindException e) {
			throw e;
		}
		
		return user;
	}
	
	private User findByPesel(int number) throws NoUserFindException {
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User searched = it.next();
			if (searched.getPesel() == number) {
				return searched;
			}
		}
		throw new NoUserFindException();
	}
	
}

class Account implements Serializable {
	private double resources;
	//private int userSystemNumber;
	
	Account(double money) {
		resources = money;
	//	userSystemNumber = userSysNo;
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
			System.out.println("There is no resources to do this!");
		}
	}
	
	
	 
}

class User implements Serializable {
	private int systemNumber;
	private String firstname;
	private String lastname;
	private int pesel;
	private String adress;
	Account account;
	
	//private double resources;
	
	
	User(int sNo, String fname, String lname, int p, String adr, double money){
			systemNumber = sNo;
			firstname = fname;
			lastname = lname;
			pesel = p;
			adress = adr;
			account = new Account (money);
			//resources = account.getResources();
	}
	
	void display () {
		System.out.println(systemNumber + "\t" + firstname + "\t" + lastname + "\t" + pesel + "\t" + adress + "\t" + account.getResources());
	}
	
	static boolean compareUsers (User a, User b){
		if( a.systemNumber == b.systemNumber) {
			return true;
		} else {
			return false;
		}
	}
	
	int getNumber() {
		return systemNumber;
	}
	
	String getName () {
		return firstname;
	}
	
	String getLastName () {
		return lastname;
	}
	
	int getPesel () {
		return pesel;
	}
	
	String getAdress () {
		return adress;
	}
	
}
