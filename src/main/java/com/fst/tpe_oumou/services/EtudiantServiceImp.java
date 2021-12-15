package com.fst.tpe_oumou.services;

import com.fst.tpe_oumou.entites.Etudiant;
import com.fst.tpe_oumou.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EtudiantServiceImp implements EtudiantService{
    //@Autowired
    EtudiantRepository etudiantRepository;

    public EtudiantServiceImp(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    @Override
    public Etudiant addEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    @Override
    public Etudiant updateEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    @Override
    public void deleteEtudiant(Long id) {
        etudiantRepository.deleteById(id);

    }

    @Override
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();      //Select* From Etudiant
    }

    @Override
    public Etudiant getEtudiantParId(Long id) {
        return etudiantRepository.findById(id).get();
    }
}
