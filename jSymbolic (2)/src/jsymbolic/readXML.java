package jsymbolic;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
 






import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 






import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class readXML {
	
	DocumentBuilder builder;
	FileInputStream file;
	DocumentBuilderFactory builderFactory;
	Document xmlDocument;
	XPath xPath;
	
	//NodeList
	static NodeList data_set_id;
	static NodeList names;
	static NodeList vs;
	
	int numfeature = 0;
	
	readXML(String path)
	{
		loadXML(path);
	}
	
	public void loadXML(String path)
	{
		try 
		{
			file = new FileInputStream(new File(path));
	        builderFactory = DocumentBuilderFactory.newInstance();     
			builder = builderFactory.newDocumentBuilder();
			xmlDocument = builder.parse(file);
	
	        xPath =  XPathFactory.newInstance().newXPath();
	        
	        String expression = "/feature_vector_file/data_set/data_set_id";
	        data_set_id = (NodeList)xPath.evaluate(expression, xmlDocument, XPathConstants.NODESET);
	
	        expression = "/feature_vector_file/data_set/feature/name";
	        names = (NodeList)xPath.evaluate(expression, xmlDocument, XPathConstants.NODESET);
	        
	        expression = "/feature_vector_file/data_set/feature/v";
	        vs = (NodeList)xPath.evaluate(expression, xmlDocument, XPathConstants.NODESET);
	        
	        numfeature = names.getLength();
		}
		catch (FileNotFoundException e) {
		        e.printStackTrace();
		} catch (SAXException e) {
		        e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		} catch (ParserConfigurationException e) {
		        e.printStackTrace();
		} catch (XPathExpressionException e) {
		        e.printStackTrace();
		}    	
	}
	
	public void print_data_set_id()
	{
        System.out.println(data_set_id.item(0).getTextContent());
	}
	
	public float getFeatureValuebyName(String name)
	{
		for( int idx=0; idx<names.getLength(); idx++ )
		{
            if(names.item(idx).getTextContent().equals(name))
            	return Float.parseFloat(vs.item(idx).getTextContent()); 	
        }
		return -1;
	}
	
	public int getFeatureIdbyName(String name)
	{
		for( int idx=0; idx<names.getLength(); idx++ )
		{
			if(names.item(idx).getTextContent().equals(name))
				return idx;
        }
		return -1;
	}
	
	public double[] printList()
	{
		
		System.out.println("NodeList : <data_set_id>");
        for( int idx=0; idx<data_set_id.getLength(); idx++ ){
            System.out.println(data_set_id.item(idx).getTextContent());
        }
        System.out.println("");

        System.out.println("NodeList : <name>");
        for( int idx=0; idx<names.getLength(); idx++ ){
            System.out.println(data_set_id.item(idx).getTextContent());
        }
        System.out.println("");
        
		
		double[] feature = new double[29];
		//float[] index = {2,3,4,5,9,16,19,21,22,23,25,28,41,46,47,51,53,56,59,60,67,69,71,82,85,86,90,93,101};
		feature[0] = getFeatureValuebyName("Acoustic Guitar Fraction");
		feature[1] = getFeatureValuebyName("Amount of Arpeggiation");
		feature[2] = getFeatureValuebyName("Average Melodic Interval");
		feature[3] = getFeatureValuebyName("Average Note Duration");
		feature[4] = getFeatureValuebyName("Average Time Between Attacks");
		feature[5] = getFeatureValuebyName("Compound Or Simple Meter");
		feature[6] = getFeatureValuebyName("Dominant Spread");
		feature[7] = getFeatureValuebyName("Electric Guitar Fraction");
		feature[8] = getFeatureValuebyName("Electric Instrument Fraction");
		feature[9] = getFeatureValuebyName("Glissando Prevalence");
		feature[10] = getFeatureValuebyName("Importance of Bass Register");
		feature[11] = getFeatureValuebyName("Importance of Middle Register");
		feature[12] = getFeatureValuebyName("Most Common Melodic Interval Prevalence");
		feature[13] = getFeatureValuebyName("Note Density");
		feature[14] = getFeatureValuebyName("Number of Common Melodic Intervals");
		feature[15] = getFeatureValuebyName("Number of Relatively Strong Pulses");
		feature[16] = getFeatureValuebyName("Number of Unpitched Instruments");
		feature[17] = getFeatureValuebyName("Percussion Prevalence");
		feature[18] = getFeatureValuebyName("Polyrhythms");
		feature[19] = getFeatureValuebyName("Primary Register");
		feature[20] = getFeatureValuebyName("Relative Strength of Most Common Intervals");
		feature[21] = getFeatureValuebyName("Relative Strength of Top Pitches");
		feature[22] = getFeatureValuebyName("Rhythmic Looseness");
		feature[23] = getFeatureValuebyName("String Keyboard Fraction");
		feature[24] = getFeatureValuebyName("Triple Meter");
		feature[25] = getFeatureValuebyName("Variability of Note Duration");
		feature[26] = getFeatureValuebyName("Variability of Time Between Attacks");
		feature[27] = getFeatureValuebyName("Vibrato Prevalence");
		feature[28] = getFeatureValuebyName("Woodwinds Fraction");
		
		/*PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output1.txt"));
			System.setOut(out);
			System.out.print("Average Note Duration ");
			System.out.println(getFeatureValuebyName("Average Note Duration"));
			System.out.print("Initial Tempo ");
			System.out.println(getFeatureValuebyName("Initial Tempo"));
			System.out.print("Note Density ");
			System.out.println(getFeatureValuebyName("Note Density"));
			System.out.print("Number of Unpitched Instruments ");
			System.out.println(getFeatureValuebyName("Number of Unpitched Instruments"));
			System.out.print("Polyrhythms ");
			System.out.println(getFeatureValuebyName("Polyrhythms"));
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  */
		
		return feature;
			
			
        
	}
	
	
}
	


