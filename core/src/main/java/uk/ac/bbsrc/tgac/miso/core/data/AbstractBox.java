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

  private BoxSize size;
  private BoxUse use;

  @Override
  public long getId() {
    return boxId;
  }

  @Override
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

  @Override
  public int getTotalSize() {
    return size.getRows() * size.getColumns();
  }

  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  @Override
  public BoxUse getUse() {
    return use;
  }

  @Override
  public void setUse(BoxUse use) {
    this.use = use;
  }

  @Override
  public BoxSize getSize() {
    return size;
  }

  @Override
  public void setSize(BoxSize size) {
    this.size = size;
  }
}
