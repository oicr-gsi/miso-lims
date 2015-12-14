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

import net.sourceforge.fluxion.spi.ServiceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.ValidationFailureException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;

import java.util.HashMap;
import java.util.Map;

@ServiceProvider
public class DefaultSampleValidator extends AbstractEntityValidator<Sample> {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleValidator.class);

  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;

  public DefaultSampleValidator() {
    // Pleaaaase give me λs  😭😭

    addGlobalValidation("length", new EntityFieldValidatorFunction() {
      public boolean validate(String data) throws ValidationFailureException, MisoNamingException {
        return data.length() < 100;
      }
    });

    addValidation("alias", new EntityFieldValidatorFunction() {
      public boolean validate(String data) throws MisoNamingException {
        return sampleNamingScheme.validateField("alias", data);
      }
    });
  }

  @Override
  public boolean validate(Sample s) throws ValidationFailureException, MisoNamingException {
    Map<String, String> data = new HashMap<String, String>();
    data.put("alias", s.getAlias());
    data.put("description", s.getDescription());
    data.put("date", s.getLastUpdated().toString());
    data.put("scientificName", s.getScientificName());
    return validate(data);
  }
}
