package uk.ac.bbsrc.tgac.miso.sqlstore;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import com.eaglegenomics.simlims.core.SecurityProfile;
import java.io.IOException;
import java.util.*;

public class SQLBoxDAO implements BoxStore {
   public class BoxMapper extends CacheAwareRowMapper<Box> {
      public BoxMapper() {
         super(Box.class);
      }

      public BoxMapper(boolean lazy) {
         super(Box.class, lazy);
      }

      @Override
      public Box mapRow(ResultSet rs, int rowNum) throws SQLException {
         Box box = dataObjectFactory.getBox();
         box.setId(rs.getLong("boxId"));
         box.setName(rs.getString("name"));
         box.setAlias(rs.getString("alias"));
         box.setIdentificationBarcode(rs.getString("identificationBarcode"));
         box.setLocationBarcode(rs.getString("locationBarcode"));

         try {
            box.setType(getTypeById(rs.getLong("boxTypeId")));
            box.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
         } catch (IOException e) {
            e.printStackTrace();
         }
         return box;
      }
   }

   public class BoxTypeMapper extends CacheAwareRowMapper<BoxType> {

      public BoxTypeMapper() {
         super(BoxType.class);
      }

      @Override
      public BoxType mapRow(ResultSet rs, int rownum) throws SQLException {
         BoxType bt = new BoxType();
         bt.setId(rs.getLong("id"));
         bt.setAlias(rs.getString("alias"));
         bt.setDefaultColumns(rs.getInt("defaultColumns"));
         bt.setDefaultRows(rs.getInt("defaultRows"));
         return bt;
      }

   }

   private static String TABLE_NAME = "Box";

   public static final String BOX_DELETE = "DELETE FROM " + TABLE_NAME + "WHERE boxId=:boxId";

   public static final String BOX_SELECT = "SELECT boxId, boxTypeId, name, alias, identificationBarcode, locationBarcode FROM "
         + TABLE_NAME;

   public static final String BOX_SELECT_BY_ALIAS = BOX_SELECT + " WHERE alias = ?";

   public static final String BOX_SELECT_BY_ID = BOX_SELECT + " WHERE boxId = ?";

   public static final String BOX_SELECT_BY_ID_BARCODE = BOX_SELECT + " WHERE identificationBarcode = ?";

   public static final String BOX_SELECT_BY_SEARCH = BOX_SELECT + " WHERE identificationBarcode LIKE ? OR name LIKE ? OR alias LIKE ? ";

   public static final String BOX_SELECT_LIMIT = BOX_SELECT + " ORDER BY boxId DESC LIMIT ?";

   public static final String BOX_TYPE_BY_ID = "SELECT id, alias, defaultRows, defaultColumns FROM BoxType WHERE boxId = ?";

   public static final String BOX_UPDATE = "UPDATE " + TABLE_NAME + " SET boxTypeId:=boxTypeId, name=:name, alias=:alias, "
         + "identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode " + "WHERE boxId=:boxId";

   protected static final Logger log = LoggerFactory.getLogger(SQLBoxDAO.class);

   @Autowired
   private DataObjectFactory dataObjectFactory;

   @Autowired
   private MisoNamingScheme<Box> namingScheme;

   private Store<SecurityProfile> securityProfileDAO;

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
   public BoxType getTypeById(long id) throws IOException {
      List<BoxType> results = template.query(BOX_TYPE_BY_ID, new Object[] { id }, new BoxTypeMapper());
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
      // TODO cascade
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
      params.addValue("boxTypeId", box.getType().getId()).addValue("name", box.getName()).addValue("alias", box.getAlias())
            .addValue("securityProfile_profileId", securityProfileId).addValue("identificationBarcode", box.getIdentificationBarcode())
            .addValue("locationBarcode", box.getLocationBarcode());

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
                  new NamedParameterJdbcTemplate(template).update(BOX_DELETE,
                        new MapSqlParameterSource().addValue("boxId", newId.longValue()));
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

      // TODO write box items

      return box.getId();
   }

   public void setJdbcTemplate(JdbcTemplate template) {
      this.template = template;
   }

   public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
      this.securityProfileDAO = securityProfileDAO;
   }
}
