package com.example.agrilinkback.module.knowledge.mapper;

import com.example.agrilinkback.module.knowledge.entity.Discuss;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DiscussMapper {

    @Select("""
            select discuss_id, knowledge_id, own_name, content, create_time
            from tb_discuss
            where knowledge_id = #{knowledgeId}
            order by create_time desc
            """)
    List<Discuss> findByKnowledgeId(@Param("knowledgeId") Integer knowledgeId);

    @Select("select coalesce(max(discuss_id), 0) + 1 from tb_discuss")
    Integer nextId();

    @Insert("""
            insert into tb_discuss (discuss_id, knowledge_id, own_name, content, create_time)
            values (#{discuss.discussId}, #{discuss.knowledgeId}, #{discuss.ownName},
                    #{discuss.content}, #{discuss.createTime})
            """)
    int insert(@Param("discuss") Discuss discuss);

    @Delete("delete from tb_discuss where discuss_id = #{discussId}")
    int deleteByDiscussId(@Param("discussId") Integer discussId);

    @Delete("delete from tb_discuss where knowledge_id = #{knowledgeId}")
    int deleteByKnowledgeId(@Param("knowledgeId") Integer knowledgeId);
}
