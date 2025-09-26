package ucn.cl.factous.backArquitectura.modules.notification.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ucn.cl.factous.backArquitectura.modules.notification.dto.NotificationDTO;
import ucn.cl.factous.backArquitectura.modules.notification.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = {"${FRONT_URI}", "${FRONT_URI_ALTERNATIVE}"})
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
/* 
    // Para notificaciones no leidas    
    @GetMapping("/unread")
    // Verificar como el front manda id
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
    } */
/* 
    // para marcar notificacion como leida
    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable("id") Long notificationId) {
    }
 */

    @PostMapping("/purchase-success")
    public ResponseEntity<String> sendPurchaseSuccess(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = ((Number) payload.get("userId")).longValue();
            Long eventId = ((Number) payload.get("eventId")).longValue();
            notificationService.sendPurchaseSuccessNotification(userId, eventId);
            return ResponseEntity.ok("Notificación de compra enviada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la notificación: " + e.getMessage());
        }
    }

    @PostMapping("/event-deleted")
    public ResponseEntity<String> sendEventDeleted(@RequestBody Map<String, Object> payload) {
        try {
            Long eventId = ((Number) payload.get("eventId")).longValue();
            notificationService.sendEventDeletedNotification(eventId);
            return ResponseEntity.ok("Notificación de evento eliminado enviada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la notificación: " + e.getMessage());
        }
    }

    @PostMapping("/organizer-message")
    public ResponseEntity<String> sendOrganizerMessage(@RequestBody Map<String, Object> payload) {
        try {
            Long eventId = ((Number) payload.get("eventId")).longValue();
            String customMessage = (String) payload.get("customMessage");
            notificationService.sendOrganizerMessageNotification(eventId, customMessage);
            return ResponseEntity.ok("Notificación de mensaje de organizador enviada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la notificación: " + e.getMessage());
        }
    }

    @PostMapping("/general")
    public ResponseEntity<String> sendGeneral(@RequestBody NotificationDTO dto) {
        try {
            notificationService.sendGeneralNotification(dto.getType(), dto.getTitle(), dto.getMessage());
            return ResponseEntity.ok("Notificación general enviada y persistida.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la notificación general: " + e.getMessage());
        }
    }
}