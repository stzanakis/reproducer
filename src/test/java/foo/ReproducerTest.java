package foo;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.NamingStrategy;
import dev.morphia.mapping.codec.pojo.EntityModelBuilder;
import dev.morphia.query.experimental.filters.Filters;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

public class ReproducerTest extends TestBase {

  private static final String PACKAGE_NAME = "com.foo";

  //Enable discriminator, annotations will override this on an individual case
  final MapperOptions mapperOptions = MapperOptions.builder()
      .discriminatorKey("className")
      .discriminator(new DiscriminatorFunction() {
        public String compute(EntityModelBuilder<?> builder) {
          return builder.getType().getCanonicalName();
        }
      })
      .collectionNaming(NamingStrategy.identity()).build();

  final Datastore datastore = Morphia
      .createDatastore(getMongoClient(), getDatabase().getName(), mapperOptions);
  private Root root;
  private Root1 root1;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    //Root and embedded with normal className field
    datastore.getMapper().map(Root.class);
    root = new Root();
    root.setId(new ObjectId());
    final List<AbstractPlugin> aPlugins = List.of(new APlugin());
    root.setPlugins(aPlugins);
    datastore.save(root);

    //Root with className but embedded document with out
    datastore.getMapper().map(Root1.class);
    root1 = new Root1();
    root1.setId(new ObjectId());
    final List<AbstractPlugin1> aPlugins1 = List.of(new APlugin1());
    root1.setPlugins(aPlugins1);
    datastore.save(root1);
  }

  @Test
  public void testSuccessfulDeserializeEmbedded() {
    final Root foundRoot = datastore.find(Root.class)
        .filter(Filters.eq("_id", root.getId())).first();
    Assert.assertTrue(foundRoot.getPlugins().get(0) instanceof APlugin);
    Assert.assertEquals("A_PLUGIN", foundRoot.getPlugins().get(0).getPluginType());
  }

  @Test
  public void testSuccessfulDeserializeEmdeddedNoClassName() {
    //Root1 has the pluginType field twice inserted because of the @Embedded(discriminatorKey = "pluginType"), (Is that normal?)
    //The first occurrence contains the com.foo.APlugin1 value
    //We are removing that
    final Document query = new Document("_id", root1.getId());
    removeEmbeddedDiscriminator(query, Root1.class.getSimpleName());

    final Document root1Doc = getDatabase().getCollection("Root1").find(query).first();
    final Document document = ((List<Document>) (root1Doc.get("plugins"))).get(0);
    Assert.assertEquals("A_PLUGIN", document.get("pluginType"));

    //Now it's no more deserializable
    final Root1 foundRoot = datastore.find(Root1.class)
        .filter(Filters.eq("_id", root1.getId())).first();
    Assert.assertTrue(foundRoot.getPlugins().get(0) instanceof APlugin1);
    Assert.assertEquals("A_PLUGIN", foundRoot.getPlugins().get(0).getPluginType());
  }

  private void removeEmbeddedDiscriminator(Document query, String collectionName) {
    Document update = new Document();
    update.put("$unset", new Document("plugins.0.pluginType", ""));
    getDatabase().getCollection(collectionName).updateOne(query, update);
  }

  @Override
  protected void cleanup() {
    super.cleanup();
  }
}
