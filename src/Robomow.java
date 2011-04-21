import simbad.gui.Simbad;

public class Robomow 
{
	public static void main(String[] args)
	{
		
		// request antialising
        System.setProperty("j3d.implicitAntialiasing", "true");
        
        // create Simbad instance
		Simbad frame = new Simbad(new Backyard(), false);
		
	}
}
