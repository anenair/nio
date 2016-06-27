package com.unity.message;

import java.util.Scanner;

public enum MessageType {

	WHO_AM_I, WHO_IS_HERE, SEND_MESSAGE, SIGNOUT, EMPTY;

	private static Scanner scanner = new Scanner(System.in);

	public static String getMessage() {

		System.out.println("Enter next query for server");
		System.out.println("1. Who Am I ? ");
		System.out.println("2. Who is Here ? ");
		System.out.println("3. Send message to  others who are online");
		System.out.println("5. Signout");
		System.out.println("Enter option : ");

		int option = scanner.nextInt();

		switch (option) {
		case 1:
			return WHO_AM_I.name();
		case 2:
			return WHO_IS_HERE.name();
		case 3:
			System.out.println("Enter message to be sent : \n");
			
			// System.out.println("Enter client ids to be sent to (comma
			// separated) : \n");
			// String clientIds = scanner.next();
			return "foo ### 1";
		case 4:
			return SIGNOUT.name();
		default:
			return EMPTY.name();
		}

	}

}
