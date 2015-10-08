package uk.ac.bbsrc.tgac.miso.core.util;

/**
 * Utility class to provide helpful functions for Box-related methods in MISO
 *
 * Note: We define "position" to mean a position in a Box. A position is a combination of a single Alphabet character
 *       followed by a 2-digit integer. For example the very first position (in the top-left-most part of the box) is
 *       A01.
 * @author Dillan Cooke and Kyle Verhoog
 */
public class BoxUtils {
  /**
   * Return the character representation of an integer. This corresponds to the labeling of row on boxes
   *
   * 1 -> A, 2 -> B, ... , 26 -> Z
   *
   * @param integer representation of the row
   * @return character representation of the row
   * @throws IllegalArgumentException if an invalid integer representation is given. ie. greater than 26, less than 1.
   */
  public static char getCharForNumber(int num) {
    if (num < 1 || num > 26) throw new IllegalArgumentException("Row number must be between 1 and 26");
    return (char) ( num + 'A' - 1);
  }

  // See getCharForNumber(int num) documentation
  public static char getRowChar(int row) throws IllegalArgumentException {
    return getCharForNumber(row);
  }

  /**
   * Return the integer representation of a character which represents a row of a Box.
   *
   * A -> 1, B -> 2, ... , Z -> 26
   *
   * @param character representation of the row
   * @return integer representation of the row
   * @throws IllegalArgumentException if the given character is not in the Alphabet
   */
  public static int getNumberForChar(char letter) {
    if (letter >= 'a' && letter <= 'z') letter = Character.toUpperCase(letter);
    if (letter < 'A' || letter > 'Z') throw new IllegalArgumentException("Row letter must be between A and Z");
    return letter - 'A' + 1;
  }

  // See getNumberForchar(char letter) documentation
  public static int getRowNum(char row) throws IllegalArgumentException {
    return getNumberForChar(row);
  }

  // If parse fails, return -1, else return parsed integer
  public static int tryParseInt(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  /**
   * Return a String of the position, given the position as two integers representing the row and column.
   * Examples:
   *   assertTrue(getPositionString(1, 1).equals("A01"))
   *   assertTrue(getPositionString(26, 1).equals("Z01"))
   *   assertTrue(getPositionString(1, 12).equals("A12"))
   *
   * @param integer representations of the row and column where 1 <= row <= 26 and 1 <= column <= 99
   * @return String representation of the row and column
   * @throws IllegalArgumentException if the row and column do not meet the following conditions:
              1 <= row <= 26, 1 <= column <= 99
   */
  public static String getPositionString(int row, int column) {
    if (row < 1 || row > 26) throw new IllegalArgumentException("row must be between 1 and 26");
    if (column < 1 || column > 99) throw new IllegalArgumentException("column must be between 1 and 99");
    char letter = getCharForNumber(row);
    String position = String.format("%02d", column); // pad col with zeros
    position = letter + position;
    return position;
  }

  /**
   * Return a String of the position, given the position a character and an integer representation.
   * Examples:
   *   assertTrue(getPositionString('A', 1).equals("A01"))
   *   assertTrue(getPositionString('Z', 1).equals("Z01"))
   *   assertTrue(getPositionString('H', 12).equals("H12"))
   *
   * @param character representation of the row and integer representation of the column where:
                'A' <= row <= 'Z' and 1 <= column <= 99
   * @return String representation of the row and column
   * @throws IllegalArgumentException if the row and column do not meet the following conditions:
              'A' <= row <= 'Z', 1 <= column <= 99
   */
  public static String getPositionString(char row, int column) {
    if (row < 'A' || row > 'Z') throw new IllegalArgumentException("Row letter must be between A and Z");
    return getPositionString(getNumberForChar(row), column);
  }
}
