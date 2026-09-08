import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SimpleLoadTest extends Simulation {
    private static final int vu = Integer.getInteger("vu", 1000);
    private static final String BASE_URL = "http://localhost:8080";
    private static final String UPCOMING_EVENTS_ENDPOINT = "/events/upcoming";

    private static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (Chrome/134.0.0.0 Safari/537.36)");

    private static final ScenarioBuilder scenario = scenario("Get upcoming events")
            .exec(http("request").get(UPCOMING_EVENTS_ENDPOINT));

    private static final Assertion assertion = global().failedRequests().count().lt(1L);

    {
        setUp(
                scenario.injectOpen(
                        rampUsers(vu).during(30)  // 100 пользователей за 30 секунд
                )
        )
                .assertions(assertion)
                .protocols(httpProtocol);
    }
}