package uk.ac.bbsrc.tgac.miso.sqlstore;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

public class SQLBoxDAO implements BoxStore {
  private static String TABLE_NAME = "Box";

  public static final String BOX_SELECT =
          "SELECT boxId, boxTypeId, name, alias, identificationBarcode, locationBarcode " +
          "FROM " + TABLE_NAME;

  public static final String BOX_SELECT_LIMIT =
          BOX_SELECT + " ORDER BY boxId DESC LIMIT?";

  public static final String BOX_SELECT_BY_ID =
          BOX_SELECT + " WHERE boxId = ?";

  public static final String BOX_SELECT_BY_ID_BARCODE =
          BOX_SELECT + " WHERE identificationBarcode = ?";

  public static final String BOX_SELECT_BY_ALIAS =
          BOX_SELECT + " WHERE alias = ?";

  public static final String BOX_SELECT_BY_SEARCH =
          BOX_SELECT + " WHERE " +
          "identificationBarcode LIKE ? OR " +
          "name LIKE ? OR " +
          "alias LIKE ? ";

  public static final String BOX_UPDATE =
          "UPDATE " + TABLE_NAME + " " +
          "SET boxTypeId:=boxTypeId, name=:name, alias=:alias, " +
          "identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode " +
          "WHERE boxId=:boxId";

  public static final String BOX_DELETE =
          "DELETE FROM " + TABLE_NAME + "WHERE plateId=:plateId";

  protected static final Logger log = LoggerFactory.getLogger(SQLBoxDAO.class);

  @Autowired
  private DataObjectFactory dataObjectFactory;

  private JdbcTemplate template;

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @Override
  public long save(Box t) throws IOException {
  	// Lots of TODO Auto-generated method stub
  	return 0;
  }

  @Override
  public Box get(long boxId) throws IOException {
    List results = template.query(BOX_SELECT_BY_ID, new Object[]{boxId}, new BoxMapper());
    return results.size() > 0 ? (Box)results.get(0) : null;
  }

  @Override
  public Box lazyGet(long boxId) throws IOException {
    List results = template.query(BOX_SELECT_BY_ID, new Object[]{boxId}, new BoxMapper(true));
    return results.size() > 0 ? (Box)results.get(0) : null;
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public boolean remove(Box t) throws IOException {
  	// TODO Auto-generated method stub
  	return false;
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
  	// TODO Auto-generated method stub
  	return null;
  }

  @Override
  public Box getByBarcode(String barcode) throws IOException {
    List results = template.query(BOX_SELECT_BY_ID_BARCODE, new Object[]{barcode}, new BoxMapper(true));
    return results.size() > 0 ? (Box)results.get(0) : null;
  }

  @Override
  public Collection<Box> listAll() throws IOException {
    return template.query(BOX_SELECT, new BoxMapper());
  }

  @Override
  public Collection<Box> listWithLimit(long limit) throws IOException {
    return template.query(BOX_SELECT_LIMIT, new Object[]{limit}, new BoxMapper(true));
  }

  @Override
  public Collection<Box> listByAlias(String alias) throws IOException {
    return template.query(BOX_SELECT_BY_ALIAS, new Object[]{alias}, new BoxMapper(true));
  }

  @Override
  public Collection<Box> listBySearch(String query) throws IOException {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(BOX_SELECT_BY_SEARCH, params, new BoxMapper(true));
  }

  // No idea what goes on in here..
  public class BoxMapper extends CacheAwareRowMapper<Box> {
    public BoxMapper() {
      super(Box.class);
    }

    public BoxMapper(boolean lazy) {
      super(Box.class, lazy);
    }

  	@Override
  	public Box mapRow(ResultSet rs, int rowNum) throws SQLException {
  		// TODO Auto-generated method stub
  		return null;
  	}
  }
}
