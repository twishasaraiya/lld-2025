package router;

import org.example.atlassian.router.IRouter;
import org.example.atlassian.router.service.Router;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {
    private static IRouter router;

    @BeforeAll
    public static void init(){
        router = new Router();
    }
    @Test
    public void testExactMatchPass(){
        router.addRoute("/foo","result");
        assertEquals("result", router.callRoute("/foo").get().getOutput());
    }

    @Test
    public void testExactMatchFail(){
        router.addRoute("/foo1","result");
        assertEquals(Optional.empty(), router.callRoute("/foo"));
    }

    @Test
    public void testWildCard(){
        router.addRoute("*/bar", "result1");
        router.addRoute("foo/*","result2");
        router.addRoute("foo/bar", "result3");
        assertEquals("result3",router.callRoute("foo/bar").get().getOutput());
        assertEquals("result2", router.callRoute("foo/zyz").get().getOutput());
        assertEquals("result1", router.callRoute("zyz/bar").get().getOutput());
    }


    @Test
    public void testParams(){
        router.addRoute("users/profile", "result1");
        router.addRoute("users/:page","result2");
        assertEquals("result2", router.callRoute("users/zyz").get().getOutput());
        assertEquals("result1", router.callRoute("users/profile").get().getOutput());
    }

}
