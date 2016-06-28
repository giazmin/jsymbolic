/*
 * Main.java
 * Version 1.2.1
 *
 * Last modified on July 26, 2010.
 * McGill University and the University of Waikato
 */

package jsymbolic;
import jsymbolic.features.MIDIFeatureExtractor;
import jsymbolic.gui.FeatureSelectorPanel;
import jsymbolic.processing.MIDIFeatureProcessor;
import mckay.utilities.staticlibraries.FileMethods;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;


/**
 * Runs the jSymbolic Feature Extractor GUI, if no command line arguments are
 * specified, or the command line if there are command line arguments.
 *
 * @author Cory McKay
 */
public class Main
{
	/**
	 * Runs the GUI.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		//new CommandLine(args);

		BufferedWriter out = new BufferedWriter(new FileWriter("FeatureExtraction.txt"));
		String[][] chordprogression = new String[202][50];
		//String as[] = {"107.mid","text.xml","text1.xml"};
		int index=0;
		String filename;
		int key=0;
		
		
		out.write("\t");  out.write("#major"); out.write("\t"); out.write("#minor"); out.write("\t");
		out.write("maj/min"); out.write("\t"); out.write("C"); out.write("\t");
		out.write("Dm"); out.write("\t"); out.write("Em"); out.write("\t"); out.write("F"); out.write("\t");
		out.write("G"); out.write("\t"); out.write("Am"); out.write("\t"); out.write("Bo"); out.write("\t");
		out.write("A value"); out.write("\t"); out.write("V value"); out.write("\t");
		out.newLine();
		String path="/Users/yongkimin/Summer16/jSymbolic (2)/data";
				File dirFile=new File(path);
				File []fileList=dirFile.listFiles();
				for(File tempFile : fileList) {
				  if(tempFile.isFile()) {
				    String tempPath=tempFile.getParent();
				    String tempFileName=tempFile.getName();
				   // System.out.println("Path="+tempPath);
				   // System.out.println("FileName="+tempFileName);
				    filename = tempFileName;

				    String as[] = {path+filename,"text.xml","text1.xml"};
				    //String as[] = {"D:/eclipse ÃÖ½Å/eclipse/workspace/jSymbolic/Data/107.mid","text.xml","text1.xml"};
				    System.out.println(filename);
				    ExtractFeature c = new ExtractFeature(as);
					
//				    readXML asd = new readXML(as[0]);
//				    asd.printList();
				    /*
				    
				    
				    String chord[] = c.GetChordProgression();
					
					double minor = 0;
					double major=0;
					for(int i=0;i<chord.length;i++)
					{
						if(chord[i].contains("m") || chord[i].contains("o"))
							minor++;
						else if(chord[i].contains("X") || chord[i].contains("N"))
							;
						else
							major++;
					}
					double major_minor_ratio=0;
					
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					// 1·Î ÇÏ´Â°Ô ¸Â³ª?
					
					
					// minor°¡ 0ÀÏ °æ¿ì minor°¡ 1 ÀÌ¶ó°í °¡Á¤
					if(minor == 0)
						major_minor_ratio = major;
					else
						major_minor_ratio = major/minor;
					
					
					double Diatonic_Ratio[] = new double[7];
					for(int i=0;i<7;i++)
					{
						Diatonic_Ratio[i] = 0;
					}
					
					int number =0;
					for(int i=0;i<chord.length;i++)
					{
						if(chord[i] == "C" || chord[i] == "CM7")
							Diatonic_Ratio[0] += 1;
						else if(chord[i] == "Dm" || chord[i] == "Dm7")
							Diatonic_Ratio[1] +=1;
						else if(chord[i] == "Em" || chord[i] == "Em7")
							Diatonic_Ratio[2] +=1;
						else if(chord[i] == "F" || chord[i] == "FM7")
							Diatonic_Ratio[3] +=1;
						else if(chord[i] == "G" || chord[i] == "G7")
							Diatonic_Ratio[4] +=1;
						else if(chord[i] == "Am" || chord[i] == "Am7")
							Diatonic_Ratio[5] +=1;
						else if(chord[i] == "Bo" || chord[i] == "Bm7-5")
							Diatonic_Ratio[6] +=1;
						
						if(chord[i] != "X" && chord[i] != "N")
							number++;
					}
					
					for(int i=0;i<7;i++)
					{
						Diatonic_Ratio[i] /= number;
					}
						
					
					//double major_minor_ratio = major/minor;
					
					
					
					
					
					
					
					
					chordprogression[index++] = chord;
				
					key = c.GetKey();
					
					out.write(filename+"\t");
					
					for(int i=0;i<chord.length;i++)
					{
						out.write(chord[i]); out.write(" ");
					}
					out.write(Integer.toString(key)); out.write(" ");
					
					out.newLine();
					
					
					*/
					
					
					DecimalFormat df = new DecimalFormat("#.####"); df.setRoundingMode(RoundingMode.CEILING); //Optional
					
					//out.write(df.format(major)); out.write("\t"); out.write(df.format(minor)); out.write("\t");
					//out.write(Double.toString(major_minor_ratio)); out.write("\t");
					//out.write(df.format(major_minor_ratio)); out.write("\t");
					
					
					
					//Double d = new Double(2341234.212431324);
					//System.out.println(df.format(d));
					
					
					/*
					for(int i=0;i<7;i++)
					{
						out.write((df.format(Diatonic_Ratio[i]))); out.write("\t");
					}
					*/
					//out.newLine();
					
					
					// Get Extracted feature values
					//double f = {0,0,0,0};
					double f[][] = c.GetFeatureValues();
					//out.newLine();
					//t.wri
					key = c.GetKey();
					
					for(int i=0;i<111;i++)
					{
						if(i<101){
							out.write(df.format(f[i][0])); 
						out.write("\t");
						}
						else if(i == 108)
						{
							for(int j=0;j<12;j++){
								out.write(df.format(f[i][j]));
								out.write("\t");
							}
						}
						
					}
					
					//out.write(Integer.toString(key));
					out.newLine();
					
					
					

				  }
				}
		
		int k=0;
		k++;
		out.close();
		
		
		
		// ¿©±â¼­ºÎÅÍ ÄÚµå¶û ÆÐÅÏ¸ÅÄª
		
		/*
		
		MidiAV a = new MidiAV();
		a.GetAVvalue(as);
		double avalue=a.GetA();
		double vvalue =a.GetV();
		System.out.println(avalue);
		System.out.println(vvalue);
		*/
	}
}