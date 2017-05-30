package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class BiotechLibraryAliasValidator extends RegexValidator {

  private static final String regex = "([MF\\d]{2,5})_([S\\d]{2,5})_([L])_([a-zA-Z\\d]{3,20})";

  public BiotechLibraryAliasValidator() {
    super(regex, false, false);
  }

  @Override
  protected String getFieldName() {
    return "alias";
  }

  @Override
  protected boolean customRegexOptionEnabled() {
    return false;
  }

  @Override
  protected boolean nullabilityOptionEnabled() {
    return false;
  }

  @Override
  protected boolean enableDuplicatesOptionEnabled() {
    return false;
  }

}
