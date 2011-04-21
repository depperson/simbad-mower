import simbad.sim.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class Backyard extends EnvironmentDescription 
{
	public Backyard()
	{
		// south boundary
		Wall south = new Wall(new Vector3d(9,0,0), 20, 2, this);
		south.rotate90(1);
		add(south);

		// north boundary
		Wall north = new Wall(new Vector3d(-9,0,0), 20, 2, this);
		north.rotate90(1);
		add(north);
		
		// east boundary
		Wall east = new Wall(new Vector3d(0, 0, 9), 20, 2, this);
		add(east);

		// west boundary
		Wall west = new Wall(new Vector3d(0, 0, -9), 20, 2, this);
		add(west);
		
		// patio
		//Box patio = new Box(new Vector3d(-6, 0, 5), new Vector3f(6, 1, 15), this);
		//add(patio);
		
		// planter box 1
		Box planter1 = new Box(new Vector3d(-5, 0, -3), new Vector3f(3, 1, 2), this);
		add(planter1);
		
		
		
		add(new RobotMower(new Vector3d(0,0.25f,0), "robot mower"));
		setUsePhysics(true);
        setWorldSize(20);
	}
}
