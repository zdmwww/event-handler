package site.autzone.event.handler.rest.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import site.autzone.event.handler.domain.Item;
import site.autzone.event.handler.domain.repository.CustomItemRepository;
import site.autzone.event.handler.domain.repository.ItemCrudRepository;
import site.autzone.event.handler.rest.domain.dto.ItemDto;

@RestController
@RequestMapping("/api")
public class ItemResource {
	private final static String V1BASE = "v1";
	
	@Autowired
	ItemCrudRepository itemCrudRepository;
	@Autowired
	CustomItemRepository customItemRepository;
	
	@GetMapping(V1BASE+"/items/users/{username}")
	public List<ItemDto> findUserItemsByUsername(@PathVariable("username") String username) {
		List<ItemDto> itemDtos = new ArrayList<>();
		customItemRepository.findItemsByUsername(username)
		.forEach(item -> itemDtos.add(new ItemDto(item)));
		return itemDtos;
	}
	
	@GetMapping(V1BASE+"/items")
	public List<ItemDto> retrieveAllItems() {
		List<ItemDto> itemDtos = new ArrayList<>();
		itemCrudRepository.findAll()
		.forEach(item -> itemDtos.add(new ItemDto(item)));
		return itemDtos;
	}
	
	@GetMapping(V1BASE+"/items/{id}")
	public ItemDto retrieveItem(@PathVariable("id") long id) {
		Optional<Item> item = itemCrudRepository.findById(id);
		if(!item.isPresent()) {
			throw new ItemNotFoundException("id-" + id);
		}
		return new ItemDto(item.get());
	}
	
	@DeleteMapping(V1BASE+"/items/{id}")
	public void deleteItem(@PathVariable long id) {
		itemCrudRepository.deleteById(id);
	}
	
	@PostMapping(V1BASE+"/items")
	public ResponseEntity<Object> createItem(@RequestBody Item item) {
		if(item.getAttributes() != null) {
			item.getAttributes().forEach(attr -> attr.setItem(item));
		}
		Item savedItem = itemCrudRepository.save(item);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedItem.getId()).toUri();

		return ResponseEntity.created(location).build();
	}
	
	@PutMapping(V1BASE+"/items/{id}")
	public ResponseEntity<Object> updateItem(@RequestBody Item item, @PathVariable long id) {
		Optional<Item> itemOptional = itemCrudRepository.findById(id);

		if (!itemOptional.isPresent()) {
			throw new ItemNotFoundException("id-" + id);
		}
		item.setId(id);
		if(item.getAttributes() != null) {
			item.getAttributes().forEach(attr -> attr.setItem(item));
		}
		itemCrudRepository.save(item);
		return ResponseEntity.noContent().build();
	}
}
