package site.autzone.event.handler.repository;

import java.util.List;
import site.autzone.event.handler.domain.Item;

public interface ICustomItemRepository {
	List<Item> findItemsByUsername(String username);
	List<Item> fetchItems(String consumerKey, int maxResult);
	void cleanItems(int maxResult);
}
