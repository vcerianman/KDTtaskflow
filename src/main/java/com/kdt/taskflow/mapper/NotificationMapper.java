package com.kdt.taskflow.mapper;

import com.kdt.taskflow.domain.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {

    int insert(Notification notification);

    Optional<Notification> findById(@Param("id") Long id);

    Optional<Notification> findDuplicate(@Param("recipientId") Long recipientId,
                                        @Param("type") String type,
                                        @Param("message") String message,
                                        @Param("actorId") Long actorId);

    int refreshDuplicate(@Param("id") Long id,
                         @Param("link") String link);

    List<Notification> findByRecipientId(@Param("recipientId") Long recipientId,
                                        @Param("unreadOnly") Boolean unreadOnly,
                                        @Param("limit") Integer limit);

    long countUnreadByRecipientId(@Param("recipientId") Long recipientId);

    int updateDynamic(@Param("notificationIds") List<Long> notificationIds,
                      @Param("recipientId") Long recipientId,
                      @Param("isRead") Boolean isRead,
                      @Param("isSeen") Boolean isSeen,
                      @Param("seenAt") OffsetDateTime seenAt,
                      @Param("message") String message,
                      @Param("link") String link);

    int updateSingle(Notification notification);

    int deleteDynamic(@Param("notificationIds") List<Long> notificationIds,
                      @Param("recipientId") Long recipientId);

    int deleteById(@Param("id") Long id);

    int deleteExpiredSeenNotifications(@Param("cutoff") OffsetDateTime cutoff);
}
