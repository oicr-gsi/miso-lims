package uk.ac.bbsrc.tgac.miso.core.test.data.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;

public class BoxImplTest {

  @Test
  public void testNotDefault() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    assertTrue(box.getSize().getRows() == 10);
    assertTrue(box.getSize().getColumns() == 20);

    User user = null;
    box = new BoxImpl(user);
  }

  @Test
  public void testFreeandAdd() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    assertTrue(box.isFreePosition("A01"));
    assertTrue(box.isFreePosition("A12"));
    assertTrue(box.isFreePosition("H01"));
    assertTrue(box.isFreePosition("B01"));
    Sample sam1 = new SampleImpl();
    Library lib1 = new LibraryImpl();
    Sample sam2 = new SampleImpl();
    Library lib2 = new LibraryImpl();
    try {
      box.setBoxable("A01", sam1);
      box.setBoxable("A12", sam2);
      box.setBoxable("H01", lib1);
      box.setBoxable("H12", lib2);
    } catch (Exception e) {

    }

    assertTrue(box.isFreePosition("C01"));
    assertTrue(box.isFreePosition("D05"));
    assertTrue(box.isFreePosition("D10"));

    assertTrue(box.isFreePosition("A02"));
    assertTrue(box.isFreePosition("A03"));
    assertTrue(box.isFreePosition("A11"));

    assertTrue(!box.isFreePosition("A01"));
    assertTrue(!box.isFreePosition("A12"));
    assertTrue(!box.isFreePosition("H01"));
    assertTrue(!box.isFreePosition("H12"));

    assertTrue(box.getBoxable("A01") == sam1);
    assertTrue(box.getBoxable("A12") == sam2);
    assertTrue(box.getBoxable("H01") == lib1);
    assertTrue(box.getBoxable("H12") == lib2);
    assertTrue(!(box.getBoxable("H10") == lib2));
  }

  @Test
  public void testDefaultValidPositions() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    assertTrue(box.isValidPosition("A01"));
    assertTrue(box.isValidPosition("B01"));
    assertTrue(box.isValidPosition("D01"));
    assertTrue(box.isValidPosition("H01"));

    assertTrue(box.isValidPosition("E06"));
    assertTrue(box.isValidPosition("E10"));

    assertTrue(box.isValidPosition("A12"));
    assertTrue(box.isValidPosition("H12"));

    assertFalse(box.isValidPosition("A1"));
    assertFalse(box.isValidPosition("A13"));
    assertFalse(box.isValidPosition("A2"));
    assertFalse(box.isValidPosition("H2"));
    assertFalse(box.isValidPosition("H13"));

    assertFalse(box.isValidPosition("a23"));
    assertFalse(box.isValidPosition("dog"));
    assertFalse(box.isValidPosition("woof"));
    assertFalse(box.isValidPosition("rawr"));
    assertFalse(box.isValidPosition("o1234"));
    assertFalse(box.isValidPosition("Q12"));
    assertFalse(box.isValidPosition("P03"));
    assertFalse(box.isValidPosition("d0g"));
    assertFalse(box.isValidPosition("w00f"));
  }

  @Test
  public void getFreeTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    box.setBoxable("A01", new SampleImpl());
    box.setBoxable("B01", new LibraryImpl());
    assertTrue(box.getFree() == 96 - 2);

    BoxSize size2 = new BoxSize();
    size2.setColumns(1);
    size2.setRows(1);
    Box box2 = new BoxImpl();
    box2.setSize(size2);
    box2.setBoxable("A01", new SampleImpl());
    assertTrue(box2.getFree() == 0);
  }

  @Test
  public void getsetBoxablesTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    Map<String, Boxable> map = new HashMap<String, Boxable>();
    map.put("A01", new SampleImpl());
    map.put("B01", new LibraryImpl());
    box.setBoxables(map);

    assertTrue(box.getBoxables() == map);
  }

  @Test
  public void boxableExistsTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    Sample s = new SampleImpl();
    box.setBoxable("A01", s);
    Library l = new LibraryImpl();
    box.setBoxable("A02", l);
    assertTrue(box.boxableExists(s));
    assertTrue(box.boxableExists(l));
  }

  @Test
  public void addRemoveBoxableTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    Sample s = new SampleImpl();
    box.setBoxable("A01", s);
    assertTrue(box.boxableExists(s));
    box.removeBoxable(s);
    assertTrue(!box.boxableExists(s));

    box.setBoxable("A01", s);
    assertTrue(box.boxableExists(s));
    box.removeBoxable("A01");
    assertTrue(!box.boxableExists(s));
  }

  @Test
  public void removeAllBoxablesTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    box.setBoxable("A01", new SampleImpl());
    box.setBoxable("B01", new SampleImpl());
    box.setBoxable("C01", new SampleImpl());
    box.setBoxable("D01", new SampleImpl());
    box.setBoxable("E01", new SampleImpl());
    box.setBoxable("F01", new SampleImpl());
    box.setBoxable("G01", new SampleImpl());
    assertTrue(box.getTotalSize() - box.getFree() == 7);
    box.removeAllBoxables();
    assertTrue(box.getFree() == box.getTotalSize());

    box.setBoxable("A01", new LibraryImpl());
    assertTrue(box.getTotalSize() - box.getFree() == 1);
    box.removeAllBoxables();
    assertTrue(box.getFree() == box.getTotalSize());
  }

  @Test
  public void setBoxableEmptyTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    Sample s = new SampleImpl();
    box.setBoxable("A01", s);
    box.setBoxableEmpty("A01");
    assertTrue(s.isEmpty());
  }

  @Test
  public void setAllBoxablesEmptyTest() {
    BoxSize size = new BoxSize();
    size.setColumns(20);
    size.setRows(10);
    Box box = new BoxImpl();
    box.setSize(size);
    Sample s1 = new SampleImpl();
    Sample s2 = new SampleImpl();
    Library l1 = new LibraryImpl();
    Library l2 = new LibraryImpl();

    assertTrue(!s1.isEmpty());
    assertTrue(!s2.isEmpty());
    assertTrue(!l1.isEmpty());
    assertTrue(!l2.isEmpty());

    box.setBoxable("A01", s1);
    box.setBoxable("A02", s2);
    box.setBoxable("B01", l1);
    box.setBoxable("B02", l2);

    box.setAllBoxablesEmpty();
    assertTrue(s1.isEmpty());
    assertTrue(s2.isEmpty());
    assertTrue(l1.isEmpty());
    assertTrue(l2.isEmpty());
  }
}
