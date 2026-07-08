package com.example.agrilinkback.module.trade.mapper;

import com.example.agrilinkback.module.trade.entity.TradeOrder;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
/**
 * ?????????????????????????????
 */
public interface TradeOrderMapper {

    @Select("""
            select order_id, title, price, content, order_status, type, picture,
                   own_name, cooperation_name, create_time, update_time, address,
                   stock, spec, unit, min_purchase
            from tb_order
            order by update_time desc
            """)
    List<TradeOrder> findAll();

    @Select("""
            select order_id, title, price, content, order_status, type, picture,
                   own_name, cooperation_name, create_time, update_time, address,
                   stock, spec, unit, min_purchase
            from tb_order
            where order_id = #{orderId}
            """)
    TradeOrder findByOrderId(@Param("orderId") Integer orderId);

    @Select("""
            select order_id, title, price, content, order_status, type, picture,
                   own_name, cooperation_name, create_time, update_time, address,
                   stock, spec, unit, min_purchase
            from tb_order
            where own_name = #{ownName}
            order by update_time desc
            """)
    List<TradeOrder> findByOwner(@Param("ownName") String ownName);

    @Select("""
            select order_id, title, price, content, order_status, type, picture,
                   own_name, cooperation_name, create_time, update_time, address,
                   stock, spec, unit, min_purchase
            from tb_order
            where cooperation_name = #{cooperationName}
            order by update_time desc
            """)
    List<TradeOrder> findByCooperationName(@Param("cooperationName") String cooperationName);

    // ??? SQL ??????????? order_id???????????????
    @Select("select coalesce(max(order_id), 0) + 1 from tb_order")
    Integer nextId();

    @Insert("""
            insert into tb_order (
                order_id, title, price, content, order_status, type, picture,
                own_name, cooperation_name, create_time, update_time, address,
                stock, spec, unit, min_purchase
            ) values (
                #{order.orderId}, #{order.title}, #{order.price}, #{order.content},
                #{order.orderStatus}, #{order.type}, #{order.picture}, #{order.ownName},
                #{order.cooperationName}, #{order.createTime}, #{order.updateTime}, #{order.address},
                #{order.stock}, #{order.spec}, #{order.unit}, #{order.minPurchase}
            )
            """)
    int insert(@Param("order") TradeOrder order);

    @Update("""
            update tb_order
            set title = #{order.title},
                price = #{order.price},
                content = #{order.content},
                type = #{order.type},
                picture = #{order.picture},
                update_time = #{order.updateTime},
                address = #{order.address},
                stock = #{order.stock},
                spec = #{order.spec},
                unit = #{order.unit},
                min_purchase = #{order.minPurchase}
            where order_id = #{order.orderId}
            """)
    int update(@Param("order") TradeOrder order);

    @Update("""
            update tb_order
            set order_status = #{orderStatus},
                cooperation_name = #{cooperationName},
                update_time = current_timestamp
            where order_id = #{orderId}
            """)
    int updateStatus(
            @Param("orderId") Integer orderId,
            @Param("orderStatus") Integer orderStatus,
            @Param("cooperationName") String cooperationName
    );

    @Delete("delete from tb_order where order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Integer orderId);
}
