package uk.ac.bbsrc.tgac.miso.core.test.util;

import static org.junit.Assert.*;
import org.junit.*;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import java.lang.IllegalArgumentException;

public class BoxUtilsTest {
  @Test
  public void getCharForNumberTest() {
    assertTrue(BoxUtils.getCharForNumber(1) == 'A');
    assertTrue(BoxUtils.getCharForNumber(2) == 'B');
    assertTrue(BoxUtils.getCharForNumber(15) == 'O');
    assertTrue(BoxUtils.getCharForNumber(25) == 'Y');
    assertTrue(BoxUtils.getCharForNumber(26) == 'Z');
    assertTrue(BoxUtils.getCharForNumber(BoxUtils.getNumberForChar('A')) == 'A');
  }

  @Test(expected = IllegalArgumentException.class)
  public void getCharForNumberException() {
    BoxUtils.getCharForNumber(0);
    BoxUtils.getCharForNumber(-1);
    BoxUtils.getCharForNumber(27);
  }

  @Test
  public void getRowForNum() {
    assertTrue(BoxUtils.getRowChar(1) == 'A');
    assertTrue(BoxUtils.getRowChar(2) == 'B');
    assertTrue(BoxUtils.getRowChar(15) == 'O');
    assertTrue(BoxUtils.getRowChar(25) == 'Y');
    assertTrue(BoxUtils.getRowChar(26) == 'Z');
  }


  @Test
  public void getNumberForCharTest() {
    assertTrue(BoxUtils.getNumberForChar('A') == 1);
    assertTrue(BoxUtils.getNumberForChar('B') == 2);
    assertTrue(BoxUtils.getNumberForChar('C') == 3);
    assertTrue(BoxUtils.getNumberForChar('Z') == 26);
    assertTrue(BoxUtils.getNumberForChar('a') == 1);
    assertTrue(BoxUtils.getNumberForChar('z') == 26);
    assertTrue(BoxUtils.getNumberForChar(BoxUtils.getCharForNumber(1)) == 1);
  }

  @Test
  public void getRowForChar() {
    assertTrue(BoxUtils.getRowNum('A') == 1);
    assertTrue(BoxUtils.getRowNum('Z') == 26);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getNumForCharException() {
      BoxUtils.getNumberForChar('-');
      BoxUtils.getNumberForChar('%');
      BoxUtils.getNumberForChar('#');
      BoxUtils.getNumberForChar('~');
      BoxUtils.getNumberForChar(']');
      BoxUtils.getNumberForChar('*');
      BoxUtils.getNumberForChar((char)('A' + 'Z'));
  }


  @Test
  public void getPositionStringIntIntTest() {
    assertTrue(BoxUtils.getPositionString(1, 1).equals("A01"));
    assertTrue(BoxUtils.getPositionString(2, 1).equals("B01"));
    assertTrue(BoxUtils.getPositionString(2, 2).equals("B02"));
    assertTrue(BoxUtils.getPositionString(26, 1).equals("Z01"));
    assertTrue(BoxUtils.getPositionString(1, 10).equals("A10"));
    assertTrue(BoxUtils.getPositionString(1, 99).equals("A99"));
    assertTrue(BoxUtils.getPositionString(4, 23).equals("D23"));
  }

  @Test
  public void getPositionStringCharIntTest() {
    assertTrue(BoxUtils.getPositionString('A', 1).equals("A01"));
    assertTrue(BoxUtils.getPositionString('B', 1).equals("B01"));
    assertTrue(BoxUtils.getPositionString('B', 2).equals("B02"));
    assertTrue(BoxUtils.getPositionString('Z', 1).equals("Z01"));
    assertTrue(BoxUtils.getPositionString('E', 10).equals("E10"));
    assertTrue(BoxUtils.getPositionString('F', 99).equals("F99"));
    assertTrue(BoxUtils.getPositionString('L', 23).equals("L23"));
  }

  @Test
  public void tryParse() {
	assertTrue(BoxUtils.tryParseInt("this is not a number?") == -1);
    assertTrue(BoxUtils.tryParseInt("4") == 4);
    assertTrue(BoxUtils.tryParseInt("04") == 4);
    assertTrue(BoxUtils.tryParseInt("040") == 40);
    assertTrue(BoxUtils.tryParseInt("040") == 40);
    assertTrue(BoxUtils.tryParseInt("1000") == 1000);
    assertTrue(BoxUtils.tryParseInt("0000") == 0);
    assertTrue(BoxUtils.tryParseInt("0") == 0);
    assertTrue(BoxUtils.tryParseInt("-34") == -34);
    assertTrue(BoxUtils.tryParseInt("-1000") == -1000);
    assertTrue(BoxUtils.tryParseInt("-1") == -1);
    assertTrue(BoxUtils.tryParseInt("frog") == -1);
  }
}
