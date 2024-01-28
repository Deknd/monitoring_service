package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.port.AddressRepository;
import com.denknd.services.AddressService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public List<Address> getAddresses(Long userId) {
        return this.addressRepository.findAddressByUserId(userId);
    }

    @Override
    public Address addAddressByUser(Address address) {
        return this.addressRepository.addAddress(address);
    }
}
