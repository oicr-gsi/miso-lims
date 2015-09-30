package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

import java.io.IOException;
import java.util.Collection;


/**
 * This interface defines a DAO for storing Boxes
 *
 */
public interface BoxStore extends Store<Box>, Remover<Box> {
  /**
   * Retrieve a Box from data store given a Box alias.
   *
   * @param String alias
   * @return Box
   * @throws IOException
   */
  Box getBoxByAlias(String alias) throws IOException;

  /**
   * Retrieve a Box from data store given a Box barcode
   *
   * @param String barcode
   * @return Box
   * @throws IOException
   */
  Box getByBarcode(String barcode) throws IOException;

  /**
   * List all the boxes
   *
   * @return Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listAll() throws IOException;

  /**
   * List all the boxes with a limit.
   *
   * @param long limit
   * @return Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listWithLimit(long limit) throws IOException;

  /**
   * List all the boxes by alias
   *
   * @param String alias
   * @returns Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listByAlias(String alias) throws IOException;

  /**
   * List all the boxes matching a query
   *
   * @param String query
   * @return Collection<Box> boxes
   * @throws IOException
   */
  Collection<Box> listBySearch(String query) throws IOException;
}
