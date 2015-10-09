package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import net.sourceforge.fluxion.ajax.Ajaxified;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.RequestManagerAware;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;



@Ajaxified
public class BoxControllerHelperService implements RequestManagerAware {
  protected static final Logger log = LoggerFactory.getLogger(BoxControllerHelperService.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private MisoNamingScheme<Box> boxNamingScheme;

  public JSONObject listBoxesDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Box box : requestManager.listAllBoxes()) {
        jsonArray.add("['" +
        TableHelper.hyperLinkify("/miso/box/" + box.getId(), box.getName(), true)
        + "','" +
        TableHelper.hyperLinkify("/miso/sample/" + box.getId(), box.getAlias())
        + "','" +
        box.getSize()
        + "']");
      }
      j.put("array", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  // Retrieve Boxable Item from requestManager
  private Boxable getBoxableByBarcode(String barcode) { // throws BoxConflictException?
    try {
      Boxable sample = requestManager.getSampleByBarcode(barcode);
      Boxable library = requestManager.getLibraryByBarcode(barcode);
      if (sample != null && library != null) {
        return null;
      } else if (sample == null && library != null) {
        return library;
      } else if (sample != null && library == null) {
        return sample;
      } else {
        return null;
      }
    } catch (IOException e) {
      log.debug("Could not retrieve Boxable from RequestManager: ", e);
      return null;
    }
  }

  public JSONObject getBoxableByBarcode(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    String barcode = json.getString("barcode");

    try {
      Sample sample = requestManager.getSampleByBarcode(barcode);
      Library library = requestManager.getLibraryByBarcode(barcode);
      if (sample != null && library != null) { //Item exists for both
        //throw new
        return JSONUtils.SimpleJSONError("There exists a Sample and Library for this barcode!");
      } else if (sample != null && library == null) { //Item is a sample
        response.put("name", sample.getName());
        response.put("desc", sample.getDescription());
        response.put("alias", sample.getAlias());
        response.put("id", sample.getId());
        response.put("type", sample.getSampleType());
        response.put("project", sample.getProject().getName());
      } else if (sample == null && library != null) { //Item is a library
        response.put("name", library.getName());
        response.put("desc", library.getDescription());
        response.put("alias", library.getAlias());
        response.put("id", library.getId());
        response.put("type", library.getLibraryType());
      } else { // Item cannot be found
          log.error("Cannot find Boxable item.");
          return JSONUtils.SimpleJSONError("Cannot find Boxable item.");
      }

      if (sample.getReceivedDate() == null) {
        response.put("name", sample.getName());
        response.put("desc", sample.getDescription());
        response.put("id", sample.getId());
        response.put("type", sample.getSampleType());
        response.put("project", sample.getProject().getName());
        return response;
      }
      else {
        return JSONUtils.SimpleJSONError("Sample " + sample.getName() + " has already been received");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": This sample doesn't seem to be in the database.");
    }
  }


  public JSONObject deleteBox(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("boxId")) {
        Long boxId = json.getLong("boxId");
        try {
          requestManager.deleteBox(requestManager.getBoxById(boxId));
          return JSONUtils.SimpleJSONResponse("Box deleted");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete box: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No box specified to delete.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject validateBoxInput(HttpSession session, JSONObject json) {
    if (json.has("alias") && json.has("rows") && json.has("columns")) {
        String alias = json.getString("alias");
        Integer rows = BoxUtils.tryParseInt(json.getString("rows"));
        Integer columns = BoxUtils.tryParseInt(json.getString("columns"));
        if (rows > 26 || rows < 1) {
          log.error("Row out of bounds: " + rows.toString());
          return JSONUtils.SimpleJSONError("The Row value is out of bounds!");
        }
        else if (columns > 26 || columns < 1) {
          log.error("Column out of bounds: " + columns.toString());
          return JSONUtils.SimpleJSONError("The Column value is out of bounds!");
        }
        else if (alias.equals("")) {
          log.error("Alias field cannot be empty!");
          return JSONUtils.SimpleJSONError("The Alias cannot be empty!");
        }
        else {
          log.error("Box input OK!");
          return JSONUtils.SimpleJSONResponse("OK");
        }
    }
    else {
      return JSONUtils.SimpleJSONError("No alias and/or rows and/or columns specified");
    }
  }

  /*
   * Bulk scan using a BulkScanner
   *
   * TODO: GLT-219 add additional docs here
   *
   */
  public JSONObject initiateBulkScan(HttpSession session, JSONObject json) {
    //TODO: GLT-219
	  return null;
  }

  /*
   * Scans an individual item (using handheld scanner) and:
   *  a) if the item doesn't exist in the box: add the item to the selected position
   *  b) if the item does exist in the box, but in a different location: move the item to the selected position
   *  c) if the spot is taken: do nothing and warn the user
   *
   * Note: json must contain the following entries:
   *   "boxId"    : boxId
   *   "barcode"  : barcode
   *   "position" : position
   * where position is a String representing the rows/columns. eg. A01, H12
   *
   * @param HttpSession session, JSONObject json
   * @returns JSONObject message indicating success or failure
   */
  public JSONObject individualScan(HttpSession session, JSONObject json) {
      User user;
      try {
        user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
      }

      if (user != null && user.isAdmin()) {
        if (json.has("boxId") && json.has("barcode") && json.has("position")) {
          Long boxId = json.getLong("boxId");
          Box box;
          String barcode = json.getString("barcode");
          String position = json.getString("position");

          try {
            box = requestManager.getBoxById(boxId);
          }
          catch (IOException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError("Cannot get the Box: " + e.getMessage());
          }

          if (!box.isValidPosition(position))
            return JSONUtils.SimpleJSONError("Invalid position given!");

          // if item exists at position already, remove it
          if (box.getBoxable(position) != null) {
        	  box.removeBoxable(position);
          }

          // get the Sample/Library
          Boxable boxable = null;
          try {
            boxable = getBoxableByBarcode(barcode);
            if (boxable == null)
              return JSONUtils.SimpleJSONError("Could not find item for barcode: " + barcode + "!");
          }
          catch (DuplicateKeyException e) {
            return JSONUtils.SimpleJSONError("ERROR: A Sample and a Library both exist for barcode: " + barcode);
          }

          String response = "";

          // try to find item already at position (if one exists) and remove it
          Boxable oldBoxable = box.getBoxable(position);
          if (oldBoxable != null) {
            box.removeBoxable(position);
            response += oldBoxable.getName() + " has been removed from the box.\n";
          }

          // move the item if it exists in the box, else add it to the box
          if (box.boxableExists(boxable)) {
            box.removeBoxable(boxable);
            box.setBoxable(position, boxable);
            response += boxable.getName() + " successfully moved to position " + position;
          }
          else {
            box.setBoxable(position, boxable);
            response += boxable.getName() + " successfully added to position " + position;
          }
          return JSONUtils.SimpleJSONResponse(response);
        }
        else {
          return JSONUtils.SimpleJSONError("Invalid box, barcode or position given.");
        }
      }
      else {
        return JSONUtils.SimpleJSONError("Only logged-in admins can edit boxes.");
      }
  }

  /*
   * Empties the entire box of Boxable elements
   * Note: json must contain the keyvalue pair of "boxId" : <boxId>
   *
   * @param HttpSession session, JSONObject json
   * @returns JSONObject message indicating failure or success
   */
  public JSONObject emptyEntireBox(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("boxId")) {
        Long boxId = json.getLong("boxId");
        try {
          Box box = requestManager.getBoxById(boxId);
          box.setAllBoxablesEmpty();
          box.removeAllBoxables();
          return JSONUtils.SimpleJSONResponse("Box items emptied and removed successfully.");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot empty box: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No box specified to empty.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can empty boxes.");
    }
  }

  /*
   * Empties a single Boxable item at a given position and then removes it from the given box
   * Note: json must contain the key-value pairs:
   *  boxId : <boxId>
   *  position : <position>
   *
   * @param HttpSession session, JSONObject json containing the above key-value pairs
   * @return JSONObject containing the result of the individual empty
   */
  public JSONObject individualEmpty(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("boxId") && json.has("position")) {
        Long boxId = json.getLong("boxId");
        String position = json.getString("position");
        Box box;
        try {
          box = requestManager.getBoxById(boxId);
        }
        catch (IOException e) {
          return JSONUtils.SimpleJSONError("Cannot get Box: " + e.toString());
        }

        if (box.isFreePosition(position))
          return JSONUtils.SimpleJSONError("No item to delete at position " + position + "!");

        if (!box.isValidPosition(position))
          return JSONUtils.SimpleJSONError("Invalid position given!");

        String itemName = box.getBoxable(position).getName();
        box.setBoxableEmpty(position);
        box.removeBoxable(position);
        return JSONUtils.SimpleJSONResponse(itemName + " succesfully removed from position " + position);
      }
      else {
        return JSONUtils.SimpleJSONError("Box or position not provided.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can empty boxes.");
    }
  }

  public RequestManager getRequestManager() {
    return requestManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
