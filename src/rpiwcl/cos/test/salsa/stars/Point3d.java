/*
 * Author:		Shigeru Imai (RCSID:660855993)
 * Filename:	Point3d.java
 * Date:		Nov 11, 2010
 * 
 */

package salsa.examples.stars;

import java.io.Serializable;


public class Point3d implements Serializable {
	float x, y, z;
	
	public Point3d() {
		x = y = z = 0.0F;
	}

	public Point3d( String str ) {
		String[] coord = str.split(" ");
		
		x = Float.parseFloat( coord[0] );
		y = Float.parseFloat( coord[1] );
		z = Float.parseFloat( coord[2] );
	}
	

	public Point3d( float x, float y, float z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

    public boolean isEqual( Point3d p ) {
        return ((this.x == p.x) && (this.y == p.y) && (this.z == p.z));
    }
		
	public float distance( Point3d p ) {
		return (float) Math.sqrt( (x - p.x)*(x - p.x)  + (y - p.y)*(y - p.y) + (z - p.z)*(z - p.z) );
	}

	public void print() {
		System.out.println( x + " " + y + " " + z );
	}

	public String toString() {
		return String.format( x + " " + y + " " + z );
	}
}
