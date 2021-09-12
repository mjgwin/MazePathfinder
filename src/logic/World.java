package logic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JPanel;

public class World extends JPanel {

	private int width, height, squareSize;
	private int[][] squares;
	private boolean[][] visited;
	private boolean operating;

	public World(String filepath, int width, int height, int squareSize) {
		init(width, height, squareSize);
		loadWorldFromFile(filepath);
	}

	public World(int width, int height, int squareSize) {
		do {
			init(width, height, squareSize);
			generateMaze(0,0);
			resetVisited();
			pickRandomStartEnd();
		}while(!isCurrMazeValid());
		resetVisited();
		fillWallGaps();
	}
	
	public void reset() {
		do {
			init(width, height, squareSize);
			generateMaze(0,0);
			resetVisited();
			pickRandomStartEnd();
		}while(!isCurrMazeValid());
		resetVisited();
		fillWallGaps();
		repaint();
	}
	
	private void fillWallGaps() {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(isWallGap(i, j)) {
					squares[i][j] = 4;
				}
			}
		}
	}
	
	private boolean isWallGap(int x, int y){
		boolean isGap = true;
		
		if(inBounds(x - 1, y)) {
			if(squares[x - 1][y] != 4) {
				isGap = false;
			}
		}
		
		if(inBounds(x + 1, y)) {
			if(squares[x + 1][y] != 4) {
				isGap = false;
			}
		}
		
		if(inBounds(x, y - 1)) {
			if(squares[x][y - 1] != 4) {
				isGap = false;
			}
		}
		
		if(inBounds(x, y + 1)) {
			if(squares[x][y + 1] != 4) {
				isGap = false;
			}
		}
		return isGap;
	}
	
	private boolean isCurrMazeValid() {
		LinkedList<Vector> outcome = this.BFSMatrix(false);
		if(outcome == null) return false;
		return outcome.size() > 70;
	}
	
	private void pickRandomStartEnd() {
		boolean hasStart = false;
		boolean hasEnd = false;
		Random rand = new Random();
		while(!hasStart || !hasEnd) {
			int pickX = rand.nextInt(width);
			int pickY = rand.nextInt(height);
			if(squares[pickX][pickY] != 4 && !hasStart) {
				squares[pickX][pickY] = 1;
				hasStart = true;
			}
			pickX = rand.nextInt(width);
			pickY = rand.nextInt(height);
			if(squares[pickX][pickY] != 4 && !hasEnd) {
				squares[pickX][pickY] = 2;
				hasEnd = true;
			}
		}
		
			
	}
	
	private void resetVisited() {
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited[0].length; j++) {
				visited[i][j] = false;
			}
		}
	}
	
	private void generateMaze(int x, int y) {
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[0].length; j++) {
				if(i == 0 || j == 0 || i == squares.length - 1 || j == squares[0].length - 1) squares[i][j] = 4;
				else squares[i][j] = 0;				
			}
		}
		Stack<Vector> stack = new Stack<Vector>();
		visited[x][y] = true;
		stack.push(new Vector(x, y, squares[x][y]));
		
		while(!stack.isEmpty()) {
			Vector next = stack.pop();
			LinkedList<Vector> neighbor = getNeighbors(next);
			if(neighbor.size() > 0) {
				Vector pick = neighbor.remove();
				stack.push(pick);
				int currX = next.getX();
				int currY = next.getY();
				int pickX = pick.getX();
				int pickY = pick.getY();
				visited[pickX][pickY] = true;
				if(pickX > currX) {
					squares[currX + 1][currY] = 4;
				}else if(pickX < currX) {
					squares[currX - 1][currY] = 4;
				}else if(pickY > currY) {
					squares[currX][currY + 1] = 4;
				}else if(pickY < currY) {
					squares[currX][currY - 1] = 4;
				}
				stack.push(next);
			}  
		}
		
	}
	
	private LinkedList<Vector> getNeighbors(Vector v){
		LinkedList<Vector> neighbors = new LinkedList<Vector>();
		int x = v.getX();
		int y = v.getY();
		
		if(inBounds(x - 2, y)) {
			if(!visited[x - 2][y]) {
				neighbors.add(new Vector(x-2, y, squares[x - 2][y]));
			}
		}
		
		if(inBounds(x + 2, y)) {
			if(!visited[x + 2][y]) {
				neighbors.add(new Vector(x + 2, y, squares[x + 2][y]));
			}
		}
		
		if(inBounds(x, y - 2)) {
			if(!visited[x][y - 2]) {
				neighbors.add(new Vector(x, y - 2, squares[x][y - 2]));
			}
		}
		
		if(inBounds(x, y + 2)) {
			if(!visited[x][y + 2]) {
				neighbors.add(new Vector(x, y + 2, squares[x][y + 2]));
			}
		}
		
		Collections.shuffle(neighbors);
		return neighbors;
	}
	
	private boolean inBounds(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	
	private void init(int width, int height, int squareSize) {
		operating = false;
		this.width = width;
		this.height = height;
		this.squareSize = squareSize;
		squares = new int[width][height];
		visited = new boolean[width][height];
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited[0].length; j++) {
				visited[i][j] = false;
			}
		}
	}

	private void loadWorldFromFile(String filepath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
			String line = br.readLine();
			int i = 0;
			while (line != null) {
				String[] data = line.split(" ");
				for (int j = 0; j < data.length; j++) {
					squares[j][i] = Integer.parseInt(data[j]);
				}
				i++;
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean hasPath() {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(squares[i][j] == 3) {
					return true;
				}
			}
		}
		return false;
	}

	public LinkedList<Vector> BFSMatrix(boolean shouldDraw) {
		if(hasPath() && shouldDraw) return null;
		operating = true;
		Vector root = null;
		Vector dest = null;

		boolean found = false;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (squares[i][j] == 1) {
					root = new Vector(i, j, squares[i][j]);
					System.out.println("Starting search at index: " + root.getX() + "," + root.getY());
				}
				if (squares[i][j] == 2) {
					dest = new Vector(i, j, squares[i][j]);
					System.out.println("Looking for index " + dest.getX() + "," + dest.getY());
				}
			}
		}

		if (root == null || dest == null)
			return null;

		LinkedList<Vector> searchQueue = new LinkedList<Vector>();
		Vector[][] pred = new Vector[width][height];

		searchQueue.add(root);
		visited[root.getX()][root.getY()] = true;

		while (!searchQueue.isEmpty()) {
			Vector vec = searchQueue.remove();
			int x = vec.getX();
			int y = vec.getY();
			if (isValid(x - 1, y)) {
				if (!visit(x - 1, y)) {
					searchQueue.add(new Vector(x - 1, y, squares[x - 1][y]));
					pred[x - 1][y] = vec;
					if (squares[x - 1][y] == 2)
						break;
				}
			}
			if (isValid(x + 1, y)) {
				if (!visit(x + 1, y)) {
					searchQueue.add(new Vector(x + 1, y, squares[x + 1][y]));
					pred[x + 1][y] = vec;
					if (squares[x + 1][y] == 2)
						break;
				}
			}

			if (isValid(x, y - 1)) {
				if (!visit(x, y - 1)) {
					searchQueue.add(new Vector(x, y - 1, squares[x][y - 1]));
					pred[x][y - 1] = vec;
					if (squares[x][y - 1] == 2)
						break;
				}
			}

			if (isValid(x, y + 1)) {
				if (!visit(x, y + 1)) {
					searchQueue.add(new Vector(x, y + 1, squares[x][y + 1]));
					pred[x][y + 1] = vec;
					if (squares[x][y + 1] == 2)
						break;
				}
			}
			if(shouldDraw) {
				try {
					repaint();
					Thread.sleep(1);
					//LockSupport.parkNanos(20000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}

		LinkedList<Vector> path = new LinkedList<Vector>();
		path.add(dest);
		Vector temp = dest;
		
		if(pred[temp.getX()][temp.getY()] == null) return null;
		
		while (pred[temp.getX()][temp.getY()].getData() != 1) {
			path.add(pred[temp.getX()][temp.getY()]);
			temp = pred[temp.getX()][temp.getY()];
		}
		path.add(root);
		return path;
	}
	
	public LinkedList<WeightedNode> aStar(boolean shouldDraw){
		if(hasPath() && shouldDraw) return null;
		WeightedNode root = null;
		WeightedNode dest = null;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (squares[i][j] == 1) {
					root = new WeightedNode(i, j, squares[i][j], 0, 0, 0);
					System.out.println("Starting search at index: " + root.getX() + "," + root.getY());
				}
				if (squares[i][j] == 2) {
					dest = new WeightedNode(i, j, squares[i][j], 0, 0, 0);
					System.out.println("Looking for index " + dest.getX() + "," + dest.getY());
				}
			}
		}
		
		if (root == null || dest == null)
			return null;
		
		LinkedList<WeightedNode> openList = new LinkedList<WeightedNode>();
		LinkedList<WeightedNode> closedList = new LinkedList<WeightedNode>();
		
		openList.add(root);
		
		while(!openList.isEmpty()) {
			WeightedNode currNode = openList.remove();
			int currIndex = 0;
			for(int i = 0; i < openList.size(); i++) {
				if(openList.get(i).getF() < currNode.getF()) {
					currNode = openList.get(i);
					currIndex = i;
				}
			}
			closedList.add(openList.remove(currIndex));
			if(currNode.equals(dest)) break;
		}
		return null;
	}
	

	private boolean visit(int x, int y) {
		if (visited[x][y] == false) {
			visited[x][y] = true;
			return false;
		} else {
			return true;
		}
	}

	private boolean isValid(int i, int j) {
		return i > -1 && i < width && j > -1 && j < height && squares[i][j] != 4;
	}

	public void getPath(LinkedList<Vector> path) {
		if(path == null) {
			operating = false;
			return;
		} 
		for (int i = path.size() - 2; i > 0; i--) {
			Vector v = path.get(i);
			squares[v.getX()][v.getY()] = 3;
			try {
				repaint();
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		operating = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, super.getWidth(), super.getHeight());
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
		drawVisited(g);
	}

	private void drawVisited(Graphics g2d) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (squares[i][j] == 1) {
					g2d.setColor(Color.MAGENTA);
				} else if (squares[i][j] == 2) {
					g2d.setColor(Color.YELLOW);
				} else if (squares[i][j] == 3) {
					g2d.setColor(Color.CYAN);
				} else if (squares[i][j] == 4) {
					g2d.setColor(Color.BLACK);
				} else {
					if (visited[i][j] == false) {
						g2d.setColor(Color.RED);
					} else {
						g2d.setColor(Color.BLUE);
					}
				}
				g2d.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
			}
		}
	}

	public int getSquareSize() {
		return squareSize;
	}

	public int[][] getSquares() {
		return squares;
	}
	
	public boolean getOperating() {
		return operating;
	}

}
