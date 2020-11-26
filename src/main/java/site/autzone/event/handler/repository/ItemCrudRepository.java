package site.autzone.event.handler.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import site.autzone.event.handler.domain.Item;

public interface ItemCrudRepository extends PagingAndSortingRepository<Item, Long> {}
