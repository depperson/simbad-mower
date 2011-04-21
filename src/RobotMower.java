import static simbad.sim.BaseObject.black;
import static simbad.sim.BaseObject.white;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.sim.Agent;
import simbad.sim.DifferentialKinematic;
import simbad.sim.RangeSensorBelt;
import simbad.sim.RobotFactory;

//import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;



public class RobotMower extends Agent 
{
	String state;
	RangeSensorBelt  bumpers;
	Double leftspeed, rightspeed;
	Double statechange_odom;
	int reverseticks, wedgeticks;
	Point3d coords = new Point3d();
	
	public RobotMower(Vector3d position, String name)
	{
		super(position, name);
		
		// size info
		this.height = 0.4f;
		this.radius = 1.1f;
		this.mass = 0.7f;
		
		// defaults 
		
		// differential steering drive
		setKinematicModel(new DifferentialKinematic(getRadius()));
		
		// sensors
		//bumpers = RobotFactory.addSonarBeltSensor(this,16);
		bumpers = new RangeSensorBelt(this.radius, 0f, 1f, 16, 
						RangeSensorBelt.TYPE_SONAR, 
						RangeSensorBelt.FLAG_SHOW_FULL_SENSOR_RAY);
		bumpers.setUpdatePerSecond(3);
		
		addSensorDevice(bumpers, new Vector3d(0, 0, 0.0), 0);
	}
	
	public void initBehavior() 
	{
		state = "startup";
		// go forward 
		leftspeed = 1.0;
		rightspeed = 1.0;
	}
	
	public void performBehavior() 
	{
	
		// check rear bumpers
		if (	bumpers.hasHit(5) || bumpers.hasHit(6) || bumpers.hasHit(7) ||  
				bumpers.hasHit(8) || bumpers.hasHit(9) || bumpers.hasHit(10) || bumpers.hasHit(11) )
		{
			// bumped something in reverse
			// stop reversing
			statechange_odom = this.odometer;
			this.getCoords(coords);
			reverseticks = 0;
			System.out.println("rear bump at " + statechange_odom + " coords " + coords);
			
		}
		
		// check front bumpers
		if (	bumpers.hasHit(0) || bumpers.hasHit(1) || bumpers.hasHit(2) ||
				bumpers.hasHit(3) || bumpers.hasHit(15) || bumpers.hasHit(14) || bumpers.hasHit(13) )
		{
			// it hit something 
			statechange_odom = this.odometer;
			this.getCoords(coords);
			System.out.println("front bump at " + statechange_odom + " coords " + coords);
			reverseticks = 3;
			leftspeed = 0.0;
			rightspeed = 0.0;
			
				
		} 
		
		// wedged
		// front right and rear right sensors are tripped
		if ((bumpers.hasHit(15) || bumpers.hasHit(14) || bumpers.hasHit(13))   
				&&   (bumpers.hasHit(11) || bumpers.hasHit(10) || bumpers.hasHit(9)))
		{
			statechange_odom = this.odometer;
			this.getCoords(coords);
			rightspeed = -0.5;
			leftspeed = -0.2;
			System.out.println("wedge mode at " + statechange_odom + " coords " + coords);
			wedgeticks = 5;
		}
		
		// reverse mode
		if (reverseticks > 0)
		{
			
			// keep backing up
			leftspeed = -0.5;
			rightspeed = 0.1;
			reverseticks--;

		} 
		else if (wedgeticks > 0)
		{
			// wedge mode, hold whatever speed was set
			wedgeticks--;
		}
		else 
		{
				
			// go forward 
			leftspeed = 1.1;
			rightspeed = 1.0;
	
		} // end forward/reverse if
			
		
		
		// update wheel speeds
		setWheelsVelocity(leftspeed, rightspeed);
		
	} // end of performBehavior
	
	public void setWheelsVelocity(double left, double right) {
        ((DifferentialKinematic) kinematicModel).setWheelsVelocity(left, right);
    }
	
	
    public void create3D() {
        Color3f color = new Color3f(0.3f, 0.8f, 0.8f);
        Color3f color2 = new Color3f(1.0f, 0.0f, 0.0f);
        // body
        Appearance appear = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(color);
        appear.setMaterial(mat);
        int flags = Primitive.GEOMETRY_NOT_SHARED
                | Primitive.ENABLE_GEOMETRY_PICKING
                | Primitive.GENERATE_NORMALS;
        flags |= Primitive.ENABLE_APPEARANCE_MODIFY;
        //body = new Cylinder(radius, height, flags, appear);
        body = new Box(1.25f, 0.50f, 0.75f, flags, appear);
        // we must be able to change the pick flag of the agent
        body.setCapability(Node.ALLOW_PICKABLE_READ);
        body.setCapability(Node.ALLOW_PICKABLE_WRITE);
        body.setPickable(true);
        body.setCollidable(true);
        
        addChild(body);
                
        // direction indicator
        float coords[] = { radius / 2, height, -radius / 2,//
                radius / 2, height, radius / 2,//
                radius, height, 0 //
        };
        float normals[] = { 0, 1, 0, 0, 1, 0, 0, 1, 0 };
        TriangleArray tris = new TriangleArray(coords.length,
                GeometryArray.COORDINATES | GeometryArray.NORMALS);
        tris.setCoordinates(0, coords);
        tris.setNormals(0, normals);
        appear = new Appearance();
        appear.setMaterial(new Material(color2, black, color2, white, 100.0f));
        Shape3D s = new Shape3D(tris, appear);
        s.setPickable(false);
        addChild(s);
        
        // Add bounds for interactions and collision
	    Bounds bounds = new BoundingSphere(new Point3d(0,0,0),radius);
	    setBounds(bounds);
    }
	
}
