
import java.util.Random;
import java.util.Scanner;

public class CandyCrush {
    private static final char[] CANDIES = {'A', 'B', 'C', 'D', 'E'};
    private static final char BOMB = 'X';
    private char[][] grid;
    private static final int SIZE = 8;
    private Random random = new Random();
    private int score = 0;

    public CandyCrush() {
        grid = new char[SIZE][SIZE];
        initializeGrid_();
    }

    private void initializeGrid_() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = CANDIES[random.nextInt(CANDIES.length)];
            }
        }
        grid[random.nextInt(SIZE)][random.nextInt(SIZE)] = BOMB;
    }

    public void printGrid_() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Score: " + score);
    }

    public void playGame_() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printGrid_();
                int row1 = getValidCoordinate_(scanner, "first candy row");
                int col1 = getValidCoordinate_(scanner, "first candy column");
                int row2 = getValidCoordinate_(scanner, "second candy row");
                int col2 = getValidCoordinate_(scanner, "second candy column");

                if (Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1) {
                    swapCandies_(row1, col1, row2, col2);
                    if (!processMatches_()) {
                        System.out.println("No match was made. Try again.");
                        swapCandies_(row1, col1, row2, col2); 
                    }
                } else {
                    System.out.println("Candies are not adjacent. Please choose adjacent candies.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please restart the game.");
        }
    }

    private int getValidCoordinate_(Scanner scanner, String coordinateType) {
        while (true) {
            System.out.print("Enter " + coordinateType + " (0 to 7): ");
            if (scanner.hasNextInt()) {
                int coordinate = scanner.nextInt();
                if (coordinate >= 0 && coordinate <= 7) {
                    return coordinate;
                } else {
                    System.out.println("Invalid " + coordinateType + ". It should be between 0 and 7.");
                }
            } else {
                System.out.println("Invalid input, please enter a number.");
                scanner.next(); 
            }
        }
    }

    private void swapCandies_(int row1, int col1, int row2, int col2) {
        char temp = grid[row1][col1];
        grid[row1][col1] = grid[row2][col2];
        grid[row2][col2] = temp;
    }

    private boolean processMatches_() {
        boolean matchFound = false;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (checkForMatch_(i, j)) {
                    matchFound = true;
                }
            }
        }
        if (matchFound) {
            fillEmptySpaces_();
        }
        return matchFound;
    }

    private boolean checkForMatch_(int i, int j) {
        char candy = grid[i][j];
        int horizMatch = countMatch_(i, j, 0, 1);
        int vertMatch = countMatch_(i, j, 1, 0);
        if (candy != ' ' && (horizMatch >= 3 || vertMatch >= 3)) {
            int matchedCandies = Math.max(horizMatch, vertMatch);
            score += matchedCandies * 10;
            clearAndCascade_(i, j, candy == BOMB);
            return true;
        }
        return false;
    }

    private int countMatch_(int row, int col, int dRow, int dCol) {
        int count = 1; // Start with the current candy
        char candy = grid[row][col];
        // Count forward
        int nextRow = row + dRow;
        int nextCol = col + dCol;
        while (nextRow >= 0 && nextRow < SIZE && nextCol >= 0 && nextCol < SIZE && grid[nextRow][nextCol] == candy) {
            count++;
            nextRow += dRow;
            nextCol += dCol;
        }
        // Count backward
        nextRow = row - dRow;
        nextCol = col - dCol;
        while (nextRow >= 0 && nextRow < SIZE && nextCol >= 0 && nextCol < SIZE && grid[nextRow][nextCol] == candy) {
            count++;
            nextRow -= dRow;
            nextCol -= dCol;
        }
        return count;
    }

    private void clearAndCascade_(int row, int col, boolean isBomb) {
        if (isBomb) {
            for (int i = 0; i < SIZE; i++) {
                grid[i][col] = ' '; // Clear entire column if bomb
                grid[row][i] = ' '; // Clear entire row if bomb
            }
        } else {
            clearVertical_(row, col);
            clearHorizontal_(row, col);
        }
        fillEmptySpaces_();
    }

    private void clearVertical_(int row, int col) {
        for (int i = row; i < SIZE && grid[i][col] == grid[row][col]; i++) {
            grid[i][col] = ' ';
        }
        for (int i = row - 1; i >= 0 && grid[i][col] == grid[row][col]; i--) {
            grid[i][col] = ' ';
        }
    }

    private void clearHorizontal_(int row, int col) {
        for (int j = col; j < SIZE && grid[row][j] == grid[row][col]; j++) {
            grid[row][j] = ' ';
        }
        for (int j = col - 1; j >= 0 && grid[row][j] == grid[row][col]; j--) {
            grid[row][j] = ' ';
        }
    }

    private void fillEmptySpaces_() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = SIZE - 1; row > 0; row--) {
                if (grid[row][col] == ' ') {
                    for (int k = row - 1; k >= 0; k--) {
                        if (grid[k][col] != ' ') {
                            grid[row][col] = grid[k][col];
                            grid[k][col] = ' ';
                            break;
                        }
                    }
                }
            }
            // Fill any remaining empty cells at the top of the column
            for (int row = 0; row < SIZE && grid[row][col] == ' '; row++) {
                grid[row][col] = CANDIES[random.nextInt(CANDIES.length)];
            }
        }
    }

    public static void main(String[] args) {
        CandyCrush game = new CandyCrush();
        game.playGame_();
    }
}
