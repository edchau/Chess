import java.util.ArrayList;

public class EmptyPiece extends Piece {

	/**
	 * creates Empty piece used for unoccupied spaces
	 * @param alliance color of Empty piece (none)
	 * @param score value of Empty piece (0)
	 * @param location current location of Empty piece
	 */
	public EmptyPiece(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
	}

	@Override
	/**
	 * returns nothing, empty piece
	 */
	public ArrayList<Point> getMoves(ArrayList<Point> occupied) {
		return null;
	}
}
