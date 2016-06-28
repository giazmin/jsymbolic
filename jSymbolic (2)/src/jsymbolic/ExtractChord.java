package jsymbolic;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import jsymbolic.processing.MIDIIntermediateRepresentations;

public class ExtractChord {

	File midi;
	int TPB;
	String chordprogression[];
	MIDIIntermediateRepresentations inter;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	NoteDistribution[] ND;
	NoteDistribution[] NDChecked;
	
	double[] featureValues = null;
	public ExtractChord(File input_MIDI_file,MIDIIntermediateRepresentations a,int b, double[] as)
	{
		midi = input_MIDI_file;
		inter = a;
		TPB = b;
		featureValues = as;

	}
	public int FindKey()
	{
		double[] pitch_class_histogram = featureValues;

		double[] major_class = {5.0,2.0,3.5,2.0,4.5,4.0,2.0,4.5,2.0,3.5,1.5,4.0};
		double[] minor_class = {5.0,2.0,3.5,4.5,2.0,4.0,2.0,4.5,3.5,2.0,1.5,4.0};




		double pitch_mean = 0;
		double major_mean=0,minor_mean=0;

		for(int i=0;i<12;i++)
		{
			pitch_mean += pitch_class_histogram[i];
			major_mean += major_class[i];
			minor_mean += minor_class[i];
		}
		pitch_mean /= 12;
		major_mean /= 12;
		minor_mean /= 12;


		double pitch_dev = 0;
		double major_dev = 0, minor_dev = 0;

		for(int i=0;i<12;i++)
		{
			pitch_dev += (pitch_class_histogram[i]-pitch_mean)*(pitch_class_histogram[i]-pitch_mean);
			major_dev += (major_class[i]-pitch_mean)*(major_class[i]-pitch_mean);
			minor_dev += (minor_class[i]-pitch_mean)*(minor_class[i]-pitch_mean);
		}

		double[] Correlation = new double[24];

		for(int i=0;i<12;i++)
		{
			double covar = 0;
			for(int j=0;j<12;j++)
			{
				covar += (pitch_class_histogram[j]-pitch_mean)*(major_class[j]-major_mean);

			}

			Correlation[i] = covar / Math.sqrt(pitch_dev*major_dev);
			ShiftVector(major_class);

		}

		for(int i=0;i<12;i++)
		{
			double covar = 0;
			for(int j=0;j<12;j++)
			{
				covar += (pitch_class_histogram[j]-pitch_mean)*(minor_class[j]-minor_mean);

			}

			Correlation[i+12] = covar / Math.sqrt(pitch_dev*minor_dev);
			ShiftVector(minor_class);
		}


		//for(int i=0;i<24;i++)
		//	System.out.println(Math.abs(Correlation[i]));

		double max = 0;
		int index=0;
		for(int i=0;i<24;i++)
		{
			if(Correlation[i]>max)
			{
				max = Correlation[i];
				index = i;
			}
		}

		//System.out.println(index);
		return index;

	}
	public void ShiftVector(double[] array)
	{
		double k = array[11];
		System.arraycopy(array, 0, array, 1, 11);
		array[0] = k;

	}
	// 겹치는 부분 해결해야댐
	public void ChordRecognition()
	{
	
		// 코드 단위를 어디로 끊을 것인가
		int bar_time = (TPB * 4 * inter.num / (inter.denom)) / 2;
		if(inter.num % 3 == 0)
		{
			bar_time = (TPB * 4 * inter.num / (inter.denom));
		}
		
		
		
		System.out.println(TPB);
		System.out.println(inter.num);
		System.out.println(inter.denom);
		int NoteHistogram[]= new int[12];
		int index = 0;
		chordprogression = new String[60];
		
		
		
		
		
		
		
		
		
		int lowest = 999;
		for(int j=1;j<60;j++)
		{
			for(int k=0;k<12;k++)
				NoteHistogram[k] = 0;
			//System.out.println(NDChecked.length);
			for(int i=0;i<NDChecked.length;i++)
			{
				
					
				if((NDChecked[i].tick < bar_time*j) && (NDChecked[i].tick >= bar_time*(j-1)) 
						|| ((NDChecked[i].tick+NDChecked[i].duration <= bar_time*j) && (NDChecked[i].tick+NDChecked[i].duration > bar_time*(j-1)))
			            || ((NDChecked[i].tick+NDChecked[i].duration > bar_time*j) && (NDChecked[i].tick+NDChecked[i].duration < bar_time*(j-1)))
			            )
				{
					NoteHistogram[NDChecked[i].note] += NDChecked[i].duration;
					//System.out.println(i);
					
					if(lowest > NDChecked[i].data)
					{
						lowest = NDChecked[i].data;
					}
				}
			}
			chordprogression[index++] = IdentifyChord(NoteHistogram);
			
			/*
			
			int keyofsong = FindKey();
			int key=0;
			if(keyofsong < 12)
			{
				key = short_message.getData1() - keyofsong;
			}
			else
			{
				key = short_message.getData1() - (keyofsong-12+3);
			}			
			
			*/
			
			
			
			// 여기 수정해야됨 만약 키에 구애받지 않게 하려면
			/*
			if(chordprogression[index-1] == "N")
			{
				int octave = (lowest / 12)-1;
                int note = lowest % 12;
                String noteName = NOTE_NAMES[note];
                
                String chord="";
                if(noteName == "D")
                	chord = "Dm"; 
                else if(noteName == "E")
                	chord = "Em";
                else if(noteName == "A")
                	chord = "Am";
                else if(noteName == "B")
                	chord = "Bo";
                else
                	chord = "N";
                chordprogression[index-1] = chord;
                
                
                
			}
			*/
		}
		
		//for(int i=0;i<index;i++)
		//	System.out.println(chordprogression[i]);
		
		String c[] = chordprogression;
		chordprogression = new String[index-1];
		for(int i=0;i<index-1;i++)
		{
			chordprogression[i] = c[i];
		}
		
	}
	
	public int[] BubbleSort(int[] arr, boolean wantIndex)
	{
		int sort[] = new int[12];
		int index[] = new int[12];
		
		
		for(int i=0;i<12;i++)
		{
			sort[i] = arr[i];
			index[i] = i;
		}
			
		
		for(int i=0;i<sort.length-1;i++)
		{
			for(int j=i+1;j<sort.length;j++)
			{
				if(sort[i] < sort[j])
				{
					int temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
					
					temp = index[j];
					index[j] = index[i];
					index[i] = temp;
				}
				
			}
		}
		
		if(wantIndex)
			return index;
		else
			return sort;
		
	}
	/*
	public String IdentifyChord(int[] chord)
	{
		int sorted[] = new int[12];
		int index[] = new int[12];
		
		for(int i=0;i<12;i++)
			index[i] = i;
		
		sorted = chord;
		// bubble sort
		for(int i=0;i<11;i++)
		{
			for(int j=i+1;j<12;j++)
			{
				if(chord[i] < chord[j])
				{
					int temp = chord[j];
					chord[j] = chord[i];
					chord[i] = temp;
					
					temp = index[j];
					index[j] = index[i];
					index[i] = temp;	
				}

			}
		}
		// bubble sort end
		
		int num=0;
		for(int i=0;i<12;i++)
		{
			if(chord[i] != 0)
				num++;
		}
		
		
		
		int sortedindex[] = new int[num];
		
		for(int i=0;i<num;i++)
		{
			sortedindex[i] = index[i];
		}
		
		
		// bubble sort
		for(int i=0;i<num-1;i++)
		{
			for(int j=i+1;j<num;j++)
			{
				if(sortedindex[i] > sortedindex[j])
				{
					int temp = sortedindex[j];
					sortedindex[j] = sortedindex[i];
					sortedindex[i] = temp;
					
				}

			}
		}
		// bubble sort end
				
		String C = ChordList(sortedindex);
		
		return C;
		
		
	}*/
	
	
	public String IdentifyChord(int[] chord)
	{
		int sorted[] = new int[12];
		int index[] = new int[12];
		
		sorted = BubbleSort(chord,false);
		index = BubbleSort(chord,true);
		// duration도 포함해야됨	
		
		int num=0;
		for(int i=0;i<12;i++)
		{
			if(chord[i] != 0)
				num++;
		}
		
		
		
		int sortedindex[] = new int[num];
		
		for(int i=0;i<num;i++)
		{
			sortedindex[i] = index[i];
		}
		
		
		// bubble sort
		for(int i=0;i<num-1;i++)
		{
			for(int j=i+1;j<num;j++)
			{
				if(sortedindex[i] > sortedindex[j])
				{
					int temp = sortedindex[j];
					sortedindex[j] = sortedindex[i];
					sortedindex[i] = temp;
					
				}

			}
		}
		// bubble sort end
				
		String C;
		if(num <= 3)
			C = ChordList(sortedindex);
		else
		{
			C = ChordList4(sortedindex);
			if(C == "N")
				C = ChordList(sortedindex);
		}
		//String C = ChordList4(sortedindex);
		
		return C;
		
		
	}
	public boolean Include(int[]a, int ... nums)
	{
		int k=0;
		for(int i=0;i<a.length;i++)
		{
			for(int j=0;j<nums.length;j++)
			{
				if(a[i] == nums[j])
					k++;
			}
		
		}
		
		if(k >= nums.length)
			return true;
		else
			return false;
	}
	public String ChordList(int[] a)
	{
		int k=0;
		for(int i=0;i<a.length;i++)
		{
			if(a[i] == 0)
				k++;
		}
		if(k == a.length)
			return "X";
				
		if(Include(a,0,4,7))
			return "C";
		
		else if(Include(a,0,3,7))
			return "Cm";
		
		else if(Include(a,1,5,8))
			return "C#";
		
		else if(Include(a,1,4,8))
			//return "C#m";
			return "E";
		
		else if(Include(a,2,6,9))
			return "D";
		
		else if(Include(a,2,5,9))
			return "Dm";
		
		else if(Include(a,3,7,10))
			return "D#";
		
		else if(Include(a,3,6,10))
			return "D#m";
		
		else if(Include(a,4,8,11))
			return "E";
		
		else if(Include(a,4,7,11))
			return "Em";
		
		else if(Include(a,0,5,9))
			return "F";
		
		else if(Include(a,0,5,8))
			return "Fm";
		
		else if(Include(a,1,6,10))
			return "F#";
		
		else if(Include(a,1,6,9))
			return "F#m";
		
		else if(Include(a,2,7,11))
			return "G";
		
		else if(Include(a,2,7,10))
			return "Gm";
		
		else if(Include(a,0,3,8))
			return "G#";
		
		else if(Include(a,3,8,11))
			return "G#m";
		
		else if(Include(a,1,4,9))
			return "A";
		
		else if(Include(a,0,4,9))
			return "Am";
		
		else if(Include(a,2,5,10))
			//return "A#";
			return "Dm";
		
		else if(Include(a,1,5,10))
			return "A#m";
		
		else if(Include(a,3,6,11))
			return "B";
		
		else if(Include(a,2,6,11))
			return "Bm";
		
		//else if(Include(a,2,5,11))
		//	return "Bo";
		else
		{
			return "N";
			
			
			// similar pattern
			
			
			
			
			
			
			
		}
		//else if(Include(a,0,0,0))
		//	return "No";
		//else
		//	return "Q";
		
		/*
		if(a[0] == 0 && a[1] == 4 && a[2] == 7)
			return "C";
		
		else if(a[0] == 0 && a[1] == 3 && a[2] == 7)
			return "Cm";
		
		if(a[0] == 1 && a[1] == 5 && a[2] == 8)
			return "C#";
		
		else if(a[0] == 1 && a[1] == 4 && a[2] == 8)
			return "C#m";
		
		else if(a[0] == 2 && a[1] == 6 && a[2] == 9)
			return "D";
		
		else if(a[0] == 2 && a[1] == 5 && a[2] == 9)
			return "Dm";
		
		else if(a[0] == 3 && a[1] == 7 && a[2] == 10)
			return "D#";
		
		else if(a[0] == 3 && a[1] == 6 && a[2] == 10)
			return "D#m";
		
		else if(a[0] == 4 && a[1] == 8 && a[2] == 11)
			return "E";
		
		else if(a[0] == 4 && a[1] == 7 && a[2] == 11)
			return "Em";
		
		else if(a[0] == 0 && a[1] == 5 && a[2] == 9)
			return "F";
		
		else if(a[0] == 0 && a[1] == 5 && a[2] == 8)
			return "Fm";
		
		else if(a[0] == 1 && a[1] == 6 && a[2] == 10)
			return "F#";
		
		else if(a[0] == 1 && a[1] == 7 && a[2] == 9)
			return "F#m";
		
		else if(a[0] == 2 && a[1] == 7 && a[2] == 11)
			return "G";
		
		else if(a[0] == 2 && a[1] == 7 && a[2] == 10)
			return "Gm";
		
		else if(a[0] == 0 && a[1] == 3 && a[2] == 8)
			return "G#";
		
		else if(a[0] == 3 && a[1] == 8 && a[2] == 11)
			return "G#m";
		
		else if(a[0] == 1 && a[1] == 4 && a[2] == 9)
			return "A";
		
		else if(a[0] == 0 && a[1] == 4 && a[2] == 9)
			return "Am";
		
		else if(a[0] == 2 && a[1] == 5 && a[2] == 10)
			return "A#";
		
		else if(a[0] == 1 && a[1] == 5 && a[2] == 10)
			return "A#m";
		
		else if(a[0] == 3 && a[1] == 6 && a[2] == 11)
			return "B";
		
		else if(a[0] == 2 && a[1] == 6 && a[2] == 11)
			return "Bm";
		
		else if(a[0] == 2 && a[1] == 5 && a[2] == 11)
			return "Bo";
		
		*/
		
		
		
		/*
		else if(a[0] == 0 && a[1] == 6 && a[2] == 10)
			return "Caug";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "E";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "G#o";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "D";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "Bm";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "F#o";
		*/
		

					
		
	}
	public String ChordList4(int[] a)
	{
		int k=0;
		for(int i=0;i<a.length;i++)
		{
			if(a[i] == 0)
				k++;
		}
		if(k == a.length)
			return "X";
				
		if(Include(a,0,4,7,11))
			return "CM7";
		
		else if(Include(a,0,3,7,10))
			return "Cm7";
		
		else if(Include(a,0,4,7,10))
			return "C7";
		
		else if(Include(a,1,5,8,12))
			return "C#M7";
		
		else if(Include(a,1,4,8,11))
			return "C#m7";
			
		else if(Include(a,1,5,8,11))
			return "C#7";
		
		else if(Include(a,1,2,6,9))
			return "DM7";
		
		else if(Include(a,0,2,5,9))
			return "Dm7";
		
		else if(Include(a,0,2,6,9))
			return "D7";
		
		else if(Include(a,2,3,7,10))
			return "D#M7";
		
		else if(Include(a,1,3,6,10))
			return "D#m7";
		
		else if(Include(a,1,3,7,10))
			return "D#7";
		
		else if(Include(a,3,4,8,11))
			return "EM7";
		
		else if(Include(a,2,4,7,11))
			return "Em7";
		
		else if(Include(a,2,4,8,11))
			return "E7";
		
		else if(Include(a,0,4,5,9))
			return "FM7";
		
		else if(Include(a,0,3,5,8))
			return "Fm7";
		
		else if(Include(a,0,3,5,9))
			return "F7";
		
		else if(Include(a,1,5,6,10))
			return "F#M7";
		
		else if(Include(a,1,4,6,9))
			return "F#m7";
		
		else if(Include(a,1,4,6,10))
			return "F#7";
		
		else if(Include(a,2,6,7,11))
			return "GM7";
		
		else if(Include(a,2,5,7,10))
			return "Gm7";
		
		else if(Include(a,2,5,7,11))
			return "G7";
		
		else if(Include(a,0,3,7,8))
			return "G#M7";
		
		else if(Include(a,3,6,8,11))
			return "G#m7";
		
		else if(Include(a,0,3,6,8))
			return "G#7";
		
		else if(Include(a,1,4,8,9))
			return "AM7";
		
		else if(Include(a,0,4,7,9))
			return "Am7";
		
		else if(Include(a,1,4,7,9))
			return "A7";
		
		else if(Include(a,2,5,9,10))
			return "A#M7";
		
		else if(Include(a,1,5,8,10))
			return "A#m";
		
		else if(Include(a,2,5,8,10))
			return "A#7";
		
		else if(Include(a,3,6,10,11))
			return "BM7";
		
		else if(Include(a,2,6,9,11))
			return "Bm7";
		
		else if(Include(a,3,6,9,11))
			return "B7";
		
		//else if(Include(a,2,5,9,11))
		//	return "Bm7-5";
		
		else
		{
			return "N";
			
			
			// similar pattern
			
			
		}
		//else if(Include(a,0,0,0))
		//	return "No";
		//else
		//	return "Q";
		
		/*
		if(a[0] == 0 && a[1] == 4 && a[2] == 7)
			return "C";
		
		else if(a[0] == 0 && a[1] == 3 && a[2] == 7)
			return "Cm";
		
		if(a[0] == 1 && a[1] == 5 && a[2] == 8)
			return "C#";
		
		else if(a[0] == 1 && a[1] == 4 && a[2] == 8)
			return "C#m";
		
		else if(a[0] == 2 && a[1] == 6 && a[2] == 9)
			return "D";
		
		else if(a[0] == 2 && a[1] == 5 && a[2] == 9)
			return "Dm";
		
		else if(a[0] == 3 && a[1] == 7 && a[2] == 10)
			return "D#";
		
		else if(a[0] == 3 && a[1] == 6 && a[2] == 10)
			return "D#m";
		
		else if(a[0] == 4 && a[1] == 8 && a[2] == 11)
			return "E";
		
		else if(a[0] == 4 && a[1] == 7 && a[2] == 11)
			return "Em";
		
		else if(a[0] == 0 && a[1] == 5 && a[2] == 9)
			return "F";
		
		else if(a[0] == 0 && a[1] == 5 && a[2] == 8)
			return "Fm";
		
		else if(a[0] == 1 && a[1] == 6 && a[2] == 10)
			return "F#";
		
		else if(a[0] == 1 && a[1] == 7 && a[2] == 9)
			return "F#m";
		
		else if(a[0] == 2 && a[1] == 7 && a[2] == 11)
			return "G";
		
		else if(a[0] == 2 && a[1] == 7 && a[2] == 10)
			return "Gm";
		
		else if(a[0] == 0 && a[1] == 3 && a[2] == 8)
			return "G#";
		
		else if(a[0] == 3 && a[1] == 8 && a[2] == 11)
			return "G#m";
		
		else if(a[0] == 1 && a[1] == 4 && a[2] == 9)
			return "A";
		
		else if(a[0] == 0 && a[1] == 4 && a[2] == 9)
			return "Am";
		
		else if(a[0] == 2 && a[1] == 5 && a[2] == 10)
			return "A#";
		
		else if(a[0] == 1 && a[1] == 5 && a[2] == 10)
			return "A#m";
		
		else if(a[0] == 3 && a[1] == 6 && a[2] == 11)
			return "B";
		
		else if(a[0] == 2 && a[1] == 6 && a[2] == 11)
			return "Bm";
		
		else if(a[0] == 2 && a[1] == 5 && a[2] == 11)
			return "Bo";
		
		*/
		
		
		
		/*
		else if(a[0] == 0 && a[1] == 6 && a[2] == 10)
			return "Caug";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "E";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "G#o";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "D";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "Bm";
		else if(a[0] == 2 && a[1] == 6 && a[2] == 10)
			return "F#o";
		*/
		

					
		
	}

	public void ReadMidiFile() throws InvalidMidiDataException, IOException
	{
		Sequence s = javax.sound.midi.MidiSystem.getSequence(midi);  
		Track[] tracks = s.getTracks();

		
		int keyofsong = FindKey();
		ND = new NoteDistribution[10000];
		int k=0;
		//int [] arr = GetChord(defaultTokenizer.nextToken());

 		int NrNote=0;
		int index=0;
		for (int n_track = 0; n_track < tracks.length; n_track++)
		{
			Track track = tracks[n_track];
			for (int n_event = 0; n_event < track.size(); n_event++)
			{
				MidiEvent event = track.get(n_event);
				MidiMessage message = event.getMessage();
				// Increment pitch of a note on
				//int event_start_tick = (int) event.getTick();
                //int event_end_tick = track.size(); // when the note off occurs (default to last tick)
				if (message instanceof ShortMessage)
				{
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getChannel() != 10 - 1) // not channel 10 (percussion)
					{

						// If a Program Change message is encountered, then
						// update current_patch_numbers
						if (short_message.getCommand() == 0xc0)
						{System.out.println(short_message.getChannel());
						System.out.println(short_message.getData1());
						//short_message.setMessage(short_message.getCommand(),short_message.getStatus(), 48,short_message.getData2());
						//short_message.
						}
						//      current_patch_numbers[ short_message.getChannel() ] = short_message.getData1();

						int event_start_tick = (int) event.getTick();
                        int event_end_tick = track.size(); // when the note off occurs (default to last tick)
						//System.out.println(short_message.getData1());
						if (short_message.getCommand() == 0x90) // note on 
						{
							
							if (short_message.getData2() != 0) // not velocity 0
                            {
                                 // Increment the Note On count in pitched_instrumentation_frequencies
                                 //pitched_instrumentation_frequencies[ current_patch_numbers[ short_message.getChannel() ] ][ 0 ]++;
                                 
                                 // Look ahead to find the corresponding note off for this note on
								
                                 for (int i = n_event + 1; i < track.size(); i++)
                                 {
                                      MidiEvent end_event = track.get(i);
                                      MidiMessage end_message = end_event.getMessage();
                                      if (end_message instanceof ShortMessage)
                                      {
                                           ShortMessage end_short_message = (ShortMessage) end_message;
                                           if (end_short_message.getChannel() == short_message.getChannel()) // must be on same channel
                                           {
                                                if (end_short_message.getCommand() == 0x80) // note off
                                                {
                                                     if (end_short_message.getData1() == short_message.getData1()) // same pitch
                                                     {
                                                          event_end_tick = (int) end_event.getTick();
                                                          i = track.size() + 1; // exit loop
                                                     }
                                                }
                                                if (end_short_message.getCommand() == 0x90) // note on (with vel 0 is equiv to note off)
                                                     if (end_short_message.getData2() == 0) // velocity 0
                                                          if (end_short_message.getData1() == short_message.getData1()) // same pitch
                                                          {
                                                     event_end_tick = (int) end_event.getTick();
                                                     i = track.size() + 1; // exit loop
                                                          }
                                           }
                                      }
                                 }
                            }
							
							
							
							//System.out.print("Note On ");
							//System.out.println(short_message.getData1());
							
							
							/*
							int key=0;
							if(keyofsong < 12)
							{
								key = short_message.getData1() - keyofsong;
							}
							else
							{
								key = short_message.getData1() - (keyofsong-12+3);
							}
							
							*/
							int key = short_message.getData1();
							
							//int key = short_message.getData1();
	                        int octave = (key / 12)-1;
	                        int note = key % 12;
	                        String noteName = NOTE_NAMES[note];
	                        int velocity = short_message.getData2();
	                        //System.out.println("Tick : "+ event_start_tick+"Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
	                        int duration = event_end_tick-event_start_tick;
	                        
	                        if(duration >= 0)
	                        {
	                        	NrNote++; 
	                        	ND[k++] = new NoteDistribution(key,octave,note,velocity,noteName,duration,event_start_tick);
	                        }
	                        	
							/*
                            	if(short_message.getData1() == index)
                            		short_message.setMessage(short_message.getStatus(), arr[0],short_message.getData2());
                            	else if(short_message.getData1() == index+1)
                            		short_message.setMessage(short_message.getStatus(), arr[1],short_message.getData2());
                            	else if(short_message.getData1() == index+2)
                            		short_message.setMessage(short_message.getStatus(), arr[2],short_message.getData2());
							 */

							// note off 
						}
						
					}
				}


			}
		}
		NDChecked = new NoteDistribution[NrNote];
		for(int i=0;i<NrNote;i++)
			NDChecked[i] = ND[i];
		int zxc = 0;
		zxc++;
	}
	public String[] Run() throws InvalidMidiDataException, IOException
	{
		ReadMidiFile();
		ChordRecognition();
		return chordprogression;
	}
}

