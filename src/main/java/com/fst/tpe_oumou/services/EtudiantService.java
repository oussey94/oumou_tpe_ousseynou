package com.fst.tpe_oumou.services;

import com.fst.tpe_oumou.entites.Etudiant;

import java.util.List;

public interface EtudiantService {
    Etudiant addEtudiant(Etudiant etudiant);
    Etudiant updateEtudiant(Etudiant etudiant);
    void deleteEtudiant(Long id);
    List<Etudiant> getAllEtudiants();
    Etudiant getEtudiantParId(Long id);
}
