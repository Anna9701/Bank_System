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

class NoResourcesException extends Exception {
	NoResourcesException() {
		
	}
}

class BankSystem implements Serializable {
	private Vector<User> users;
	private String filename;
	private transient Scanner in = new Scanner (System.in);

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
	
	void deleteUser() {
		User todelete;
		try {
			todelete = enterUserNumber("delete");
			if(confirm("delete", todelete)) {
				deleteUser(todelete);
			}
		} catch (NoUserFindException e1) {
			System.out.println("No such user find!");
		}
	}
	
	void toPay() {
		User topay;
		try {
			topay = enterUserNumber("payment");
			if(confirm("payment", topay)) {
				payIn(topay);
			}
		} catch (NoUserFindException e1) {
			System.out.println("No such user find!");
		}
	}
	
	void toTake() {
		User totake;
		try {
			totake = enterUserNumber("pay out");
			if(confirm("payout", totake)) {
				try {
					payOut(totake);
				} catch (NoResourcesException e) {
					System.out.println("There is no resources to do this!");
				}
			}
		} catch (NoUserFindException e1) {
			System.out.println("No such user find!");
		}
	}
	
	void menu() {
		int choise;
		
		do{	
			choise = chooseMenu();
			switch(choise) {
				case 0:
					if(confirm("exit")) {
						System.exit(0);
					} else {
						break;
					}
				case 1:
					addUser();
					break;
				case 2:
					deleteUser();
					break;
				case 3:
					toPay();
					break;
				case 4:
					toTake();
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
					if(confirm("save state")) {
						saveState();
					}
					break;
				}
			System.out.println();
			
		} while (true);
	}
	
	private boolean confirm (String text) {
		System.out.println("Do you want to confirm " + text + "?");	
		return confirmPress();
	}
	
	private boolean confirmPress() {
		System.out.println("Press Y to confirm. Other keys will abort");
		char choise = Character.toUpperCase(in.next().charAt(0));
		if(choise == 'Y') {
			return true;
		} else {
			return false;
		}
	}
	private boolean confirm (String text, User u) {
		System.out.println("Do you want to confirm " + text + ":");
		u.display();
		return confirmPress();
	}
	
	private void menuDisplayOption() { 
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
	
	private int chooseMenu () {
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
		long pesel = in.nextLong();
		in.nextLine();
		while((int)(Math.log10(pesel)+1) != 11) {
			System.out.println("Wrong pesel");
					pesel = in.nextLong();
					in.nextLine();
		}
		System.out.println("Enter adress:");
		String adress = in.nextLine();
		System.out.println("Enter amount of money: ");
		double money = in.nextDouble();
		in.nextLine();
		addUser(sNo, fname, lname, pesel, adress, money);
	}

	void addUser (int sNo, String fname, String lname, long p, String adr, double money){
		User add = new User(sNo, fname, lname, p, adr, money);
		if(confirm("add", add)) {
			users.addElement(add);
		}
	}
	
	private User enterUserNumber (String text) throws NoUserFindException {
		System.out.println("Enter system number of user to " + text);
		int number = in.nextInt();
		try {
			User tmp = this.findByNumber(number);
			return tmp;
		} catch (NoUserFindException e) {
			throw e;
		}
	}
	
	void payIn(User topay) {
		System.out.println("Enter amount of money to pay in: ");
		double moneytopay = in.nextDouble();
		if(moneytopay <= 0) {
			System.out.println("You cannot pay in less than 0!");
			return;
		}
		if(confirm("amount " + Double.toString(moneytopay))) {
			topay.account.payment(moneytopay);
		}
	}
	
	private void payIn(User topay, double money) {
		topay.account.payment(money);
	}
	
	void payOut(User topayout) throws NoResourcesException {
		System.out.println("Enter amount of money: ");
		double moneytopayout = in.nextDouble();
		if(moneytopayout <= 0) {
			System.out.println("You cannot take less than 0!");
			return;
		}
		if(confirm("amount " + Double.toString(moneytopayout))) {
			try {
				topayout.account.payout(moneytopayout);
			} catch (NoResourcesException e) {
				throw e;
			}
		}
	}
	
	private double payOutTransfer(User topayout) throws NoResourcesException {
		final int smtwentwrong = -1;
		System.out.println("Enter amount of money: ");
		double moneytopayout = in.nextDouble();
		if(moneytopayout <= 0) {
			System.out.println("You cannot take less than 0!");
			return smtwentwrong;
		}
		if(confirm("amount " + Double.toString(moneytopayout))) {
			try {
				topayout.account.payout(moneytopayout);
			} catch (NoResourcesException e) {
				throw e;
			}
		} else {
			moneytopayout = smtwentwrong;
		}
		
		return moneytopayout;
	}
	
	void transferMoney() {
		User user1, user2;
		String txt1 = "pay in", txt2 = "take from";
		try {
			user1 = enterUserNumber (txt1);
			user2 = enterUserNumber (txt2);
		} catch (NoUserFindException e) {
			System.out.println("No such user find.");
			return;
		}
		try {
			if(confirm(txt2, user2) && confirm(txt1, user1)) {
				double money = payOutTransfer(user2);
				if(money <= 0) {
					return;
				}
				payIn(user1, money);
			} else {
				return;
			}
		} catch (NoResourcesException e) {
			System.out.println("No resources to do this!");
		}
	}
	
	void deleteUser(User todelete) {
		users.remove(todelete);
}
	
	void displayAll () {
		System.out.println();
		//System.out.println("No.\t FName\t\t LName\t\t PESEL\t Adress\t\t Money");
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
	
	
	private void findDisplay () {
		System.out.println("Do you want search by: ");
		System.out.println("1. Users number in system");
		System.out.println("2. Name");
		System.out.println("3. Last name");
		System.out.println("4. PESEL");
		System.out.println("5. Adress");
		System.out.println("0. Exit");
	}
	
	private int chooseFind () {
		int choise;
		do {
			findDisplay();
			choise = in.nextInt();
		} while (choise < 0 || choise > 5);
			
		return choise;
		
	}
	
	private Vector<User> find () throws NoUserFindException {
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
		long numbertofind = in.nextLong();
		
		try {
			user = findByPesel(numbertofind);
		} catch (NoUserFindException e) {
			throw e;
		}
		
		return user;
	}
	
	private User findByPesel(long number) throws NoUserFindException {
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

	
	Account(double money) {
		resources = money;
	}
	
	double getResources () {
		return resources;
	}
	
	void payment (double money) {
		resources += money;
	}
	
	void payout (double money) throws NoResourcesException {
		if (money <= resources) {
			resources -= money;
		} else {
			throw new NoResourcesException();
		}
	}
	
	
	 
}

class User implements Serializable {
	private int systemNumber;
	private String firstname;
	private String lastname;
	private long pesel;
	private String adress;
	Account account;
	
	
	User(int sNo, String fname, String lname, long p, String adr, double money){
			systemNumber = sNo;
			firstname = fname;
			lastname = lname;
			pesel = p;
			adress = adr;
			account = new Account (money);

	}
	
	void display () {
		System.out.println(systemNumber + "\t" + firstname + "\t" + lastname + "\t" + pesel + "\t" + adress + "\t" + account.getResources());
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
	
	long getPesel () {
		return pesel;
	}
	
	String getAdress () {
		return adress;
	}
	
}
