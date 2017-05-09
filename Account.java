class NoResourcesException extends Exception {
	NoResourcesException() {
	}
}

class Account implements java.io.Serializable {
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
