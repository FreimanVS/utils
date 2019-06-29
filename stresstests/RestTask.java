import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/restTask")
public class RestTask {

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("tasksPerSecond") String tasksPerSecond) {
        StresstestService.execute(
                () -> {
                    System.out.println("hi " + Thread.currentThread().getName());
                },
                Integer.valueOf(tasksPerSecond)
        );
        return Response.ok("the the is completed").build();
    }
}
