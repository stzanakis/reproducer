package foo;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.Mapper;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;

public abstract class TestBase {

  protected static final String TEST_DB_NAME = "morphia_test";
  private final MongoClient mongoClient;
  private final Mapper mapper;
  private final MongoDatabase database;
  private final Datastore ds;

  protected TestBase() {
    this(MongoClients.create(new ConnectionString(getMongoURI())));
  }

  protected TestBase(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
    this.ds = Morphia.createDatastore(this.getMongoClient(), TEST_DB_NAME);
    this.database = this.ds.getDatabase();
    this.mapper = this.ds.getMapper();
  }

  protected static String getMongoURI() {
    return System.getProperty("MONGO_URI", "mongodb://localhost:27017");
  }

  public MongoDatabase getDatabase() {
    return this.database;
  }

  public Datastore getDs() {
    return this.ds;
  }

  public MongoClient getMongoClient() {
    return this.mongoClient;
  }

  public Mapper getMapper() {
    return this.mapper;
  }

  public boolean isReplicaSet() {
    return this.runIsMaster().get("setName") != null;
  }

  @Before
  public void setUp() throws Exception {
    this.cleanup();
  }

  @After
  public void tearDown() {
    this.cleanup();
    this.getMongoClient().close();
  }

  protected void checkMinServerVersion(double version) {
    Assume.assumeTrue(this.serverIsAtLeastVersion(version));
  }

  protected void cleanup() {
    final MongoDatabase database = this.getDatabase();
    if (database != null) {
      database.drop();
    }

  }

  protected boolean serverIsAtLeastVersion(double version) {
    String serverVersion = (String) this.getMongoClient().getDatabase("admin").runCommand(new BasicDBObject("serverStatus", "version"))
        .get("version");
    return Double.parseDouble(serverVersion.substring(0, 3)) >= version;
  }

  protected boolean serverIsAtMostVersion(double version) {
    String serverVersion = (String) this.getMongoClient().getDatabase("admin").runCommand(new BasicDBObject("serverStatus", "version"))
        .get("version");
    return Double.parseDouble(serverVersion.substring(0, 3)) <= version;
  }

  private Document runIsMaster() {
    return this.mongoClient.getDatabase("admin").runCommand(new BasicDBObject("ismaster", 1));
  }

  public BasicDBObject obj(String key, Object value) {
    return new BasicDBObject(key, value);
  }

  protected static <E> List<E> toList(MongoCursor<E> cursor) {
    ArrayList results = new ArrayList();

    try {
      while (cursor.hasNext()) {
        results.add(cursor.next());
      }
    } finally {
      cursor.close();
    }

    return results;
  }
}
