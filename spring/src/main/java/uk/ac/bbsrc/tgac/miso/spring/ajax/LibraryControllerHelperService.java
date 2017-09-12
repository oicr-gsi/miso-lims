/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class LibraryControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(LibraryControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private LibraryService libraryService;

  public JSONObject validateLibraryAlias(HttpSession session, JSONObject json) {
    if (json.has("alias")) {
      String alias = json.getString("alias");
      if (isStringEmptyOrNull(alias) && namingScheme.hasLibraryAliasGenerator()) {
        // alias will be generated by DAO during save
        return JSONUtils.SimpleJSONResponse("OK");
      }
      ValidationResult aliasValidation = namingScheme.validateLibraryAlias(alias);
      if (aliasValidation.isValid()) {
        log.debug("Library alias OK!");
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        log.error("Library alias not valid: " + alias);
        return JSONUtils.SimpleJSONError(aliasValidation.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No alias specified");
    }
  }

  public JSONObject addLibraryNote(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Library library = libraryService.get(libraryId);
      Note note = new Note();
      internalOnly = internalOnly.equals("on") ? "true" : "false";
      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      libraryService.addNote(library, note);
    } catch (IOException e) {
      log.error("add library note", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject deleteLibraryNote(HttpSession session, JSONObject json) {
    Long libraryId = json.getLong("libraryId");
    Long noteId = json.getLong("noteId");

    try {
      Library library = libraryService.get(libraryId);
      libraryService.deleteNote(library, noteId);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("delete library note", e);
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject deleteLibrary(HttpSession session, JSONObject json) {
    if (json.has("libraryId")) {
      Long libraryId = json.getLong("libraryId");
      try {
        libraryService.delete(libraryService.get(libraryId));
        return JSONUtils.SimpleJSONResponse("Library deleted");
      } catch (IOException e) {
        log.error("cannot delete library", e);
        return JSONUtils.SimpleJSONError("Cannot delete library: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No library specified to delete.");
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setLibraryNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }
}
