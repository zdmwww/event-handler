package site.autzone.event.handler.domain.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.task.listener.TaskStatus;

@Service
public class CustomItemRepository implements ICustomItemRepository{
	@Autowired
	EntityManager em;
	@Autowired
	ItemCrudRepository itemCrudRepository;
	
	@Override
	public List<Item> findItemsByUsername(String username) {
		TypedQuery<Item> query = em.createQuery("select i from Item as i where i.creator = :username", 
				Item.class);
		query.setParameter("username", username);
		return query.getResultList();
	}

	@Override
	@Transactional
	public List<Item> fetchItems(String consumerKey, int maxResult) {
		TypedQuery<Item> query = em.createQuery("select i from Item as i where i.consumerKey = :consumerKey and i.status = :status", 
				Item.class);
		query.setParameter("consumerKey", consumerKey);
		query.setParameter("status", TaskStatus.Created.getName());
		query.setLockMode(LockModeType.OPTIMISTIC);
		query.setMaxResults(maxResult);
		List<Item> items = query.getResultList();
		items.forEach(item -> {
			item.setStatus(TaskStatus.Running.getName());
			em.persist(item);
		});
		return items;
	}

	@Override
	@Transactional
	public void cleanItems(int maxResult) {
		TypedQuery<Item> query = em.createQuery("select i from Item as i where i.status = :status", 
				Item.class);
		query.setParameter("status", TaskStatus.RanToCompletion.getName());
		query.setLockMode(LockModeType.OPTIMISTIC);
		query.setMaxResults(maxResult);
		itemCrudRepository.deleteAll(query.getResultList());
	}
	
}
