package io.farmx.event;

import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {

    private final Notification notification;
    private final UserEntity recipient;

    public NotificationEvent(Object source, Notification notification, UserEntity recipient) {
        super(source);
        this.notification = notification;
        this.recipient = recipient;
    }

    public Notification getNotification() {
        return notification;
    }

    public UserEntity getRecipient() {
        return recipient;
    }
}
