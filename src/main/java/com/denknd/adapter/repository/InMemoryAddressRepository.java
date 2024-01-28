package com.denknd.adapter.repository;

import com.denknd.entity.Address;
import com.denknd.port.AddressRepository;

import java.util.*;

public class InMemoryAddressRepository implements AddressRepository {

    private final Map<Long, List<Address>> addressUserRelationship = new HashMap<>();
    private final Map<Long, Address> addressIdUserIdMap = new HashMap<>();
    private final Random random = new Random();

    @Override
    public Address addAddress(Address address) {
        var userId = address.getOwner().getUserId();
        long addressId;
        if (address.getAddressId() == null) {

            do {
                addressId = Math.abs(random.nextLong());

            } while (this.addressUserRelationship.containsKey(addressId));
        } else {
            return null;
        }
        address.setAddressId(addressId);

        if (this.addressUserRelationship.containsKey(userId)) {
            var addresses = this.addressUserRelationship.get(userId);
            addresses.add(address);
            this.addressUserRelationship.put(userId, addresses);
        } else {

            var addresses = new ArrayList<Address>();
            addresses.add(address);
            this.addressUserRelationship.put(userId, addresses);
        }
        addressIdUserIdMap.put(addressId, address);
        return address;
    }

    @Override
    public List<Address> findAddressByUserId(Long userId) {
        return addressUserRelationship.getOrDefault(userId, List.of());
    }
}
