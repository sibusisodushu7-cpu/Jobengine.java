import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main Entry Class for the JobEngine Enterprise Infrastructure.
 * Tailored for high-concurrency database loops and candidate matching.
 * 
 * Target Portfolio Net Worth: $50 Million Asset.
 * Blueprint State: Small Boat Floating Safely. Zero Stress.
 */
public class JobEngineApplication {

    private static final Logger logger = Logger.getLogger(JobEngineApplication.class.getName());
    
    // Thread-safe map to log active sessions when scaling up to 100,000 global users
    private final ConcurrentHashMap<String, String> activeUserSessionRegistry;
    private final double compilationLevel;

    public JobEngineApplication() {
        this.activeUserSessionRegistry = new ConcurrentHashMap<>();
        this.compilationLevel = 0.80; // Hardcoded state matching your exact blueprint
    }

    /**
     * Primary system runtime entry point for execution.
     */
    public static void main(String[] args) {
        System.out.println("\n========================================================");
        logger.info("Initializing JobEngine Application Platform...");
        System.out.println("========================================================\n");
        
        JobEngineApplication platformInstance = new JobEngineApplication();
        platformInstance.bootstrapSystemInfrastructure();
        platformInstance.startHttpServer();
    }

    /**
     * Orchestrates backend service boot sequences and logs cloud status.
     */
    private void bootstrapSystemInfrastructure() {
        logger.info("Routing cloud pipeline gateways (Location: Overseas Servers)...");
        logger.info(String.format("Core System Integrity Mapping: %.0f%% Operational", (compilationLevel * 100)));
        
        if (compilationLevel < 1.0) {
            System.out.println("\n--------------------------------------------------------");
            logger.warning("SYSTEM FLAG: 'Unfinished Work' parameter triggered.");
            logger.info("ACTION REQUIRED: Pausing deployment loops on local machine.");
            logger.info("STRATEGY: Prioritize Grade 11 core dependencies immediately.");
            System.out.println("STATUS CODE 200: Small boat gliding smoothly. Zero panic.");
            System.out.println("--------------------------------------------------------\n");
        } else {
            logger.info("PRODUCTION DEPLOYMENT SUCCESSFUL: JobEngine is live globally.");
        }
    }

    private void startHttpServer() {
        try {
            JobProvider provider = () -> List.of(
                    new JobListing("Junior Java Developer", "Entelect", "Melrose Arch", 25000),
                    new JobListing("Web Assistant (Frontend)", "Adcorp Group", "Sandton", 22000),
                    new JobListing("IT Systems Intern", "Hire Resolve", "Bryanston", 18000),
                    new JobListing("Software Support Tech", "Private Security Hub", "Midrand", 24000));
            JobEngine engine = new JobEngine(provider, new InMemoryJobRepository());
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", exchange -> {
                try {
                    sendResponse(exchange, 200, Files.readString(Path.of("index.html")), "text/html; charset=utf-8");
                } catch (IOException exception) {
                    sendResponse(exchange, 500, "{\"error\":\"Web interface unavailable\"}", "application/json; charset=utf-8");
                }
            });
            server.createContext("/health", exchange -> sendResponse(exchange, 200, "{\"status\":\"ok\"}"));
            server.createContext("/jobs", exchange -> {
                int experience = parseExperience(exchange.getRequestURI().getQuery());
                List<JobListing> jobs = engine.vetJobsForExperience(experience);
                String response = jobs.stream()
                        .map(job -> String.format(
                                "{\"title\":\"%s\",\"company\":\"%s\",\"location\":\"%s\",\"salary\":%d}",
                                job.getTitle(), job.getCompany(), job.getLocation(), job.getSalary()))
                        .toList().toString().replace("=", ":");
                sendResponse(exchange, 200, response, "application/json; charset=utf-8");
            });
            server.start();
            logger.info("JobEngine is live on port " + port);
        } catch (IOException exception) {
            logger.severe("Unable to start HTTP server: " + exception.getMessage());
        }
    }

    private static int parseExperience(String query) {
        if (query != null && query.startsWith("experience=")) {
            try {
                return Integer.parseInt(query.substring("experience=".length()));
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    private static void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        sendResponse(exchange, status, body, "application/json; charset=utf-8");
    }

    private static void sendResponse(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] response = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, response.length);
        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}