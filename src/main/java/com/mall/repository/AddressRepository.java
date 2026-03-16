package com.mall.repository;

import com.mall.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {

    Address save(Address address);

    Optional<Address> findById(Long id);

    List<Address> findByUserId(Long userId);

    Optional<Address> findDefaultByUserId(Long userId);

    Address update(Address address);

    void deleteById(Long id);

    void deleteByUserId(Long userId);

    void setDefaultAddress(Long userId, Long addressId);

    Boolean existsById(Long id);
}
