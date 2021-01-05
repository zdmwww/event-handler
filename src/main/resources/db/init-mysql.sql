CREATE TABLE `smart_item_p` (
  `id_` bigint(20) NOT NULL,
  `batch_id_` varchar(255) DEFAULT NULL,
  `name_` varchar(255) DEFAULT NULL,
  `route_key_` varchar(255) DEFAULT NULL,
  `consumer_key_` varchar(255) DEFAULT NULL,
  `item_source_` varchar(255) DEFAULT NULL,
  `create_time_` datetime(6) DEFAULT NULL,
  `creator_` varchar(255) DEFAULT NULL,
  `modify_time_` datetime(6) DEFAULT NULL,
  `status_` varchar(255) DEFAULT NULL,
  `finish_message_` varchar(255) DEFAULT NULL,
  `desc_` varchar(2000) DEFAULT NULL,
  `detail_` text,
  `version_` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `smart_arguments_p` (
  `item_id_` bigint(20) NOT NULL,
  `value_` text,
  PRIMARY KEY (`item_id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `smart_item_p` ADD INDEX `smart_item_p_consumer_key_idx` (`consumer_key_` ASC);
ALTER TABLE `smart_item_p` ADD INDEX `smart_item_p_status_idx` (`status_` ASC);