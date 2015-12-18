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
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@ServiceProvider
public class DefaultSampleValidator extends AbstractEntityValidator<Sample> {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleValidator.class);

  @Autowired
  private RequestManager requestManager;
  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public MisoNamingScheme<Sample> getSampleNamingScheme() {
    return sampleNamingScheme;
  }

  public void setSampleNamingScheme(MisoNamingScheme<Sample> sampleNamingScheme) {
    this.sampleNamingScheme = sampleNamingScheme;
  }

  public DefaultSampleValidator() {
    // JAVA 8 TODO: replace with much less verbose λs 😭?, also prevents duplication

    addValidation("name", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(sampleNamingScheme.validateField("name", data), "Sample name does not conform to naming scheme");
      }
    });

    addValidation("name", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data != null && data != "", "Sample name cannot be null or empty");
      }
    });

    addValidation("project", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data != null && data != "", "A project must be selected for the sample");
      }
    });

    addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data != null && data != "", "Sample alias cannot be null or empty");
      }
    });

    addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data.length() <= 100, "Sample alias must be less than or equal to 100 characters");
      }
    });

    addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(sampleNamingScheme.validateField("alias", data), "Sample alias does not conform to naming scheme.");
      }
    });

    addValidation("date", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data != null && data != "", "Sample date received cannot be null or empty");
      }
    });

    addValidation("description", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data != null && data != "", "Sample description cannot be null or empty");
      }
    });

    addValidation("description", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data.length() <= 100, "Sample description must be less than or equal to 100 characters");
      }
    });

    addValidation("scientificName", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data != null && data != "", "Sample scientific name cannot be null or empty");
      }
    });

    addValidation("scientificName", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data.length() <= 100, "Sample scientific name must be less than or equal to 100 characters");
      }
    });

    addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        try {
          return new EntityValidationResult(sampleNamingScheme.allowDuplicateEntityNameFor("alias") ||
                  requestManager.listSamplesByAlias(data).isEmpty(),
                  "Sample alias already exists in the database with that alias");
        } catch (IOException ex) {
          log.error("Could not connect to database to retrieve samples during validation");
          return new EntityValidationResult(true, "");
        }
      }
    });
  }

  @Override
  public boolean validate(Sample s) throws ValidationFailureException, MisoNamingException {
    Map<String, String> data = new LinkedHashMap<>();
    data.put("name", s.getName());
    data.put("project", s.getProject() == null ? null : "not null");
    data.put("alias", s.getAlias());
    data.put("description", s.getDescription());
    data.put("date", s.getReceivedDate() == null ? null : s.getReceivedDate().toString());
    data.put("scientificName", s.getScientificName());

    validate(data);
    return true;
  }
}
