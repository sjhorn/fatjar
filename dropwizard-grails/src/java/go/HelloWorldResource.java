package go;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.yammer.metrics.annotation.Timed;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    @GET
    @Timed
    public String sayHello(@QueryParam("name") String name) {
        return "Hello "+name;
    }
}
