package com.example.agrilinkback.module.user.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.user.dto.AddressRequest;
import com.example.agrilinkback.module.user.entity.Address;
import com.example.agrilinkback.module.user.mapper.AddressMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    private final AddressMapper addressMapper;

    public AddressService(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public List<Address> listAddresses() {
        return addressMapper.findAll();
    }

    public Address getAddress(Integer id) {
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new BusinessException(404, "Address not found");
        }
        return address;
    }

    public List<Address> listAddressesByOwner(String ownName) {
        return addressMapper.findByOwner(ownName);
    }

    @Transactional
    public Address createAddress(AddressRequest request) {
        if (Integer.valueOf(1).equals(request.isDefault())) {
            addressMapper.clearDefaultByOwner(request.ownName());
        }
        Address address = new Address(
                addressMapper.nextId(),
                request.ownName(),
                request.consignee(),
                request.phone(),
                request.addressDetail(),
                request.isDefault()
        );
        addressMapper.insert(address);
        return getAddress(address.id());
    }

    @Transactional
    public Address updateAddress(Integer id, AddressRequest request) {
        getAddress(id);
        if (Integer.valueOf(1).equals(request.isDefault())) {
            addressMapper.clearDefaultByOwner(request.ownName());
        }
        Address address = new Address(
                id,
                request.ownName(),
                request.consignee(),
                request.phone(),
                request.addressDetail(),
                request.isDefault()
        );
        addressMapper.update(address);
        return getAddress(id);
    }

    public void deleteAddress(Integer id) {
        getAddress(id);
        addressMapper.deleteById(id);
    }
}
