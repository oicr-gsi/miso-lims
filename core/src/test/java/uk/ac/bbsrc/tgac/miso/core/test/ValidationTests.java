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

package uk.ac.bbsrc.tgac.miso.core.test;

import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.ValidationFailureException;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.validation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


public class ValidationTests {
  protected static final Logger log = LoggerFactory.getLogger(RunTests.class);

  @InjectMocks
  private DefaultSampleValidator sampleValidator;

  private EntityValidator<Pool> poolValidator;

  @Mock
  private MisoRequestManager requestManager;
  @Mock
  private MisoNamingScheme<Sample> sampleNamingScheme;

  @Rule
  public final ExpectedException validationException = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    sampleValidator = new DefaultSampleValidator();
    sampleValidator.setSampleNamingScheme(sampleNamingScheme);

    poolValidator = new DefaultEntityValidator<Pool>();
  }


  @Test
  public void testAddValidator() throws ValidationFailureException, MisoNamingException {
    poolValidator.addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data.contains("ha"), "Pool alias does not contain laughter");
      }
    });
    assert(poolValidator.validateField("alias", "ha"));

    try {
      poolValidator.validateField("alias", "nope");
      assert(false);
    } catch (ValidationFailureException ex) {
    }

    poolValidator.addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data.length() <3, "Pool contains no love");
      }
    });

    assert(poolValidator.validateField("alias", "ha"));

    try {
      poolValidator.validateField("alias", "hahahaha");
    } catch (ValidationFailureException ex) {
    }

    poolValidator.addValidation("field", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data.endsWith("4"), "field does not end in 4");
      }
    });

    assert(poolValidator.validateField("field", "fdsajkldfskjlfdsajklfadsjkl4"));

    try {
      poolValidator.validateField("field", "12345");
      assert(false);
    } catch (ValidationFailureException ex) {
    }

    Map<String, String> data = new LinkedHashMap<>();
    data.put("alias", "ha");
    data.put("field", "fdljkcmn fsamfn dsa  !$#@$#@! 4");
    assert(poolValidator.validate(data));
  }


  @Test
  public void testDefaultSampleValidator() throws ValidationFailureException, MisoNamingException, IOException {
    Collection<Sample> c = new LinkedList();
    when(requestManager.listSamplesByAlias(anyString())).thenReturn(c);
    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(true);
    when(sampleNamingScheme.allowDuplicateEntityNameFor("alias")).thenReturn(true);

    sampleValidator.addValidation("alias", new EntityFieldValidatorFunction() {
      public EntityValidationResult validate(String data) throws ValidationFailureException, MisoNamingException {
        return new EntityValidationResult(data == "frog", "Sample alias not equal to frog.");
      }
    });

    assert(sampleValidator.validateField("alias", "frog"));

    try {
      sampleValidator.validateField("alias", "notfrog");
      assert(false);
    } catch (ValidationFailureException ex) {
    }

    try {
      sampleValidator.validateField("alias", "FROG");
      assert(false);
    } catch (ValidationFailureException ex) {
    }
  }

  @Test
  public void testDefaultSampleValidatorName() throws ValidationFailureException, MisoNamingException, IOException {
    sampleValidator.setSampleNamingScheme(sampleNamingScheme);
    Map<String, String> data = new LinkedHashMap<>();

    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(true);
    data.put("name", "SAM001");
    sampleValidator.validate(data);

    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(false);
    data.put("name", "SAM001");
    try {
      sampleValidator.validate(data);
      assert(false);
    } catch (ValidationFailureException ex) {
    }
  }

  @Test
  public void testDefaultSampleValidatorProject() throws ValidationFailureException, MisoNamingException, IOException {
    sampleValidator.setSampleNamingScheme(sampleNamingScheme);
    Map<String, String> data = new LinkedHashMap<>();

    data.put("project", "project1");
    assert(sampleValidator.validate(data));

    data.put("project", null);
    try {
      sampleValidator.validate(data);
      assert(false);
    } catch (ValidationFailureException ex) {
    }

    try {
      sampleValidator.validate(data);
      assert(false);
    } catch (ValidationFailureException ex) {
    }
  }

  @Test
  public void testDefaultSampleValidatorAlias() throws ValidationFailureException, MisoNamingException, IOException {
    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(true);
    when(sampleNamingScheme.allowDuplicateEntityNameFor("alias")).thenReturn(true);
    Collection<Sample> c = new LinkedList();
    when(requestManager.listSamplesByAlias(anyString())).thenReturn(c);

    // Test without naming scheme
    Map<String, String> data = new LinkedHashMap<>();
    data.put("alias", "thisisanalias");
    assert(sampleValidator.validate(data));

    // Test invalid naming scheme
    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(false);
    data.put("alias", "bob");
    try {
      sampleValidator.validate(data);
      assert(false);
    } catch (ValidationFailureException ex) {
    }

    // Test duplicate aliases
    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(true);
    when(sampleNamingScheme.allowDuplicateEntityNameFor("alias")).thenReturn(false);
    c.add(new SampleImpl());
    when(requestManager.listSamplesByAlias(anyString())).thenReturn(c);
    sampleValidator.setRequestManager(requestManager);
    try {
      sampleValidator.validate(data);
      assert(false);
    } catch (ValidationFailureException ex) {
    }
  }

  @Test
  public void testDefaultSampleValidatorDescription() throws ValidationFailureException, MisoNamingException, IOException {
    when(sampleNamingScheme.validateField(anyString(), anyString())).thenReturn(true);
    when(sampleNamingScheme.allowDuplicateEntityNameFor("alias")).thenReturn(true);
    Collection<Sample> c = new LinkedList();
    when(requestManager.listSamplesByAlias(anyString())).thenReturn(c);

    Map<String, String> data = new LinkedHashMap<>();
    data.put("description", "");

    try {
      sampleValidator.validate(data);
      assert(false);
    } catch (ValidationFailureException ex) {
    }
  }

  @After
  public void tearDown() {
    sampleValidator = null;
  }
}
