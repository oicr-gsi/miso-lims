package uk.ac.bbsrc.tgac.miso.core.data;

public class BoxSize {
  private long id;
  private int rows;
  private int columns;

  public int getColumns() {
    return columns;
  }

  public long getId() {
    return id;
  }

  public int getRows() {
    return rows;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }
}
