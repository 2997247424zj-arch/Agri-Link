package com.example.agrilinkback.module.finance.mapper;

import com.example.agrilinkback.module.finance.entity.Bank;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BankMapper {

    @Select("""
            select bank_id, bank_name, introduce, bank_phone, money, rate, repayment
            from tb_bank
            order by bank_id desc
            """)
    List<Bank> findAll();

    @Select("""
            select bank_id, bank_name, introduce, bank_phone, money, rate, repayment
            from tb_bank
            where bank_id = #{bankId}
            """)
    Bank findByBankId(@Param("bankId") Integer bankId);

    @Select("""
            select bank_id, bank_name, introduce, bank_phone, money, rate, repayment
            from tb_bank
            where money >= #{amount}
            order by rate asc, money desc
            """)
    List<Bank> findMatchedBanks(@Param("amount") java.math.BigDecimal amount);

    @Select("select coalesce(max(bank_id), 0) + 1 from tb_bank")
    Integer nextId();

    @Insert("""
            insert into tb_bank (bank_id, bank_name, introduce, bank_phone, money, rate, repayment)
            values (#{bank.bankId}, #{bank.bankName}, #{bank.introduce}, #{bank.bankPhone},
                    #{bank.money}, #{bank.rate}, #{bank.repayment})
            """)
    int insert(@Param("bank") Bank bank);

    @Update("""
            update tb_bank
            set bank_name = #{bank.bankName},
                introduce = #{bank.introduce},
                bank_phone = #{bank.bankPhone},
                money = #{bank.money},
                rate = #{bank.rate},
                repayment = #{bank.repayment}
            where bank_id = #{bank.bankId}
            """)
    int update(@Param("bank") Bank bank);

    @Delete("delete from tb_bank where bank_id = #{bankId}")
    int deleteByBankId(@Param("bankId") Integer bankId);
}
