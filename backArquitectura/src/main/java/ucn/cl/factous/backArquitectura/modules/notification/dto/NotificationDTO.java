package ucn.cl.factous.backArquitectura.modules.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String type;
    private String title;
    private String message;
    private String timestamp;
    private Long eventId;
    private String eventTitle;
}