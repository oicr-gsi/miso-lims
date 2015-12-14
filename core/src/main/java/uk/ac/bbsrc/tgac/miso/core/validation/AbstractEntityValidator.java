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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEntityValidator<T> implements EntityValidator<T> {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleValidator.class);

  // TODO change this from map to just a list, so there can be duplicate validators, each with different error messages
  protected Map<String, EntityFieldValidatorFunction> validators;

  // These are applied to every field
  private Map<String, EntityFieldValidatorFunction> globalValidators;

  public AbstractEntityValidator() {
    validators = new HashMap<String, EntityFieldValidatorFunction>();
    globalValidators = new HashMap<String, EntityFieldValidatorFunction>();
  }

  @Override
  public boolean validateField(String field, String data) throws ValidationFailureException, MisoNamingException {
    return validators.get(field).validate(data);
  }

  @Override
  public boolean validate(Map<String, String> data) throws ValidationFailureException, MisoNamingException {
    for (Map.Entry<String, String> i : data.entrySet()) {
      if (validators.get(i.getKey()).validate(i.getValue()) == false)
        return false;
    }
    return true;
  }

  @Override
  public void addValidation(String field, EntityFieldValidatorFunction fn) {
    validators.put(field, fn);
  }

  @Override
  public void addGlobalValidation(String name, EntityFieldValidatorFunction fn) {
    globalValidators.put(name, fn);
  }

  protected EntityFieldValidatorFunction getValidation(String field) {
    return validators.get(field);
  }
}
