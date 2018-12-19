import java.util.ArrayList;

public class Queen extends Piece {

	/**
	 * creates Queen piece
	 * @param alliance color of Queen (black or white)
	 * @param score value of Queen
	 * @param location current location of Queen
	 */
	public Queen(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
	}

	@Override
	/**
	 * generates all possible moves of the Queen
	 * @param occupied list of points that are occupied by another piece
	 * @return possible moves of the Queen
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
		for(int i = 1; i < 9; i++) {
			Point p = new Point(location.x, location.y + i);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		for(int i = 1; i < 9; i++) {
			Point p = new Point(location.x + i, location.y);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		for(int i = 1; i < 9; i++) {
			Point p = new Point(location.x, location.y - i);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		for(int i = 1; i < 9; i++) {
			Point p = new Point(location.x - i, location.y);
			possibleMoves.add(p);
			if(occupied.contains(p))
				break;
		}
		return possibleMoves;
	}
}
