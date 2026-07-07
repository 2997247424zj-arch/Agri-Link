package com.example.agrilinkback.module.consultation.mapper;

import com.example.agrilinkback.module.consultation.entity.Reserve;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ReserveMapper {

    @Select("""
            select id, expert_name, questioner, area, address, plant_name, soil_condition,
                   plant_condition, plant_detail, phone, message, answer, status
            from tb_reserve
            order by id desc
            """)
    List<Reserve> findAll();

    @Select("""
            select id, expert_name, questioner, area, address, plant_name, soil_condition,
                   plant_condition, plant_detail, phone, message, answer, status
            from tb_reserve
            where id = #{id}
            """)
    Reserve findById(@Param("id") Integer id);

    @Select("select coalesce(max(id), 0) + 1 from tb_reserve")
    Integer nextId();

    @Insert("""
            insert into tb_reserve (
                id, expert_name, questioner, area, address, plant_name, soil_condition,
                plant_condition, plant_detail, phone, message, answer, status
            ) values (
                #{reserve.id}, #{reserve.expertName}, #{reserve.questioner}, #{reserve.area},
                #{reserve.address}, #{reserve.plantName}, #{reserve.soilCondition},
                #{reserve.plantCondition}, #{reserve.plantDetail}, #{reserve.phone},
                #{reserve.message}, #{reserve.answer}, #{reserve.status}
            )
            """)
    int insert(@Param("reserve") Reserve reserve);

    @Update("""
            update tb_reserve
            set answer = #{answer},
                status = #{status}
            where id = #{id}
            """)
    int updateAnswer(@Param("id") Integer id, @Param("answer") String answer, @Param("status") Integer status);

    @Delete("delete from tb_reserve where id = #{id}")
    int deleteById(@Param("id") Integer id);
}
