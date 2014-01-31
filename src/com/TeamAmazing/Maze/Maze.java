package com.TeamAmazing.Maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

public class Maze implements Parcelable {
	private int width;
	private int height;
	private Cell[] cells; // An array of the cells in row major order
	private List<Wall> walls;
	private Random rand = new Random();

	public Maze(int width, int height) {
		this.width = width;
		this.height = height;
		int id = 0;
		this.cells = new Cell[width * height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[j * width + i] = new Cell(id, i, j);
				id++;
			}
		}
	}

	private void makeAllWalls() {
		this.walls = new ArrayList<Wall>(2 * (width + 1) * (height + 1));
		for (int i = 0; i < width; i++) {
			// Add walls on the top and bottom
			walls.add(new Wall(cells[i], null));
			walls.add(new Wall(cells[i + (width * (height - 1))], null));
		}
		for (int j = 0; j < height; j++) {
			// Add walls on the left and right
			walls.add(new Wall(cells[j * width], null));
			walls.add(new Wall(cells[j * width + (width - 1)], null));
		}

		// Add the walls on the inside of the maze.
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// add wall to the right if there is space
				if (i < width - 1) {
					walls.add(new Wall(cells[j * width + i], cells[j * width
							+ (i + 1)]));
				}

				// add wall to the bottom if there is space
				if (j < height - 1) {
					walls.add(new Wall(cells[j * width + i], cells[(j + 1)
							* width + i]));
				}
			}
		}
	}

	private void union(Cell n1, Cell n2) {
		Cell r1 = find(n1);
		Cell r2 = find(n2);
		if (r1.rank > r2.rank)
			r2.ref = r1;
		else if (r1.rank < r2.rank)
			r1.ref = r2;
		else if (!r1.equals(r2)) {
			r2.ref = r1;
			r1.rank++;
		}
	}

	private Cell find(Cell node) {
		if (node.id != node.ref.id) {
			node.ref = find(node.ref);
			return node.ref;
		} else
			return node;
	}

	/**
	 * Creates a maze using a union-find algorithm. Also known as Kruskal's
	 * algorithm.
	 */
	public void makeKruskalMaze() {
		makeAllWalls();
		Collections.shuffle(walls);
		for (Iterator<Wall> it = walls.iterator(); it.hasNext();) {
			Wall w = it.next();
			// Avoid the walls on the boundary
			if (w.getV1() != null && w.getV2() != null) {
				if (find(w.getV1()).ref.id != find(w.getV2()).ref.id) {
					// The two cells the wall is between are not connected
					// by a path, so delete the wall and union the cell's
					// partitions.
					union(w.getV1(), w.getV2());
					it.remove();
				}
			}
		}
	}

	/** Recursive backtracker DFS algorithm */
	public void makeDFSMaze() {
		makeAllWalls();
		Deque<Cell> stack = new ArrayDeque<Cell>();
		// Make the initial cell the current cell and mark it as visited
		Cell currentCell = cells[cells.length/2 + rand.nextInt(cells.length/2)];
		currentCell.markVisited();
		Cell nextCell = null;
		int numOfUnvisitedCells = width * height - 1;
		// While there are unvisited cells
		while (numOfUnvisitedCells > 0) {
			// If the current cell has any neighbors which have not been visited
			// Choose randomly one of the unvisited neighbors
			nextCell = getRandomUnvistedNeighbor(currentCell);
			if (nextCell != null) {
				// Push the current cell to the stack
				stack.addFirst(currentCell);
				// Remove the wall between the current cell and the chosen cell
				walls.remove(new Wall(currentCell, nextCell));
				// Make the chosen cell the current cell and mark it as visited
				currentCell = nextCell;
				currentCell.markVisited();
				numOfUnvisitedCells--;
			} else if (!stack.isEmpty()) {
				// Pop a cell from the stack and make it the current cell
				currentCell = stack.removeFirst();
			} else {
				// Pick a unvisited cell, make it the current cell and mark it
				// as
				// visited
				for (Cell c : cells) {
					if (c.isUnvisited()) {
						currentCell = c;
						currentCell.markVisited();
						numOfUnvisitedCells--;
						break;
					}

				}
			}
		}
	}

	private List<Cell> getNeighborCells(Cell cell) {
		List<Cell> neighbors = new ArrayList<Cell>(4);
		if (cell.coordinates.x != 0) {
			// Cell is not in the left column
			neighbors.add(cells[cell.coordinates.y * width
					+ (cell.coordinates.x - 1)]);
		}
		if (cell.coordinates.x != width - 1) {
			// Cell is not in the right column
			neighbors.add(cells[cell.coordinates.y * width
					+ (cell.coordinates.x + 1)]);
		}
		if (cell.coordinates.y != 0) {
			// Cell is not in the top row
			neighbors.add(cells[(cell.coordinates.y - 1) * width
					+ cell.coordinates.x]);
		}
		if (cell.coordinates.y != height - 1) {
			// Cell is not in the bottom row
			neighbors.add(cells[(cell.coordinates.y + 1) * width
					+ cell.coordinates.x]);
		}
		return neighbors;
	}

	private Cell getRandomUnvistedNeighbor(Cell cell) {
		List<Cell> neighbors = getNeighborCells(cell);
		Collections.shuffle(neighbors);

		for (Cell c : neighbors) {
			if (c.isUnvisited())
				return c;
		}
		return null;
	}

	/**
	 * Return the first Cell of the given type.
	 * 
	 * @param type
	 *            The type of the Cell to be returned.
	 * @return Returns the first Cell found of the given type, if no Cell is
	 *         found returns null.
	 */
	public Cell getCell(int type) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (cells[j * width + i].type == type)
					return cells[j * width + i];
			}

		}
		return null;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the cells
	 */
	public Cell[] getCells() {
		return cells;
	}

	/**
	 * @return the walls
	 */
	public List<Wall> getWalls() {
		return walls;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(width);
		out.writeInt(height);
		out.writeTypedArray(cells, flags);
		out.writeList(walls);

	}

	private void readFromParcel(Parcel in) {
		width = in.readInt();
		height = in.readInt();
		in.readTypedArray(cells, Cell.CREATOR);
		in.readList(walls, Wall.class.getClassLoader());
	}

	/**
	 * Constructor to use when re-constructing object from a parcel
	 * 
	 * @param in
	 *            a parcel from which to read this object
	 */
	public Maze(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Maze> CREATOR = new Parcelable.Creator<Maze>() {
		public Maze createFromParcel(Parcel in) {
			return new Maze(in);
		}

		public Maze[] newArray(int size) {
			return new Maze[size];
		}
	};
}
