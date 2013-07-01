package gameoflife;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
 /**
  * A program that creates a graphical interface for Conway's Game of Life. It includes a
  * 50 x 50 grid, run, stop, and step buttons, and shows the generation in progress.
  * Live cells are represented by 1, and dead cells are empty.
  * @author Daniel Kapit
  * Period 6
  * 3/5/11
  */
public class World extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton run;
	private JButton stop;
	private JButton step;
	private JSlider speed;
	private int gen = 0;
	private JLabel generations = new JLabel("Generations: " + gen);
	private JButton[][] cells;
	private JButton[][] live;
	private JButton[][] die;
	private int r = 50;
	private int c = 50;
	Timer t = new Timer(100, new Run());
	
	/**
	 * Constructs the graphical representation of the game.
	 */
	protected World() {
		JMenuBar bar = new JMenuBar();
		bar.add(createFileMenu());
		bar.add(createGameMenu());
		setJMenuBar(bar);
		run = new JButton("Run");
		run.addActionListener(new Start());
		
		stop = new JButton("Stop");
		stop.addActionListener(new Stop());
		
		step = new JButton("Step");
		step.addActionListener(new Step());
		
		cells = new JButton[r][c];
		live = new JButton[r][c];
		die = new JButton[r][c];
		JPanel p = new JPanel(new GridLayout(r, c));
		JPanel control = new JPanel(new GridLayout(1, 4));
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				cells[i][j] = new JButton();
				cells[i][j].addMouseListener(new Click());
				p.add(cells[i][j]);
			}
		}
		control.add(run);
		control.add(stop);
		control.add(step);
		control.add(generations);
		add(p, BorderLayout.CENTER);
		add(control, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 700);
	}
	
	//---------------------- Creates Menus/Menu Items -------------------//
	/**
	 * Creates the file menu for the frame.
	 * @return the file menu
	 */
	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createExitItem());
		return menu;
	}
	
	/**
	 * Creates the "game" menu for the frame.
	 * @return the game menu
	 */
	private JMenu createGameMenu() {
		JMenu menu = new JMenu("Game");
		menu.add(createResetItem());
		return menu;
	}
	
	/**
	 * Creates the item that quits the game
	 * @return the exit item
	 */
	private JMenuItem createExitItem() {
		JMenuItem item = new JMenuItem("Quit");
		class Quit implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		}
		item.addActionListener(new Quit());
		return item;
	}
	
	/**
	 * Creates an item that resets all of the cells in the game
	 * @return the reset item
	 */
	private JMenuItem createResetItem() {
		JMenuItem item = new JMenuItem("Reset");
		class Reset implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				for (int i = 0; i < r; i++) {
					for (int j = 0; j < c; j++) {
						cells[i][j].setText("");
					}
				}
				gen = 0;
				generations.setText("Generations: " + gen);
			}
		}
		item.addActionListener(new Reset());
		return item;
	}
	
	//----------------------- Makes Listeners ----------------------------//
	
	/**
	 * Starts timer, which controls the amount of time between ticks.
	 */
	class Start implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			t.start();
		}
	}
	
	/**
	 * Runs the game.
	 */
	class Run implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			run.setEnabled(false);
			stop.setEnabled(true);
			live();
		}
	}
	
	/**
	 * Stops the game
	 */
	class Stop implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			run.setEnabled(true);
			stop.setEnabled(false);
			t.stop();
		}
	}
	
	/**
	 * Makes one tick occur
	 */
	class Step implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			live();
		}
	}
	
	/**
	 * Controls the speed between ticks (not yet implemented)
	 */
	class Speed implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			
		}
	}
	
	/**
	 * Causes the values of the cells to change when clicked
	 */
	class Click extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			JButton button = (JButton) e.getComponent();
			if (button.getText().equals("")) {
				button.setText("1");
			}
			
			else if (button.getText().equals("1")) {
				button.setText("");
			}
		}
	}
	
	//------------------------ The Functions for Life ----------------------//
	
	/**
	 * The Game of Life algorithm.
	 * Iterates through the grid, adding cells to different arrays according to the rules
	 * of the game. Then, it iterates through the grid again, changing values of the 
	 * cells according to the cells that it added to the arrays.
	 */
	public void live() {
		live = new JButton[r][c];
		die = new JButton[r][c];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {					
				if (this.cells[i][j].getText().equals("1")) {
					if (getLiveNeighbors(this.cells, i, j) == 2 ||
							getLiveNeighbors(this.cells, i, j) == 3) {
						live[i][j] = this.cells[i][j];
					}
					
					else {
						die[i][j] = this.cells[i][j];
					}
				}
				else {
					if (getLiveNeighbors(this.cells, i, j) == 3) {
						live[i][j] = this.cells[i][j];
					}
				}
			}
		}
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {					
				if (die[i][j] instanceof JButton)
					this.cells[i][j].setText("");
			}
		}
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {					
				if (live[i][j] instanceof JButton)
					this.cells[i][j].setText("1");
			}
		}
		
		gen += 1;
		generations.setText("Generations: " + gen);
		
	}
	
	/**
	 * Gets the live neighbors of a cell at (row, col)
	 * @param buttons the array of buttons in the grid
	 * @param row the row of the specified button
	 * @param col the column of the specified button
	 * @return the number of live neighbors that the cell has
	 */
	public int getLiveNeighbors(JButton[][] buttons, int row, int col) {
		int count = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			if ( i < 0 || i >= r)
				continue;
			else {
				for (int j = col - 1; j <= col + 1; j++) {
					if (i == row && j == col)
						continue;
					else if (j < 0 || j >= c)
						continue;
					else {
						if (buttons[i][j].getText().equals("1")) {
							count++;
						}
					}
				}
			}	
		}	
		return count;
	}
	
}
