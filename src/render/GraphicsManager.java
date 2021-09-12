package render;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic.Vector;
import logic.World;

public class GraphicsManager {

	private JFrame mainFrame;
	private int width, height;
	private World world;
	private JPanel fixedPanel, buttonPanel;
	private JButton startButton, resetButton;

	public GraphicsManager(int width, int height, World world) {
		this.width = width;
		this.height = height;
		this.world = world;
		initSwing();
	}

	private void initSwing() {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		mainFrame.setTitle("Maze Pathfinder");
		mainFrame.setSize(new Dimension(width, height));
		fixedPanel = new JPanel(new BorderLayout());
		fixedPanel.setPreferredSize(mainFrame.getSize());
		world.setPreferredSize(new Dimension(600, 600));
		fixedPanel.add(world, BorderLayout.CENTER);
		setupButtons();
		mainFrame.getContentPane().add(fixedPanel);
		mainFrame.setVisible(true);
	}

	private void setupButtons() {
		buttonPanel = new JPanel();
		startButton = new JButton("Start");
		startButton.setPreferredSize(new Dimension(90, 30));
		resetButton = new JButton("Reset");
		resetButton.setPreferredSize(new Dimension(90, 30));
		buttonPanel.setPreferredSize(new Dimension(100, 300));
		buttonPanel.setBackground(Color.LIGHT_GRAY);
		buttonPanel.add(startButton);
		buttonPanel.add(resetButton);
		fixedPanel.add(buttonPanel, BorderLayout.EAST);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runMaze = new Thread(new Runnable() {
					@Override
					public void run() {	
						LinkedList<Vector> path = world.BFSMatrix(true);
						world.getPath(path);
					}
				});
				runMaze.start();
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runReset = new Thread(new Runnable() {
					@Override
					public void run() {
						if(!world.getOperating()) {
							world.reset();
						}
						
					}
				});
				runReset.start();
			}
		});

	}

}
