package com.fst.tpe_oumou;

import com.fst.tpe_oumou.entites.Etudiant;
import com.fst.tpe_oumou.services.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TpeOumouApplication implements CommandLineRunner {
    @Autowired
    EtudiantService etudiantService;

    public static void main(String[] args) {
        SpringApplication.run(TpeOumouApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
       // etudiantService.addEtudiant(new Etudiant(null,"Oumou","Aidara",20));
        //etudiantService.addEtudiant(new Etudiant(null,"Oussey","Mbodji",25));

    }
}
