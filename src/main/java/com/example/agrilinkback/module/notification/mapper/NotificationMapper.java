package com.example.agrilinkback.module.notification.mapper;

import com.example.agrilinkback.module.notification.entity.Notification;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 通知数据访问层，操作 tb_notification 表。
 */
@Mapper
public interface NotificationMapper {

    @Select("""
            select notification_id, target_user, title, content, type, is_read, creator, create_time
            from tb_notification
            where target_user = #{userName} or target_user is null
            order by create_time desc
            """)
    List<Notification> findByTargetUser(@Param("userName") String userName);

    @Select("""
            select count(*)
            from tb_notification
            where (target_user = #{userName} or target_user is null)
              and is_read = 0
            """)
    int countUnread(@Param("userName") String userName);

    @Insert("""
            insert into tb_notification (target_user, title, content, type, creator, create_time)
            values (#{n.targetUser}, #{n.title}, #{n.content}, #{n.type}, #{n.creator}, #{n.createTime})
            """)
    int insert(@Param("n") Notification n);

    @Update("update tb_notification set is_read = 1 where notification_id = #{id}")
    int markAsRead(@Param("id") Integer id);

    @Update("""
            update tb_notification set is_read = 1
            where (target_user = #{userName} or target_user is null) and is_read = 0
            """)
    int markAllAsRead(@Param("userName") String userName);
}
