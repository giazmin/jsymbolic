
package jsymbolic;
import jsymbolic.features.MIDIFeatureExtractor;
import jsymbolic.gui.FeatureSelectorPanel;
import jsymbolic.processing.MIDIFeatureProcessor;
import mckay.utilities.staticlibraries.FileMethods;

import java.io.File;

import java.lang.Math;


public class MidiAV {
	private double A,V;
	private double[] feature;
	private double model[][]  = {{-0.440035013,	0.050593145},
			{0.748870345,	0.245055158},
			{-0.016652423,	0.024654345},
			{-0.073440029,	-0.195106185},
			{0.048873153,	-0.000634609},
			{-0.171918725,	-0.078506699},
			{-0.195648847,	0.018620594},
			{0.138215126,	-0.009032275},
			{0.394293744,	0.160685234},
			{0.040819402,	0.031745595},
			{1.298330672,	0.526172751},
			{-0.258479494,	0.067062545},
			{0.501543179,	0.391971766},
			{0.044191786,	-0.000689586},
			{0.131590327,	0.199921285},
			{0.176736135,	0.030238122},
			{0.235496749,	0.131894458},
			{0.448614286,	0.334986715},
			{0.10488269,	0.072648713},
			{0.045471159,	0.056114473},
			{-0.063766815,	-0.212595709},
			{-0.081905383,	0.139047919},
			{-0.252077211,	-0.010391125},
			{-0.246249902,	-0.131360076},
			{-0.202346656,	-0.121703403},
			{-0.350812076,	-0.155167636},
			{0.18667917,	-0.141215766},
			{-0.004655423,	0.000574739},
			{-0.151042917,	0.031977706}
	};
	public void GetAVvalue(String[] args)
	{
		// input string args = midifilename outputfeaturexmlfile outputfeaturexmlfile2
		ExtractFeature c = new ExtractFeature(args);
		
		feature = c.GetFeature();
		
		A = 0;
		for(int i=0;i<29;i++)
		{
			
			A += feature[i] * model[i][0];
		}
		V = 0;
		for(int i=0;i<29;i++)
		{
			
			V += feature[i] * model[i][1];
		}
		
		if (V > 5)
		{
			V = V*Math.exp(Math.abs(5-V)/2);
		}
		else
			V = V*Math.exp(-Math.abs(5-V)/2);
		if(A > 9)
			A = 9;
		if(A < 1)
			A = 1;
		if(V > 9 )
			V = 9;
		if(V < 1)
			V = 1;
		
		
		if(A < 5 && V < 5)
		{
			V = V + Math.exp(Math.abs(9-V)/9);
		}
		
		if(A > 9)
			A = 9;
		if(A < 1)
			A = 1;
		if(V > 9 )
			V = 9;
		if(V < 1)
			V = 1;
	}
	
	public double GetA() { return A; }
	public double GetV() { return V; };

}
