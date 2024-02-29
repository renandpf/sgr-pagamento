CREATE TABLE IF NOT EXISTS `sgr_pagamento_database`.`Pagamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `identificadorPagamentoExterno` varchar(255) NOT NULL,
  `valor` decimal(10,0) NOT NULL,
  `pedidoId` int DEFAULT NULL,
  
  KEY `FK_caf0721618f8177f10ca43a79f4` (`pedidoId`),
  CONSTRAINT `FK_caf0721618f8177f10ca43a79f4` FOREIGN KEY (`pedidoId`) REFERENCES `Pedido` (`id`)
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sgr_pagamento_database`.`Pedido` (
  `id` int NOT NULL AUTO_INCREMENT,
  `valor` decimal(10,0) NOT NULL,
  `clienteId` int DEFAULT NULL,
  
  KEY `FK_caf0721618f8177f10ca43a79f5` (`clienteId`),
  CONSTRAINT `FK_caf0721618f8177f10ca43a79f5` FOREIGN KEY (`clienteId`) REFERENCES `Cliente` (`id`)
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sgr_pagamento_database`.`Cliente` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `telefone` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `PlataformaPagamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`id`)
);
