package com.example.agrilinkback.module.trade.mapper;

import com.example.agrilinkback.module.trade.entity.Purchase;
import com.example.agrilinkback.module.trade.entity.PurchaseDetail;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PurchaseMapper {

    @Select("""
            select purchase_id, own_name, purchase_type, total_price, address,
                   purchase_status, create_time, update_time, cancel_reason, delivery_no
            from tb_purchase
            order by update_time desc
            """)
    List<Purchase> findAll();

    @Select("""
            select purchase_id, own_name, purchase_type, total_price, address,
                   purchase_status, create_time, update_time, cancel_reason, delivery_no
            from tb_purchase
            where purchase_id = #{purchaseId}
            """)
    Purchase findByPurchaseId(@Param("purchaseId") Integer purchaseId);

    @Select("""
            select purchase_id, own_name, purchase_type, total_price, address,
                   purchase_status, create_time, update_time, cancel_reason, delivery_no
            from tb_purchase
            where own_name = #{ownName}
            order by update_time desc
            """)
    List<Purchase> findByOwner(@Param("ownName") String ownName);

    @Select("""
            select detail_id, purchase_id, order_id, unin_price, sum_price, count
            from tb_purchase_detail
            where purchase_id = #{purchaseId}
            order by detail_id
            """)
    List<PurchaseDetail> findDetailsByPurchaseId(@Param("purchaseId") Integer purchaseId);

    @Select("select coalesce(max(purchase_id), 0) + 1 from tb_purchase")
    Integer nextPurchaseId();

    @Select("select coalesce(max(detail_id), 0) + 1 from tb_purchase_detail")
    Integer nextDetailId();

    @Insert("""
            insert into tb_purchase (
                purchase_id, own_name, purchase_type, total_price, address,
                purchase_status, create_time, update_time, cancel_reason, delivery_no
            ) values (
                #{purchase.purchaseId}, #{purchase.ownName}, #{purchase.purchaseType},
                #{purchase.totalPrice}, #{purchase.address}, #{purchase.purchaseStatus},
                #{purchase.createTime}, #{purchase.updateTime}, #{purchase.cancelReason},
                #{purchase.deliveryNo}
            )
            """)
    int insertPurchase(@Param("purchase") Purchase purchase);

    @Insert("""
            insert into tb_purchase_detail (detail_id, purchase_id, order_id, unin_price, sum_price, count)
            values (#{detail.detailId}, #{detail.purchaseId}, #{detail.orderId}, #{detail.uninPrice},
                    #{detail.sumPrice}, #{detail.count})
            """)
    int insertDetail(@Param("detail") PurchaseDetail detail);

    @Update("""
            update tb_purchase
            set purchase_status = #{purchaseStatus},
                cancel_reason = #{cancelReason},
                delivery_no = #{deliveryNo},
                update_time = current_timestamp
            where purchase_id = #{purchaseId}
            """)
    int updateStatus(
            @Param("purchaseId") Integer purchaseId,
            @Param("purchaseStatus") Integer purchaseStatus,
            @Param("cancelReason") String cancelReason,
            @Param("deliveryNo") String deliveryNo
    );

    @Delete("delete from tb_purchase_detail where purchase_id = #{purchaseId}")
    int deleteDetailsByPurchaseId(@Param("purchaseId") Integer purchaseId);

    @Delete("delete from tb_purchase where purchase_id = #{purchaseId}")
    int deleteByPurchaseId(@Param("purchaseId") Integer purchaseId);
}
