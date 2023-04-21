package com.example.helppsy.repository;

import com.example.helppsy.entity.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PsyRepository extends JpaRepository<Psychologist, Integer> {
}
