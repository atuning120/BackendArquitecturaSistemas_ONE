package ucn.cl.factous.backArquitectura.modules.notification.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ucn.cl.factous.backArquitectura.modules.event.repository.EventRepository;
import ucn.cl.factous.backArquitectura.modules.notification.dto.NotificationDTO;
import ucn.cl.factous.backArquitectura.modules.notification.entity.Notification;
import ucn.cl.factous.backArquitectura.modules.notification.repository.NotificationRepository;
import ucn.cl.factous.backArquitectura.shared.repository.TicketRepository;

@Service
public class NotificationService {

    @Autowired
    @Qualifier("brokerMessagingTemplate")
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notifications.addAll(notificationRepository.findByUserIdIsNull());
        return notifications.stream().map(this::convertToDTO).toList();
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null && !notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void sendPurchaseSuccessNotification(Long userId, Long eventId) {
        try {
            if (messagingTemplate == null) {
                System.out.println("SimpMessagingTemplate no disponible, saltando notificación WebSocket");
                return;
            }
            String eventTitle = eventRepository.findTitleById(eventId);
            if (eventTitle == null) {
                eventTitle = "Evento Desconocido";
            }
            
            String title = "Entrada Comprada";
            String message = "Tu compra para el evento " + eventTitle + " fue realizada con éxito";
            
            Notification notification = new Notification("purchase_success", title, message, eventId, userId);
            notificationRepository.save(notification);
            // enviar como string un JSON con el dto de usuario
            messagingTemplate.convertAndSendToUser(("{\"user\":" + userId + "}"), "/queue/notifications", convertToDTO(notification));
        } catch (Exception e) {
            System.err.println("Error enviando notificación general WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendEventDeletedNotification(Long eventId) {
        try {
            if (messagingTemplate == null) {
                System.out.println("SimpMessagingTemplate no disponible, saltando notificación WebSocket");
                return;
            }
            String eventTitle = eventRepository.findTitleById(eventId);
            if (eventTitle == null) {
                eventTitle = "Evento Desconocido";
            }

            String type = "event_deleted";
            String title = "Evento Cancelado";
            String message = "El evento \"" + eventTitle + "\" ha sido cancelado por el organizador.";

            List<Long> userIds = ticketRepository.findDistinctUserIdsByEventId(eventId);

            if (userIds.isEmpty()) {
                System.out.println("Evento " + eventTitle + " sin tickets vendidos");
                return;
            }

            for (Long userId : userIds) {
                Notification notification = new Notification(type, title, message, eventId, userId);
                notificationRepository.save(notification);

                try {
                    messagingTemplate.convertAndSendToUser(("{\"user\":" + userId + "}"), "/queue/notifications", convertToDTO(notification));
                } catch (Exception e) {
                    System.err.println("El envío WS para usuario " + userId + " falló");
                }
            }
        } catch (Exception e) {
            System.err.println("Error enviando notificación general WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendOrganizerMessageNotification(Long eventId, String customMessage) {
        try {
            if (messagingTemplate == null) {
                System.out.println("SimpMessagingTemplate no disponible, saltando notificación WebSocket");
                return;
            }

            String eventTitle = eventRepository.findTitleById(eventId);
            if (eventTitle == null) {
                eventTitle = "Evento Desconocido";
            }
            String type = "organizer_message";
            String title = eventTitle + " - Mensaje del Organizador";
            
            List<Long> userIds = ticketRepository.findDistinctUserIdsByEventId(eventId);

            if (userIds.isEmpty()) {
                System.out.println("Evento " + eventTitle + " sin tickets vendidos");
                return;
            }

            for (Long userId : userIds) {
                Notification notification = new Notification(type, title, customMessage, eventId, userId);
                notificationRepository.save(notification);

                try {
                    messagingTemplate.convertAndSendToUser(("{\"user\":" + userId + "}"), "/queue/notifications", convertToDTO(notification));
                } catch (Exception e) {
                    System.err.println("El envío WS para usuario " + userId + " falló");
                }
            }
        } catch (Exception e) {
            System.err.println("Error enviando notificación general WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendGeneralNotification(String type, String title, String message) {
        try {
            if (messagingTemplate == null) {
                System.out.println("SimpMessagingTemplate no disponible, saltando notificación WebSocket");
                return;
            }

            Notification notification = new Notification(type, title, message, null, null);
            notificationRepository.save(notification);
            messagingTemplate.convertAndSend("/topic/notifications", convertToDTO(notification));
            System.out.println("Notificación general enviada: " + title);
        } catch (Exception e) {
            System.err.println("Error enviando notificación general WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(notification.getType(), notification.getTitle(), notification.getMessage(), notification.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), notification.getEventId(), notification.getUserId(), notification.isRead());
    }
}