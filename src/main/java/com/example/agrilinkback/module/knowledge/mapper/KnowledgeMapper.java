package com.example.agrilinkback.module.knowledge.mapper;

import com.example.agrilinkback.module.knowledge.entity.Knowledge;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface KnowledgeMapper {

    @Select("""
            select knowledge_id, title, content, pic_path, own_name, coalesce(status, 1) as status, create_time, update_time
            from tb_knowledge
            order by update_time desc
            """)
    List<Knowledge> findAll();

    @Select("""
            select knowledge_id, title, content, pic_path, own_name, coalesce(status, 1) as status, create_time, update_time
            from tb_knowledge
            where knowledge_id = #{knowledgeId}
            """)
    Knowledge findByKnowledgeId(@Param("knowledgeId") Integer knowledgeId);

    @Select("select coalesce(max(knowledge_id), 0) + 1 from tb_knowledge")
    Integer nextId();

    @Insert("""
            insert into tb_knowledge (knowledge_id, title, content, pic_path, own_name, status, create_time, update_time)
            values (#{knowledge.knowledgeId}, #{knowledge.title}, #{knowledge.content}, #{knowledge.picPath},
                    #{knowledge.ownName}, coalesce(#{knowledge.status}, 1), #{knowledge.createTime}, #{knowledge.updateTime})
            """)
    int insert(@Param("knowledge") Knowledge knowledge);

    @Update("""
            update tb_knowledge
            set title = #{knowledge.title},
                content = #{knowledge.content},
                pic_path = #{knowledge.picPath},
                update_time = #{knowledge.updateTime}
            where knowledge_id = #{knowledge.knowledgeId}
            """)
    int update(@Param("knowledge") Knowledge knowledge);

    @Update("""
            update tb_knowledge
            set status = #{status},
                update_time = #{updateTime}
            where knowledge_id = #{knowledgeId}
            """)
    int updateStatus(
            @Param("knowledgeId") Integer knowledgeId,
            @Param("status") Integer status,
            @Param("updateTime") java.time.LocalDateTime updateTime
    );

    @Delete("delete from tb_knowledge where knowledge_id = #{knowledgeId}")
    int deleteByKnowledgeId(@Param("knowledgeId") Integer knowledgeId);
}
