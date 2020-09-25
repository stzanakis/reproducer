package foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

@Entity
public class Root {

  @Id
  private ObjectId id = new ObjectId();
  private List<AbstractPlugin> plugins = new ArrayList<>();

  public Root() {
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public List<AbstractPlugin> getPlugins() {
    return plugins;
  }

  public void setPlugins(List<AbstractPlugin> plugins) {
    this.plugins = plugins;
  }
}
