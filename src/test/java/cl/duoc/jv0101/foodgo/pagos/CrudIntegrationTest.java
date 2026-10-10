package cl.duoc.jv0101.foodgo.pagos;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String unique(String json) { return json.replace("TEST20261009", UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()); }

    private long createParent() throws Exception {
        String result = mvc.perform(post("/api/pagos").contentType("application/json")
                .content(unique("""
{"pedido":"PED-TEST20261009","metodo":"TARJETA","monto":19980}
"""))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    private long createChild(String nested) throws Exception {
        String result = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"tipo":"COBRO","monto":19980,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
""")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        long id = createParent();
        String nested = "/api/pagos/" + id + "/transacciones";
        long childId = createChild(nested);
        mvc.perform(get("/api/pagos/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.transacciones[0].id").value(childId));
        mvc.perform(get("/api/pagos")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/transacciones/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/pagos/" + id).contentType("application/json").content(unique("""
{"pedido":"PED-TEST20261009","metodo":"TRANSFERENCIA","monto":19980}
"""))).andExpect(status().isOk());
        mvc.perform(put("/api/transacciones/" + childId).contentType("application/json").content("""
{"tipo":"COBRO","monto":19980,"estado":"APROBADA","referencia":"FG-TEST20261009-02"}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/transacciones/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/transacciones/" + childId)).andExpect(status().isNotFound());
        long cascadeId = createChild(nested);
        mvc.perform(delete("/api/pagos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/pagos/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/transacciones/" + cascadeId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/pagos").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/pagos/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/pagos").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors").isNotEmpty());
        mvc.perform(get("/api/pagos/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/pagos/9223372036854775807/transacciones").contentType("application/json")
                .content("""
{"tipo":"COBRO","monto":19980,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
""")).andExpect(status().isNotFound());
        var longBody = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree("""
{"pedido":"PED-TEST20261009","metodo":"TARJETA","monto":19980}
""");
        longBody.put("pedido", "X".repeat(300));
        mvc.perform(post("/api/pagos").contentType("application/json").content(longBody.toString())).andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
            Arguments.of("Método inválido", "parent", """
{"pedido":"PED-TEST20261009","metodo":"CRIPTO","monto":19980}
""", "metodo"),
            Arguments.of("Monto negativo", "parent", """
{"pedido":"PED-TEST20261009","metodo":"TARJETA","monto":-100}
""", "monto"),
            Arguments.of("Monto fraccionario", "parent", """
{"pedido":"PED-TEST20261009","metodo":"TARJETA","monto":9990.5}
""", "monto"),
            Arguments.of("Tipo inválido", "child", """
{"tipo":"PORCENTAJE","monto":19980,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
""", "tipo"),
            Arguments.of("Estado inválido", "child", """
{"tipo":"COBRO","monto":19980,"estado":"CREADO","referencia":"FG-TEST20261009-01"}
""", "estado"),
            Arguments.of("Transacción mayor que pago", "child", """
{"tipo":"COBRO","monto":29970,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
""", "monto")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidInputs")
    void businessValidationReturns400WithField(String name, String target, String body, String field) throws Exception {
        if ("parent".equals(target)) {
            mvc.perform(post("/api/pagos").contentType("application/json").content(unique(body)))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
        } else {
            long id = createParent();
            mvc.perform(post("/api/pagos/" + id + "/transacciones").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
            mvc.perform(delete("/api/pagos/" + id)).andExpect(status().isNoContent());
        }
    }

    @Test
    void noPermiteSobreCobrosNiReembolsosSinCobroYProtegeLaActualizacion() throws Exception {
        long id = createParent();
        String nested = "/api/pagos/" + id + "/transacciones";
        mvc.perform(post(nested).contentType("application/json").content("""
{"tipo":"REEMBOLSO","monto":2000,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
"""))
                .andExpect(status().isBadRequest());
        long cobro = createChild(nested);
        mvc.perform(post(nested).contentType("application/json").content("""
{"tipo":"COBRO","monto":1000,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
""")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/pagos/" + id).contentType("application/json").content("""
{"pedido":"PED-TEST20261009","metodo":"TRANSFERENCIA","monto":1000}
""")).andExpect(status().isBadRequest());
        String refund = mvc.perform(post(nested).contentType("application/json").content("""
{"tipo":"REEMBOLSO","monto":2000,"estado":"APROBADA","referencia":"FG-TEST20261009-01"}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long reembolso = mapper.readTree(refund).get("id").asLong();
        mvc.perform(delete("/api/transacciones/" + cobro)).andExpect(status().isBadRequest());
        mvc.perform(put("/api/transacciones/" + cobro).contentType("application/json").content("""
{"tipo":"COBRO","monto":19980,"estado":"RECHAZADA","referencia":"FG-TEST20261009-01"}
""")).andExpect(status().isBadRequest());
        mvc.perform(delete("/api/transacciones/" + reembolso)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/transacciones/" + cobro)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/pagos/" + id)).andExpect(status().isNoContent());
    }

}
