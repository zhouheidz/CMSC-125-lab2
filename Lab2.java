import java.util.*;
import java.io.*;
import javax.swing.*;

public class Lab2 {

	public static class Process {

		int sequence;
		int arrival;
		int burst;
		int priority;

		Process(int sequence, int arrival, int burst, int priority) {
			this.sequence = sequence;
			this.arrival = arrival;
			this.burst = burst;
			this.priority = priority;
		}

		int getSequence() {
			return this.sequence;
		}

		int getArrival() {
			return this.arrival;
		}

		int getBurst() {
			return this.burst;
		}

		int getPriority() {
			return this.priority;
		}

		void setBurst(int burst) {
			this.burst = burst;
		}

		public static Comparator<Process> BurstComparator = new Comparator<Process>() {
			public int compare(Process p1, Process p2) {
				int burst1 = p1.getBurst();
				int burst2 = p2.getBurst();

				// System.out.println("" + burst1 + " && " + burst2 + " == " + (burst1-burst2));
				return burst1-burst2;
			}
		};

		public static Comparator<Process> PriorityComparator = new Comparator<Process>() {
			public int compare(Process p1, Process p2) {
				int priority1 = p1.getPriority();
				int priority2 = p2.getPriority();

				// System.out.println("" + priority1 + " && " + priority2 + " == " + (priority1-priority2));
				return priority1-priority2;
			}
		};

		@Override
		public boolean equals(Object process) {
			return this.getSequence() == ((Process)process).getSequence();
		}

		public static void output(ArrayList processes) {
			float wt = 0;
			float awt = 0;

			for(int i = 0; i < processes.size(); i++) {
				System.out.print(((Process)processes.get(i)).sequence);
				wt+= ((Process)processes.get(i)).burst;
				if(i < processes.size()-1) {
					System.out.print(", ");
					awt += wt;
				}
			}
			awt = awt/processes.size();
			System.out.println("\nAWT: " + awt + "\n");
		}

		public static void FCFS(ArrayList processes) {
			System.out.println("FCFS");
			output(processes);
		}

		public static void SJF(ArrayList processes) {
			System.out.println("SJF");
			Collections.sort(processes, Process.BurstComparator);
			output(processes);
		}

		public static void priority(ArrayList processes) {
			System.out.println("PRIORITY");
			Collections.sort(processes, Process.PriorityComparator);
			output(processes);
		}

		public static void SRPT(ArrayList processes) {
			System.out.println("SRPT");
		}

		public static void RR(ArrayList processes) {
			System.out.println("RR");
			ArrayList rrprocess = new ArrayList<Process>();

			boolean hasremaining = true;
			while(hasremaining) {
				hasremaining = false;
				for(int i = 0; i < processes.size(); i++) {
					if(((Process)processes.get(i)).burst > 0) {
						hasremaining = true;
					}
					if(((Process)processes.get(i)).burst >= 4) {
						rrprocess.add(new Process(
							((Process)processes.get(i)).sequence, 
							((Process)processes.get(i)).arrival, 4, 
							((Process)processes.get(i)).priority));
						processes.set(i, new Process(((Process)processes.get(i)).sequence,
							((Process)processes.get(i)).arrival, 
							(((Process)processes.get(i)).burst-4),
							((Process)processes.get(i)).priority));
					} else if (((Process)processes.get(i)).burst > 0) {
						rrprocess.add(new Process(
							((Process)processes.get(i)).sequence, 
							((Process)processes.get(i)).arrival,
							(((Process)processes.get(i)).burst%4), 
							((Process)processes.get(i)).priority));
						processes.set(i, new Process(((Process)processes.get(i)).sequence,
							((Process)processes.get(i)).arrival, 0,
							((Process)processes.get(i)).priority));
					}
				}
			}

			for(int i = 0; i < rrprocess.size(); i++) {
				System.out.print(((Process)rrprocess.get(i)).sequence);
				if(i < rrprocess.size()-1) {
					System.out.print(", ");
				}
			}
			System.out.println();

			int repeat;
			float wt = 0;
			float awt = 0;
			int repeat2;
			int lastburst = 0;
			for(int i = 0; i < processes.size(); i++) {
				repeat = -1;
				repeat2 = count(rrprocess, (Process)processes.get(i));
				wt = 0;
				for(int j = 0; j < rrprocess.size(); j++) {
					lastburst = ((Process)rrprocess.get(j)).burst;
					wt+= lastburst;
					if(((Process)rrprocess.get(j)).equals(((Process)processes.get(i)))) {
						repeat++;
					}
					if((repeat2-1) == repeat) {
						break;
					}
				}
				wt = wt - (repeat*4) - lastburst;
				awt+=wt;
			}	
			awt = awt/processes.size();
			System.out.println("\nAWT: " + awt + "\n");			
		}

	}

	public static int count(ArrayList rrprocess, Process process) {
		int answer = 0;
		for(int i = 0; i < rrprocess.size(); i++) {
			if(((Process)rrprocess.get(i)).equals(process)) {
				answer++;
			}
		}
		return answer;
	}

	public static void copy(ArrayList processes, ArrayList processes2) {
		processes2 = new ArrayList<Process>();
		for(int i = 0; i < processes.size(); i++) {
			processes2.add((Process)processes.get(i));
		}
	}
	
	public static void main(String[] args) {

		String[] options2 = {"Yes", "No"};

		do {

			BufferedReader br = null;
			FileReader fr = null;
			StringTokenizer st;

			int sequence;
			int arrival;
			int burst;
			int priority;

			try {
				fr = new FileReader(JOptionPane.showInputDialog("Filename: "));
				// fr = new FileReader("process1.txt");
				br = new BufferedReader(fr);

				String line;
				ArrayList processes = new ArrayList<Process>();
				ArrayList processes2 = new ArrayList<Process>();

				br.readLine();

				while ((line = br.readLine()) != null) {

					st = new StringTokenizer(line);

					sequence = Integer.parseInt(st.nextToken());
					arrival = Integer.parseInt(st.nextToken());
					burst = Integer.parseInt(st.nextToken());
					priority = Integer.parseInt(st.nextToken());

					processes.add(new Process(sequence, arrival, burst, priority));
					processes2.add(new Process(sequence, arrival, burst, priority));
				}

				int choice;
				do {
					String[] options = {"FCFS", "SJF", "Priority", "SRPT", "Round Robin"};
					choice = JOptionPane.showOptionDialog(null, "Compute for which scheduling algorithm?", "", 
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

					

					switch(choice) {
						case 0:
							Process.FCFS(processes2);
							break;
						case 1:
							Process.SJF(processes2);
							break;
						case 2:
							Process.priority(processes2);
							break;
						case 3:
							Process.SRPT(processes2);
							break;
						case 4:
							Process.RR(processes2);
							break;
						default:
							break;
					}

					processes2 = new ArrayList<Process>();
					for(int i = 0; i < processes.size(); i++) {
						processes2.add((Process)processes.get(i));
					}

				}while (JOptionPane.showOptionDialog(null, "Next Algorithm?", "", 
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, 
					null, options2, options2[0]) == 0);

					

			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				try {

					if (br != null)
						br.close();

					if (fr != null)
						fr.close();

				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}
		
		}while (JOptionPane.showOptionDialog(null, "Next File?", "", 
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, 
					null, options2, options2[0]) == 0);
	}
}