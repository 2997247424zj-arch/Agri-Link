package com.example.agrilinkback.module.consultation.mapper;

import com.example.agrilinkback.module.consultation.entity.Question;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuestionMapper {

    @Select("""
            select id, expert_name, questioner, phone, plant_name, title, question, answer, status,
                   attachments as attachments_text
            from tb_question
            order by id desc
            """)
    List<Question> findAll();

    @Select("""
            select id, expert_name, questioner, phone, plant_name, title, question, answer, status,
                   attachments as attachments_text
            from tb_question
            where id = #{id}
            """)
    Question findById(@Param("id") Integer id);

    @Select("select coalesce(max(id), 0) + 1 from tb_question")
    Integer nextId();

    @Insert("""
            insert into tb_question (
                id, expert_name, questioner, phone, plant_name, title, question, answer, status, attachments
            ) values (
                #{question.id}, #{question.expertName}, #{question.questioner}, #{question.phone},
                #{question.plantName}, #{question.title}, #{question.question}, #{question.answer},
                #{question.status}, #{question.attachmentsText}
            )
            """)
    int insert(@Param("question") Question question);

    @Update("""
            update tb_question
            set answer = #{answer},
                status = #{status}
            where id = #{id}
            """)
    int updateAnswer(@Param("id") Integer id, @Param("answer") String answer, @Param("status") Integer status);

    @Delete("delete from tb_question where id = #{id}")
    int deleteById(@Param("id") Integer id);
}
