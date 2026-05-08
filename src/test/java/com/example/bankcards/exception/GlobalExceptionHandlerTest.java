package com.example.bankcards.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handlesNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("missing"));
    }

    @Test
    void handlesBusinessException() throws Exception {
        mockMvc.perform(get("/test/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("business"));
    }

    @Test
    void handlesMethodArgumentNotValid() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details.value").exists());
    }

    @Test
    void handlesHttpMessageNotReadable() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed request"));
    }

    @Test
    void handlesMethodArgumentTypeMismatch() throws Exception {
        mockMvc.perform(get("/test/type/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed request"));
    }

    @Test
    void handlesMissingServletRequestParameter() throws Exception {
        mockMvc.perform(get("/test/required-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed request"));
    }

    @Test
    void handlesDataIntegrityViolation() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Data integrity violation"));
    }

    @Test
    void handlesAccessDeniedException() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    void handlesUnexpectedException() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/not-found")
        public void notFound() {
            throw new NotFoundException("missing");
        }

        @GetMapping("/business")
        public void business() {
            throw new BusinessException("business");
        }

        @PostMapping("/validate")
        public void validate(@RequestBody @Valid ValidationRequest request) {
            // validation happens before method body
        }

        @GetMapping("/type/{id}")
        public void type(@PathVariable Long id) {
        }

        @GetMapping("/required-param")
        public void requiredParam(@RequestParam String required) {
        }

        @GetMapping("/conflict")
        public void conflict() {
            throw new DataIntegrityViolationException("duplicate");
        }

        @GetMapping("/forbidden")
        public void forbidden() {
            throw new AccessDeniedException("denied");
        }

        @GetMapping("/unexpected")
        public void unexpected() {
            throw new RuntimeException("boom");
        }
    }

    record ValidationRequest(@NotBlank String value) {
    }
}
