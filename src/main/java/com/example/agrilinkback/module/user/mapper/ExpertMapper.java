package com.example.agrilinkback.module.user.mapper;

import com.example.agrilinkback.module.user.entity.Expert;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ExpertMapper {

    @Select("""
            select user_name, real_name, phone, profession, position, belong
            from tb_expert
            order by user_name
            """)
    List<Expert> findAll();

    @Select("""
            select user_name, real_name, phone, profession, position, belong
            from tb_expert
            where user_name = #{userName}
            """)
    Expert findByUserName(@Param("userName") String userName);

    @Insert("""
            insert into tb_expert (user_name, real_name, phone, profession, position, belong)
            values (#{expert.userName}, #{expert.realName}, #{expert.phone},
                    #{expert.profession}, #{expert.position}, #{expert.belong})
            """)
    int insert(@Param("expert") Expert expert);

    @Update("""
            update tb_expert
            set real_name = #{expert.realName},
                phone = #{expert.phone},
                profession = #{expert.profession},
                position = #{expert.position},
                belong = #{expert.belong}
            where user_name = #{expert.userName}
            """)
    int update(@Param("expert") Expert expert);

    @Delete("delete from tb_expert where user_name = #{userName}")
    int deleteByUserName(@Param("userName") String userName);
}
