package com.fst.tpe_oumou.controllers;

import com.fst.tpe_oumou.entites.Etudiant;
import com.fst.tpe_oumou.services.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin("*")
public class EtudiantRestController {
    @Autowired
    EtudiantService etudiantService;

    @GetMapping
    List<Etudiant> getAll(){
        return etudiantService.getAllEtudiants();
    }

    @GetMapping("/{id}")
    Etudiant getEtudiant(@PathVariable("id") Long id){
        return etudiantService.getEtudiantParId(id);
    }
    @PostMapping
    Etudiant add(@RequestBody Etudiant etudiant){
        return etudiantService.addEtudiant(etudiant);
    }

    @DeleteMapping("/{id}")
    void del(@PathVariable("id") Long id){
        etudiantService.deleteEtudiant(id);
    }

    @PutMapping("/{id}")
    Etudiant update(@PathVariable("id") Long id, @RequestBody Etudiant etudiant){
        Etudiant e=etudiantService.getEtudiantParId(id);
        e.setPrenom(etudiant.getPrenom());
        e.setNom(etudiant.getNom());
        e.setAge(etudiant.getAge());
        return etudiantService.updateEtudiant(etudiant);
    }
}
