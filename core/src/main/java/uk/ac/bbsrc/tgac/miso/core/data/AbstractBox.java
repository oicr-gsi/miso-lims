package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.exception.InvalidBoxPositionException;
import java.util.Map;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

public abstract class AbstractBox implements Box {
   public static final Long UNSAVED_ID = 0L;

   private String alias;
   private long boxId = AbstractBox.UNSAVED_ID;
   private int columns;
   private String identificationBarcode;
   private String locationBarcode;
   private String name;

   private int rows;
   private SecurityProfile securityProfile;
   private BoxType type;

   @Override
   public String getAlias() {
      return alias;
   }

   @Override
   public abstract Boxable getBoxItem(String position) throws InvalidBoxPositionException;

   @Override
   public abstract Map<String, Boxable> getBoxItems();

   @Override
   public long getId() {
      return boxId;
   }

   @Override
   public String getIdentificationBarcode() {
      return identificationBarcode;
   }

   @Override
   public String getLocationBarcode() {
      return locationBarcode;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public int getNumColumns() {
      return columns;
   }

   @Override
   public int getNumRows() {
      return rows;
   }

   @Override
   public SecurityProfile getSecurityProfile() {
      return securityProfile;
   }

   @Override
   public int getSize() {
      return rows * columns;
   }

   @Override
   public BoxType getType() {
      return type;
   }

   @Override
   public void setAlias(String alias) {
      this.alias = alias;
   }

   @Override
   public abstract void setBoxItem(String position, Boxable item) throws InvalidBoxPositionException;

   @Override
   public abstract void setBoxItems(Map<String, Boxable> items) throws InvalidBoxPositionException;

   @Override
   public void setId(long id) {
      this.boxId = id;
   }

   @Override
   public void setIdentificationBarcode(String identificationBarcode) {
      this.identificationBarcode = identificationBarcode;
   }

   @Override
   public void setLocationBarcode(String locationBarcode) {
      this.locationBarcode = locationBarcode;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public void setNumColumns(int columns) {
      this.columns = columns;
   }

   @Override
   public void setNumRows(int rows) {
      this.rows = rows;
   }

   @Override
   public void setSecurityProfile(SecurityProfile securityProfile) {
      this.securityProfile = securityProfile;
   }

   @Override
   public void setType(BoxType type) {
      this.type = type;
   }

   @Override
   public boolean userCanRead(User user) {
      return securityProfile.userCanRead(user);
   }

   @Override
   public boolean userCanWrite(User user) {
      return securityProfile.userCanWrite(user);
   }
}
