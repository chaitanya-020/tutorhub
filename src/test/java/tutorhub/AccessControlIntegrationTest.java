package tutorhub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack tests against a REAL PostgreSQL started in Docker by Testcontainers.
 * @ServiceConnection wires Spring's datasource to the container automatically;
 * Flyway then runs V1 on the fresh database. These tests exercise the actual
 * security filters, tenant resolution, and SQL — so they PROVE the access model.
 *
 * Requires Docker to be running.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AccessControlIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void crossTenantAndPerStudentIsolationAreEnforced() throws Exception {
        String maria = register("maria.iso@x.com", "Maria");
        String bob = register("bob.iso@x.com", "Bob");
        String sara = register("sara.iso@x.com", "Sara");

        long academyA = createAcademy(maria, "Academy A", "iso-a");
        long academyB = createAcademy(bob, "Academy B", "iso-b");

        addMember(maria, academyA, "sara.iso@x.com", "STUDENT");
        long course1 = createCourse(maria, academyA, "Math");
        long course2 = createCourse(maria, academyA, "Science");
        enroll(maria, academyA, course1, "sara.iso@x.com");

        // Cross-tenant: Maria is not a member of Academy B -> 403
        mockMvc.perform(get("/api/courses")
                        .header("Authorization", bearer(maria))
                        .header("X-Academy-Id", academyB))
                .andExpect(status().isForbidden());

        // Cross-tenant: Bob is a member of B, but course1 belongs to A -> 404 (not even revealed)
        mockMvc.perform(get("/api/courses/" + course1)
                        .header("Authorization", bearer(bob))
                        .header("X-Academy-Id", academyB))
                .andExpect(status().isNotFound());

        // Per-student: Sara sees ONLY the course she's enrolled in
        mockMvc.perform(get("/api/courses")
                        .header("Authorization", bearer(sara))
                        .header("X-Academy-Id", academyA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(course1));

        // Per-student: Sara cannot see a course she's not enrolled in -> 404
        mockMvc.perform(get("/api/courses/" + course2)
                        .header("Authorization", bearer(sara))
                        .header("X-Academy-Id", academyA))
                .andExpect(status().isNotFound());
    }

    @Test
    void gradingIsProtectedByOptimisticLocking() throws Exception {
        String maria = register("maria.grade@x.com", "Maria");
        String tom = register("tom.grade@x.com", "Tom");
        String sara = register("sara.grade@x.com", "Sara");

        long academy = createAcademy(maria, "Grading Academy", "grade-a");
        addMember(maria, academy, "tom.grade@x.com", "TUTOR");
        addMember(maria, academy, "sara.grade@x.com", "STUDENT");
        long course = createCourse(maria, academy, "Math");
        enroll(maria, academy, course, "sara.grade@x.com");
        long assignment = createAssignment(tom, academy, course);

        long submissionId = submit(sara, academy, assignment);

        // First grade with the current version (0) -> 200
        mockMvc.perform(post("/api/assignments/" + assignment + "/submissions/" + submissionId + "/grade")
                        .header("Authorization", bearer(tom))
                        .header("X-Academy-Id", String.valueOf(academy))
                        .contentType(APPLICATION_JSON)
                        .content("{\"score\":90,\"version\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("GRADED"))
                .andExpect(jsonPath("$.score").value(90));

        // Grading again with the now-stale version (0) -> 409 conflict
        mockMvc.perform(post("/api/assignments/" + assignment + "/submissions/" + submissionId + "/grade")
                        .header("Authorization", bearer(tom))
                        .header("X-Academy-Id", String.valueOf(academy))
                        .contentType(APPLICATION_JSON)
                        .content("{\"score\":50,\"version\":0}"))
                .andExpect(status().isConflict());
    }

    // ---------- helpers ----------

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String register(String email, String name) throws Exception {
        String body = """
                {"email":"%s","password":"password123","displayName":"%s"}
                """.formatted(email, name);
        String json = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("token").asText();
    }

    private long createAcademy(String token, String name, String slug) throws Exception {
        String body = """
                {"name":"%s","slug":"%s"}
                """.formatted(name, slug);
        String json = mockMvc.perform(post("/api/academies")
                        .header("Authorization", bearer(token))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private void addMember(String token, long academyId, String email, String role) throws Exception {
        String body = """
                {"email":"%s","role":"%s"}
                """.formatted(email, role);
        mockMvc.perform(post("/api/members")
                        .header("Authorization", bearer(token))
                        .header("X-Academy-Id", String.valueOf(academyId))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    private long createCourse(String token, long academyId, String title) throws Exception {
        String body = """
                {"title":"%s","subject":"General","term":"Fall 2026"}
                """.formatted(title);
        String json = mockMvc.perform(post("/api/courses")
                        .header("Authorization", bearer(token))
                        .header("X-Academy-Id", String.valueOf(academyId))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private void enroll(String token, long academyId, long courseId, String studentEmail) throws Exception {
        String body = """
                {"studentEmail":"%s"}
                """.formatted(studentEmail);
        mockMvc.perform(post("/api/courses/" + courseId + "/enrollments")
                        .header("Authorization", bearer(token))
                        .header("X-Academy-Id", String.valueOf(academyId))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    private long createAssignment(String token, long academyId, long courseId) throws Exception {
        String body = """
                {"title":"PS1","instructions":"Do it","dueDate":"2030-12-31T23:59:00Z","maxScore":100}
                """;
        String json = mockMvc.perform(post("/api/courses/" + courseId + "/assignments")
                        .header("Authorization", bearer(token))
                        .header("X-Academy-Id", String.valueOf(academyId))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private long submit(String token, long academyId, long assignmentId) throws Exception {
        String body = """
                {"content":"My answers"}
                """;
        String json = mockMvc.perform(post("/api/assignments/" + assignmentId + "/submissions")
                        .header("Authorization", bearer(token))
                        .header("X-Academy-Id", String.valueOf(academyId))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }
}