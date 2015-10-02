package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public class BoxType implements Serializable, Comparable<BoxType> {
   private String alias;
   private long id;
   private int defaultRows;
   private int defaultColumns;

   @Override
   public int compareTo(BoxType o) {
      return Long.compare(this.id, o.id);
   }

   public String getAlias() {
      return alias;
   }

   public long getId() {
      return id;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public void setId(long id) {
      this.id = id;
   }

   public int getDefaultColumns() {
      return defaultColumns;
   }

   public void setDefaultColumns(int defaultColumns) {
      this.defaultColumns = defaultColumns;
   }

   public int getDefaultRows() {
      return defaultRows;
   }

   public void setDefaultRows(int defaultRows) {
      this.defaultRows = defaultRows;
   }

}
