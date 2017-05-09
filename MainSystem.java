
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
