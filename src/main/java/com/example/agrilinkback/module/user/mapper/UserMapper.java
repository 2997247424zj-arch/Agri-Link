package com.example.agrilinkback.module.user.mapper;

import com.example.agrilinkback.module.user.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("""
            select user_name, password, nick_name, phone, identity_num, address, role,
                   create_time, update_time, integral, credit, avatar, real_name, enabled
            from tb_user
            order by create_time desc
            """)
    List<User> findAll();

    @Select("""
            select user_name, password, nick_name, phone, identity_num, address, role,
                   create_time, update_time, integral, credit, avatar, real_name, enabled
            from tb_user
            where user_name = #{userName}
            """)
    User findByUserName(@Param("userName") String userName);

    @Insert("""
            insert into tb_user (
                user_name, password, nick_name, phone, identity_num, address, role,
                create_time, update_time, integral, credit, avatar, real_name, enabled
            ) values (
                #{user.userName}, #{user.password}, #{user.nickName}, #{user.phone},
                #{user.identityNum}, #{user.address}, #{user.role}, #{user.createTime},
                #{user.updateTime}, #{user.integral}, #{user.credit}, #{user.avatar}, #{user.realName},
                #{user.enabled}
            )
            """)
    int insert(@Param("user") User user);

    @Update("""
            update tb_user
            set password = #{user.password},
                nick_name = #{user.nickName},
                phone = #{user.phone},
                identity_num = #{user.identityNum},
                address = #{user.address},
                role = #{user.role},
                update_time = #{user.updateTime},
                avatar = #{user.avatar},
                real_name = #{user.realName}
            where user_name = #{user.userName}
            """)
    int update(@Param("user") User user);

    @Delete("delete from tb_user where user_name = #{userName}")
    int deleteByUserName(@Param("userName") String userName);

    @Update("""
            update tb_user
            set password = #{password},
                update_time = current_timestamp
            where user_name = #{userName}
            """)
    int updatePassword(@Param("userName") String userName, @Param("password") String password);

    @Update("""
            update tb_user
            set role = #{role},
                update_time = current_timestamp
            where user_name = #{userName}
            """)
    int updateRole(@Param("userName") String userName, @Param("role") String role);

    @Update("""
            update tb_user
            set enabled = #{enabled},
                update_time = current_timestamp
            where user_name = #{userName}
            """)
    int updateEnabled(@Param("userName") String userName, @Param("enabled") boolean enabled);
}
