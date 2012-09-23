package go;

import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuppressWarnings("FieldMayBeFinal")
public class GoConfiguration extends Configuration {

    @NotEmpty
    private String template;
    
    @NotEmpty
    private String defaultName = "Stranger";

    
    public String getTemplate() {
        return template;
    }

    public String getDefaultName() {
        return defaultName;
    }

    
}
