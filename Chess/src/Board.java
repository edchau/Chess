/**
* @author Edward Chau
*
* This program simulates a chess game following conventional
* rules. It can also save and restore progress of a chess
* game.
*
*I have tested this program and it has no issues.
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Board extends JPanel{
	
	private JButton[][] board;
	private final Piece.Alliance WHITE = Piece.Alliance.WHITE;
	private final Piece.Alliance BLACK = Piece.Alliance.BLACK;
	private final Piece.Alliance NONE = Piece.Alliance.NONE;
	private Map<JButton, Piece> game;
	private ArrayList<Point> moves;
	private ArrayList<Point> allMovesSaved;
	private int count = 0;
	private int turn = 0;
	private int checkCount = 0;
	private Piece undoPiece;
	private Piece newPiece;
	private Point current;
	private JLabel whiteScore;
	private JLabel blackScore;
	private JLabel showTurn;
    private JButton reset;
    private JButton save;
    private JButton restore;
    private JTextArea moveDisplay;
    
    /**
     * creates and wires components of chess game
     */
	public Board() {
		createComponents();
		wireComponents();
	}
	
	/**
	 * creates components of chess game
	 */
	public void createComponents() {
		board = new JButton[8][8];
		game = new HashMap<>();
		moves = new ArrayList<>();
		allMovesSaved = new ArrayList<>();
		setLayout(new BorderLayout());
		JPanel chessBoard = new JPanel();
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new BorderLayout());
		chessBoard.setLayout(new GridLayout(8,8));
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				board[i][j] = new JButton();
				game.put(board[i][j], new EmptyPiece(NONE, 0, new Point(i,j)));
				Color tile;
				if(i%2 == 0)
					tile = j%2 == 0 ? Color.WHITE : Color.GRAY;
				else
					tile = j%2 == 0 ? Color.GRAY : Color.WHITE;
				board[i][j].setBackground(tile);
				board[i][j].setOpaque(true);
				board[i][j].setBorder(null);
				chessBoard.add(board[i][j]);
			}
		}
		createToolBar();
		placePieces();
		JPanel letters = new JPanel(new GridLayout(0, 8));
		for(int i = 0; i < 8; i++) {
			letters.add(new JLabel((Character.toString((char)('A' + i))), SwingConstants.CENTER));
		}
		JPanel numbers = new JPanel(new GridLayout(8, 0));
		for(int i = 0; i < 8; i++) {
			numbers.add(new JLabel(" " + Integer.toString(1 + i), SwingConstants.CENTER));
		}
		boardPanel.add(letters, BorderLayout.PAGE_START);
		add(numbers, BorderLayout.LINE_START);
		boardPanel.add(chessBoard, BorderLayout.CENTER);
		add(boardPanel, BorderLayout.CENTER);
		moveDisplay = new JTextArea("Moves      ");
		moveDisplay.setEditable(false);
		JScrollPane scroll = new JScrollPane(moveDisplay);
		add(scroll, BorderLayout.EAST);
	}
	
	/**
	 * creates tool bar with scores and reset, save, restore buttons
	 */
	public void createToolBar() {
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        add(tools, BorderLayout.PAGE_START);
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(1, 4));
        toolBarListener lis = new toolBarListener();
        reset = new JButton("Reset");
        save = new JButton("Save");
        restore = new JButton("Restore");
        reset.addActionListener(lis);
        save.addActionListener(lis);
        restore.addActionListener(lis);
        toolPanel.add(reset);
        toolPanel.add(save);
        toolPanel.add(restore); 
        tools.add(toolPanel);
        tools.addSeparator();
        showTurn = new JLabel("White's Turn");
        tools.add(showTurn);
        tools.add(Box.createHorizontalGlue());
        tools.add(new JLabel("White: "));
        whiteScore = new JLabel("0");
        tools.add(whiteScore);
        tools.addSeparator();
        tools.add(new JLabel("Black: "));
        blackScore = new JLabel("0");
        tools.add(blackScore);
	}
	
	public class toolBarListener implements ActionListener {

		/**
		 * reset entire chess game
		 */
		public void clearBoard() {
			removeAll();
			turn = 0;
			count = 0;
			whiteScore.setText("0");
			blackScore.setText("0");
			createComponents();
			wireComponents();
			revalidate();
			repaint();
		}
		
		/**
		 * read in game from file
		 */
		public void readFile() {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(Board.this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = chooser.getSelectedFile();
				try (Scanner in = new Scanner(file)){
					clearBoard();
					allMovesSaved.clear();
					String input = in.nextLine();
					String[] temp = input.split(" ");
					whiteScore.setText(temp[0]);
					blackScore.setText(temp[1]);
					turn = Integer.parseInt(temp[2]);
					while(in.hasNextLine()) {
						String p = in.nextLine();
						String[] points = p.split(" ");
						Point t = new Point(Integer.parseInt(points[0]),Integer.parseInt(points[1]));
						allMovesSaved.add(t);
					}
				} catch (FileNotFoundException e) {}
				execute();
	        }
		}
	
		/**
		 * execute moves read from file
		 */
		public void execute() {
			Point cur = new Point(0,0);
			Point newP = new Point(0,0);
			String label = allMovesSaved.size() % 2 == 1 ? "White's Turn" : "Black's Turn";
			showTurn.setText(label);
			for(int i = 0; i < allMovesSaved.size(); i++) {
				if(i % 2 == 0)
					cur = allMovesSaved.get(i);
				else {
					newP = allMovesSaved.get(i);
					String from = Character.toString((char)(cur.y + 'A'))
							+ Integer.toString(cur.x+1);
					String to = Character.toString((char)(newP.y + 'A'))
							+ Integer.toString(newP.x+1);
					moveDisplay.append("\n" + from + " -> " + to);
					Piece p = game.get(board[cur.x][cur.y]);
					if(game.get(board[cur.x][cur.y]) instanceof King && 
							cur.y == 4) {
						if(p.getAlliance() == WHITE) {
							if(newP.equals(new Point(7,6)))
								handleCastle(newP, WHITE);
							else if(newP.equals(new Point(7, 1))) {
								handleCastle(newP, WHITE);
							}
						} else {
							if(newP.equals(new Point(0,6)))
								handleCastle(newP,BLACK);
							else if(newP.equals(new Point(0,1)))
								handleCastle(newP, BLACK);
						}
					}
					board[newP.x][newP.y].setIcon(
							board[cur.x][cur.y].getIcon());
					board[newP.x][newP.y].setDisabledIcon(
							board[cur.x][cur.y].getIcon());
					board[newP.x][newP.y].setOpaque(true);
					board[newP.x][newP.y].setBorder(null);
					game.put(board[newP.x][newP.y], 
							game.get(board[cur.x][cur.y]));
					game.put(board[cur.x][cur.y], 
							new EmptyPiece(NONE, 0, new Point(cur.x,cur.y)));
					board[cur.x][cur.y].setIcon(null);
					board[cur.x][cur.y].setOpaque(true);
					board[cur.x][cur.y].setBorder(null);
					Piece newPiece = game.get(board[newP.x][newP.y]);
					newPiece.move(newP);
					disableTurn();
					pawnEnd();
				}
			}
			Piece.Alliance curA = turn == 0 ? WHITE : BLACK;
			if((check(curA) && (checkMate(curA)) && simulateTake(curA, newP))) {
				disableAll();
				popUp("CHECK MATE!", "Status");
			} else if(check(curA)) {
				popUp("Check!", "Status");
				checkCount = 1;
			}
		}
		
		/**
		 * save current game to file
		 */
		public void saveFile() {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showSaveDialog(Board.this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	File file = chooser.getSelectedFile();
	        	try (PrintWriter out = new PrintWriter(file)) {
					out.println(whiteScore.getText() + " " + 
								blackScore.getText() + " " + 
								Integer.toString(turn));
					for(int i = 0; i < allMovesSaved.size(); i++) {
						out.println(allMovesSaved.get(i).toString());
					}
				} catch (FileNotFoundException e) {}
	        }
		}
		
		@Override
		/**
		 * @param e action to be processed
		 */
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == reset) {
				clearBoard();
			} else if(e.getSource() == restore) {
				readFile();
			} else if(e.getSource() == save) {
				saveFile();
			}
		}
		
	}
	
	/**
	 * creates pieces on chess board
	 */
	public void placePieces() {
		pawn();
		rook();
		knight();
		bishop();
		queen();
		king();
	}
	
	/**
	 * creates pawns
	 */
	public void pawn() {
		ImageIcon bp = new ImageIcon("BP.gif");
		ImageIcon wp = new ImageIcon("WP.gif");
		for(int i = 0; i < 8; i++) {
			Point p = new Point(1, i);
			game.put(board[1][i], new Pawn(BLACK, 10, p));
			board[1][i].setIcon(bp);
			board[1][i].setDisabledIcon(bp);
			board[1][i].setOpaque(true);
			board[1][i].setBorder(null);
			board[1][i].setEnabled(false);
			Point p2 = new Point(6, i);
			game.put(board[6][i],new Pawn(WHITE, 10, p2));
			board[6][i].setIcon(wp);
			board[6][i].setDisabledIcon(wp);
			board[6][i].setOpaque(true);
			board[6][i].setBorder(null);
		}
	}
	
	/**
	 * creates rooks
	 */
	public void rook() {
		ImageIcon wr = new ImageIcon("WR.gif");
		ImageIcon br = new ImageIcon("BR.gif");
		for(int i = 0; i < 2; i++) {
			Point p = new Point(0, 7 * i);
			game.put(board[0][p.y], new Rook(BLACK, 50, p));
			board[0][p.y].setIcon(br);
			board[0][p.y].setDisabledIcon(br);
			board[0][p.y].setOpaque(true);
			board[0][p.y].setBorder(null);
			board[0][p.y].setEnabled(false);
			Point p2 = new Point(7, 7 * i);
			game.put(board[7][p2.y],new Rook(WHITE, 50, p2));
			board[7][p2.y].setIcon(wr);
			board[7][p2.y].setDisabledIcon(wr);
			board[7][p2.y].setOpaque(true);
			board[7][p2.y].setBorder(null);
		}
	}
	
	/**
	 * creates knights
	 */
	public void knight() {
		ImageIcon wn = new ImageIcon("WN.gif");
		ImageIcon bn = new ImageIcon("BN.gif");
		for(int i = 0; i < 2; i++) {
			Point p = new Point(0, 5 * i + 1);
			game.put(board[0][p.y], new Knight(BLACK, 20, p));
			board[0][p.y].setIcon(bn);
			board[0][p.y].setDisabledIcon(bn);
			board[0][p.y].setOpaque(true);
			board[0][p.y].setBorder(null);
			board[0][p.y].setEnabled(false);
			Point p2 = new Point(7, 5 * i + 1);
			game.put(board[7][p2.y],new Knight(WHITE, 20, p2));
			board[7][p2.y].setIcon(wn);
			board[7][p2.y].setDisabledIcon(wn);
			board[7][p2.y].setOpaque(true);
			board[7][p2.y].setBorder(null);
		}
	}
	
	/**
	 * creates bishops
	 */
	public void bishop() {
		ImageIcon wb = new ImageIcon("WB.gif");
		ImageIcon bb = new ImageIcon("BB.gif");
		for(int i = 0; i < 2; i++) {
			Point p = new Point(0, 3 * i + 2);
			game.put(board[0][p.y], new Bishop(BLACK, 30, p));
			board[0][p.y].setIcon(bb);
			board[0][p.y].setDisabledIcon(bb);
			board[0][p.y].setOpaque(true);
			board[0][p.y].setBorder(null);
			board[0][p.y].setEnabled(false);
			Point p2 = new Point(7, 3 * i + 2);
			game.put(board[7][p2.y],new Bishop(WHITE, 30, p2));
			board[7][p2.y].setIcon(wb);
			board[7][p2.y].setDisabledIcon(wb);
			board[7][p2.y].setOpaque(true);
			board[7][p2.y].setBorder(null);
		}
	}
	
	/**
	 * creates queens
	 */
	public void queen() {
		ImageIcon wq = new ImageIcon("WQ.gif");
		ImageIcon bq = new ImageIcon("BQ.gif");
		Point p = new Point(0, 3);
		game.put(board[0][3], new Queen(BLACK, 70, p));
		board[0][3].setIcon(bq);
		board[0][3].setDisabledIcon(bq);
		board[0][3].setOpaque(true);
		board[0][3].setBorder(null);
		board[0][3].setEnabled(false);
		Point p2 = new Point(7, 3);
		game.put(board[7][3], new Queen(WHITE, 70, p2));
		board[7][3].setIcon(wq);
		board[7][3].setDisabledIcon(wq);
		board[7][3].setOpaque(true);
		board[7][3].setBorder(null);
	}
	
	/**
	 * creates kings
	 */
	public void king() {
		ImageIcon wk = new ImageIcon("WK.gif");
		ImageIcon bk = new ImageIcon("BK.gif");
		Point p = new Point(0, 4);
		game.put(board[0][4], new King(BLACK, 100, p));
		board[0][4].setIcon(bk);
		board[0][4].setDisabledIcon(bk);
		board[0][4].setOpaque(true);
		board[0][4].setBorder(null);
		board[0][4].setEnabled(false);
		Point p2 = new Point(7, 4);
		game.put(board[7][4], new King(WHITE, 100, p2));
		board[7][4].setIcon(wk);
		board[7][4].setDisabledIcon(wk);
		board[7][4].setOpaque(true);
		board[7][4].setBorder(null);
	}
	
	/**
	 * wires components of chess game
	 */
	public void wireComponents() {
		TileListener tl = new TileListener();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				board[i][j].addActionListener(tl);
			}
		}
	}
	
	/**
	 * disables all black pieces
	 */
	public void disableBlack() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(game.get(board[i][j]) instanceof EmptyPiece)
					board[i][j].setEnabled(false);
				else {
					if(game.get(board[i][j]).getAlliance().equals(BLACK))
						board[i][j].setEnabled(false);
					else
						board[i][j].setEnabled(true);
				}
			}
		}
	}
	
	/**
	 * disables all white pieces
	 */
	public void disableWhite() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(game.get(board[i][j]) instanceof EmptyPiece)
					board[i][j].setEnabled(false);
				else {
					if(game.get(board[i][j]).getAlliance().equals(WHITE))
						board[i][j].setEnabled(false);
					else
						board[i][j].setEnabled(true);
				}
			}
		}
	}
	
	/**
	 * disable all tiles on chess board
	 */
	public void disableAll() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				board[i][j].setEnabled(false);
			}
		}
	}
	
	/**
	 * generates all white moves 
	 * @return list of all moves of white pieces
	 */
	public ArrayList<Point> getAllWhiteMoves() {
		ArrayList<Point> whiteMoves = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Piece p = game.get(board[i][j]);
				if(p.getAlliance().equals(WHITE) && !(p instanceof King)) {
					ArrayList<Point> curMoves = 
							p.getMoves(getAllTiles());
					whiteMoves.addAll(curMoves);
				}
			}
		}
		return whiteMoves;
	}
	
	/**
	 * generates all black moves 
	 * @return list of all moves of black pieces
	 */
	public ArrayList<Point> getAllBlackMoves() {
		ArrayList<Point> blackMoves = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Piece p = game.get(board[i][j]);
				if(p.getAlliance().equals(BLACK) && !(p instanceof King)) {
					ArrayList<Point> curMoves = 
							p.getMoves(getAllTiles());
					blackMoves.addAll(curMoves);
				}
			}
		}
		return blackMoves;
	}
	
	/**
	 * generates all black moves separated by lists
	 * @return list of lists of moves of each piece
	 */
	public ArrayList<ArrayList<Point>> getBlack() {
		ArrayList<ArrayList<Point>> blackMoves = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Piece p = game.get(board[i][j]);
				if(p.getAlliance().equals(BLACK) && !(p instanceof King)) {
					ArrayList<Point> curMoves = 
							p.getMoves(getAllTiles());
					for(int k = 1; k < curMoves.size(); k++) {
						int moveX = curMoves.get(k).x;
						int moveY = curMoves.get(k).y;
						if(!(moveX < 8 && moveX > -1 && moveY < 8 && moveY > -1) ||
							game.get(board[moveX][moveY]).getAlliance().equals(BLACK)) {
							curMoves.remove(k);
						}
					}
					blackMoves.add(curMoves);
				}
			}
		}
		return blackMoves;
	}
	
	/**
	 * generates all white moves separated by lists
	 * @return list of lists of moves of each piece
	 */
	public ArrayList<ArrayList<Point>> getWhite() {
		ArrayList<ArrayList<Point>> whiteMoves = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Piece p = game.get(board[i][j]);
				if(p.getAlliance().equals(WHITE) && !(p instanceof King)) {
					ArrayList<Point> curMoves = 
							p.getMoves(getAllTiles());
					for(int k = 1; k < curMoves.size(); k++) {
						int moveX = curMoves.get(k).x;
						int moveY = curMoves.get(k).y;
						if(!(moveX < 8 && moveX > -1 && moveY < 8 && moveY > -1) ||
							game.get(board[moveX][moveY]).getAlliance().equals(WHITE)) {
							curMoves.remove(k);
						}
					}
					whiteMoves.add(curMoves);
				}
			}
		}
		return whiteMoves;
	}

	/**
	 * determines if king is in check
	 * @param a current alliance of king to check
	 * @return true if king is in check, false otherwise
	 */
	public boolean check(Piece.Alliance a) {
		ArrayList<Point> check = a == WHITE ? getAllBlackMoves() 
				: getAllWhiteMoves();
		for(int i = 0; i < check.size(); i++) {
			int moveX = check.get(i).x;
			int moveY = check.get(i).y;
			if(moveX < 8 && moveX > -1 && moveY < 8 && moveY > -1) {
				Piece temp = game.get(board[moveX][moveY]);
				if(temp instanceof King && temp.getAlliance().equals(a)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * gets location of white king
	 * @return location of white king
	 */
	public Point whiteKing() { 
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Piece p = game.get(board[i][j]);
				if(p instanceof King && p.getAlliance().equals(WHITE))
					return p.getLocation();
			}
		}
		return new Point(-1,-1);
	}
	
	/**
	 * gets location of black king
	 * @return location of black king
	 */
	public Point blackKing() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Piece p = game.get(board[i][j]);
				if(p instanceof King && p.getAlliance().equals(BLACK))
					return p.getLocation();
			}
		}
		return new Point(-1,-1);
	}
	
	/**
	 * get all locations of game pieces
	 * @return list of locations of game pieces
	 */
	public ArrayList<Point> getAllTiles() {
		ArrayList<Point> tiles = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!(game.get(board[i][j]) instanceof EmptyPiece))
					tiles.add(new Point(i, j));
			}
		}
		return tiles;
	}
	
	/**
	 * gets all current black pieces
	 * @return list of locations of all black pieces
	 */
	public ArrayList<Point> getBlackPieces() {
		ArrayList<Point> tiles = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!(game.get(board[i][j]) instanceof EmptyPiece) 
				&& game.get(board[i][j]).getAlliance().equals(BLACK))
					tiles.add(new Point(i, j));
			}
		}
		return tiles;
	}
	
	/**
	 * gets all current white pieces
	 * @return list of locations of all white pieces
	 */
	public ArrayList<Point> getWhitePieces() {
		ArrayList<Point> tiles = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!(game.get(board[i][j]) instanceof EmptyPiece) 
				&& game.get(board[i][j]).getAlliance().equals(WHITE))
					tiles.add(new Point(i, j));
			}
		}
		return tiles;
	}
			
	/**
	 * display moves of chess piece selected
	 * @param moves current moves to be displayed
	 */
	public void displayMoves(ArrayList<Point> moves) {
		for(int i = 0; i < moves.size(); i++) {
			int moveX = moves.get(i).x;
			int moveY = moves.get(i).y;
			if(moveX < 8 && moveX > -1 && moveY < 8 && moveY > -1) {
				if(!game.get(board[current.x][current.y]).getAlliance()
					.equals(game.get(board[moveX][moveY]).getAlliance())
					|| current == moves.get(i)) {
					board[moveX][moveY].setBorder(
							new LineBorder(Color.GREEN, 1));
					board[moveX][moveY].setEnabled(true);
				} 
			}
		}
	}
	
	/**
	 * display castle moves
	 */
	public void castle() {
		boolean castleRight = true;
		boolean castleLeft = true;
		if(current.equals(new Point(0,4)) && game.get(board[0][4]) instanceof King) {
			King k = (King)game.get(board[0][4]);
			if(!k.first()) 
				return;
			for(int i = 1; i < 3; i++) {
				if(!(game.get(board[0][4+i]) instanceof EmptyPiece)) {
					castleRight = false;
					break;
				}
			}
			for(int i = 1; i < 4; i++) {
				if(!(game.get(board[0][4-i]) instanceof EmptyPiece)) {
					castleLeft = false;
					break;
				}
			}
			if(!(game.get(board[0][7]) instanceof Rook)) {
				castleRight = false;
			}
			if(!(game.get(board[0][0]) instanceof Rook)) {
				castleLeft = false;
			}
			if(castleRight) {
				board[0][6].setBorder(
						new LineBorder(Color.GREEN, 1));
				board[0][6].setEnabled(true);
			}
			if(castleLeft) {
				board[0][1].setBorder(
						new LineBorder(Color.GREEN, 1));
				board[0][1].setEnabled(true);
			}
		} else if (current.equals(new Point(7, 4)) && game.get(board[0][4]) instanceof King) {
			King k = (King)game.get(board[7][4]);
			if(!k.first()) 
				return;
			for(int i = 1; i < 3; i++) {
				if(!(game.get(board[7][4+i]) instanceof EmptyPiece)) {
					castleRight = false;
					break;
				}
			}
			for(int i = 1; i < 4; i++) {
				if(!(game.get(board[7][4-i]) instanceof EmptyPiece)) {
					castleLeft = false;
					break;
				}
			}
			if(!(game.get(board[7][7]) instanceof Rook)) {
				castleRight = false;
			}
			if(!(game.get(board[7][0]) instanceof Rook)) {
				castleLeft = false;
			}
			if(castleRight) {
				board[7][6].setBorder(
						new LineBorder(Color.GREEN, 1));
				board[7][6].setEnabled(true);
			}
			if(castleLeft) {
				board[7][1].setBorder(
						new LineBorder(Color.GREEN, 1));
				board[7][1].setEnabled(true);
			}
		}
	}
	
	/**
	 * clears borders of game board
	 */
	public void clearBorders() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				board[i][j].setBorder(null);
			}
		}
	}
	
	/**
	 * Creates pop up
	 * @param message message to be displayed
	 */
	public void popUp(String message, String status) {
		JOptionPane.showMessageDialog(null, message, 
				status, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * updates score of players
	 * @param p piece to get score from
	 */
	public void updateScore(Piece p) {
		if(p.getAlliance().equals(WHITE)) {
			blackScore.setText(Integer.toString(Integer.parseInt(
					blackScore.getText())+p.getScore()));
		} else if(p.getAlliance().equals(BLACK)) {
			whiteScore.setText(Integer.toString(Integer.parseInt(
					whiteScore.getText())+p.getScore()));
		}
	}
	
	/**
	 * switches turns
	 */
	public void disableTurn() {
		if(turn == 0) {
			disableBlack();
			showTurn.setText("White's Turn");
		}
		if(turn == 1) {
			disableWhite(); 
			showTurn.setText("Black's Turn");
		}
	}
	
	/**
	 * Simulates taking piece that is checking king
	 * @param a color of king in check
	 * @param take piece that is checking king
	 * @return true if can take, false otherwise
	 */
	public boolean simulateTake(Piece.Alliance a, Point take) {
		for(int j = 0; j < 8; j++) {
			for(int k = 0; k < 8; k++) {
				if(game.get(board[j][k]).getAlliance().equals(a)) {
					ArrayList<Point> movesSim = game.get(board[j][k]).getMoves(getAllTiles());
					if(movesSim.contains(take)) {
						current = new Point(j,k);
						moveTo(game.get(board[take.x][take.y]), true);
						if(!check(a)) {
							undo();
							return false;
						}
						undo();
					}
					if(game.get(board[j][k]) instanceof King) { //king can move
						current = new Point(j,k);
						for(int i = 0; i < movesSim.size(); i++) {
							int moveX = movesSim.get(i).x;
							int moveY = movesSim.get(i).y;
							if(moveX < 8 && moveX > -1 && moveY < 8 && moveY > -1) {
								if(!game.get(board[j][k]).getAlliance()
										.equals(game.get(board[moveX][moveY]).getAlliance())) {
									moveTo(game.get(board[moveX][moveY]), true);
									if(!check(a)) {
										undo();
										return false;
									}
									undo();
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * moves Rook for castling
	 * @param p point of king move
	 * @param a color of king
	 */
	public void handleCastle(Point p, Piece.Alliance a) {
		if(p.y == 6) {
			if(a.equals(WHITE)) {
				game.put(board[7][5], 
						game.get(board[7][7]));
				game.put(board[7][7], 
						new EmptyPiece(NONE, 0, 
						new Point(7,7)));
				board[7][5].setIcon(
						board[7][7].getIcon());
				board[7][5].setDisabledIcon(
						board[7][7].getIcon());
				board[7][5].setOpaque(true);
				board[7][5].setBorder(null);
				board[7][7].setIcon(null);
				board[7][7].setOpaque(true);
				board[7][7].setBorder(null);
				Piece newP = game.get(board[7][5]);
				newP.move(new Point(7,5));
			} else {
				game.put(board[0][5], 
						game.get(board[0][7]));
				game.put(board[0][7], 
						new EmptyPiece(NONE, 0, 
						new Point(0,7)));
				board[0][5].setIcon(
						board[0][7].getIcon());
				board[0][5].setDisabledIcon(
						board[0][7].getIcon());
				board[0][5].setOpaque(true);
				board[0][5].setBorder(null);
				board[0][7].setIcon(null);
				board[0][7].setOpaque(true);
				board[0][7].setBorder(null);
				Piece newP = game.get(board[0][5]);
				newP.move(new Point(0,5));
			}
		} else if(p.y == 1) {
			if(a.equals(WHITE)) {
				game.put(board[7][2], 
						game.get(board[7][0]));
				game.put(board[7][0], 
						new EmptyPiece(NONE, 0, 
						new Point(7,0)));
				board[7][2].setIcon(
						board[7][0].getIcon());
				board[7][2].setDisabledIcon(
						board[7][0].getIcon());
				board[7][2].setOpaque(true);
				board[7][2].setBorder(null);
				board[7][0].setIcon(null);
				board[7][0].setOpaque(true);
				board[7][0].setBorder(null);
				Piece newP = game.get(board[7][2]);
				newP.move(new Point(7,2));
			} else {
				game.put(board[0][2], 
						game.get(board[0][0]));
				game.put(board[0][0], 
						new EmptyPiece(NONE, 0, 
						new Point(0,0)));
				board[0][2].setIcon(
						board[0][0].getIcon());
				board[0][2].setDisabledIcon(
						board[0][0].getIcon());
				board[0][2].setOpaque(true);
				board[0][2].setBorder(null);
				board[0][0].setIcon(null);
				board[0][0].setOpaque(true);
				board[0][0].setBorder(null);
				Piece newP = game.get(board[0][2]);
				newP.move(new Point(0,2));
			}
		}
	}
	
	/**
	 * move piece to another location
	 * @param p piece to move
	 */
	public void moveTo(Piece p, boolean t) {
		Point newPoint = p.getLocation();
		if(game.get(board[current.x][current.y]) instanceof King && 
				current.y == 4) {
			if(turn == 0) {
				if(newPoint.equals(new Point(7,6)))
					handleCastle(newPoint, WHITE);
				else if(newPoint.equals(new Point(7, 1))) {
					handleCastle(newPoint, WHITE);
				}
			} else {
				if(newPoint.equals(new Point(0,6)))
					handleCastle(newPoint,BLACK);
				else if(newPoint.equals(new Point(0,1)))
					handleCastle(newPoint, BLACK);
			}
		}
		undoPiece = game.get(board[newPoint.x][newPoint.y]);
		String from = Character.toString((char)(current.y + 'A')) 
				+ Integer.toString(current.x+1);
		String to = Character.toString((char)(newPoint.y + 'A')) 
				+ Integer.toString(newPoint.x+1);
		allMovesSaved.add(new Point(current.x, current.y));
		allMovesSaved.add(new Point(newPoint.x, newPoint.y));
		game.put(board[newPoint.x][newPoint.y], 
				game.get(board[current.x][current.y]));
		game.put(board[current.x][current.y], 
				new EmptyPiece(NONE, 0, 
				new Point(current.x,current.y)));
		Piece.Alliance curA = turn == 0 ? BLACK : WHITE;
		if(((check(WHITE) || check(BLACK)) && checkCount > 0) || t) {
			allMovesSaved.remove(allMovesSaved.size()-1);
			allMovesSaved.remove(allMovesSaved.size()-1);
			if(!t) {
				undo();
				popUp("STILL IN CHECK", "Invalid");
			}
			return;
		}
		updateScore(p);
		moveDisplay.append("\n" + from + " -> " + to);
		board[newPoint.x][newPoint.y].setIcon(
				board[current.x][current.y].getIcon());
		board[newPoint.x][newPoint.y].setDisabledIcon(
				board[current.x][current.y].getIcon());
		board[newPoint.x][newPoint.y].setOpaque(true);
		board[newPoint.x][newPoint.y].setBorder(null);
		board[current.x][current.y].setIcon(null);
		board[current.x][current.y].setOpaque(true);
		board[current.x][current.y].setBorder(null);
		Piece newP = game.get(board[newPoint.x][newPoint.y]);
		newP.move(newPoint);
		count = (count + 1) % 2;
		clearBorders();
		turn = (turn + 1) % 2;
		disableTurn();
		pawnEnd();
		Piece.Alliance cmA = turn == 0 ? WHITE : BLACK;
		newPiece = newP;
		if((check(curA) && (checkMate(cmA)) && simulateTake(cmA, newPoint))) {
			disableAll();
			popUp("CHECK MATE!", "Status");
		} else if(check(curA)) {
			popUp("Check!", "Status");
			checkCount++;
		} else {
			checkCount = 0;
		}
	}
	
	/**
	 * Checks for check mate
	 * @param a color to check for
	 * @return true if game over, false otherwise
	 */
	public boolean checkMate(Piece.Alliance a) {
		if(a.equals(WHITE)) {
			Point wk = whiteKing();
			King k = (King)(game.get(board[wk.x][wk.y]));
			return k.checkMate(getAllTiles(), newPiece, getAllWhiteMoves(), getBlack(), WHITE);
		} else {
			Point bk = blackKing();
			King k = (King)(game.get(board[bk.x][bk.y]));
			return k.checkMate(getAllTiles(), newPiece, getAllBlackMoves(), getWhite(), BLACK);
		}
	}
	
	/**
	 * set initial point for piece
	 * @param p selected piece
	 */
	public void moveFrom(Piece p) {
		moves = p.getMoves(getAllTiles());
		current = p.getLocation();
		displayMoves(moves);
		if(p instanceof King) {
			castle();
		}
		count++;
	}
	
	/**
	 * Undo only previous move
	 */
	public void undo() {
		count = 0;
		game.put(board[current.x][current.y], 
				game.get(board[undoPiece.getLocation().x][undoPiece.getLocation().y]));
		game.put(board[undoPiece.getLocation().x][undoPiece.getLocation().y], undoPiece);
		Piece newP = game.get(board[current.x][current.y]);
		newP.move(current);
		clearBorders();
	}
	
	public class TileListener implements ActionListener {

		@Override
		/**
		 * @param e move to be processed
		 */
		public void actionPerformed(ActionEvent e) {
			disableTurn();
			if(count > 0 && game.get(e.getSource()).getLocation().equals(
					current)) {
				count = 0;
				clearBorders();
				return;
			}
			if(count == 0) {
				if(game.get(e.getSource()) instanceof Piece && 
						!(game.get(e.getSource()) instanceof EmptyPiece)) {
					disableAll();
					Piece p = game.get(e.getSource());
					moveFrom(p);
				}
			} else {
				Piece p = game.get(e.getSource());
				moveTo(p, false);
			}
			repaint();
		}
	}
	
	/**
	 * Swap out pawn when it reaches the end
	 */
	public void pawnEnd() {
		for(int i = 0; i < 8; i++) {
			if(game.get(board[0][i]) instanceof Pawn) {
				changePiece(WHITE, new Point(0, i));
				return;
			}
			if(game.get(board[7][i]) instanceof Pawn) {
				changePiece(BLACK, new Point(7, i));
				return;
			}
		}
	}
	
	/**
	 * Swaps piece with pawn
	 * @param a color of pawn that reaches end
	 * @param p point at which pawn is located
	 */
	public void changePiece(Piece.Alliance a, Point p) { 
		Map<String, Piece> choices = new HashMap<>();
		choices.put("Knight", new Knight(a, 20, p));
		choices.put("Queen", new Queen(a, 70, p));
		choices.put("Rook", new Rook(a, 50, p));
		choices.put("Bishop", new Bishop(a, 30, p));
		Map<String, String> bImage = new HashMap<>();
		bImage.put("Knight", "BN.gif");
		bImage.put("Queen", "BQ.gif");
		bImage.put("Rook", "BR.gif");
		bImage.put("Bishop", "BB.gif");
		Map<String, String> wImage = new HashMap<>();
		wImage.put("Knight", "WN.gif");
		wImage.put("Queen", "WQ.gif");
		wImage.put("Rook", "WR.gif");
		wImage.put("Bishop", "WB.gif");
		String[] sel = {"Knight", "Queen", "Rook", "Bishop"};
	    String input = (String) JOptionPane.showInputDialog(null, "New Piece",
	            "Select", JOptionPane.QUESTION_MESSAGE, null,
	            sel,
	            sel[1]); 
	    game.put(board[p.x][p.y], choices.get(input));
	    if(a.equals(WHITE)) { 
	    	ImageIcon w = new ImageIcon(wImage.get(input));
	    	board[p.x][p.y].setIcon(w);
	    	board[p.x][p.y].setDisabledIcon(w);
	    	board[p.x][p.y].setOpaque(true);
	    }
	    else if(a.equals(BLACK)) { 
	    	ImageIcon b = new ImageIcon(bImage.get(input));
	    	board[p.x][p.y].setIcon(b);
	    	board[p.x][p.y].setDisabledIcon(b);
	    	board[p.x][p.y].setOpaque(true);
	    }
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setTitle("Chess");
		JPanel panel = new Board();
		panel.setPreferredSize(new Dimension(800, 800));
		f.add(panel);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}