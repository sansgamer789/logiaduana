package com.logiaduana.service;

import com.logiaduana.model.Carga;
import com.logiaduana.repository.CargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CargaService {

    @Autowired
    private CargaRepository cargaRepository;

    public List<Carga> findAll() {
        return cargaRepository.findAll();
    }

    public Carga save(Carga carga) {
        return cargaRepository.save(carga);
    }

    public long count() {
        return cargaRepository.count();
    }
}
