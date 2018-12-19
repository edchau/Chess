import java.util.ArrayList;
import java.util.Map;

public class King extends Piece {

	private boolean firstMove;
	
	/**
	 * creates King piece
	 * @param alliance color of King (black or white)
	 * @param score value of King
	 * @param location current location of King
	 */
	public King(Alliance alliance, int score, Point location) {
		super(alliance, score, location);
		firstMove = true;
	}
	
	@Override
	/**
	 * moves King to a new location
	 * @param p new point to move
	 */
	public void move(Point p) {
		super.move(p);
		firstMove = false;
	}
	
	/**
	 * determines if king's first move
	 * @return true if king has not moved, false otherwise
	 */
	public boolean first() {
		return firstMove;
	}

	@Override
	/**
	 * generates all possible moves of the King
	 * @param occupied list of points that are occupied by another piece
	 * @return possible moves of the King
	 */
	public ArrayList<Point> getMoves(ArrayList<Point> occupied) {
		ArrayList<Point> possibleMoves = new ArrayList<>();
		possibleMoves.add(location);
		possibleMoves.add(new Point(location.x+1, location.y+1));
		possibleMoves.add(new Point(location.x-1, location.y-1));
		possibleMoves.add(new Point(location.x-1, location.y+1));
		possibleMoves.add(new Point(location.x+1, location.y-1));
		possibleMoves.add(new Point(location.x, location.y+1));
		possibleMoves.add(new Point(location.x, location.y-1));
		possibleMoves.add(new Point(location.x-1, location.y));
		possibleMoves.add(new Point(location.x+1, location.y));
		for(int i = 7; i >= 0; i--) {
			if(possibleMoves.get(i).x > 7 || possibleMoves.get(i).x < 0
			   || possibleMoves.get(i).x > 7 || possibleMoves.get(i).x < 0) {
				possibleMoves.remove(i);
			}
		}
		return possibleMoves;
	}
	
	/**
	 * determines if king is in checkmate
	 * @param occupied all occupied spaces
	 * @param m mapped board with point as key, piece as value
	 * @param moves all possible moves of current color
	 * @param opMoves all possible moves of opposing color
	 * @return true if king is in checkmate, false otherwise
	 */
	public boolean checkMate(ArrayList<Point> occupied, Piece p,
						ArrayList<Point> moves, ArrayList<ArrayList<Point>> opMoves,
						Piece.Alliance a) {
		ArrayList<Point> possibleMoves = getMoves(occupied);
		for(int i = possibleMoves.size()-1; i > 0; i--) {
			if(occupied.contains(possibleMoves.get(i))) { 
				possibleMoves.remove(i);
			}
		}
		for(int i = possibleMoves.size()-1; i >= 0; i--) {
			for(int j = 0; j < opMoves.size(); j++) {
				if(opMoves.get(j).contains(possibleMoves.get(i))) {
					possibleMoves.remove(i);
					break;
				}
			}
		}
		if(possibleMoves.size() > 1) {
			return false;
		} else {
			for(int j = opMoves.size()-1; j >= 0; j--) {
				ArrayList<Point> cur = opMoves.get(j);
				if(!cur.contains(location))
					opMoves.remove(j);
				else {
					double distance = Math.abs(location.distance(cur.get(0)));
					double tempDist = distance;
					int t = 1;
					if(!(p instanceof Knight)) {
						while(tempDist >= 2) {
							tempDist = distance / t;
							t++;
						}
					}
					int loc = cur.indexOf(location);
					if(p instanceof Knight) {
						return true;
					}
					for(int i = loc; i >= 0; i--) {
						if(a.equals(Piece.Alliance.BLACK) && (cur.get(i).x > cur.get(0).x)) {
							continue;
						}
						else if(a.equals(Piece.Alliance.WHITE) && (cur.get(i).x < cur.get(0).x)) {
							continue;
						}
						if(distance < 0 || t == 1) {
							break;
						}
						if(tempDist == 1 && distance == 0) {
							break;
						}
						if(moves.contains(cur.get(i)) && !cur.get(i).equals(location)) {
							opMoves.remove(j);
							break;
						}
						distance -= tempDist;
					
					}
				}
			}
		}
		if(opMoves.size() < 1)
			return false;
		return true;
	}
}