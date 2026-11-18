package dev.lambdajedi.catalog.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Test
    void getReturnsProduct() throws Exception {
        when(service.get(1L)).thenReturn(new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 10));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("KB-001"))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    void getUnknownProductReturns404() throws Exception {
        when(service.get(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithInvalidBodyReturns400() throws Exception {
        String invalid = "{\"sku\":\"\",\"name\":\"\",\"price\":0,\"stockQuantity\":-1}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveWithInsufficientStockReturns409() throws Exception {
        when(service.reserve(1L, 50)).thenThrow(new InsufficientStockException("KB-001", 50, 10));

        mockMvc.perform(post("/api/products/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":50}"))
                .andExpect(status().isConflict());
    }
}
