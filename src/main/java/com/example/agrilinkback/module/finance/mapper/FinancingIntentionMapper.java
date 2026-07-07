package com.example.agrilinkback.module.finance.mapper;

import com.example.agrilinkback.module.finance.entity.FinancingIntention;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FinancingIntentionMapper {

    @Select("""
            select id, user_name, real_name, address, amount, application, item,
                   repayment_period, area, phone, create_time, update_time
            from tb_financing_intention
            order by update_time desc
            """)
    List<FinancingIntention> findAll();

    @Select("""
            select id, user_name, real_name, address, amount, application, item,
                   repayment_period, area, phone, create_time, update_time
            from tb_financing_intention
            where id = #{id}
            """)
    FinancingIntention findById(@Param("id") Integer id);

    @Select("""
            select id, user_name, real_name, address, amount, application, item,
                   repayment_period, area, phone, create_time, update_time
            from tb_financing_intention
            where amount <= #{maxAmount}
            order by amount desc, update_time desc
            """)
    List<FinancingIntention> findMatchedIntentions(@Param("maxAmount") java.math.BigDecimal maxAmount);

    @Select("select coalesce(max(id), 0) + 1 from tb_financing_intention")
    Integer nextId();

    @Insert("""
            insert into tb_financing_intention (
                id, user_name, real_name, address, amount, application, item,
                repayment_period, area, phone, create_time, update_time
            ) values (
                #{intention.id}, #{intention.userName}, #{intention.realName}, #{intention.address},
                #{intention.amount}, #{intention.application}, #{intention.item},
                #{intention.repaymentPeriod}, #{intention.area}, #{intention.phone},
                #{intention.createTime}, #{intention.updateTime}
            )
            """)
    int insert(@Param("intention") FinancingIntention intention);

    @Update("""
            update tb_financing_intention
            set real_name = #{intention.realName},
                address = #{intention.address},
                amount = #{intention.amount},
                application = #{intention.application},
                item = #{intention.item},
                repayment_period = #{intention.repaymentPeriod},
                area = #{intention.area},
                phone = #{intention.phone},
                update_time = #{intention.updateTime}
            where id = #{intention.id}
            """)
    int update(@Param("intention") FinancingIntention intention);

    @Delete("delete from tb_financing_intention where id = #{id}")
    int deleteById(@Param("id") Integer id);
}
