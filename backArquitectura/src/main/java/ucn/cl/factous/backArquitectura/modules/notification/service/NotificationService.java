package ucn.cl.factous.backArquitectura.modules.notification.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ucn.cl.factous.backArquitectura.modules.notification.dto.NotificationDTO;

@Service
public class NotificationService {

    @Autowired
    @Qualifier("brokerMessagingTemplate")
    private SimpMessagingTemplate messagingTemplate;

    public void sendEventDeletedNotification(Long eventId, String eventTitle) {
        try {
            System.out.println("=== ENVIANDO NOTIFICACIÓN DE EVENTO ELIMINADO ===");
            System.out.println("EventId: " + eventId + ", EventTitle: " + eventTitle);

            NotificationDTO notification = new NotificationDTO();
            notification.setType("event_deleted");
            notification.setTitle("Evento Cancelado");
            notification.setMessage("El evento \"" + eventTitle + "\" ha sido cancelado por el organizador");
            notification.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            notification.setEventId(eventId);
            notification.setEventTitle(eventTitle);

            System.out.println("📤 Enviando a canal: /topic/notifications");
            
            // Envía la notificación a todos los clientes conectados al canal de notificaciones
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            
            System.out.println("✅ Notificación enviada exitosamente");
            System.out.println("=== FIN NOTIFICACIÓN ===");
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendGeneralNotification(String type, String title, String message) {
        try {
            if (messagingTemplate == null) {
                System.out.println("SimpMessagingTemplate no disponible, saltando notificación WebSocket");
                return;
            }

            NotificationDTO notification = new NotificationDTO();
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            messagingTemplate.convertAndSend("/topic/notifications", notification);
            System.out.println("Notificación general enviada: " + title);
        } catch (Exception e) {
            System.err.println("Error enviando notificación general WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}