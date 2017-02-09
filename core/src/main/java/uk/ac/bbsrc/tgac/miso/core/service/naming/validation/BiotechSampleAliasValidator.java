package uk.ac.bbsrc.tgac.miso.core.service.naming.validation;

public class BiotechSampleAliasValidator extends RegexValidator {

  private static final String MF_PART = "([MF\\d]{2,5})";
  private static final String SAMPLE_NUMBER_PART = "([S\\d]{2,5})";
  private static final String NAME_PART = "([a-zA-Z\\d]{3,20})";

  private static final String REGEX = MF_PART + "_" + SAMPLE_NUMBER_PART + "_" + NAME_PART;

  public BiotechSampleAliasValidator() {
    super(REGEX, false, false);
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
