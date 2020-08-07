package de.fhdo.swt.example.repository;

import de.fhdo.swt.example.entity.Journey;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class JourneyDAO {
    @PersistenceContext
    private final EntityManager entityManager;

    public JourneyDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Journey> findById(long id) {
        return Optional.ofNullable(this.entityManager.find(Journey.class, id));
    }
    public void save(Journey journey) {
        this.entityManager.persist(journey);
    }

    public void deleteById(long id) {
        this.findById(id).ifPresent(j -> this.entityManager.remove(j));
    }

    public void update(Journey journey) {
        this.entityManager.merge(journey);
    }

    public List<Journey> findAll() {
        CriteriaQuery<Journey> query = this.entityManager.getCriteriaBuilder().createQuery(Journey.class);
        Root<Journey> from = query.from(Journey.class);
        CriteriaQuery<Journey> selectAll = query.select(from);
        return this.entityManager.createQuery(selectAll).getResultList();
    }
}
