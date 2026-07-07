package com.example.agrilinkback.module.trade.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.trade.dto.ShoppingCartRequest;
import com.example.agrilinkback.module.trade.entity.ShoppingCart;
import com.example.agrilinkback.module.trade.mapper.ShoppingCartMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartService(ShoppingCartMapper shoppingCartMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
    }

    public List<ShoppingCart> listShoppingCarts() {
        return shoppingCartMapper.findAll();
    }

    public List<ShoppingCart> listShoppingCartsByOwner(String ownName) {
        return shoppingCartMapper.findByOwner(ownName);
    }

    public ShoppingCart getShoppingCart(Integer shoppingId) {
        ShoppingCart shoppingCart = shoppingCartMapper.findByShoppingId(shoppingId);
        if (shoppingCart == null) {
            throw new BusinessException(404, "Shopping cart item not found");
        }
        return shoppingCart;
    }

    public ShoppingCart createShoppingCart(ShoppingCartRequest request) {
        LocalDateTime now = LocalDateTime.now();
        ShoppingCart shoppingCart = new ShoppingCart(
                shoppingCartMapper.nextId(),
                request.orderId(),
                request.count(),
                request.ownName(),
                now,
                now
        );
        shoppingCartMapper.insert(shoppingCart);
        return getShoppingCart(shoppingCart.shoppingId());
    }

    public ShoppingCart updateShoppingCart(Integer shoppingId, ShoppingCartRequest request) {
        getShoppingCart(shoppingId);
        shoppingCartMapper.updateCount(shoppingId, request.count());
        return getShoppingCart(shoppingId);
    }

    public void deleteShoppingCart(Integer shoppingId) {
        getShoppingCart(shoppingId);
        shoppingCartMapper.deleteByShoppingId(shoppingId);
    }
}
