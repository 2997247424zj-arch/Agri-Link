package com.example.agrilinkback.module.user.mapper;

import com.example.agrilinkback.module.user.entity.Address;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AddressMapper {

    @Select("""
            select id, own_name, consignee, phone, address_detail, is_default
            from tb_address
            order by is_default desc, id desc
            """)
    List<Address> findAll();

    @Select("""
            select id, own_name, consignee, phone, address_detail, is_default
            from tb_address
            where id = #{id}
            """)
    Address findById(@Param("id") Integer id);

    @Select("""
            select id, own_name, consignee, phone, address_detail, is_default
            from tb_address
            where own_name = #{ownName}
            order by is_default desc, id desc
            """)
    List<Address> findByOwner(@Param("ownName") String ownName);

    @Select("select coalesce(max(id), 0) + 1 from tb_address")
    Integer nextId();

    @Insert("""
            insert into tb_address (id, own_name, consignee, phone, address_detail, is_default)
            values (#{address.id}, #{address.ownName}, #{address.consignee}, #{address.phone},
                    #{address.addressDetail}, #{address.isDefault})
            """)
    int insert(@Param("address") Address address);

    @Update("""
            update tb_address
            set consignee = #{address.consignee},
                phone = #{address.phone},
                address_detail = #{address.addressDetail},
                is_default = #{address.isDefault}
            where id = #{address.id}
            """)
    int update(@Param("address") Address address);

    @Update("""
            update tb_address
            set is_default = 0
            where own_name = #{ownName}
            """)
    int clearDefaultByOwner(@Param("ownName") String ownName);

    @Delete("delete from tb_address where id = #{id}")
    int deleteById(@Param("id") Integer id);
}
