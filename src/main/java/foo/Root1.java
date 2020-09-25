package foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

@Entity
public class Root1 {

  @Id
  private ObjectId id = new ObjectId();
  private List<AbstractPlugin1> plugins = new ArrayList<>();

  public Root1() {
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public List<AbstractPlugin1> getPlugins() {
    return plugins;
  }

  public void setPlugins(List<AbstractPlugin1> plugins) {
    this.plugins = plugins;
  }
}
