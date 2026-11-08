package com.cloudmaint.asset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudmaint.asset.model.Asset;

// Al emplear JPARepo tenemos métodos como: save(..), findAll(), findById(id) sin escribir SQL
public interface AssetRepository extends JpaRepository<Asset, Long> {

}