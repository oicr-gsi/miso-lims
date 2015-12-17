/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */
package uk.ac.bbsrc.tgac.miso.core.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.ValidationFailureException;
import uk.ac.bbsrc.tgac.miso.core.util.Pair;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public abstract class AbstractEntityValidator<T> implements EntityValidator<T> {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleValidator.class);

  // Key represents the name of the field, Value is the corresponding validator function
  private List<Pair<String, EntityFieldValidatorFunction>> validators;

  // Global validators apply to each field
  private List<Pair<String, EntityFieldValidatorFunction>> globalValidators;


  public AbstractEntityValidator() {
    validators = new ArrayList<>();
    globalValidators = new ArrayList<>();
  }

  // Refer to interface for documentation
  @Override
  public boolean validateField(String field, String data) throws ValidationFailureException, MisoNamingException {
    // Apply all validators for field on data
    for (Pair<String, EntityFieldValidatorFunction> p : validators) {
      if (p.getKey() == field) {
        EntityValidationResult result = p.getValue().validate(data);
        if (result.getPassed() == false)
          throw new ValidationFailureException(result.getFailureMessage());
      }
    }

    return true;
  }

  // Refer to interface for documentation
  @Override
  public boolean validate(Map<String, String> data) throws ValidationFailureException, MisoNamingException {
    for (Map.Entry<String, String> i : data.entrySet()) {
      // Apply global validations
      for (Pair<String, EntityFieldValidatorFunction> p : globalValidators) {
        if (p.getValue().validate(i.getValue()).getPassed() == false)
          return false;
      }

      // No validation rule found
      if (getValidationFunction(i.getKey()) == null) {
        log.info("No validation rule found for given field: "+i.getKey());
        continue;
      }

      if (validateField(i.getKey(), i.getValue()) == false)
        return false;
    }
    return true;
  }

  // Refer to interface for documentation
  @Override
  public void addValidation(String field, EntityFieldValidatorFunction fn) {
    validators.add(new Pair(field, fn));
  }

  // Refer to interface for documentation
  @Override
  public void addGlobalValidation(String name, EntityFieldValidatorFunction fn) {
    globalValidators.add(new Pair(name, fn));
  }

  private EntityFieldValidatorFunction getValidationFunction(String field) {
    Pair<String, EntityFieldValidatorFunction> p = getValidatorPair(field);
    return p == null ? null : p.getValue();
  }

  /**
   * Retrieves the corresponding Validator Pair given it's field and null otherwise
   */
  private Pair<String, EntityFieldValidatorFunction> getValidatorPair(String field) {
    for (Pair<String, EntityFieldValidatorFunction> p : validators) {
      if (p.getKey() == field)
        return p;
    }
    return null;
  }
}
