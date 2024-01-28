package com.denknd.services;

import com.denknd.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddresses(Long userId);

    Address addAddressByUser(Address address);
}
