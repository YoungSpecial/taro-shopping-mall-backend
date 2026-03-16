package com.mall.repository.impl;

import com.mall.model.Address;
import com.mall.repository.AddressRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MockAddressRepository implements AddressRepository {

    private final ConcurrentHashMap<Long, Address> addresses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Long>> userIdToAddressIds = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Address save(Address address) {
        if (address.getId() == null) {
            address.setId(idGenerator.getAndIncrement());
            address.setCreatedAt(LocalDateTime.now());
        }
        address.setUpdatedAt(LocalDateTime.now());

        addresses.put(address.getId(), address);
        userIdToAddressIds.computeIfAbsent(address.getUserId(), k -> new ArrayList<>()).add(address.getId());

        return address;
    }

    @Override
    public Optional<Address> findById(Long id) {
        return Optional.ofNullable(addresses.get(id));
    }

    @Override
    public List<Address> findByUserId(Long userId) {
        List<Long> addressIds = userIdToAddressIds.get(userId);
        if (addressIds == null) {
            return new ArrayList<>();
        }

        return addressIds.stream()
                .map(addresses::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Address> findDefaultByUserId(Long userId) {
        return findByUserId(userId).stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                .findFirst();
    }

    @Override
    public Address update(Address address) {
        return save(address);
    }

    @Override
    public void deleteById(Long id) {
        Address address = addresses.remove(id);
        if (address != null) {
            List<Long> addressIds = userIdToAddressIds.get(address.getUserId());
            if (addressIds != null) {
                addressIds.remove(id);
            }
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        List<Long> addressIds = userIdToAddressIds.remove(userId);
        if (addressIds != null) {
            addressIds.forEach(addresses::remove);
        }
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        findByUserId(userId).forEach(a -> a.setIsDefault(false));
        Address defaultAddress = addresses.get(addressId);
        if (defaultAddress != null) {
            defaultAddress.setIsDefault(true);
        }
    }

    @Override
    public Boolean existsById(Long id) {
        return addresses.containsKey(id);
    }
}
