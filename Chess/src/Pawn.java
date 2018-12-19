import java.util.ArrayList;

public class Pawn extends Piece {
	
	private boolean firstMove;

	/**
	 * creates Pawn piece
	 * @param alliance color of Pawn (black or white)
	 * @param score value of Pawn
	 * @param location current location of Pawn
	 */
	public Pawn(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
		firstMove = true;
	}
	
	@Override
	/**
	 * moves Pawn to a new location
	 * @param p new point to move
	 */
	public void move(Point p) {
		super.move(p);
		firstMove = false;
	}

	@Override
	/**
	 * generates all possible moves of the Pawn
	 * @param occupied list of points that are occupied by another piece
	 * @return possible moves of the Pawn
	 */
	public ArrayList<Point> getMoves(ArrayList<Point> occupied) {
		ArrayList<Point> possibleMoves = new ArrayList<>();
		int add = alliance == Alliance.BLACK ? 1 : -1;
		possibleMoves.add(location);
		Point oneMove = new Point(location.x+add, location.y);
		Point p = new Point(location.x+add, location.y+add);
		Point p1 = new Point(location.x+add, location.y-add);
		if(occupied.contains(p))
			possibleMoves.add(p);
		if(occupied.contains(p1))
			possibleMoves.add(p1);
		if(!occupied.contains(oneMove))
			possibleMoves.add(oneMove);
		else {
			return possibleMoves;
		}
		if(firstMove) {
			Point twoMove = new Point(location.x+add*2, location.y);
			if(!occupied.contains(twoMove))
				possibleMoves.add(twoMove);
		}
		return possibleMoves;
	}
}
