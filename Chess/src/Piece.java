import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Piece {
	
	enum Alliance {
		BLACK, WHITE, NONE;
	}
	
	protected Alliance alliance;
	protected int score;
	protected Point location;
	
	/**
	 * creates Chess piece
	 * @param alliance color of piece (black, white, none)
	 * @param score value of chess piece
	 * @param location current location of chess piece
	 */
	public Piece(Alliance alliance, int score, Point location) {
		this.alliance = alliance;
		this.score = score;
		this.location = location;
	}
	
	/**
	 * gets the color of piece
	 * @return color of piece
	 */
	public Alliance getAlliance() {
		return alliance;
	}
	
	/**
	 * sets the color of piece
	 */
	public void setAlliance(Alliance alliance) {
		this.alliance = alliance;
	}
	
	/**
	 * gets current point of piece
	 * @return 
	 */
	public Point getLocation() {
		return location;
	}
	
	/**
	 * gets current value of piece
	 * @return value of piece
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * moves piece to a new location
	 * @param p new point to move
	 */
	public void move(Point p) {
		location.x = p.x;
		location.y = p.y;
	}

	/**
	 * generates all possible moves of piece
	 * @param occupied list of points that are occupied by another piece
	 * @return Possible moves of piece
	 */
	public abstract ArrayList<Point> getMoves(ArrayList<Point> occupied);
}
