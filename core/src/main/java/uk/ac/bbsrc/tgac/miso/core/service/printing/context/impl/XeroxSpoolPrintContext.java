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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl;

import java.io.File;
import java.io.IOException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.util.FileUtils;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.impl.XeroxSpoolPrintStrategy;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 30-Jun-2011
 * @since 0.0.3
 */
@ServiceProvider
public class XeroxSpoolPrintContext implements PrintContext<File> {
  protected static final Logger log = LoggerFactory.getLogger(XeroxSpoolPrintContext.class);
  private XeroxSpoolPrintStrategy ps = new XeroxSpoolPrintStrategy();
  public String host;
  private PrintService printService;
  private PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();  
  private PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

  public PrintService getPrintService() {
    if (defaultPrintService != null) { log.info("print default " + defaultPrintService.getName()); }
    if (printServices != null) { for (int i = 0; i < printServices.length; i++) { log.info("print " + printServices[i].getName()); } }
    if (printService != null) { log.info("print service " + printService.getName()); 
				return printService; 
   }else { return defaultPrintService; }
  }

  public void setPrintService(PrintService printService) {
    this.printService = printService;
  }

  public void setPrintStrategy(XeroxSpoolPrintStrategy ps) {
    this.ps = ps;
  }

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String getName() {
    return "xerox-type-spool-printer";
  }

  @Override
  public String getDescription() {
    return "Prints to a Xerox-type printer via the normal spooling method";
  }

  @Override
  public boolean print(File content) throws IOException {
    String s = FileUtils.readFile(content);
    return ps.print(s, this);
  }
}
