package com.example.agrilinkback.module.trade.mapper;

import com.example.agrilinkback.module.trade.entity.ShoppingCart;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShoppingCartMapper {

    @Select("""
            select shopping_id, order_id, count, own_name, create_time, update_time
            from tb_shoppingcart
            order by update_time desc
            """)
    List<ShoppingCart> findAll();

    @Select("""
            select shopping_id, order_id, count, own_name, create_time, update_time
            from tb_shoppingcart
            where shopping_id = #{shoppingId}
            """)
    ShoppingCart findByShoppingId(@Param("shoppingId") Integer shoppingId);

    @Select("""
            select shopping_id, order_id, count, own_name, create_time, update_time
            from tb_shoppingcart
            where own_name = #{ownName}
            order by update_time desc
            """)
    List<ShoppingCart> findByOwner(@Param("ownName") String ownName);

    @Select("select coalesce(max(shopping_id), 0) + 1 from tb_shoppingcart")
    Integer nextId();

    @Insert("""
            insert into tb_shoppingcart (shopping_id, order_id, count, own_name, create_time, update_time)
            values (#{shoppingCart.shoppingId}, #{shoppingCart.orderId}, #{shoppingCart.count},
                    #{shoppingCart.ownName}, #{shoppingCart.createTime}, #{shoppingCart.updateTime})
            """)
    int insert(@Param("shoppingCart") ShoppingCart shoppingCart);

    @Update("""
            update tb_shoppingcart
            set count = #{count},
                update_time = current_timestamp
            where shopping_id = #{shoppingId}
            """)
    int updateCount(@Param("shoppingId") Integer shoppingId, @Param("count") Integer count);

    @Delete("delete from tb_shoppingcart where shopping_id = #{shoppingId}")
    int deleteByShoppingId(@Param("shoppingId") Integer shoppingId);
}
