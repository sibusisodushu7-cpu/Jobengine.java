import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JobEngineApplication {
    private record Job(String title, String company, String location, int salary, int experience) {}

    private static final List<Job> JOBS = List.of(
            new Job("Junior Java Developer", "Entelect", "Melrose Arch", 25000, 0),
            new Job("Web Assistant (Frontend)", "Adcorp Group", "Sandton", 22000, 0),
            new Job("IT Systems Intern", "Hire Resolve", "Bryanston", 18000, 0),
            new Job("Software Support Tech", "Private Security Hub", "Midrand", 24000, 0));

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> serveFile(exchange, "index.html", "text/html; charset=utf-8"));
        server.createContext("/health", exchange -> respond(exchange, 200, "{\"status\":\"ok\"}", "application/json"));
        server.createContext("/jobs", JobEngineApplication::serveJobs);
        server.createContext("/subscribe", JobEngineApplication::serveSubscription);
        server.start();
        System.out.println("JobEngine live on port " + port);
    }

    private static void serveJobs(HttpExchange exchange) throws IOException {
        String response = JOBS.stream()
                .map(job -> String.format("{\"title\":\"%s\",\"company\":\"%s\",\"location\":\"%s\",\"salary\":%d}",
                        job.title(), job.company(), job.location(), job.salary()))
                .toList().toString().replace("=", ":");
        respond(exchange, 200, response, "application/json");
    }

    private static void serveSubscription(HttpExchange exchange) throws IOException {
        String merchantId = System.getenv().getOrDefault("PAYFAST_MERCHANT_ID", "");
        String merchantKey = System.getenv().getOrDefault("PAYFAST_MERCHANT_KEY", "");
        if (merchantId.isBlank() || merchantKey.isBlank()) {
            respond(exchange, 503, "Payment setup is not complete. Add PayFast credentials in Render.", "text/plain");
            return;
        }
        String returnUrl = System.getenv().getOrDefault("PAYFAST_RETURN_URL", "http://localhost:8080/");
        String cancelUrl = System.getenv().getOrDefault("PAYFAST_CANCEL_URL", returnUrl);
        String notifyUrl = System.getenv().getOrDefault("PAYFAST_NOTIFY_URL", returnUrl + "health");
        String form = "<form id=\"payfast\" action=\"https://www.payfast.co.za/eng/process\" method=\"post\">"
                + hidden("merchant_id", merchantId) + hidden("merchant_key", merchantKey)
                + hidden("amount", "99.00") + hidden("item_name", "JobEngine Premium Subscription")
                + hidden("return_url", returnUrl) + hidden("cancel_url", cancelUrl)
                + hidden("notify_url", notifyUrl) + "</form><script>document.getElementById('payfast').submit()</script>";
        respond(exchange, 200, "<!doctype html><html><body>Redirecting to PayFast..." + form + "</body></html>", "text/html");
    }

    private static String hidden(String name, String value) {
        return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\">";
    }

    private static void serveFile(HttpExchange exchange, String file, String type) throws IOException {
        try {
            respond(exchange, 200, Files.readString(Path.of(file)), type);
        } catch (IOException exception) {
            respond(exchange, 500, "{\"error\":\"Web file unavailable\"}", "application/json");
        }
    }

    private static void respond(HttpExchange exchange, int status, String body, String type) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
