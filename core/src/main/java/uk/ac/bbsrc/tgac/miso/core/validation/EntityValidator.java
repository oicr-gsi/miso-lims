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

import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.ValidationFailureException;

import java.util.Map;

@Spi
public interface EntityValidator<T> {

  /**
   * Validates a single field, given the field name and corresponding data for that field
   * @param field
   * @param data
   * @return true if the validation succeeded
   * @throws ValidationFailureException
   * @throws MisoNamingException
   */
  public boolean validateField(String field, String data) throws ValidationFailureException, MisoNamingException;

  /**
   * Given an entity, T, validate it completely. If validation fails, throw a ValidationFailureException
   * @param entity to validate
   * @return true if validation succeeds
   * @throws ValidationFailureException
   * @throws MisoNamingException
   */
  public boolean validate(T t) throws ValidationFailureException, MisoNamingException;

  /**
   *  Validates a map of <field, value> pairs. For example, <"alias", "SAM_S0001_TEST">.
   *  The keys (representing the field) must correspond to those stored in the validations map
   * @param Map<String, String> data>
   * @return boolean
   * @throws ValidationFailureException
   * @throws MisoNamingException
   */
  public boolean validate(Map<String, String> data) throws ValidationFailureException, MisoNamingException;

  /**
   * Adds a validation rule to be checked when validating an entire Entity
   * @param field
   * @param fn
   */
  public void addValidation(String field, EntityFieldValidatorFunction fn);

  /**
   * Add a global validation rule that will apply to all fields when validating
   * @param name
   * @param fn
   */
  public void addGlobalValidation(String name, EntityFieldValidatorFunction fn);
}
