import java.util.ArrayList;

public class Knight extends Piece {

	/**
	 * creates Knight piece
	 * @param alliance color of Knight (black or white)
	 * @param score value of Knight
	 * @param location current location of Knight
	 */
	public Knight(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
	}

	@Override
	/**
	 * generates all possible moves of the Knight
	 * @param occupied list of points that are occupied by another piece
	 * @return possible moves of the Knight
	 */
	public ArrayList<Point> getMoves(ArrayList<Point> occupied) {
		ArrayList<Point> possibleMoves = new ArrayList<>();
		possibleMoves.add(location);
		possibleMoves.add(new Point(location.x+1, location.y+2));
		possibleMoves.add(new Point(location.x+1, location.y-2));
		possibleMoves.add(new Point(location.x+2, location.y+1));
		possibleMoves.add(new Point(location.x+2, location.y-1));
		possibleMoves.add(new Point(location.x-1, location.y+2));
		possibleMoves.add(new Point(location.x-1, location.y-2));
		possibleMoves.add(new Point(location.x-2, location.y+1));
		possibleMoves.add(new Point(location.x-2, location.y-1));
		return possibleMoves;
	}
}
