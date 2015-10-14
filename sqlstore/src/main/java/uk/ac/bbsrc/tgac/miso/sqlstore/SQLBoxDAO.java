package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import com.eaglegenomics.simlims.core.SecurityProfile;

public class SQLBoxDAO implements BoxStore {
  public static final String BOX_POSITIONS_SELECT = "SELECT boxPositionId, column, row WHERE boxId = ?";

  public class BoxMapper extends CacheAwareRowMapper<Box> {
    public BoxMapper() {
      super(Box.class);
    }

    public BoxMapper(boolean lazy) {
      super(Box.class, lazy);
    }

    @Override
    public Box mapRow(ResultSet rs, int rowNum) throws SQLException {
      final Box box = dataObjectFactory.getBox();
      box.setId(rs.getLong("boxId"));
      box.setName(rs.getString("name"));
      box.setAlias(rs.getString("alias"));
      box.setIdentificationBarcode(rs.getString("identificationBarcode"));
      box.setLocationBarcode(rs.getString("locationBarcode"));

      try {
        box.setUse(getUseById(rs.getLong("boxTypeId")));
        box.setSize(getSizeById(rs.getLong("boxSizeId")));
        box.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        box.getBoxables().clear();
        template.query(BOX_POSITIONS_SELECT, new RowCallbackHandler() {

          @Override
          public void processRow(ResultSet inner_rs) throws SQLException {
            long positionId = inner_rs.getLong("boxPositionId");
            int row = inner_rs.getInt("row");
            int column = inner_rs.getInt("column");
            Boxable item = libraryDAO.getByPositionId(positionId);
            if (item == null) {
              item = sampleDAO.getByPositionId(positionId);
            }
            box.getBoxables().put(BoxUtils.getPositionString(row, column), item);
          }

        }, box.getId());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return box;
    }
  }

  public class BoxUseMapper extends CacheAwareRowMapper<BoxUse> {

    public BoxUseMapper() {
      super(BoxUse.class);
    }

    @Override
    public BoxUse mapRow(ResultSet rs, int rownum) throws SQLException {
      BoxUse use = new BoxUse();
      use.setId(rs.getLong("boxUseId"));
      use.setAlias(rs.getString("alias"));
      return use;
    }

  }

  public class BoxSizeMapper extends CacheAwareRowMapper<BoxSize> {

    public BoxSizeMapper() {
      super(BoxSize.class);
    }

    @Override
    public BoxSize mapRow(ResultSet rs, int rownum) throws SQLException {
      BoxSize size = new BoxSize();
      size.setId(rs.getLong("boxSizeId"));
      size.setRows(rs.getInt("rows"));
      size.setColumns(rs.getInt("columns"));
      return size;
    }

  }

  private static String TABLE_NAME = "Box";

  public static final String BOX_DELETE = "DELETE FROM " + TABLE_NAME + "WHERE boxId=:boxId";

  public static final String BOX_SELECT = "SELECT boxId, boxTypeId, name, alias, identificationBarcode, locationBarcode FROM " + TABLE_NAME;

  public static final String BOX_SELECT_BY_ALIAS = BOX_SELECT + " WHERE alias = ?";

  public static final String BOX_SELECT_BY_ID = BOX_SELECT + " WHERE boxId = ?";

  public static final String BOX_SELECT_BY_ID_BARCODE = BOX_SELECT + " WHERE identificationBarcode = ?";

  public static final String BOX_SELECT_BY_SEARCH = BOX_SELECT + " WHERE identificationBarcode LIKE ? OR name LIKE ? OR alias LIKE ? ";

  public static final String BOX_SELECT_LIMIT = BOX_SELECT + " ORDER BY boxId DESC LIMIT ?";

  public static final String BOX_USE_SELECT = "SELECT boxUseId, alias FROM BoxUse";

  public static final String BOX_USE_SELECT_BY_ID = BOX_USE_SELECT + " WHERE boxUseId = ?";

  public static final String BOX_SIZE_SELECT = "SELECT boxSizeId, rows, columns FROM BoxSize";

  public static final String BOX_SIZE_SELECT_BY_ID = BOX_USE_SELECT + " WHERE boxSizeId = ?";

  public static final String BOX_UPDATE = "UPDATE " + TABLE_NAME + " SET boxTypeId:=boxTypeId, name=:name, alias=:alias, "
      + "identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode " + "WHERE boxId=:boxId";

  protected static final Logger log = LoggerFactory.getLogger(SQLBoxDAO.class);

  private static final String BOX_DELETE_CONTENTS = "DELETE FROM " + TABLE_NAME + " WHERE boxId = ?";

  private static final String BOX_INSERT_CONTENTS = "INSERT INTO " + TABLE_NAME
      + " (boxId, row, column, boxPositionId) VALUES (?, ?, ?, ?)";

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private MisoNamingScheme<Box> namingScheme;

  private Store<SecurityProfile> securityProfileDAO;

  private SampleStore sampleDAO;
  private LibraryStore libraryDAO;

  private JdbcTemplate template;

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Box get(long boxId) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ID, new Object[] { boxId }, new BoxMapper());
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ALIAS, new Object[] { alias }, new BoxMapper(true));
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Box getByBarcode(String barcode) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ID_BARCODE, new Object[] { barcode }, new BoxMapper(true));
    return results.size() > 0 ? results.get(0) : null;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  @Override
  public BoxUse getUseById(long id) throws IOException {
    List<BoxUse> results = template.query(BOX_USE_SELECT_BY_ID, new Object[] { id }, new BoxUseMapper());
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public BoxSize getSizeById(long id) throws IOException {
    List<BoxSize> results = template.query(BOX_SIZE_SELECT_BY_ID, new Object[] { id }, new BoxSizeMapper());
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Box lazyGet(long boxId) throws IOException {
    List<Box> results = template.query(BOX_SELECT_BY_ID, new Object[] { boxId }, new BoxMapper(true));
    return results.size() > 0 ? results.get(0) : null;
  }

  @Override
  public Collection<Box> listAll() throws IOException {
    return template.query(BOX_SELECT, new BoxMapper());
  }

  @Override
  public Collection<Box> listByAlias(String alias) throws IOException {
    return template.query(BOX_SELECT_BY_ALIAS, new Object[] { alias }, new BoxMapper(true));
  }

  @Override
  public Collection<Box> listBySearch(String query) throws IOException {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(BOX_SELECT_BY_SEARCH, params, new BoxMapper(true));
  }

  @Override
  public Collection<Box> listWithLimit(long limit) throws IOException {
    return template.query(BOX_SELECT_LIMIT, new Object[] { limit }, new BoxMapper(true));
  }

  @Override
  public boolean remove(Box box) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.update(BOX_DELETE, new MapSqlParameterSource().addValue("boxId", box.getId())) == 1;
  }

  @Override
  public long save(Box box) throws IOException {
    Long securityProfileId = box.getSecurityProfile().getProfileId();
    if (securityProfileId == null) {
      securityProfileId = getSecurityProfileDAO().save(box.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("boxSizeId", box.getSize().getId());
    params.addValue("boxUseId", box.getUse().getId());
    params.addValue("name", box.getName());
    params.addValue("alias", box.getAlias());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("identificationBarcode", box.getIdentificationBarcode());
    params.addValue("locationBarcode", box.getLocationBarcode());

    if (box.getId() == AbstractDilution.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("boxId");
      try {
        box.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", box);
        box.setName(name);

        if (namingScheme.validateField("name", box.getName())) {
          String barcode = name + "::" + box.getAlias();
          params.addValue("name", name);

          params.addValue("identificationBarcode", barcode);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != box.getId()) {
            log.error("Expected LibraryDilution ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(BOX_DELETE, new MapSqlParameterSource().addValue("boxId", newId.longValue()));
            throw new IOException("Something bad happened. Expected LibraryDilution ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Box - invalid field:" + box.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Box - issue with naming scheme", e);
      }

    } else {
      try {
        if (namingScheme.validateField("name", box.getName())) {
          params.addValue("boxId", box.getId()).addValue("name", box.getName())
              .addValue("identificationBarcode", box.getName() + "::" + box.getAlias());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(BOX_UPDATE, params);
        } else {
          throw new IOException("Cannot save Box - invalid field:" + box.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Box - issue with naming scheme", e);
      }
    }
    template.update(BOX_DELETE_CONTENTS, box.getId());
    for (Entry<String, Boxable> entry : box.getBoxables().entrySet()) {
      int row = BoxUtils.getRowChar(entry.getKey().charAt(0));
      int column = Integer.parseInt(entry.getKey().substring(1));
      template.update(BOX_INSERT_CONTENTS, box.getId(), row, column, entry.getValue());
    }

    return box.getId();
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  @Override
  public Collection<BoxUse> listAllUses() throws IOException {
    return template.query(BOX_USE_SELECT, new Object[0], new BoxUseMapper());
  }

  @Override
  public Collection<BoxSize> listAllSizes() throws IOException {
    return template.query(BOX_SIZE_SELECT, new Object[0], new BoxSizeMapper());
  }

  public LibraryStore getLibraryDAO() {
    return libraryDAO;
  }

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  public SampleStore getSampleDAO() {
    return sampleDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }
}
