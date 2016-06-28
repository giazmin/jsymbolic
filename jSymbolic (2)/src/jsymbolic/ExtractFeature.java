package jsymbolic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import jsymbolic.features.MIDIFeatureExtractor;
import jsymbolic.gui.FeatureSelectorPanel;
import jsymbolic.processing.MIDIFeatureProcessor;
import jsymbolic.processing.MIDIIntermediateRepresentations;
import mckay.utilities.staticlibraries.FileMethods;

public class ExtractFeature {

	double []feature;
	double[][] featureValues = null;
	String[] chordprogression;
	int key;
	/**
	 * Interprets the command line arguments and begins feature extraction.
	 */
	public ExtractFeature(String[] args)
	{
		// If there are a proper number of command line arguments
		if (args.length == 3)				
		{
			extractFeatures(args[0], args[1], args[2], true);

			//	System.out.println(featureValues[0]);

			//FindKey();


			//double[] f = GetFeature();

			//double[][]s = 

			//readXML rrr = new readXML("feature.xml");
			//feature = rrr.printList();



			//System.exit(0);
		}

		// If invalid command line arguments are used
		else
		{
			System.err.println("Incorrest usage of jSymbolic. Proper usage requires one of the following:");
			System.err.println("\t1) No arguments: Runs the GUI");
			System.err.println("\t2) <SourceMIDIPath> <FeatureValuesOutputPath> <FeatureDescriptionsOutputPath>");
			System.exit(-1);
		}





	}


	public int FindKey()
	{
		double[] pitch_class_histogram = featureValues[108];

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

	public int[] GetChord(String c)
	{
		int[] chord = {1,2,3};


		switch(c)
		{
		case "C" : 
			chord[0] = 48; chord[1] = 52; chord[2] = 55;
			break;
		case "Dm" :
			chord[0] = 50; chord[1] = 53; chord[2] = 57;
			break;
		case "Em" :
			chord[0] = 52; chord[1] = 55; chord[2] = 59;
			break;
		case "F" :
			chord[0] = 53; chord[1] = 57; chord[2] = 48;
			break;
		case "G" :
			chord[0] = 55; chord[1] = 59; chord[2] = 50;
			break;
		case "Am" :
			chord[0] = 57; chord[1] = 48; chord[2] = 52;
			break;
		case "Bo" :
			chord[0] = 59; chord[1] = 50; chord[2] = 53;
			break;
		case "Caug" :
			chord[0] = 48; chord[1] = 52; chord[2] = 56;
			break;
		case "E" :
			chord[0] = 52; chord[1] = 56; chord[2] = 59;
			break;
		case "G#o" :
			chord[0] = 56; chord[1] = 59; chord[2] = 50;
			break;
		case "D" :
			chord[0] = 50; chord[1] = 54; chord[2] = 57;
			break;
		case "Bm" :
			chord[0] = 59; chord[1] = 50; chord[2] = 54;
			break;
		case "F#o" :
			chord[0] = 54; chord[1] = 57; chord[2] = 48;
			break;

		}

		return chord;
	}

	public void ModifyMidiFiles(File input_MIDI_file) throws InvalidMidiDataException, IOException
	{


		BufferedReader br = new BufferedReader(new FileReader("chord.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line;
			do{
				line = br.readLine();


				StringTokenizer defaultTokenizer = new StringTokenizer(line);
				Sequence s = javax.sound.midi.MidiSystem.getSequence(input_MIDI_file);  
				Track[] tracks = s.getTracks();
				int index=0;
				int aa=0;
				while (defaultTokenizer.hasMoreTokens())
				{
					int [] arr = GetChord(defaultTokenizer.nextToken());

					for (int n_track = 0; n_track < tracks.length; n_track++)
					{
						Track track = tracks[n_track];
						for (int n_event = 0; n_event < track.size(); n_event++)
						{
							MidiEvent event = track.get(n_event);
							MidiMessage message = event.getMessage();
							// Increment pitch of a note on
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
									short_message.setMessage(short_message.getCommand(),short_message.getStatus(), 48,short_message.getData2());
									//short_message.
									}
									//      current_patch_numbers[ short_message.getChannel() ] = short_message.getData1();


									//System.out.println(short_message.getData1());
									if (short_message.getCommand() == 0x90) // note on 
									{
										if(short_message.getData1() == index)
											short_message.setMessage(short_message.getStatus(), arr[0],short_message.getData2());
										else if(short_message.getData1() == index+1)
											short_message.setMessage(short_message.getStatus(), arr[1],short_message.getData2());
										else if(short_message.getData1() == index+2)
											short_message.setMessage(short_message.getStatus(), arr[2],short_message.getData2());


										// note off 
									}
									else if(short_message.getCommand() == 0x80)
									{
										if(short_message.getData1() == index)
											short_message.setMessage(short_message.getStatus(), arr[0],short_message.getData2());
										else if(short_message.getData1() == index+1)
											short_message.setMessage(short_message.getStatus(), arr[1],short_message.getData2());
										else if(short_message.getData1() == index+2)
										{
											short_message.setMessage(short_message.getStatus(), arr[2],short_message.getData2());
											index += 3;
											break;
											/*aa++;
	                                		if(aa == 2)
	                                		{
	                                			index += 3;
	                                			aa = 0;
	                                			break;
	                                		}*/
										}
									}
								}
							}
						}

					}





					//System.out.println(defaultTokenizer.nextToken());
				}
				String filename = "D:/eclipse ÃÖ½Å/eclipse/workspace/jSymbolic/chord/";
				System.out.println(filename+line+".mid");
				File file = new File(filename+line+".mid");
				MidiSystem.write(s,1,file);

			}while(line != null);


		} finally {
			br.close();
		}




		System.out.println("good");



	}

	public double[][] GetFeatureValues(){return featureValues;}

	public double[] GetFeature() {return feature;}

	/**
	 * Extracts all available features from a single MIDI file. Any errors
	 * encountered are printed to standard error.
	 * 
	 * @param input_MIDI_path					The path of the MIDI file to
	 *											extract features from.
	 * @param feature_values_save_path			The path to save the resulting
	 *											ACE XML Feature Values file to.
	 * @param feature_descriptions_save_path	The path to save the resulting
	 *											ACE XML Feature Description file
	 *											to.
	 * @param print_log							Whether or not to print a log
	 *											of actions to standard out.
	 */
	public void extractFeatures(String input_MIDI_path,
			String feature_values_save_path,
			String feature_descriptions_save_path,
			boolean print_log)
	{
		try
		{
			// Note progress
			if (print_log) System.out.println("jSymbolic is parsing " + input_MIDI_path + "...");

			// Prepare and validate the input file
			File input_MIDI_file = new File(input_MIDI_path);
			FileMethods.validateFile(input_MIDI_file, true, false);
			try {javax.sound.midi.MidiSystem.getSequence(input_MIDI_file);}
			catch (javax.sound.midi.InvalidMidiDataException e)
			{
				throw new Exception(input_MIDI_path + " is not a valid MIDI file.");
			}

// Get all available features
			MIDIFeatureExtractor[] feature_extractors = FeatureSelectorPanel.getAllAvailableFeatureExtractors(null);

			// Choose to extract all features
			// NOTE: could instead get defaults isntead by using non-null argument above for FeatureSelectorPanel.getAllAvailableFeatureExtractors() call
			boolean[] features_to_save = new boolean[feature_extractors.length];
			for (int i = 0; i < features_to_save.length; i++)
				features_to_save[i] = true;

			// Set the default feature extraction parameters
			boolean extract_overall_only = true;
			boolean save_features_for_each_window = false;
			boolean save_overall_recording_features = true;
			double window_size = 1.0;
			double window_overlap = 0.0;

			// Note progress
			if (print_log) System.out.println("jSymbolic is extracting features from " + input_MIDI_path + "...");

			// Prepare to extract features
			MIDIFeatureProcessor processor = new MIDIFeatureProcessor(extract_overall_only,
					window_size,
					window_overlap,
					feature_extractors,
					features_to_save,
					save_features_for_each_window,
					save_overall_recording_features,
					feature_values_save_path,
					feature_descriptions_save_path );

			// Extract features from the MIDI file and save them in an XML file
			processor.extractFeatures(input_MIDI_file);

			//processor.getFeatures(windows)
			//featureValues = null;
			featureValues = processor.getExtractedFeature();
			// Finalize saved XML files
			processor.finalize();

			MIDIIntermediateRepresentations inter = processor.GetMidiIntermediate();

			Object[] arr =inter.meta_data;
			int TPB = inter.GetTickPerBeat();
// Extract Chord
		//	inter.num;
			//ModifyMidiFiles(input_MIDI_file);
			ExtractChord extractChord = new ExtractChord(input_MIDI_file,inter,TPB,featureValues[108]);
			chordprogression = extractChord.Run();


			key = FindKey();
			
			








			// Note progress
			if (print_log) System.out.println("jSymbolic succesfully extracted features from " + input_MIDI_path + "...");
		}
		catch (Throwable t)
		{
			// Print a preparatory error message
			System.err.println("JSYMBOLIC ERROR WHILE PROCESSING " + input_MIDI_path + ":");

			// React to the Java Runtime running out of memory
			if (t.toString().startsWith("java.lang.OutOfMemoryError"))
			{
				System.err.println("- The Java Runtime ran out of memory.");
				System.err.println("- Please rerun this program with more more assigned to the runtime heap.");
			}
			else if (t instanceof Exception)
			{
				Exception e = (Exception) t;
				System.err.println("- " + e.getMessage());
				// e.printStackTrace(System.err);
			}


			// End execution
			System.exit(-1);
		}
	}

	public String[] GetChordProgression()
	{
		return chordprogression;
	}


	public int GetKey()
	{
		return key;
	}



}
