package com.logiaduana.service;

import com.logiaduana.model.Envio;
import com.logiaduana.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public List<Envio> findAll() {
        return envioRepository.findAll();
    }

    public long count() {
        return envioRepository.count();
    }
}