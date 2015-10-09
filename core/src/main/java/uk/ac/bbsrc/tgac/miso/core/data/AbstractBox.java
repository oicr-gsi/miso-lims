package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

public abstract class AbstractBox implements Box {
  public static final Long UNSAVED_ID = 0L;

  private SecurityProfile securityProfile = null;

  private long boxId = AbstractBox.UNSAVED_ID;
  private String name;
  private String alias;
  private String identificationBarcode;
  private String locationBarcode;

  private int columns;
  private int rows;
  private BoxType type;
  
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
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    }
    else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }

  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  @Override
  public int getSize() {
    return getNumRows() * getNumColumns();
  }

  @Override
  public void setType(BoxType type) {
    this.type = type;
  }

  @Override
  public BoxType getType() {
    return type;
  }
}
