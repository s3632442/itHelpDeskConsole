package HelpDeskTicketSystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menus {
	// array of created ticket objects
	private static ArrayList<Ticket> tickets = new ArrayList<Ticket>();
	private static User user = null;

	public static void main(String[] args) {
		String userInput;
		String enteredTicketNo;
		String enteredSeverity;
		Scanner sc = new Scanner(System.in);
		// Initialize selection variable to ASCII null to keep compiler happy
		char selection = '\0';

		String[] login = new String[2];

		// Check to see is persistent data for tickets and load if present
		tickets = FileHandler.loadTicketDatabase("tickets.txt", "TICKET");

		do {
			login[0] = getInput(sc, "Employee No");
			login[1] = getInput(sc, "Password");
			user = FileHandler.loginUser("users.txt", login);
			if (user == null) {
				System.out.println("Error - Invalid credentials");
			} else {
				System.out.println("Logging in..");
			}
		} while (user == null);

		do {
			if (user instanceof StaffUser) {
				List<String> menu = Arrays.asList("Create Ticket", "Exit Program");
				List<String> menuSelections = Arrays.asList("C", "X");

				printMenu("STAFF MENU", menu, menuSelections);
				userInput = sc.nextLine();
				System.out.println();

				if (userInput.length() != 1) {
					System.out.println("Error - invalid selection!");
				} else {
					// make selection case insensitive
					selection = Character.toUpperCase(userInput.charAt(0));

					// process user menu selection
					switch (selection) {
					// create ticket case
					case 'C': {
						Ticket ticket = createTicketMenu(sc);
						if (ticket != null) {
							tickets.add(ticket);
						}
						break;
					}
					case 'P': {
						for (int i = 0; i < tickets.size(); i++) {
							tickets.get(i).print();
						}
						break;
					}
					// exit case
					case 'X': {
						System.out.println("Exiting the program...");
						// Writes out any created tickets to file
						FileHandler.writeTicketDatabase("tickets.txt", tickets);
						break;
					}
					// invalid selection case
					default: {
						System.out.println("Error - invalid selection!");
					}
					}
				}
			} else {
				do {
					// set menu selections
					List<String> menu = Arrays.asList("Close Ticket", "Exit Program");
					List<String> menuSelections = Arrays.asList("C", "X");

					// menu title
					printMenu("TECH MENU", menu, menuSelections);
					userInput = sc.nextLine();

					System.out.println();

					// validate selection input length
					if (userInput.length() != 1) {
						System.out.println("Error - invalid selection!");
					} else {
						// make selection case insensitive
						selection = Character.toUpperCase(userInput.charAt(0));

						// process user menu selection
						switch (selection) {
						case 'C': {
							userInput = getInput(sc, "ticket number for the ticket you want to close");
							if (tickets != null) {
								for (int i = 0; i < tickets.size(); i++) {
									if (tickets.get(i).equals(userInput)) {
										if (((TechUser) user).isTechnician(tickets.get(i))) {
											tickets.get(i).setStatus(false);
											System.out.printf("Ticket %s has been closed!");
											break;
										} else {
											System.out.println("Error - Can not close another technician's ticket");
										}
									}
								}
							} else {
								System.out.println("Error - There are currently no tickets in the database!");
							}
							break;
						}
						
						case 'S': {
							enteredTicketNo = getInput(sc, "Enter ticket number to update its severity ");
							
							//create ticket object 
							enteredSeverity = getInput(sc, "Enter new severty by entering 'LOW', 'MEDIUM', 'HIGH' :");
							
							//set severity
							System.out.println("Severity of " + enteredTicketNo + " has been set to : " + enteredSeverity);
							break;
							
							
						}
						
						// exit case
						case 'X': {
							System.out.println("Exiting the program...");
							break;
						}
						// invalid selection case
						default: {
							System.out.println("Error - invalid selection!");
						}
						}
						System.out.println();
					}
				} while (selection != 'X');
			}
		} while (selection != 'X');
	}

	protected static void displayOpenTickets() {
		if (tickets != null && !tickets.isEmpty()) {
			for (int i = 0; i < tickets.size(); i++) {

				// evaluation for tech allocation
				/*
				 * if (tickets.get(i).getTechnicianFirstName().equals(user.getFirstName()) &&
				 * tickets.get(i).getTechnicianLastName().equals(user.getLastName())) {
				 */

				// displays details for each open ticket object
				System.out.println("Open tickets");
				System.out.print("ID: " + tickets.get(i).getId() + "| Status:" + tickets.get(i).getStatus()
						+ "| Severity:" + tickets.get(i).getSeverity());
				System.out.print("\n---------------------------\n\n");
//			}
			}
		}
	}

	// print menu method
	protected static void printMenu(String title, List<String> menu, List<String> menuSelections) {
		System.out.printf("%s\n---------------------------\n\n", title);
		
		// displays open tickets
		displayOpenTickets();
	
		for (int i = 0; i < menu.size(); i++) {
			System.out.printf("%-25s%s\n", menu.get(i), menuSelections != null ? menuSelections.get(i) : i + 1);
		}
		System.out.println();
		System.out.println("Enter selection: ");
	}

	protected static Ticket.TicketSeverity checkTicketSeverity(String input) {
		if (input.equals(Ticket.TicketSeverity.HIGH.name())) {
			return Ticket.TicketSeverity.HIGH;
		}
		if (input.equals(Ticket.TicketSeverity.MEDIUM.name())) {
			return Ticket.TicketSeverity.MEDIUM;
		}
		if (input.equals(Ticket.TicketSeverity.LOW.name())) {
			return Ticket.TicketSeverity.LOW;
		}
		return null;
	}

	protected static String getInput(Scanner scanner, String request) {
		String input = "\0";
		// Regex that matches email addresses
		String emailPattern = "\\b[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}\\b";
		// Regex that matches Australian formatted phone numbers
		String phonePattern = "^(?:\\+?(61))? ?(?:\\((?=.*\\)))?(0?[2-57-8])\\)? ?(\\d\\d(?:[- ](?=\\d{3})|(?!\\d\\d[- ]?\\d[- ]))\\d\\d[- ]?\\d[- ]?\\d{3})$";

		// request input
		System.out.printf("Enter your %s: ", request);
		input = scanner.nextLine();

		// if exit command
		if ("X!".equals(input.toUpperCase())) {
			return input;
		}
		// if empty
		if ("".equals(input)) {
			System.out.println("Error - input can not be empty!");
			input = getInput(scanner, request);
		}
		// if invalid email
		if (request.equalsIgnoreCase("email") && !input.matches(emailPattern)) {
			System.out.println("Error - invalid email address!");
			input = getInput(scanner, request);
		}
		// if invalid phone number
		if (request.equalsIgnoreCase("contact number") && !input.matches(phonePattern)) {
			System.out.println("Error - invalid phone number, must use Australian format! e.g. +61290001234");
			input = getInput(scanner, request);
		}
		// if valid ticket severity
		if (request.equalsIgnoreCase("severity")) {
			if (checkTicketSeverity(input.toUpperCase()) == null) {
				System.out.println("Error - invalid severity, must be LOW, MEDIUM, OR HIGH!");
				input = getInput(scanner, request);
			}
			input = input.toUpperCase();
		}
		return input;
	}

	protected static boolean[] exitResults() {
		boolean cancel = false;
		boolean exit = false;
		return new boolean[] { cancel, exit };
	}

	protected static boolean[] exit(Scanner sc) {
		String exit = "\0";
		boolean cancel = false, exitResult = false, exitCase = false;
		System.out.println("Exiting will cause any progress to be lost");
		System.out.println("You will be given the option to edit your ticket after filling out all details");
		System.out.println("Are you sure you want to exit? Y/N");
		exit = sc.nextLine();
		do {
			if (exit.length() == 1) {
				switch (Character.toUpperCase(exit.charAt(0))) {
				case 'N': {
					System.out.println("Returning to create ticket menu..");
					cancel = true;
					break;
				}
				case 'Y': {
					System.out.println("Returning to staff menu..");
					cancel = true;
					exitResult = true;
					exitCase = true;
					break;
				}
				default: {
					System.out.println("Error - invalid selection, must be Y or N");
					exit = sc.nextLine();
				}
				}
			} else {
				System.out.println("Error - invalid selection, must be Y or N");
				exit = sc.nextLine();
			}
		} while (!cancel);
		return new boolean[] { cancel, exitResult, exitCase };
	}

	/*
	 * Generate a ticket identification number Ticket ID Format:
	 * yyyyMMdd-ticketIDCounter
	 */
	protected static String generateTicketId() {
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = date.format(dateFormat);
		return formattedDate + "-" + Ticket.ticketIDCounter;
	}

	protected static Ticket createTicketMenu(Scanner scanner) {
		String input = null, exit = "\0";
		Boolean cancel = false;
		String[] ticket = new String[8];
		Ticket.TicketSeverity severity = null;
		boolean[] exitResults = { false, false, false };

		List<String> menu = Arrays.asList("Surname", "Given name", "Staff number", "Email", "Contact number",
				"Description", "Severity", "Confirm", "Exit");
		List<String> menuSelections = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "C", "X");

		do {
			printMenu("CREATE TICKET MENU", menu, menuSelections);
			input = scanner.nextLine();
			if (input.length() >= 1) {
				switch (Character.toUpperCase(input.charAt(0))) {
				case '1':
					ticket[1] = getInput(scanner, menu.get(0));
					break;
				case '2':
					ticket[2] = getInput(scanner, menu.get(1));
					break;
				case '3':
					ticket[3] = getInput(scanner, menu.get(2));
					break;
				case '4':
					ticket[4] = getInput(scanner, menu.get(3));
					break;
				case '5':
					ticket[5] = getInput(scanner, menu.get(4));
					break;
				case '6':
					ticket[6] = getInput(scanner, menu.get(5));
					break;
				case '7':
					ticket[7] = getInput(scanner, menu.get(6));
					severity = checkTicketSeverity(ticket[7]);
					break;

				case 'C':
					ticket[0] = generateTicketId();
					boolean missingField = false;
					for (int i = 1; i < ticket.length; i++) {
						if (ticket[i] == null) {
							System.out.printf("Error - missing field: %s\n", menu.get(i - 1));
							missingField = true;
						}
					}
					if (!missingField) {
						return new Ticket(ticket[0], ticket[1], ticket[2], ticket[3], ticket[4], ticket[5], ticket[6],
								severity);
					}
					System.out.println("Error - all fields are required!");
					break;

				case 'X':
					System.out.println("Exiting will cause any progress to be lost");
					System.out.println("Are you sure you want to exit? Y/N");
					exit = scanner.nextLine();
					do {
						if (exit.length() == 1) {
							switch (Character.toUpperCase(exit.charAt(0))) {
							case 'N':
								System.out.println("Returning to create ticket menu...");
								cancel = true;
								break;
							case 'Y':
								System.out.println("Returning to staff menu...");
								cancel = true;
								break;
							default:
								System.out.println("Error - invalid selection, must be Y or N");
								exit = scanner.nextLine();
							}
						} else {
							System.out.println("Error - invalid selection, must be Y or N");
							exit = scanner.nextLine();
						}

					} while (!cancel);
					cancel = false;
					break;

				default:
					System.out.println("Error - invalid selection!");
				}
			} else {
				System.out.println("Error - invalid selection!");
			}
		} while (Character.toUpperCase(exit.charAt(0)) != 'Y');
		return null;
	}

}
