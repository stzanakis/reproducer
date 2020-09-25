package foo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import dev.morphia.annotations.Embedded;


@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "pluginType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = APlugin.class, name = "A_PLUGIN")
})
@Embedded
//@Embedded(useDiscriminator = false)
//@Embedded(discriminatorKey = "pluginType")
public abstract class AbstractPlugin {

  protected String pluginType;

  public AbstractPlugin() {
    //Required from Morphia
  }

  public AbstractPlugin(String pluginType) {
    this.pluginType = pluginType;
  }

  public String getPluginType() {
    return pluginType;
  }
}
