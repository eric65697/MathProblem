package com.ezhao;

import java.util.function.ToDoubleBiFunction;
import java.lang.Math;

public class LogMatrix {

  private final static char[] SUPERSCRIPT_NUMBERS = {'⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸',
      '⁹'};

  private final static class Cell {

    final int x;
    final int y;
    final double value;
    final ToDoubleBiFunction<Integer, Integer> function;

    public Cell(int x, int y, ToDoubleBiFunction<Integer, Integer> function) {
      this.x = x;
      this.y = y;
      this.function = function;
      this.value = function.applyAsDouble(x, y);
    }

    @Override
    public String toString() {
      if (x == 1 && y == 1) {
        return String.format("(CENTER) -> %.4f", 1.0);
      }
      if (function == logXpY) {
        return String.format("log(%d%c)  -> %.4f", x, SUPERSCRIPT_NUMBERS[y], value);
      }
      if (function == logYpX) {
        return String.format("log(%d%c)  -> %.4f", y, SUPERSCRIPT_NUMBERS[x], value);
      }
      if (function == logXmY) {
        return String.format("log(%d*%d) -> %.4f", x, y, value);
      }
      if (function == logYdX) {
        return String.format("log(%d/%d) -> %.4f", y, x, value);
      }
      return String.format("{x:%d, y:%d} -> %.4f", x, y, value);
    }
  }

  private final static ToDoubleBiFunction<Integer, Integer> logXpY = (Integer x, Integer y) -> Math.log10(
      Math.pow(x, y));
  private final static ToDoubleBiFunction<Integer, Integer> logYpX = (Integer x, Integer y) -> Math.log10(
      Math.pow(y, x));
  private final static ToDoubleBiFunction<Integer, Integer> logXmY = (Integer x, Integer y) -> Math.log10(
      x * y);
  private final static ToDoubleBiFunction<Integer, Integer> logYdX = (Integer x, Integer y) -> Math.log10(
      (1.0 * y) / x);
  private final static ToDoubleBiFunction<Integer, Integer> alwaysOne = (Integer x, Integer y) -> 1.0;
  private final static ToDoubleBiFunction<Integer, Integer>[][] transforms = new ToDoubleBiFunction[][]{
      new ToDoubleBiFunction[]{logXpY, logXmY, logYdX},
      new ToDoubleBiFunction[]{logXmY, alwaysOne, logXmY},
      new ToDoubleBiFunction[]{logYdX, logXmY, logYpX}};

  private int solution = 0;

  private boolean checkCell(Cell[][] matrix, int i, int k) {
    if (i > 0 && matrix[i - 1][k].value >= matrix[i][k].value) {
      return false;
    }
    if (k > 0 && matrix[i][k - 1].value >= matrix[i][k].value) {
      return false;
    }
    if (i <= 1 && k <= 1 && matrix[i][k].value >= 1.0) {
      return false;
    }
    if (i > 1 && k > 1 && matrix[i][k].value <= 1.0) {
      return false;
    }

    return true;
  }

  private void printMatrix(Cell[][] matrix) {
    System.out.println();
    for (Cell[] cells : matrix) {
      for (int k = 0; k < matrix[0].length; k++) {
        System.out.print(cells[k]);
        if (k == matrix[0].length - 1) {
          System.out.println();
        } else {
          System.out.print("\t");
        }
      }
    }
  }

  private void find(boolean[] xArray, boolean[] yArray, Cell[][] matrix, int i, int k) {
    if (i == matrix.length) {
      return;
    }

    if (i == 1 && k == 1) {
      find(xArray, yArray, matrix, i, k + 1);
      return;
    }

    for (int x = 0; x < xArray.length; x++) {
      if (xArray[x]) {
        continue; // Already used
      }
      if (x == 0 && (transforms[i][k] == logYdX || transforms[i][k] == logXpY)) {
        continue;
      }

      xArray[x] = true;
      for (int y = 0; y < yArray.length; y++) {
        if (yArray[y]) {
          continue;
        }
        matrix[i][k] = new Cell(x, y, transforms[i][k]);
        if (checkCell(matrix, i, k)) {
          if (i == matrix.length - 1 && k == matrix[0].length - 1) {
            if (solution % 10000 == 0) {
              printMatrix(matrix);
            }
            solution++;
            continue;
          }

          yArray[y] = true;
          if (k != matrix[0].length - 1) {
            find(xArray, yArray, matrix, i, k + 1);
          } else {
            find(xArray, yArray, matrix, i + 1, 0);
          }
          yArray[y] = false;
        }
      }
      xArray[x] = false;
    }
  }

  public static void main(String[] args) {
    final boolean[] xArray = new boolean[10];
    final boolean[] yArray = new boolean[10];
    Cell[][] matrix = new Cell[3][3];
    matrix[1][1] = new Cell(1, 1, alwaysOne);

    final LogMatrix logMatrix = new LogMatrix();
    logMatrix.find(xArray, yArray, matrix, 0, 0);
    System.out.println("Total solution: " + logMatrix.solution);
  }
}
