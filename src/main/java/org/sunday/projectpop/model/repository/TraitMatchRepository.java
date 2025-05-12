package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.sunday.projectpop.model.entity.TraitMatch;

public interface TraitMatchRepository extends JpaRepository<TraitMatch, String > {

}
