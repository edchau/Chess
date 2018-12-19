import java.util.ArrayList;

public class Bishop extends Piece {

	/**
	 * creates Bishop piece
	 * @param alliance color of Bishop (black or white)
	 * @param score value of Bishop
	 * @param location current location of Bishop
	 */
	public Bishop(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
	}

	@Override
	/**
	 * generates all possible moves of the Bishop
	 * @param occupied list of points that are occupied by another piece
	 * @return possible moves of the Bishop
	 */
	public ArrayList<Point> getMoves(ArrayList<Point> occupied) {
		ArrayList<Point> possibleMoves = new ArrayList<>();
		possibleMoves.add(location);
		for(int i = 1; i < 9; i++) { //northeast
			Point p = new Point(location.x+i, location.y+i);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		for(int i = 1; i < 9; i++) { //southeast
			Point p = new Point(location.x+i, location.y-i);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		for(int i = 1; i < 9; i++) { //southwest
			Point p = new Point(location.x-i, location.y-i);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		for(int i = 1; i < 9; i++) { //northwest
			Point p = new Point(location.x-i, location.y+i);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		return possibleMoves;
	}
}
