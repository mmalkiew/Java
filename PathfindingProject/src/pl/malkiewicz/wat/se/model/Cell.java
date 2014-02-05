package pl.malkiewicz.wat.se.model;

import java.util.ArrayList;

public class Cell {
    int row;   // the row number of the cell(row 0 is the top)
    int col;   // the column number of the cell (Column 0 is the left)
    int g;     // the value of the function g of A* and Greedy algorithms
    int h;     // the value of the function h of A* and Greedy algorithms
    int f;     // the value of the function h of A* and Greedy algorithms
    int dist;  // the distance of the cell from the initial position of the robot
               // Ie the label that updates the Dijkstra's algorithm
    Cell prev; // Each state corresponds to a cell
               // and each state has a predecessor which
               // is stored in this variable
    
    public Cell(int row, int col){
       this.row = row;
       this.col = col;
    }
}
