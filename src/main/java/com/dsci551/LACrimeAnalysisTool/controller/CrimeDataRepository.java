package com.dsci551.LACrimeAnalysisTool.controller;

import com.dsci551.LACrimeAnalysisTool.model.CrimeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrimeDataRepository extends JpaRepository<CrimeData, String> {
    // Custom methods can be defined here
}