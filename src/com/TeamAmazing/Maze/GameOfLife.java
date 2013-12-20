package com.TeamAmazing.Maze;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameOfLife {

	// The number of alive neighbors a cell must have to live between
	// generations.
	private static Set<Byte> ruleToLive = new HashSet<Byte>();
	// The number of alive neighbors a cell must have to be born.
	private static Set<Byte> ruleToBeBorn = new HashSet<Byte>();
	private final int NUM_OF_BOARDS = 2;
	private byte[][][] boards;
	private int currentBoard = 0;
	Random rand = new Random();
	public static final byte ALIVE_MASK = 16;
	private static final byte NEIGHBORS_MASK = 15;

	public GameOfLife() {
		// The rules
		ruleToLive.add((byte) 1);
		ruleToLive.add((byte) 2);
		ruleToLive.add((byte) 3);
		ruleToLive.add((byte) 4);
		// ruleToLive.add((byte)5);
		ruleToBeBorn.add((byte) 3);
	}

	public byte[][] getCurrentBoard() {
		return boards[currentBoard];
	}

	public void initializeCells(int width, int height) {
		boards = new byte[NUM_OF_BOARDS][width][height];

		// TODO fail gracefully when GridWidth or GridHeight is less than
		// 10.
		// Create 20 to 30 random starting cells.
		// TODO set maxGenerations to be a multiple of GridHeight/GridWeight
//		int numOfStartingCells = rand.nextInt(10) + 20;
		int numOfStartingCells = 2;
		int randHorzOffset, randVertOffset;
		while (numOfStartingCells > 0) {
			randHorzOffset = rand.nextInt(10) + (width / 2 - 5);
			randVertOffset = rand.nextInt(10) + (height / 2 - 5);
			if ((boards[currentBoard][randHorzOffset][randVertOffset] & ALIVE_MASK) == 0) {
				// cell is dead, make it alive
				makeAlive(randHorzOffset, randVertOffset, currentBoard);
			}
			numOfStartingCells--;
		}
	}

	// a cell should never have more than 8 neighbors so bit 4 should never
	// carry into bit 5, which is the cell state bit.
	private void makeAlive(int x, int y, int boardIndex) {
		boards[currentBoard][x][y] |= ALIVE_MASK;
		// update the neighbors
		boards[boardIndex][(x + 1) % boards[boardIndex].length][y] += 1;
		boards[boardIndex][(x + 1) % boards[boardIndex].length][(y + 1)
				% boards[boardIndex][0].length] += 1;
		boards[boardIndex][(x + 1) % boards[boardIndex].length][(y - 1 + boards[boardIndex][0].length)
				% boards[boardIndex][0].length] += 1;
		boards[boardIndex][x][(y + 1) % boards[boardIndex][0].length] += 1;
		boards[boardIndex][x][(y - 1 + boards[boardIndex][0].length)
				% boards[boardIndex][0].length] += 1;
		boards[boardIndex][(x - 1 + boards[boardIndex].length)
				% boards[boardIndex].length][y] += 1;
		boards[boardIndex][(x - 1 + boards[boardIndex].length)
				% boards[boardIndex].length][(y + 1)
				% boards[boardIndex][0].length] += 1;
		boards[boardIndex][(x - 1 + boards[boardIndex].length)
				% boards[boardIndex].length][(y - 1 + boards[boardIndex][0].length)
				% boards[boardIndex][0].length] += 1;
	}

	private void kill(int x, int y, int boardIndex) {
		boards[currentBoard][x][y] &= ~ALIVE_MASK;
		// update the neighbors
		if ((boards[boardIndex][(x + 1) % boards[boardIndex].length][y] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][(x + 1) % boards[boardIndex].length][y] -= 1;
		}
		if ((boards[boardIndex][(x + 1) % boards[boardIndex].length][(y + 1)
				% boards[boardIndex][0].length] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][(x + 1) % boards[boardIndex].length][(y + 1)
					% boards[boardIndex][0].length] -= 1;
		}
		if ((boards[boardIndex][(x + 1) % boards[boardIndex].length][(y - 1 + boards[boardIndex][0].length)
				% boards[boardIndex][0].length] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][(x + 1) % boards[boardIndex].length][(y - 1 + boards[boardIndex][0].length)
					% boards[boardIndex][0].length] -= 1;
		}
		if ((boards[boardIndex][x][(y + 1) % boards[boardIndex][0].length] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][x][(y + 1) % boards[boardIndex][0].length] -= 1;
		}
		if ((boards[boardIndex][x][(y - 1 + boards[boardIndex][0].length)
				% boards[boardIndex][0].length] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][x][(y - 1 + boards[boardIndex][0].length)
					% boards[boardIndex][0].length] -= 1;
		}
		if ((boards[boardIndex][(x - 1 + boards[boardIndex].length)
				% boards[boardIndex].length][y] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][(x - 1 + boards[boardIndex].length)
					% boards[boardIndex].length][y] -= 1;
		}
		if ((boards[boardIndex][(x - 1 + boards[boardIndex].length)
				% boards[boardIndex].length][(y + 1)
				% boards[boardIndex][0].length] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][(x - 1 + boards[boardIndex].length)
					% boards[boardIndex].length][(y + 1)
					% boards[boardIndex][0].length] -= 1;
		}
		if ((boards[boardIndex][(x - 1 + boards[boardIndex].length)
				% boards[boardIndex].length][(y - 1 + boards[boardIndex][0].length)
				% boards[boardIndex][0].length] & NEIGHBORS_MASK) > 0) {
			boards[boardIndex][(x - 1 + boards[boardIndex].length)
					% boards[boardIndex].length][(y - 1 + boards[boardIndex][0].length)
					% boards[boardIndex][0].length] -= 1;
		}
	}

	public void nextGeneration() {
		int nextBoard = (currentBoard + 1) % NUM_OF_BOARDS;
		// copy over the current board into the next one.
		for (int x = 0; x < boards[currentBoard].length; x++) {
			for (int y = 0; y < boards[currentBoard][x].length; y++) {
				boards[nextBoard][x][y] = boards[currentBoard][x][y];
			}
		}
		for (int x = 0; x < boards[currentBoard].length; x++) {
			for (int y = 0; y < boards[currentBoard][x].length; y++) {
				if ((boards[currentBoard][x][y] & ALIVE_MASK) != 0) {
					// cell is alive
					// check if it should die.
					if (!ruleToLive.contains(boards[currentBoard][x][y]
							& NEIGHBORS_MASK)) {
						// kill the cell in the next generation
						kill(x, y, nextBoard);
					}
				} else {
					// cell is dead
					// check if it should be born
					if (ruleToBeBorn.contains(boards[currentBoard][x][y]
							& NEIGHBORS_MASK)) {
						makeAlive(x, y, nextBoard);
					}

				}
			}
		}
		// Some random stuff
		if ((boards[nextBoard][1][1] & ALIVE_MASK) == 0){
			// cell is dead, make it alive.
			makeAlive(1,1,nextBoard);
			// then kill it
			kill(1,1,nextBoard);
		}
		
		
		currentBoard = nextBoard;
	}

	// TODO determine if this is something I want to implement
	// Restart if the nextGeneration is the same as any of the past
	// NUM_OF_CONFIGS previous ones.
	// for (int i = 1; i < NUM_OF_CONFIGS; i++) {
	// if (Arrays.deepEquals(board,
	// boards[(currentBoard + 1) % NUM_OF_CONFIGS])) {
	// restarting = true;
	// }
	// }
	// currentBoard = nextBoard;
}