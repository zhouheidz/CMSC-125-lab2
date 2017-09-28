import java.util.*;
import java.io.*;

public class SRTF
{

	//Assumes arrival time is sorted
	public static void schedule(int[] sequence,int[] burst,int[] arrival) 
	{

		int current_time = arrival[0];
		int count = 0;
		int[] q = new int[100];
		boolean allArrived = false;
		int qcount = 0;

		for(int i=0;i<arrival.length;i++)
		{
			if(arrival[i] == current_time)
			{
				q[qcount] = sequence[i];
				qcount++;
				count++;
			}
			else if(arrival[i]>current_time)
				break;
		}

		if(count == arrival.length)
				allArrived = true;

		while(!allArrived || qcount !=0)
		{
			int index = findMin(q, burst, count); //index of pidmin
				
			int pidmin = index;
			if (pidmin == -1)
				System.out.println("Chosen PROCESSID: none");
			else
				System.out.println("Chosen PROCESSID: "+pidmin);

			if(!allArrived)
			{
			//air bubble
			if(qcount == 0)
			{
				current_time = arrival[count];
				q[qcount] = sequence[count];
				qcount++;
				count++;

				if(count == arrival.length)
					allArrived = true;
				continue;
			}
			if(arrival[count]<(current_time+burst[pidmin]))
			{	

				//Check, execute pre-empt if required. Loop back.
				int updated_time = arrival[count];
				q[qcount] = sequence[count];
				
				count++;
				qcount++;
				burst[pidmin] = burst[pidmin] - (updated_time - current_time);
				current_time = updated_time;
				
				if(count == arrival.length)
					allArrived = true;
				continue;

				
			}

			}
			current_time = current_time+burst[pidmin];
			burst[pidmin] = 0;

			int i = q[qcount-1];
			q[index] = i;
			qcount--;

			
			System.out.println("Current time: "+current_time);			
		}
	}

	public static int findMin(int q[],int burst[], int count)
	{
		int index = -1;
		int min = 100;
		
		for(int i=0;i<count;i++)
		{
			if(burst[i]!=0 && burst[i]<min)
			{
				min = burst[i];
				index = i;
			}
		}	

		return index;
	}

	//sequence has to be 0,1,2..
	public static void main(String args[])
	{

		int arrival[]=new int[] {0,1,2,3};
        int burst[]=new int[] {5,2,3,1}; 
        int sequence[] = new int[]{0,1,2,3};

  		schedule(sequence, burst, arrival);

	}
}