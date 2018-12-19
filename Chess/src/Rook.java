import java.util.ArrayList;

public class Rook extends Piece {
	
	/**
	 * creates Rook piece
	 * @param alliance color of Rook (black or white)
	 * @param score value of Rook
	 * @param location current location of Rook
	 */
	public Rook(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
	}

	@Override
	/**
	 * generates all possible moves of the Rook
	 * @param occupied list of points that are occupied by another piece
	 * @return possible moves of the Rook
	 */
	public ArrayList<Point> getMoves(ArrayList<Point> occupied) {
		ArrayList<Point> possibleMoves = new ArrayList<>();
		possibleMoves.add(location);
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
