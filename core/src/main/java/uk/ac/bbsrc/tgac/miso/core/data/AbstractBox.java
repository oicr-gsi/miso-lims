package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.exception.InvalidBoxPositionException;
import java.util.Map;

public abstract class AbstractBox implements Box {
  public static final Long UNSAVED_ID = 0L;

  private long boxId = AbstractBox.UNSAVED_ID;
  private String name;
  private String alias;
  private String identificationBarcode;
  private String locationBarcode;

  private int columns;
  private int rows;

  @Override
  public long getId() {
    return boxId;
  }

  public void setId(long id) {
    this.boxId = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @Override
  public int getNumRows() {
    return rows;
  }

  @Override
  public void setNumRows(int rows) {
    this.rows = rows;
  }

  @Override
  public int getNumColumns() {
    return columns;
  }

  @Override
  public void setNumColumns(int columns) {
    this.columns = columns;
  }


  @Override
  public abstract Map<String, Boxable> getBoxItems();

  @Override
  public abstract void setBoxItem(String position, Boxable item) throws InvalidBoxPositionException;


  @Override
  public abstract Boxable getBoxItem(String position) throws InvalidBoxPositionException;

  @Override
  public abstract void setBoxItems(Map<String, Boxable> items) throws InvalidBoxPositionException;

  @Override
  public int getSize() {
    return rows*columns;
  }

}
