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

		public static float output(ArrayList processes) {
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
			return awt;
		}

		public static float FCFS(ArrayList processes) {
			System.out.println("FCFS");
			return output(processes);
		}

		public static float SJF(ArrayList processes) {
			System.out.println("SJF");
			Collections.sort(processes, Process.BurstComparator);
			return output(processes);
		}

		public static float priority(ArrayList processes) {
			System.out.println("PRIORITY");
			Collections.sort(processes, Process.PriorityComparator);
			return output(processes);
		}

		public static float SRPT(ArrayList processes) {
			System.out.println("SRPT");
			ArrayList srptprocess = new ArrayList<Process>();
			LinkedList processq = new LinkedList<Process>();

			int hasarrived = 0;
			int timer = 0;
			
			do {
				if(hasarrived < processes.size()) {
					for(int i = 0; i < processes.size(); i++) {
						if(((Process)processes.get(i)).arrival == timer) {
							processq.add(((Process)processes.get(i)));
							Collections.sort(processq, Process.BurstComparator);
							hasarrived++;
						}
					}
				}
				if(srptprocess.size() == 0) {
					srptprocess.add(new Process(((Process)processq.get(0)).sequence,
						((Process)processes.get(0)).arrival, 1,
						((Process)processes.get(0)).priority));
				} else {
					if(((Process)srptprocess.get(srptprocess.size()-1)).equals(((Process)processq.get(0)))) {
						srptprocess.set(srptprocess.size()-1, new Process(((Process)processq.get(0)).sequence,
							((Process)srptprocess.get(srptprocess.size()-1)).arrival,
							((Process)srptprocess.get(srptprocess.size()-1)).burst+1,
							((Process)srptprocess.get(srptprocess.size()-1)).priority));
					} else {
						srptprocess.add(new Process(((Process)processq.get(0)).sequence,
							((Process)processes.get(0)).arrival, 1,
							((Process)processes.get(0)).priority));
					}
				}

				processq.set(0, new Process(((Process)processq.get(0)).sequence,
					((Process)processq.get(0)).arrival,
					((Process)processq.get(0)).burst-1,
					((Process)processq.get(0)).priority));

				if(((Process)processq.get(0)).burst == 0) {
					processq.removeFirst();
				}

				timer++;
			}while(processq.size() > 0);

			for(int i = 0; i < srptprocess.size(); i++) {
				System.out.print(((Process)srptprocess.get(i)).sequence);
				if(i < srptprocess.size()-1) {
					System.out.print(", ");
				}
			}
			System.out.println();

			float wt = 0;
			float awt = 0;
			int notfinal = 0;
			int lastburst = 0;
			int count = 0;
			for(int i = 0; i < processes.size(); i++) {
				count = count(srptprocess, (Process)processes.get(i));
				notfinal = 0;				
				wt = 0;
				for(int j = 0; j < srptprocess.size(); j++) {
					lastburst = ((Process)srptprocess.get(j)).burst;
					wt+= lastburst;
					if(((Process)srptprocess.get(j)).equals(((Process)processes.get(i)))) {
						if(count > 1) {
							notfinal+=((Process)srptprocess.get(j)).burst;
							count--;
						} else {
							break;
						}
					}
				}
				wt = wt - ((Process)processes.get(i)).arrival - notfinal - lastburst;
				awt+=wt;
			}	
			awt = awt/processes.size();
			System.out.println("\nAWT: " + awt + "\n");

			return awt;

		}

		public static float RR(ArrayList processes) {
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
			return awt;
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
	
	public static void main(String[] args) {
		BufferedReader br = null;
		FileReader fr = null;
		StringTokenizer st;

		int sequence;
		int arrival;
		int burst;
		int priority;

		float[] results = new float[5];

		try {
			fr = new FileReader("process1.txt");
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

			int min = 0;

			results[0] = Process.FCFS(processes2);
			processes2 = new ArrayList<Process>();
			for(int i = 0; i < processes.size(); i++) {
				processes2.add((Process)processes.get(i));
			}

			results[1] = Process.SJF(processes2);
			processes2 = new ArrayList<Process>();
			for(int i = 0; i < processes.size(); i++) {
				processes2.add((Process)processes.get(i));
			}

			if(results[min] > results[1]) {
				min = 1;
			}

			results[2] = Process.priority(processes2);
			processes2 = new ArrayList<Process>();
			for(int i = 0; i < processes.size(); i++) {
				processes2.add((Process)processes.get(i));
			}

			if(results[min] > results[2]) {
				min = 2;
			}
			
			results[3] = Process.SRPT(processes2);
			processes2 = new ArrayList<Process>();
			for(int i = 0; i < processes.size(); i++) {
				processes2.add((Process)processes.get(i));
			}

			if(results[min] > results[3]) {
				min = 3;
			}
			
			results[4] = Process.RR(processes2);
			if(results[min] > results[4]) {
				min = 4;
			}

			System.out.print("\nThe fastest waiting time is " + results[min] + " by the ");

			switch(min) {
				case 0:
					System.out.print("FCFS algorithm");
					break;
				case 1:
					System.out.print("SJF algorithm");
					break;
				case 2:
					System.out.print("Priority algorithm");
					break;
				case 3:
					System.out.print("SRPT algorithm");
					break;
				case 4:
					System.out.print("Round Robin algorithm");
					break;
			}


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
	}
}