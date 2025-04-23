import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class Scheduler {
    private List<PCB> queue1; 
    private List<PCB> queue2; 
    private List<PCB> completedProcesses;
    private List<String> schedulingOrder;
    private int currentTime = 0;
    private final int quantum = 3;

    public Scheduler() {
        queue1 = new ArrayList<>();
        queue2 = new ArrayList<>();
        completedProcesses = new ArrayList<>();
        schedulingOrder = new ArrayList<>();
    }

    public void addProcess(PCB process) {//1  on Priority
        if (process.getPriority() == 1) {
            queue1.add(process);
        } else {
            queue2.add(process);
        }
    }

    public void scheduleProcesses() {
        while (!queue1.isEmpty() || !queue2.isEmpty()) {//it keeps processing tasks until both queues are empty
            if (!queue1.isEmpty()) {
                PCB process = queue1.remove(0);//removes the first process(at index 0) from queue1
                int execTime = Math.min(process.getCpuBurst(), quantum);//Calculates the execution time(it takes small num)*
                process.setStartTime(currentTime);//Sets the process’s start time and response time.
                process.setResponseTime(currentTime - process.getArrivalTime());
                currentTime += execTime;//Updates the current time
                process.setCpuBurst(process.getCpuBurst() - execTime);//Adjusts the remaining CPU burst for the process.
                schedulingOrder.add(process.getProcessId()); 

                if (process.getCpuBurst() > 0) {//If the process still has remaining burst time
                    queue1.add(process);//add it to q1 and it will continue
                } else {
                    process.setTerminationTime(currentTime);
                    process.setTurnaroundTime(currentTime - process.getArrivalTime());
                    process.setWaitingTime(process.getTurnaroundTime() - process.getCpuBurst());
                    completedProcesses.add(process);
                }
            }

            if (queue1.isEmpty() && !queue2.isEmpty()) {//sjf
                queue2.sort(Comparator.comparing(PCB::getCpuBurst));//sorted based on their remaining CPU burst time*
                PCB process = queue2.remove(0);
                process.setStartTime(currentTime);
                process.setResponseTime(currentTime - process.getArrivalTime());
                currentTime += process.getCpuBurst();//Updates the current time(CpuBurst)
                schedulingOrder.add(process.getProcessId());
                process.setTerminationTime(currentTime);
                process.setTurnaroundTime(currentTime - process.getArrivalTime());
                process.setWaitingTime(process.getTurnaroundTime() - process.getCpuBurst());
                completedProcesses.add(process);
            }
        }
    }

    public void printSchedulingOrder() {
        System.out.print("Scheduling Order: [");
        for (int i = 0; i < schedulingOrder.size(); i++) {
            if (i > 0) {
                System.out.print(" | ");
            }
            System.out.print(schedulingOrder.get(i));
        }
        System.out.println("]");
    }

    public void printReports() {
        completedProcesses.forEach(process -> System.out.println(process));// easy way to print use tostring
        printAverages();
    }

    private void printAverages() {
        double avgTurnaround = completedProcesses.stream().mapToInt(PCB::getTurnaroundTime).average().orElse(0);//Type Conversion and easy way to calculate AVG (allows you to process the elements )
        double avgWaiting = completedProcesses.stream().mapToInt(PCB::getWaitingTime).average().orElse(0);
        double avgResponse = completedProcesses.stream().mapToInt(PCB::getResponseTime).average().orElse(0);

        System.out.printf("Average Turnaround Time: %.2f ms\n", avgTurnaround);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaiting);
        System.out.printf("Average Response Time: %.2f ms\n", avgResponse);
    }

    public void writeToFile() {//The FileOutputStream  is responsible for writing raw bytes to a file.The PrintWriter provides additional methods for writing formatted data.
        try (PrintWriter out = new PrintWriter(new FileOutputStream(new File("Report.txt"), true))) {//appendedto the file rather than overwriting(save while adding new information.)
            out.println("Scheduling Order: " + schedulingOrder); 
            completedProcesses.forEach(out::println);
            out.printf("Average Turnaround Time: %.2f ms\n", completedProcesses.stream().mapToInt(PCB::getTurnaroundTime).average().orElse(0));
            out.printf("Average Waiting Time: %.2f ms\n", completedProcesses.stream().mapToInt(PCB::getWaitingTime).average().orElse(0));
            out.printf("Average Response Time: %.2f ms\n", completedProcesses.stream().mapToInt(PCB::getResponseTime).average().orElse(0));
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file.");
        }
    }
}
