package site.autzone.event.handler.domain.repository;

import org.springframework.data.repository.CrudRepository;
import site.autzone.event.handler.domain.Attribute;

public interface AttributeCrudRepository extends CrudRepository<Attribute, Long> {

}
