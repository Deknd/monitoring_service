package com.denknd.port;

import com.denknd.entity.Address;

import java.util.List;

public interface AddressRepository {

    Address addAddress(Address address);
    List<Address> findAddressByUserId(Long userId);
}
