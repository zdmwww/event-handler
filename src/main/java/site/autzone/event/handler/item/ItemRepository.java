package site.autzone.event.handler.item;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import site.autzone.event.handler.cfg.Register;
import site.autzone.event.handler.task.TaskStatus;
import site.autzone.sqlbee.SqlRunner;
import site.autzone.sqlbee.builder.SqlBuilder;
import site.autzone.sqlbee.value.Value;

import javax.sql.DataSource;

/**
 * Item增删改查
 *
 * @author wisesean
 */
public class ItemRepository {

  private static Logger logger= LoggerFactory.getLogger(ItemRepository.class);

  @Autowired SqlRunner sqlRunner;
  @Autowired Register register;
  @Qualifier("event-datasource")
  @Autowired
  DataSource dataSource;
  private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public synchronized int[] batch(String itemP, Object[][] params) {

    return sqlRunner.batch("update "+ itemP + " set status_ = ?,version_=(version_+1), modify_time_ = ? where id_ = ? and version_ = ?", params);
  }

  public synchronized Argument getArgByItem(Item item) {

    return sqlRunner.queryBean(
            SqlBuilder.createQuery()
                    .table("smart_arguments" + item.getPartition(), "a")
                    .condition("=")
                    .left("a.item_id_")
                    .right(new Value(item.getId()))
                    .end()
                    .build(),
            Argument.class);
  }

  /**
   * 判断ITEM是否存在
   *
   * @param item
   * @param status
   * @return
   */
  public synchronized long count(Item item, TaskStatus status) {

    return sqlRunner.count(
            SqlBuilder.createQuery()
                    .table("smart_item" + item.getPartition(), "i")
                    .end()
                    .condition("=")
                    .left("i.id_")
                    .right(new Value(item.getId()))
                    .end()
                    .condition("=")
                    .left("i.status_")
                    .right(new Value(status.getCode()))
                    .end()
                    .isCount()
                    .build());
  }

  /**
   * 更新ITEM状态
   *
   * @param item
   * @param status
   * @return
   */
  public synchronized int updateTaskStatus(Item item, TaskStatus status) {

    return sqlRunner.update(
            "update smart_item"
                    + item.getPartition()
                    + " set status_ = ?,finish_message_ = ?,version_ = (version_+1) where id_ = ? and version_ = ?",
            status.getCode(),
            status.getName(),
            item.getId(),
            item.getVersion());
  }

  /**
   * 保存item
   *
   * @param item
   * @return
   * @throws Exception
   */
  public synchronized int save(Item item) {


    int ret =
            sqlRunner.insert(
                    SqlBuilder.createInsert()
                            .table("smart_arguments" + p(item.getId(), item.getConsumerKey()))
                            .column("item_id_", new Value(item.getId()))
                            .column("value_", new Value(item.getAttribute().getValue()))
                            .build());
    if (ret == 1) {
      return sqlRunner.insert(
              SqlBuilder.createInsert()
                      .table("smart_item" + p(item.getId(), item.getConsumerKey()))
                      .column("id_", new Value(item.getId()))
                      .column("batch_id_", new Value(item.getBatchId()))
                      .column("consumer_key_", new Value(item.getConsumerKey()))
                      .column("create_time_", new Value(dateformat.format(item.getCreateTime())))
                      .column("creator_", new Value(item.getCreator()))
                      .column("desc_", new Value(item.getDesc()))
                      .column("detail_", new Value(item.getDetail()))
                      .column("finish_message_", new Value(item.getFinishMessage()))
                      .column("item_source_", new Value(item.getItemSource()))
                      .column("modify_time_", new Value(dateformat.format(item.getModifyTime())))
                      .column("name_", new Value(item.getName()))
                      .column("route_key_", new Value(item.getRouteKey()))
                      .column("status_", new Value(item.getStatus()))
                      .column("version_", new Value(item.getVersion()))
                      .build());
    } else {
      return ret;
    }
  }

  public synchronized int update(Item item) {

    int ret =
            sqlRunner.update(
                    SqlBuilder.createUpdate().table("smart_arguments" + p(item.getId(), item.getConsumerKey()))
                            .column("item_id_", new Value(item.getId()))
                            .column("value_", new Value(item.getAttribute().getValue()))
                            .condition("=")
                            .left("item_id_")
                            .right(new Value(item.getId())).end()
                            .build());
    if (ret == 1) {
      return sqlRunner.update(
              SqlBuilder.createUpdate().table("smart_item" + p(item.getId(), item.getConsumerKey()))
                      .column("batch_id_", new Value(item.getBatchId()))
                      .column("consumer_key_", new Value(item.getConsumerKey()))
                      .column("create_time_", new Value(dateformat.format(item.getCreateTime())))
                      .column("creator_", new Value(item.getCreator()))
                      .column("desc_", new Value(item.getDesc()))
                      .column("detail_", new Value(item.getDetail()))
                      .column("finish_message_", new Value(item.getFinishMessage()))
                      .column("item_source_", new Value(item.getItemSource()))
                      .column("modify_time_", new Value(dateformat.format(item.getModifyTime())))
                      .column("name_", new Value(item.getName()))
                      .column("route_key_", new Value(item.getRouteKey()))
                      .column("status_", new Value(item.getStatus()))
                      .column("version_", new Value(item.getVersion()))
                      .condition("=")
                      .left("id_")
                      .right(new Value(item.getId()))
                      .end()
                      .build());
    } else {
      return ret;
    }
  }

  /**
   * 根据Item创建人查询Item
   *
   * @param creator
   * @return
   */
  public synchronized List<Item> findItemsByCreator(String creator) {

    return sqlRunner.queryBeans(
            SqlBuilder.createQuery().table("smart_item", "i")
                    .condition("=")
                    .left("i.creator_")
                    .right(new Value(creator))
                    .end()
                    .build(),
            Item.class);
  }

  public synchronized int delete(int maxResults, int partition) {

    return sqlRunner.delete(
            SqlBuilder.createDelete().table("smart_item_p" + partition, "i")
                    .condition("=")
                    .left("i.status_")
                    .right(new Value(TaskStatus.RanToCompletion.getCode()))
                    .end()
                    .maxResults(maxResults)
                    .build());
  }

  public synchronized int deleteById(long id) {

    return sqlRunner.delete(
            SqlBuilder.createDelete().table("smart_item")
                    .condition("=")
                    .left("id_")
                    .right(new Value(id))
                    .end()
                    .build());
  }

  public synchronized Iterable<Item> findAll() {

    return sqlRunner.queryBeans(
            SqlBuilder.createQuery().table("smart_item", "i").end().build(),
            Item.class);
  }

  public synchronized Item findById(long id) {

    return sqlRunner.queryBean(
            SqlBuilder.createQuery()
                    .table("smart_item", "i")
                    .condition("=")
                    .left("id_")
                    .right(new Value(id))
                    .end()
                    .build(),
            Item.class);
  }

  private synchronized String p(long id, String consumerKey) {
    int fetchers = register.getRegisterTaskProperties().get(consumerKey).getFetchers();
    if (fetchers == 0) {
      return "_p0";
    }
    return "_p" + id % fetchers;
  }

  /**
   * 查询item
   * @param tableName
   * @param consumerKey
   * @param maxResult
   * @param status
   * @return
   */
  public  synchronized List<Item> fetchItems(String tableName, String consumerKey, int maxResult, TaskStatus status) {
    SqlBuilder queryBuilder = SqlBuilder.createQuery().table(tableName, "i")
            .condition("=")
            .left("i.consumer_key_")
            .right(new Value(consumerKey))
            .end()
            .condition("=")
            .left("i.status_")
            .right(new Value(status.getCode()))
            .end()
            .maxResults(maxResult);

    return sqlRunner.queryBeans(queryBuilder.build(), Item.class);
  }
}