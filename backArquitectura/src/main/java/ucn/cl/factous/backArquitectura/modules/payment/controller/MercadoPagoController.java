package ucn.cl.factous.backArquitectura.modules.payment.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import io.github.cdimascio.dotenv.Dotenv;
import ucn.cl.factous.backArquitectura.modules.payment.dto.PaymentPreferenceDTO;

@RestController
@RequestMapping("/api/mercadopago")
@CrossOrigin(origins = { "${FRONT_URI}", "${FRONT_URI_ALTERNATIVE}" })
public class MercadoPagoController {

    @PostMapping("/create-preference")
    public String createPaymentPreference(@RequestBody PaymentPreferenceDTO paymentData) {
        try {
            // Cargar variables de entorno (desde sistema o .env si existe)
            String accessToken = getEnvironmentVariable("TEST_ACCESS_TOKEN", "MERCADOPAGO_ACCESS_TOKEN");
            String frontUri = getEnvironmentVariable("FRONT_URI", "FRONTEND_URL");

            if (accessToken == null || accessToken.isEmpty()) {
                return "Error: ACCESS_TOKEN no está configurado";
            }

            if (frontUri == null || frontUri.isEmpty()) {
                return "Error: FRONTEND_URL no está configurado";
            }

            // Configurar credencial
            MercadoPagoConfig.setAccessToken(accessToken);

            // Normalizar la URI del frontend (remover barra final si existe)
            String normalizedFrontUri = frontUri.endsWith("/") ? frontUri.substring(0, frontUri.length() - 1)
                    : frontUri;

            // Validar que la URI no esté vacía después de la normalización
            if (normalizedFrontUri.isEmpty()) {
                return "Error: FRONT_URI está vacío o solo contiene una barra";
            }

            // Construir URLs de retorno
            String successUrl = normalizedFrontUri + "/available-events";
            String pendingUrl = normalizedFrontUri + "/available-events";
            String failureUrl = normalizedFrontUri + "/available-events";

            System.out.println("URLs de retorno configuradas:");
            System.out.println("Success: " + successUrl);
            System.out.println("Pending: " + pendingUrl);
            System.out.println("Failure: " + failureUrl);

            // Detectar si estamos en modo desarrollo
            boolean isLocalhost = normalizedFrontUri.contains("localhost") || normalizedFrontUri.contains("127.0.0.1");
            System.out.println("Modo desarrollo (localhost): " + isLocalhost);

            // URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .pending(pendingUrl)
                    .failure(failureUrl)
                    .build();

            // Item de la preferencia
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("EVENT_" + paymentData.getEventId())
                    .title("Tickets para " + paymentData.getEventName())
                    .description("Compra de " + paymentData.getQuantity() + " ticket(s) para el evento: "
                            + paymentData.getEventName())
                    .categoryId("entertainment")
                    .quantity(paymentData.getQuantity())
                    .currencyId("CLP")
                    .unitPrice(BigDecimal.valueOf(paymentData.getUnitPrice())) // Precio seguro
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // Armar request de preferencia
            PreferenceRequest preferenceRequest;

            // Solo agregar autoReturn si no estamos en localhost (desarrollo)
            if (!isLocalhost) {
                preferenceRequest = PreferenceRequest.builder()
                        .items(items)
                        .backUrls(backUrls)
                        .autoReturn("all")
                        .externalReference("USER_" + paymentData.getUserId() + "_EVENT_" + paymentData.getEventId())
                        .build();
                System.out.println("Configuración: Producción con autoReturn");
            } else {
                // Para desarrollo local, no usar autoReturn
                preferenceRequest = PreferenceRequest.builder()
                        .items(items)
                        .backUrls(backUrls)
                        .externalReference("USER_" + paymentData.getUserId() + "_EVENT_" + paymentData.getEventId())
                        .build();
                System.out.println("Configuración: Desarrollo sin autoReturn");
            }

            // Crear preferencia con el SDK de MercadoPago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getId();  // Devolver solo el preferenceId

        } catch (MPApiException apiException) {
            System.err.println("MercadoPago API Error: " + apiException.getApiResponse().getContent());
            return "Error creating payment preference: " + apiException.getApiResponse().getContent();
        } catch (MPException mpException) {
            System.err.println("MercadoPago SDK Error: " + mpException.getMessage());
            return "Error creating payment preference: " + mpException.getMessage();
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            return "Error creating payment preference: " + e.getMessage();
        }
    }

    /**
     * Método helper para obtener variables de entorno
     * Intenta obtener de variables del sistema primero, luego de .env si existe
     */
    private String getEnvironmentVariable(String devName, String prodName) {
        // Primero intentar variables de entorno del sistema (producción)
        String value = System.getenv(prodName);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        value = System.getenv(devName);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        // Si no están en el sistema, intentar cargar desde .env (desarrollo)
        try {
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
            
            value = dotenv.get(prodName);
            if (value != null && !value.isEmpty()) {
                return value;
            }
            
            return dotenv.get(devName);
        } catch (Exception e) {
            // Si no se puede cargar .env, simplemente retornar null
            System.out.println("No se pudo cargar archivo .env: " + e.getMessage());
            return null;
        }
    }
}
