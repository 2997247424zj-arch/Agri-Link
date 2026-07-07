package com.example.agrilinkback.module.finance.mapper;

import com.example.agrilinkback.module.finance.entity.Finance;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FinanceMapper {

    @Select("""
            select finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
                   money, rate, repayment, create_time, update_time, combination_name1,
                   combination_phone1, combination_idnum1, combination_name2,
                   combination_phone2, combination_idnum2, file_info
            from tb_finance
            order by update_time desc
            """)
    List<Finance> findAll();

    @Select("""
            select finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
                   money, rate, repayment, create_time, update_time, combination_name1,
                   combination_phone1, combination_idnum1, combination_name2,
                   combination_phone2, combination_idnum2, file_info
            from tb_finance
            where finance_id = #{financeId}
            """)
    Finance findByFinanceId(@Param("financeId") Integer financeId);

    @Select("select coalesce(max(finance_id), 0) + 1 from tb_finance")
    Integer nextId();

    @Insert("""
            insert into tb_finance (
                finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
                money, rate, repayment, create_time, update_time, combination_name1,
                combination_phone1, combination_idnum1, combination_name2, combination_phone2,
                combination_idnum2, file_info
            ) values (
                #{finance.financeId}, #{finance.bankId}, #{finance.ownName}, #{finance.realName},
                #{finance.phone}, #{finance.idNum}, #{finance.status}, #{finance.remark},
                #{finance.money}, #{finance.rate}, #{finance.repayment}, #{finance.createTime},
                #{finance.updateTime}, #{finance.combinationName1}, #{finance.combinationPhone1},
                #{finance.combinationIdnum1}, #{finance.combinationName2}, #{finance.combinationPhone2},
                #{finance.combinationIdnum2}, #{finance.fileInfo}
            )
            """)
    int insert(@Param("finance") Finance finance);

    @Update("""
            update tb_finance
            set status = #{status},
                remark = #{remark},
                update_time = current_timestamp
            where finance_id = #{financeId}
            """)
    int updateStatus(
            @Param("financeId") Integer financeId,
            @Param("status") Integer status,
            @Param("remark") String remark
    );

    @Delete("delete from tb_finance where finance_id = #{financeId}")
    int deleteByFinanceId(@Param("financeId") Integer financeId);
}
