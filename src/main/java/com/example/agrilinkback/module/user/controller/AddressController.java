package com.example.agrilinkback.module.user.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.user.dto.AddressRequest;
import com.example.agrilinkback.module.user.entity.Address;
import com.example.agrilinkback.module.user.service.AddressService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收货地址接口，支持按用户查询和维护地址。
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ApiResponse<List<Address>> listAddresses() {
        return ApiResponse.success(addressService.listAddresses());
    }

    @GetMapping("/{id}")
    public ApiResponse<Address> getAddress(@PathVariable Integer id) {
        return ApiResponse.success(addressService.getAddress(id));
    }

    @GetMapping("/owners/{ownName}")
    public ApiResponse<List<Address>> listAddressesByOwner(@PathVariable String ownName) {
        return ApiResponse.success(addressService.listAddressesByOwner(ownName));
    }

    @PostMapping
    public ApiResponse<Address> createAddress(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.createAddress(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Address> updateAddress(@PathVariable Integer id, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.updateAddress(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ApiResponse.ok();
    }
}
