package jsymbolic;

public class NoteDistribution {
    
    public int data;
    public int octave;
    public int note;
    public int velocity;
    public String note_name;
    public int duration;
    public int tick;
    
    public NoteDistribution(int d,int o, int n, int v,String nn,int du,int t)
    {
    	data = d;
    	octave = o;
    	note = n;
    	velocity = v;
    	note_name = nn;
    	duration = du;
    	tick = t;
    }
    
    
}
