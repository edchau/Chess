
public class Point {
	
	int x;
	int y;

	/**
	 * Creates point
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * reset x and y values
	 */
	public void reset() {
		x = -1;
		y = -1;
	}
	
	@Override
	/**
	 * converts point to string
	 */
	public String toString() {
		return x + " " + y;
	}
	
	@Override
	/**
	 * checks if point is equal
	 */
	public boolean equals(Object other) {
		if(other instanceof Point) {
			Point comp = (Point) other;
			return this.x == comp.x && this.y == comp.y;
		}
		return false;
	}
	
	/**
	 * Finds distance between two points
	 * @param other point to find distance of
	 * @return distance
	 */
	public double distance(Point other) {
		return Math.sqrt(Math.pow(x-other.x,2)+Math.pow(y-other.y,2));
	}
}
