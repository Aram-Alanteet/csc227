import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scheduler scheduler = new Scheduler();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Enter process information");
            System.out.println("2. Report detailed information about processes and scheduling criteria");
            System.out.println("3. Exit");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        enterProcessInformation();
                        break;
                    case 2:
                        scheduler.scheduleProcesses();
                        scheduler.printSchedulingOrder();
                        scheduler.printReports();
                        scheduler.writeToFile();
                        break;
                    case 3:
                        System.out.println("Exiting program.");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please select 1, 2, or 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    private static void enterProcessInformation() {
        System.out.println("Enter the number of processes:");
        int numberOfProcesses = getIntInput();

        for (int i = 1; i <= numberOfProcesses; i++) {
            System.out.println("Enter information for process " + i);
            int priority = getPriority();
            System.out.println("Arrival Time:");
            int arrivalTime = getIntInput();
            System.out.println("CPU Burst:");
            int cpuBurst = getIntInput();

            PCB process = new PCB("P" + i, priority, arrivalTime, cpuBurst);
            scheduler.addProcess(process);
        }
    }

    private static int getPriority() {
        int priority = 0;
        while (priority != 1 && priority != 2) {
            System.out.println("Priority (1 or 2):");
            priority = getIntInput();
            if (priority != 1 && priority != 2) {
                System.out.println("Invalid priority. Only 1 or 2 is accepted.");
            }
        }
        return priority;
    }

    private static int getIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
            }
        }
    }
}
