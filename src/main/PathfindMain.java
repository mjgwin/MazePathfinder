package main;

import java.util.LinkedList;
import logic.Vector;

import logic.World;
import render.GraphicsManager;

public class PathfindMain {

	public static void main(String[] args) {
		World world = new World(60, 60, 10);
		GraphicsManager manager = new GraphicsManager(800, 640, world);
	}

}
