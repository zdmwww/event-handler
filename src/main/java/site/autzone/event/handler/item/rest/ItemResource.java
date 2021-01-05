package site.autzone.event.handler.item.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import net.xdevelop.snowflake.SnowflakeUidGenerator;
import site.autzone.event.handler.EventHandlerInitialization;
import site.autzone.event.handler.cfg.EventTasksProperties;
import site.autzone.event.handler.cfg.Register;
import site.autzone.event.handler.item.Item;
import site.autzone.event.handler.item.ItemRepository;
import site.autzone.event.handler.item.builder.ItemBuilder;
import site.autzone.event.handler.item.rest.dto.ItemDto;

@RestController
@RequestMapping("/api")
public class ItemResource {
  private static final String V1BASE = "v1";

  @Autowired ItemRepository itemRepository;
  @Autowired SnowflakeUidGenerator snowflakeUidGenerator;
  @Autowired EventHandlerInitialization eventHandlerInitialization;
  @Autowired Register register;

  @GetMapping(V1BASE + "/event/handler/stop")
  public ResponseEntity<String> stop() {
    eventHandlerInitialization.unload(true);
    return ResponseEntity.ok("successfull.");
  }

  @GetMapping(V1BASE + "/event/handler/restart")
  public ResponseEntity<String> restart() {
    eventHandlerInitialization.reload();
    return ResponseEntity.ok("successfull.");
  }

  @PostMapping(V1BASE + "/event/handler/tasks")
  @ResponseBody
  public EventTasksProperties tasks() {
    EventTasksProperties tasks = new EventTasksProperties();
    tasks.getEventTasks().addAll(register.getRegisterTaskProperties().values());
    return tasks;
  }

  @PostMapping(V1BASE + "/event/handler/reload")
  public ResponseEntity<String> reload(@RequestBody EventTasksProperties tasks) {
    tasks
        .getEventTasks()
        .forEach(
            task -> {
              register.getRegisterTaskProperties().put(task.getConsumerKey(), task);
            });
    eventHandlerInitialization.reload();
    return ResponseEntity.ok("successfull.");
  }

  @GetMapping(V1BASE + "/item/creator/{creator}")
  public List<ItemDto> findUserItemsByUsername(@PathVariable("creator") String creator) {
    List<ItemDto> itemDtos = new ArrayList<>();
    itemRepository.findItemsByCreator(creator).forEach(item -> itemDtos.add(new ItemDto(item)));
    return itemDtos;
  }

  @GetMapping(V1BASE + "/item")
  public List<ItemDto> retrieveAllItems() {
    List<ItemDto> itemDtos = new ArrayList<>();
    itemRepository.findAll().forEach(item -> itemDtos.add(new ItemDto(item)));
    return itemDtos;
  }

  @GetMapping(V1BASE + "/item/{id}")
  public ItemDto retrieveItem(@PathVariable("id") long id) {
    Item item = itemRepository.findById(id);
    if (item != null) {
      throw new ItemNotFoundException("id-" + id);
    }
    return new ItemDto(item);
  }

  @DeleteMapping(V1BASE + "/item/{id}")
  public void deleteItem(@PathVariable long id) {
    itemRepository.deleteById(id);
  }

  @PostMapping(V1BASE + "/item")
  public ResponseEntity<Object> createItem(@RequestBody ItemDto item) {
    if (item.getConsumerKey() == null) {
      throw new RuntimeException("consumer key is not found.");
    }
    itemRepository.save(new ItemBuilder().item(item).build());
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(item.getId())
            .toUri();
    return ResponseEntity.created(location).build();
  }

  @PutMapping(V1BASE + "/item/{id}")
  public ResponseEntity<Object> updateItem(@RequestBody Item item, @PathVariable long id) {
    Item itemFetch = itemRepository.findById(id);

    if (itemFetch == null) {
      throw new ItemNotFoundException("id-" + id);
    }
    item.setId(id);
    itemRepository.update(item);
    return ResponseEntity.noContent().build();
  }
}
