package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class DisplayInfoFilter implements PixelFilter, Interactive {
    private int cropWidth;
    private int cropHeight;

    public DisplayInfoFilter() {
        System.out.println("Filter running...");
        cropHeight = 670;
        cropWidth = 520; //247
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();


        int startRow = 55;
        int startCol = 55;


        grid = cropImage(grid, startRow, startCol, cropHeight, cropWidth);


        System.out.println("Image is " + grid.length + " by " + grid[0].length);
        int blackCount = 0;
        int whiteCount = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] < 10) blackCount++;
                if (grid[r][c] > 240) whiteCount++;
            }
        }
        System.out.println(blackCount + " nearly black pixels and " + whiteCount + " nearly white pixels");
        getBubbledAnswer(grid, 20, 200,12);


        System.out.println(getAveragePixelVal(grid, 65, 133, 5));

        img.setPixels(grid);

        List<Point> blackPoints = getBlackPointsAroundWhite(grid, 240);
        List<Cluster> clusters = performClustering(blackPoints, 200); // assuming 200 as the threshold for dark points
        checkSurroundedByWhiterPixels(grid);
        int clusterCount = countClusters(clusters);
        System.out.println("Amount of Answers: " + clusterCount);

        return img;
    }


    /*public void getBubbledAnswer (short[][] grid, int startR, int startC, int deltaR, int deltaC, int bubbleSize, int answerThreshold, int numQuestions){
           for (int i = 0; i < numQuestions; i++) {
             getAveragePixelVal(grid, startR, startC, bubbleSize);

               startR += deltaR;
               startC += deltaC;
            }

           int bubbledAnswer;
            for (int r = 0; r < grid.length; r += bubbleSize) {
                bubbledAnswer = 0;
                for (int c = 0; c < grid[0].length; c += bubbleSize) {
                    if (grid[r][c] < answerThreshold) {
                      System.out.print(bubbledAnswer + ", ");
                  }

             }
             bubbledAnswer++;
          }
        }*/
    public void getBubbledAnswer(short[][] grid, int bubbleSize, int answerThreshold, int numQuestions) {
        int[][] answerRanges = {{50, 70}, {74, 94}, {100, 120}, {124, 144}, {148, 168}};
        char[] answerLetters = {'A', 'B', 'C', 'D', 'E'};
        int rowSize = 20;
        int rowSkip = 30;
        int questionsProcessed = 0;

        for (int i = 0; i < numQuestions && questionsProcessed < 12; i++) {
            int startRow = i * (rowSize + rowSkip);
            if (startRow + rowSize <= grid.length) { // Replaced continue with if statement
                for (int j = 0; j < answerRanges.length; j++) {
                    if (answerRanges[j][1] <= grid[0].length) { // Replaced continue with if statement
                        int total = 0;
                        int count = 0;
                        for (int r = startRow; r < startRow + rowSize; r++) {
                            for (int c = answerRanges[j][0]; c < answerRanges[j][1]; c++) {
                                total += grid[r][c];
                                count++;
                            }
                        }
                        int average = total / count;
                        if (average >= 130 && average <= 145) {
                            System.out.println("Question " + (questionsProcessed + 1) + ": Answer " + answerLetters[j] + " has an average RGB value within the range 130 to 145.");
                        }
                        if (average < answerThreshold) {
                            System.out.println("Question " + (questionsProcessed + 1) + ": Answer " + answerLetters[j]);
                            questionsProcessed++;
                        }
                    }
                }
            }
        }
    }


    public double getAveragePixelVal(short[][] grid, int row, int col, int bubbleSize) {
        double total = 0;
        int count = 0;

        for (int r = Math.max(0, row - bubbleSize); r < Math.min(grid.length, row + bubbleSize); r++) {
            for (int c = Math.max(0, col - bubbleSize); c < Math.min(grid[0].length, col + bubbleSize); c++) {
                total += grid[r][c];
                count++;
            }
        }

        return total / count;
    }

    public void checkSurroundedByWhiterPixels(short[][] grid) {
        int startRow = 49;
        int startCol = 49;
        int endRow = Math.min(169, grid.length);
        int endCol = Math.min(607, grid[0].length);

        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                // Check if the current pixel is surrounded by whiter pixels
                boolean isSurroundedByWhiterPixels = true;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr;
                        int nc = c + dc;
                        // Check if the neighboring pixel is within the image bounds and is whiter than the current pixel
                        if (nr >= startRow && nr < endRow && nc >= startCol && nc < endCol && grid[nr][nc] <= grid[r][c]) {
                            isSurroundedByWhiterPixels = false;
                            break;
                        }
                    }
                    if (!isSurroundedByWhiterPixels) {
                        break;
                    }
                }
                // If the current pixel is surrounded by whiter pixels, do something with it
                if (isSurroundedByWhiterPixels) {
                    // Do something with the pixel at (r, c)
                }
            }
        }
    }
    /*public int getbubbledAnswer(short[][] grid){
        int getbubbledAnswer = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] < 10) getbubbledAnswer++;
            }
        }
        return getbubbledAnswer;
    }*/

        public List<Point> getBlackPointsAroundWhite ( short[][] grid, int threshold){
            List<Point> blackPoints = new ArrayList<>();
            for (int r = 0; r < grid.length; r++) {
                for (int c = grid[0].length / 2; c < grid[0].length; c++) { // start from the middle of the image
                    if (grid[r][c] > threshold) { // this is a white point
                        // check the surrounding points
                        for (int dr = -1; dr <= 1; dr++) {
                            for (int dc = -1; dc <= 1; dc++) {
                                int nr = r + dr;
                                int nc = c + dc;
                                if (nr >= 0 && nr < grid.length && nc >= grid[0].length / 2 && nc < grid[0].length && grid[nr][nc] < threshold) {
                                    // this is a black point around a white point
                                    blackPoints.add(new Point(nr, nc, grid[nr][nc]));
                                }
                            }
                        }
                    }
                }
            }
            return blackPoints;
        }

        public List<Cluster> performClustering (List < Point > blackPoints,int threshold){
            List<Cluster> clusters = new ArrayList<>();
            int clusterSize = 42;

            for (int i = 0; i < blackPoints.size(); i += clusterSize * clusterSize) {
                Cluster cluster = new Cluster(blackPoints.get(i));
                for (int j = 0; j < clusterSize * clusterSize && i + j < blackPoints.size(); j++) {
                    Point point = blackPoints.get(i + j);
                    if (point.getColor() < threshold) { // this is a dark point
                        cluster.addPoint(point);
                    }
                }
                clusters.add(cluster);
            }
            return clusters;
        }

        public int countClusters (List < Cluster > clusters) {
            return clusters.size();
        }


        private short[][] goThroughEachRow ( short[][] grid, int startRow, int startCol, int endRow, int endCol){
            startRow = 49;
            startCol = 49;
            endRow = 49;
            endCol = 169;
            short[][] cropped = new short[endRow - startRow][endCol - startCol];
            for (int r = startRow; r < endRow; r++) {
                for (int c = startCol; c < endCol; c++) {
                    cropped[r - startRow][c - startCol] = grid[r][c];
                }
            }
            return cropped;
        }

        public short[][] cropImage ( short[][] original, int row, int col, int rowEnd, int colEnd){
            short[][] cropped = new short[rowEnd - row][colEnd - col];

            for (int r = row; r < rowEnd; r++) {
                for (int c = col; c < colEnd; c++) {
                    cropped[r - row][c - col] = original[r][c];
                }
            }

            return cropped;
        }


        public void mouseClicked ( int mouseX, int mouseY, DImage img){
            short[][] gridNew = img.getBWPixelGrid();

            double avg = getAveragePixelVal(gridNew, mouseY, mouseX, 5);
            System.out.println("Average Val: " + avg);


        }

        @Override
        public void keyPressed ( char key){
            if (key == 'a') {
                cropWidth += 50;
                cropHeight += 50;
            }

            if (key == '-') {
                cropWidth -= 10;
                cropHeight -= 10;

                if (cropWidth < 0) {
                    cropWidth = 0;
                }

                if (cropHeight < 0) {
                    cropHeight = 0;
                }
            }

        }

}