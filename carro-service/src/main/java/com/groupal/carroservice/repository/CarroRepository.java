package com.groupal.carroservice.repository;

import com.groupal.carroservice.entity.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarroRepository extends JpaRepository<Carro, Integer> {
    List<Carro> findByUsuarioId(int usuarioId);
}
