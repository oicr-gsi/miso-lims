package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Controller
@RequestMapping("/box")
@SessionAttributes("box")
public class EditBoxController {
  protected static final Logger log = LoggerFactory.getLogger(EditBoxController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  /*
  @ModelAttribute("materialTypes")
  public Collection<String> populateMaterialTypes() throws IOException {
    return requestManager.listAllStudyTypes();
  }

  public Collection<TagBarcode> populateAvailableTagBarcodes() throws IOException {
    List<TagBarcode> barcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodes());
    Collections.sort(barcodes);
    return barcodes;
  }

  public String tagBarcodesString(String platformName) throws IOException {
    List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodes());
    Collections.sort(tagBarcodes);
    List<String> names = new ArrayList<String>();
    for (TagBarcode tb : tagBarcodes) {
      names.add("\"" + tb.getName() + " ("+tb.getSequence()+")\"" + ":" + "\"" + tb.getId() + "\"");
    }
    return LimsUtils.join(names, ",");
  }
  */

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newBox(ModelMap model) throws IOException {
    return setupForm(AbstractPlate.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/rest/{boxId}", method = RequestMethod.GET)
  public @ResponseBody Box jsonRest(@PathVariable Long boxId) throws IOException {
    //return requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
    return requestManager.getBoxById(boxId);
  }

  @RequestMapping(value = "/{boxId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long boxId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Box box = null;
      if (boxId == AbstractBox.UNSAVED_ID) {
        box = dataObjectFactory.getBox(user);
        //plate = dataObjectFactory.getPlateOfSize(96, user);
        model.put("title", "New Box");
      }
      else {
        //plate = requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
        box = requestManager.getBoxById(boxId);
        model.put("title", "Box " + boxId);
      }

      if (box != null) {
        if (!box.userCanRead(user)) {
          throw new SecurityException("Permission denied.");
        }
        model.put("formObj", box);
        model.put("box", box);
      }
      else {
        throw new SecurityException("No such Box");
      }

      return new ModelAndView("/pages/editBox.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Box", ex);
      }
      throw ex;
    }
  }
}
